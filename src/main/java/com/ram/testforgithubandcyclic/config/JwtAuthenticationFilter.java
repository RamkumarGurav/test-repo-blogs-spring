package com.ram.testforgithubandcyclic.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
// if any feild is made final then it automatically wires it and creates constructor using that field
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    //this "JwtAuthenticationFilter" now operates for every request that hits our backend

    @Autowired // if you use @RequiredArgsConstructor then using @Autowired annotation is not necessary just for sake
    //of readablity I have used here
    private final JwtService jwtService;

    @Autowired
    private final UserDetailsService userDetailsService;// its bean that is created inside the ApplicaitonConfig class


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {


        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        final String userEmailFromToken;


        log.info("inside jwtAuthFilter");


//----------------skipping jwtFilter for public url paths------------------
        log.info("*****************: "+request.getRequestURI());
// Define URLs that should skip JWT authentication
        List<AntPathRequestMatcher> skipAuthMatchers = Arrays.asList(
                new AntPathRequestMatcher("/public/**"),
                new AntPathRequestMatcher("/")
                // Add more URLs as needed
        );
        log.info(skipAuthMatchers.toString());

        // Check if the request URL matches any of the skipAuthMatchers
        boolean skipAuth = skipAuthMatchers.stream()
                .anyMatch(matcher -> matcher.matches(request));
        log.info(String.valueOf(skipAuth));

        if (skipAuth) {
            log.info("skipping jwtAuthFilter");
            log.info("*****************: skipped jwtFilter for whitelisted urls");
            // If the request URL matches a skipAuthMatcher, skip the JWT filter move to next filter
            filterChain.doFilter(request, response);
            return;
        }
//-----------------------------------------------------------------------------------------------------

        log.info("I am inside the jwt filter");

        //step1-----------getting jwt token ----------


//----------------------------------------------------------------------
        Cookie[] cookies = request.getCookies();


        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            log.info("****** jwt by Header"+jwt);
        } else if (cookies!=null && cookies.length>0) {

//-----------with using optionals------------------------------------------------
            Optional<String> jwtCookieValue = Arrays.stream(cookies)
                    .filter(cookie -> "jwt".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst();

            if (jwtCookieValue.isPresent()) {
                String cookieValue = jwtCookieValue.get();
                //after user is loggedout if he tries without loggin we give 403 in authmanager
                if ("dummytokenWithoutSpace".equals(cookieValue)) {
                    log.info("******: Dummy jwt cookie");
                    filterChain.doFilter(request, response);
                    return;
                }
                log.info("****** jwt by Cookie" + cookieValue);
                jwt = cookieValue;
            }else{
                filterChain.doFilter(request, response);
                return;
            }
//----------------without using optionals---------------------------------------------------
//            for (Cookie cookie : cookies) {
//                log.info("******: inside of forloop");
//                if ("jwt".equals(cookie.getName())) {
//                    log.info(cookie.getName());
//                    if (cookie.getValue().equals("dummytokenWithoutSpace")){
//                        log.info("******: Dummy jwt cookie");
//                        filterChain.doFilter(request, response);
//                        return;
//                    }
//                    log.info("****** jwt by Cookie"+  cookie.getValue());
//                    jwt = cookie.getValue();
//                }
//            }
//------------------------------------------------------------------------------------

        }else{
            log.info("******No jwt");
            log.info("skipping jwtAuthFilter");
            filterChain.doFilter(request, response);
            return;
        }



        //-----------------------------------------------------------------------------

        //step2---------------getting userEmail from given token-----

            userEmailFromToken = jwtService.extractUsernameFromToken(jwt);


        log.info("******************* :"+userEmailFromToken);

        //step3 ----------if token is valid then allowing him and storing his details inside Security context

        // // getting userDetails from DB if user is not loggedin yet ,by implementing UserDetails interface of the spring security--
        if (userEmailFromToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //getting user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmailFromToken);

            // checking if the token is valid and if valid using his details creating Authentication object of type UsernamePasswordAuthentication object
            // and storing this obj inside securityContextHolder for further requests,(dont wory if token is invalid then it throw the appropriate error)
            if (jwtService.isTokenValid(jwt, userDetails)) {
                log.info("inside jwtAuthFilter ,jwtToken is valid ");

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,//userDetails object of the current user
                        null,//since it's a request without login or register operation ,so don't have any credentials
                        // from it also we don't need to pass the credentials here
                        userDetails.getAuthorities()// Authorities (roles/permissions) associated with the user
                );

                //setting some request details inside our authToken
                //An Authentication object (like the UsernamePasswordAuthenticationToken) can have additional details attached to it. These details provide extra information about the authentication process.
                //The WebAuthenticationDetailsSource is a Spring class used to create a WebAuthenticationDetails object.
                //WebAuthenticationDetails contains information about the HTTP request that initiated the authentication, like IP address, session ID, and so on.
                //By setting these request details, you're adding more context to the authentication process. For example, you might know where the request came from and other useful information.
                //The .buildDetails(request) method creates these details based on the incoming HTTP request.
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                log.info("inside jwtAuthFilter ,putting valid user's authToken into the SecurityContextHolder,");
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }


        }

        log.info("after processing inside jwtAuthFilter moving to next filter");
        filterChain.doFilter(request, response);


    }

}
