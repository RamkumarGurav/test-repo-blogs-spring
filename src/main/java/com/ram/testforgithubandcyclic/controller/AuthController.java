package com.ram.testforgithubandcyclic.controller;


import com.ram.testforgithubandcyclic.collection.User;
import com.ram.testforgithubandcyclic.config.JwtService;
import com.ram.testforgithubandcyclic.error.CustomException;
import com.ram.testforgithubandcyclic.dto.*;
import com.ram.testforgithubandcyclic.repository.UserRepository;
import com.ram.testforgithubandcyclic.service.EmailSenderService;
import com.ram.testforgithubandcyclic.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
public class AuthController {

    @Value("${frontendURL}")
    String frontendURL;

    @Value("${adminSecretPassword}")
    String adminSecretPassword;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    public UserDetailsService userDetailsService;

    @Autowired
    public EmailSenderService emailSenderService;


    @Autowired
    public PasswordEncoder passwordEncoder;


    //CREATESENDTOKEN METHOD
    private ResponseEntity<Object> createSendToken(HttpServletRequest request,
                                                  HttpServletResponse response,
                                                  User user,
                                                  HttpStatus httpStatus
    ){
        String jwtToken = jwtService.generateToken(user);


        // Create a cookie with the token
        Cookie jwtCookie = new Cookie("jwt", jwtToken);
        jwtCookie.setMaxAge(90 * 24 * 60 * 60); // Set the cookie's expiration time in seconds
        jwtCookie.setPath("/"); // Set the cookie's path to "/" so it's accessible across the whole app
        jwtCookie.setHttpOnly(true); // HttpOnly cookies cannot be accessed by JavaScript

        // Add the cookie to the response
        response.addCookie(jwtCookie);

//        //converting user to userDto
//        UserDto userDto=userService.userToUserDto(user);

        //converting user to userWithRoleNameDto object
        UserWithRoleNameDto userDto=userService.userToUserDtoWithRoleName(user);

        TokenRBody rbody = new TokenRBody("success", jwtToken, userDto);
        return ResponseEntity.status(httpStatus).body(rbody);
    }

    //
    //---------------USER REGISTER----------------------------------
    @PostMapping("/public/auth/users/register")
    public ResponseEntity<Object> register(@Valid @RequestBody User userBody,
                                           HttpServletRequest request, HttpServletResponse response,
                                           Authentication authentication) throws MessagingException {

        //step1- after validation filtering the input and building user object with only required fields
        User filteredUser=User.builder()
                .name(userBody.getName())
                .email(userBody.getEmail())
                .password(userBody.getPassword())
                .confirmPassword(userBody.getConfirmPassword())
                .avatar(userBody.getAvatar())
                .mobile(userBody.getMobile())
                .gender(userBody.getGender()).build();


        //step2- creating user
        User newUser1 = userService.register(filteredUser);
        Optional<User> newUser = Optional.ofNullable(newUser1);
        if (!newUser.isPresent()){
            throw new CustomException("Error while creating User",HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {

            return createSendToken(request,response,newUser1,HttpStatus.CREATED);

        } catch (Exception ex) {
            // if an exception occurs after the user has bean created(like during creating token) then ,delete the user else dont
            if (newUser.isPresent()) {
                userRepository.deleteById(newUser1.getId());
            }
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

//        //------------register with email sending-------------------------
//
//
//        User  newUser1 = userService.register(user);
//
//        try {
//        log.info("getting user details using input email and using this details ,generating jwt token");
//        var newUser2 = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//
//
//        //jwt token
//        String jwtToken = jwtService.generateToken(newUser2);
//
//
//
//        // Create a cookie with the token
//        Cookie jwtCookie = new Cookie("jwt", jwtToken);
//        jwtCookie.setMaxAge(90 * 24 * 60 * 60); // Set the cookie's expiration time in seconds
//        jwtCookie.setPath("/"); // Set the cookie's path to "/" so it's accessible across the whole app
//        jwtCookie.setHttpOnly(true); // HttpOnly cookies cannot be accessed by JavaScript
//
//
//        //sending email
//
//        String htmlContent = "<h1>Welcome to our EKart Family, "+newUser2.getName().toUpperCase()+" </h1>"
//                + "<p>Thank you for joining us. We are excited to have you as part of our community!</p>"
//                + "<p>Explore our wide range of products and enjoy a seamless shopping experience.</p>"
//                + "<p>If you have any questions or need assistance, feel free to reach out to our support team.</p>"
//                + "<p>Happy shopping!</p>"
//                + "<p>Best regards,<br>The EKart Team</p>";
//
//            emailSenderService.sendRegistrationEmail(user.getEmail(), htmlContent);
//            // Add the cookie to the response
//            response.addCookie(jwtCookie);
//
//
//            TokenRBody rbody1 = new TokenRBody("success", jwtToken, newUser2);
//            return ResponseEntity.status(HttpStatus.CREATED).body(rbody1);
//
////        emailSenderService.sendPasswordResetTokenUrlEmail(user.getEmail(),htmlContentx);
//        }catch (Exception ex) {
//            Optional<User> newUser= Optional.ofNullable(newUser1);
//            if(newUser.isPresent()){
//                userRepository.deleteById(newUser1.getUserId());
//            }
//            log.error("********:"+ex.getMessage());
//            throw new CustomException(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
////
//        }
        //----------------------------------------------------------------

    }


    //---------------ADMIN REGISTER----------------------------------

    @PostMapping("/public/auth/users/admin-register")
    public ResponseEntity<Object> adminRegister(@RequestParam("adminSecretPass") String adminSecretPass,@Valid @RequestBody User userBody,
                                           HttpServletRequest request, HttpServletResponse response,
                                           Authentication authentication) throws MessagingException {

        if(!adminSecretPass.equals(adminSecretPassword)){
            throw new CustomException("You are not allowed to perform this action",HttpStatus.FORBIDDEN);
        }

        //step1- after validation filtering the input and building user object with only required fields
        User filteredUser=User.builder()
                .name(userBody.getName())
                .email(userBody.getEmail())
                .password(userBody.getPassword())
                .confirmPassword(userBody.getConfirmPassword())
                .avatar(userBody.getAvatar())
                .mobile(userBody.getMobile())
                .gender(userBody.getGender()).build();


        //step2- creating user
        User newUser1 = userService.registerAdmin(filteredUser);
        Optional<User> newUser = Optional.ofNullable(newUser1);
        if (!newUser.isPresent()){
            throw new CustomException("Error while creating User",HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {

            return createSendToken(request,response,newUser1,HttpStatus.CREATED);

        } catch (Exception ex) {
            // if an exception occurs after the user has bean created(like during creating token) then ,delete the user else dont
            if (newUser.isPresent()) {
                userRepository.deleteById(newUser1.getId());
            }
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    //--------------- REGISTER A USER BY ADMIN----------------------------------

    @PostMapping("/admin-protected/auth/users/register")
    public ResponseEntity<Object> registerUserbyAdmin(@RequestParam("adminSecretPass") String adminSecretPass,@Valid @RequestBody User userBody,
                                                HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws MessagingException {

        if(!adminSecretPass.equals(adminSecretPassword)){
            throw new CustomException("You are not allowed to perform this action",HttpStatus.FORBIDDEN);
        }

        //step1- after validation filtering the input and building user object with only required fields
        User filteredUser=User.builder()
                .name(userBody.getName())
                .email(userBody.getEmail())
                .password(userBody.getPassword())
                .confirmPassword(userBody.getConfirmPassword())
                .avatar(userBody.getAvatar())
                .mobile(userBody.getMobile())
                .gender(userBody.getGender()).build();


        //step2- creating user
        User newUser1 = userService.registerUserByAdmin(filteredUser);
        Optional<User> newUser = Optional.ofNullable(newUser1);
        if (!newUser.isPresent()){
            throw new CustomException("Error while creating User",HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {

            return createSendToken(request,response,newUser1,HttpStatus.CREATED);

        } catch (Exception ex) {
            // if an exception occurs after the user has bean created(like during creating token) then ,delete the user else dont
            if (newUser.isPresent()) {
                userRepository.deleteById(newUser1.getId());
            }
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    //-------------------LOGIN----------------------------------
    @PostMapping("/public/auth/users/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest, Authentication authentication,
                                        HttpServletRequest request, HttpServletResponse response) {
        log.info("inside login controller before authentication");
        //--authenticating user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        log.info("inside login controller after authentication,");
        log.info("getting user details using input email and using this details ,generating jwt token");
        var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));


        //jwt token
        String jwtToken = jwtService.generateToken(user);


        // Create a cookie with the token
        Cookie jwtCookie = new Cookie("jwt", jwtToken);
        jwtCookie.setMaxAge(90 * 24 * 60 * 60); // Set the cookie's expiration time in seconds
        jwtCookie.setPath("/"); // Set the cookie's path to "/" so it's accessible across the whole app
        jwtCookie.setHttpOnly(true); // HttpOnly cookies cannot be accessed by JavaScript

        // Add the cookie to the response
        response.addCookie(jwtCookie);

        UserWithRoleNameDto userDto=userService.userToUserDtoWithRoleName(user);

        TokenRBody rbody = new TokenRBody("success", jwtToken, userDto);
        return ResponseEntity.status(HttpStatus.OK).body(rbody);
    }

    //------------------------LOGOUT---------------------
    @GetMapping("/user-protected/auth/users/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request, HttpServletResponse response) {


        // Get the currently authenticated user's authentication object
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // If the user is authenticated, perform logout
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        String jwtToken = "dummytokenWithoutSpace";

        // Create a cookie with the token
        Cookie jwtCookie = new Cookie("jwt", jwtToken);
        jwtCookie.setMaxAge(2); // Set the cookie's expiration time in seconds
        jwtCookie.setPath("/"); // Set the cookie's path to "/" so it's accessible across the whole app
        jwtCookie.setHttpOnly(true); // HttpOnly cookies cannot be accessed by JavaScript

        // Add the cookie to the response
        response.addCookie(jwtCookie);


        TokenRBody rbody = new TokenRBody("success", jwtToken, "Successful Logout");
        return ResponseEntity.status(HttpStatus.OK).body(rbody);
    }


    //-----------FORGOT PASSWORD--------------------------
    @PatchMapping("/public/auth/users/password/forgot")
    public ResponseEntity<Object> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) throws MessagingException {


        //step1- checking if the user is present,if not then generating an Exception
        Optional<User> userOptional = userRepository.findByEmail(forgotPasswordRequest.getEmail());
        if (!userOptional.isPresent()) {
            throw new CustomException("There is no user with this email address", HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();
        try {

            //step2-generating passwordResetToken
            String passwordResetToken = UUID.randomUUID().toString();

            //step3-creating 10min expire time
            Date currentTime = new Date();
            Long currentTimeInMS = new Date().getTime();
            Date tokenExpiresIn = new Date(currentTimeInMS + (1000 * 60 * 10L));


            //loading token and time into user object
            user.setPasswordResetToken(passwordResetToken);
            user.setPasswordResetTokenExpires(tokenExpiresIn);

            //step4- updating the user
            User updatedUser = userService.updateUser(user);


            //step5 - sending Email
            String resetUrl = frontendURL+"/public/auth/users/password/reset?token=" + passwordResetToken;
            String htmlContent = "<html>"
                    + "<body>"
                    + "<h1>Password Reset Request</h1>"
                    + "<p>Hi! , We have received a request to reset your password. If you didn't request this, you can ignore this email.</p>"
                    + "<p>To reset your password, click on the following link:</p>"
                    + "<p><a href=\"" + resetUrl + "\">Reset Password</a></p>"
                    + "<p>This link will expire in 10min for security reasons.</p>"
                    + "<p>If you have any questions or need assistance, feel free to contact our support team.</p>"
                    + "<p>Best regards,<br>The EKart Team</p>"
                    + "</body>"
                    + "</html>";

            log.info("********* :  tokenExpiresIn :" + tokenExpiresIn+", resetUrl :"+resetUrl);
        emailSenderService.sendPasswordResetTokenUrlEmail(forgotPasswordRequest.getEmail(),htmlContent);

            MsgRBody rbody = new MsgRBody("success","PasswordReset URL Sent to your Email Successfully");
            return ResponseEntity.status(HttpStatus.OK).body(rbody);
        } catch (Exception ex) {
            //loading token and time into user object
            user.setPasswordResetToken(null);
            user.setPasswordResetTokenExpires(null);
            User updatedUser = userService.updateUser(user);
            throw new CustomException("Error while sending email", HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }


    //----------------PASSWORD RESET----------------------------------
    @PatchMapping("/public/auth/users/password/reset")
    public ResponseEntity<Object> resetPassword(@RequestParam String token,
                                                @Valid @RequestBody ResetPasswordRequest resetPasswordRequest,
                                                HttpServletRequest request, HttpServletResponse response
                                                ) {


        //step3- finding user whose passwordResetToken is not yet expired based on token
        Date currentTime = new Date();
        Optional<User> userOptional = userRepository.findByPasswordResetTokenAndPasswordResetTokenExpiresAfter(token, currentTime);
        if (!userOptional.isPresent()) {
            throw new CustomException("User Not Found with this token or Token has expired", HttpStatus.BAD_REQUEST);
        }


        User user = userOptional.get();

        try {
            //step4-loading user object
            user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
            user.setPasswordResetTokenExpires(null);
            user.setPasswordChangedAt(new Date());

            log.info("*******: token" + token);

            //step5- updating user in db
            User updatedUser = userService.updateUser(user);


            //jwt token
            String jwtToken = jwtService.generateToken(updatedUser);


            // Create a cookie with the token
            Cookie jwtCookie = new Cookie("jwt", jwtToken);
            jwtCookie.setMaxAge(90 * 24 * 60 * 60); // Set the cookie's expiration time in seconds
            jwtCookie.setPath("/"); // Set the cookie's path to "/" so it's accessible across the whole app
            jwtCookie.setHttpOnly(true); // HttpOnly cookies cannot be accessed by JavaScript

            // Add the cookie to the response
            response.addCookie(jwtCookie);

            UserWithRoleNameDto userDto=userService.userToUserDtoWithRoleName(updatedUser);

            TokenRBody rbody = new TokenRBody("success", jwtToken,userDto );

            return ResponseEntity.status(HttpStatus.OK).body(rbody);
        } catch (Exception ex) {
            user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
            User updatedUser = userService.updateUser(user);
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }


    //--------------------UPDATE PASSWORD----------------------------
    @PatchMapping("/user-protected/auth/users/password/update")
    public ResponseEntity<Object> updateMyPassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest,
                                                   HttpServletRequest request, HttpServletResponse response,
                                                   Authentication authentication) {

        //step1: getting loggedin user
        Optional<Authentication> authOptional = Optional.ofNullable(authentication);
        if (!authOptional.isPresent()) {
            throw new CustomException("You are not Logged In, Please Login!", HttpStatus.BAD_REQUEST);
        }

        String email = authOptional.get().getName();
        log.info("*******: email"+email);

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            throw new CustomException("You are not Logged In, Please Login!", HttpStatus.BAD_REQUEST);
        }

        User loggedinUser = userOptional.get();



        //step2:checking if the encoded oldpassword is matching with the given oldpassword

        if (!passwordEncoder.matches(updatePasswordRequest.getOldPassword(), loggedinUser.getPassword())){
            throw new CustomException("Enter valid old password", HttpStatus.BAD_REQUEST);
        }

        //step4:update user object with encoded newPassowrd and PasswordChangedA
        loggedinUser.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
        loggedinUser.setPasswordChangedAt(new Date());


        //step5:update db
        User updatedUser = userService.updateUser(loggedinUser);

        //jwt token
        String jwtToken = jwtService.generateToken(updatedUser);


        // Create a cookie with the token
        Cookie jwtCookie = new Cookie("jwt", jwtToken);
        jwtCookie.setMaxAge(90 * 24 * 60 * 60); // Set the cookie's expiration time in seconds
        jwtCookie.setPath("/"); // Set the cookie's path to "/" so it's accessible across the whole app
        jwtCookie.setHttpOnly(true); // HttpOnly cookies cannot be accessed by JavaScript

        // Add the cookie to the response
        response.addCookie(jwtCookie);

        UserWithRoleNameDto userDto=userService.userToUserDtoWithRoleName(updatedUser);
        TokenRBody rbody = new TokenRBody("success", jwtToken,userDto );

        return ResponseEntity.status(HttpStatus.OK).body(rbody);

    }

}
