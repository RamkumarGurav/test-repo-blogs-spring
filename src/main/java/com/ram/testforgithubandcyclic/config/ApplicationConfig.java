package com.ram.testforgithubandcyclic.config;

import com.ram.testforgithubandcyclic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    @Autowired
    private final UserRepository userRepository;

    //---------UserDetailsService bean-------------------------------------------------------------
    //It provides user details for authentication and authorization based on the provided email (username).
    //this bean is created so that it can be used in our AuthenticationProvider Bean and JwtAuthenticationFilter and JwtService
    @Bean
    public UserDetailsService userDetailsService() {
        log.info("****** Inside userDetailsService BEAN ");
        //UserDetailsService is an inbuilt interface,that has a single method to get the userDetails from the DB ,which is known as
        //"loadUserByUsername()" which takes the username so here we override this method ,since it has a single method we can
        // convert this method as lambda expression
        return username -> userRepository.findByEmailAndActive(username,true).orElseThrow(() -> new UsernameNotFoundException("User not Found"));
//        return new UserDetailsService() {
//            @Override
//            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//                return userRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("User not Found"));
//            }
//        }
    }
    //----------------------------------------------------------------------


    //--------PasswordEncoder bean--------------------------------------------------------------
    // Creating a PasswordEncoder bean for password hashing , it is used in the  AuthenticationProvider Bean and
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    //----------------------------------------------------------------------


    //----------------------------------------------------------------------
    //AuthenticationProvider:
    //The AuthenticationProvider interface is responsible for authenticating a user's credentials
    // against a user store (such as a database) and returning an Authentication object if the credentials
    // are valid. It encapsulates the logic for verifying user credentials and constructing an authenticated
    // Authentication object.
    //
    //Responsibilities:
    //
    //1.Verifying user credentials (username and password).
    //2.Optionally, handling additional authentication mechanisms (e.g., two-factor authentication).
    //3.Constructing an Authentication object with the user's authorities and other relevant information.
    //AuthenticationProvider's Connection to UserDetailsService:
    //The AuthenticationProvider often collaborates with the UserDetailsService to retrieve user details
    // based on the provided username. It uses the retrieved user details to compare passwords and perform
    // authentication.
    //
    //AuthenticationProvider's Connection to PasswordEncoder:
    //To validate passwords, the AuthenticationProvider might use the PasswordEncoder to compare
    // the provided password with the stored password (after applying a hashing algorithm).
    //

    //we need to add this authenticationProvider to securityFilterChain to use it
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;

    }
    //----------------------------------------------------------------------


    //----------------------------------------------------------------------
    //The AuthenticationManager interface is a higher-level component responsible for orchestrating the
    // thentication process. It coordinates the authentication attempt, including handling multiple
    // AuthenticationProvider instances, and selects the appropriate provider based on the authentication
    // request.
    //
    //Responsibilities:
    //
    //1.Delegating the authentication attempt to one or more AuthenticationProvider instances.
    //2.Selecting the appropriate provider for a given authentication request.
    //3.Handling various authentication mechanisms in a unified manner.
    //AuthenticationManager's Connection to AuthenticationProvider:
    //The AuthenticationManager uses one or more AuthenticationProvider instances to perform the actual
    // authentication. It iterates through the available providers until one successfully authenticates the
    // user or all providers fail.
    //
    //Interlinking AuthenticationProvider and AuthenticationManager:
    //In a Spring Security configuration, you typically configure an AuthenticationManager by providing one or
    // more AuthenticationProvider instances. The AuthenticationManager delegates the authentication process
    // to these providers.
    //
    //Here's how they are typically interlinked in a Spring Security configuration:
    //
    //You create one or more AuthenticationProvider beans using the DaoAuthenticationProvider (or custom implementations).
    //You create an AuthenticationManager bean and set its AuthenticationProvider instances to the
    // previously defined providers.

    //The reason for returning configuration.getAuthenticationManager() instead of "
    // @Bean
    //public AuthenticationManager authenticationManager() {
    //    return new ProviderManager(Arrays.asList(authenticationProvider));
    //}"
    // is because that the approach using ProviderManager requires you to manually create and configure an
    // AuthenticationProvider instance with all its dependencies (like UserDetailsService and PasswordEncoder),
    // whereas using configuration.getAuthenticationManager() leverages Spring Boot's auto-configuration and
    // provides an already configured AuthenticationManager instance which uses previously created
    // authenticationProvider bean which is a DaoAuthenticationProvider  .
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();//this automatically selects our authenticationProvider bean
    }
    //----------------------------------------------------------------------

}
