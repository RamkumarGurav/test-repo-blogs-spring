package com.ram.testforgithubandcyclic.config;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ProjectSecurityConfig {


    private final String[] whiteListedUrls = {
            "/public/**",
            "/", "",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/webjars/**"


    };


    @Autowired
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //here we have disabled csrf so that we can perform post,put,patch,delete actions

        //to make sesseion stateless which means not storing session in the server for every request// here we used this
//      sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))


        http
                .cors(cors -> cors.disable())//disabling default cors settings so that our custom cors setting can be applied
                .csrf(csrf -> csrf.disable())// disabling default csrf so that we can make post,put,patch and delete requests
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(whiteListedUrls).permitAll() //whitelisting some urls for public usage
                                .requestMatchers("/user-protected/**").hasAnyRole("USER","AUTHOR","ADMIN")//allowing some urls only for "USER"s
                                .requestMatchers("/author-protected/**").hasAnyRole("AUTHOR")//allowing some urls only for "AUTHOR"s
                                .requestMatchers("/admin-protected/**").hasRole("ADMIN")//allowing some urls only for "ADMIN"s
                                .anyRequest().authenticated()// rest of the url paths from above need to be authenticated before we access them(ie only for logged in users irrespective of their roles)
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))//NOT storing session in the server for every request by using STATELESS SessionCreationPolicya
                .authenticationProvider(authenticationProvider) //adding our authenticationProvider (DaoAuthenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);//adding jwtAuthFilter before UsernamePasswordAuthenticationFilter
       //       .formLogin(Customizer.withDefaults())//not recommended for RESTfull user since we dont require form to login
        //        .httpBasic(Customizer.withDefaults());//ot recommended for RESTfull user since we dont require to send username and password inside every request//
        //        httpBasic method  enables Basic Authentication for our application.
                // It configures Spring Security to expect Basic Authentication headers in incoming requests.
        // Basic Authentication is a simple authentication mechanism where the client (usually a web browser) sends
        // a username and password with each request, encoded in base64 format, to the server. The server then checks
        // these credentials to allow or deny access to protected resources

        //----why we don't need httpBasic() for RESTful api----
        //Using httpBasic() authentication in a RESTful API is less common compared to other authentication
        // methods, especially when building public-facing APIs. httpBasic() authentication involves sending
        // the username and password with each request, usually in the form of a header.which means each sesseion has a state  but RESTful api must be
        //stateless .so we use jwtAuthentication filter for login using passing tokens using header and cookies.




        return http.build();
    }


    //----------------CORS----------------------------------------------------------------------
    // we configure cors configuration for urls inside UrlBasedCorsConfigurationSource object by using
    // registerCorsConfiguration  of UrlBasedCorsConfigurationSource object which provides corsConfigurationSource object (ie cors configurations settings) for
    // all the given urls  that matches  our given url patterns "/**" ,this means all our api's urls must follow our corsConfigurationSource rules
//    creating a bean that is UrlBasedCorsConfigurationSource object of type CorsConfigurationSource(here
//    corsConfigurationSource method returns UrlBasedCorsConfigurationSource object
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        //creating "CorsConfiguration" instance and adding properties to
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        //adding allowedOrigins to corsConfiguration
//        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:4000", "http://localhost:5000"));
        corsConfiguration.setAllowedOrigins(Arrays.asList("*"));

        //adding allowedMethods to corsConfiguration
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));

        //// Allow cookies and credentials to be sent in cross-origin requests
        corsConfiguration.setAllowCredentials(true);

        //creating UrlBasedCorsConfigurationSource object (UrlBasedCorsConfigurationSource
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        //adding our corsConfiguration settings to all of our  backend api URL paths by passing
        //our "corsConfiguration" to as arguement to
        source.registerCorsConfiguration("/**", corsConfiguration);

        //returning
        return source;
    }


}



