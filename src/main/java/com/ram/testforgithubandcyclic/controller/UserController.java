package com.ram.testforgithubandcyclic.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ram.testforgithubandcyclic.collection.Role;
import com.ram.testforgithubandcyclic.collection.User;
import com.ram.testforgithubandcyclic.dto.*;
import com.ram.testforgithubandcyclic.error.CustomException;
import com.ram.testforgithubandcyclic.repository.RoleRepository;
import com.ram.testforgithubandcyclic.repository.UserRepository;
import com.ram.testforgithubandcyclic.service.RoleService;
import com.ram.testforgithubandcyclic.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper userMapper;

    //***************** USER PROTECTED  ******************************************

    //-----------------------------------------------------------------------------------------
    //--getMyDetails--------------
    @GetMapping("/user-protected/users/me")
    public ResponseEntity<Object> getMyDetails(Authentication auth) {


        // GETTING LOGGED IN USER
        String email = auth.getName();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            throw new CustomException("You are not Logged In, Please Login!", HttpStatus.BAD_REQUEST);
        }

        User loggedinUser = userOptional.get();

        //DTO CONVERSION
        UserWithRoleNameDto userDto = userService.userToUserDtoWithRoleName(loggedinUser);

        //RESPONSE
        RBody rbody = new RBody("success", userDto);
        return ResponseEntity.status(HttpStatus.OK).body(rbody);

    }

    //-----------------------------------------------------------------------------------------
    //---updateMe-------------
    @PatchMapping("/user-protected/users/update-me")
    public ResponseEntity<Object> updateMe(@Valid @RequestBody UpdateMeRequest updateMeRequest, Authentication auth) {

        // GETTING LOGGED IN USER
        String email = auth.getName();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            throw new CustomException("You are not Logged In, Please Login!", HttpStatus.BAD_REQUEST);
        }

        User loggedinUser = userOptional.get();


        //VERIFYING GIVEN EMAIL FOR UNIQUENES


        Optional<String> inputEmailOptional = Optional.ofNullable(updateMeRequest.getEmail());

        //check if email given by user present in request body
        if (inputEmailOptional.isPresent()) {

            //if email given by user is same as existing his own account's email then generate an exception
            if (loggedinUser.getEmail().equals(inputEmailOptional.get())) {
                throw new CustomException("Provide different email ID other than Your Existing email", HttpStatus.BAD_REQUEST);
            }
            //if email given by user  has an account already in db  then generate an exception
            Optional<User> verifyUserOptional = userService.getSingleUserByEmailId(inputEmailOptional.get());
            if (verifyUserOptional.isPresent()) {
                throw new CustomException("User Already exists with this email! Provide different Email", HttpStatus.BAD_REQUEST);
            }

            //if email given by user is present and its unique then loading given email into user object
            loggedinUser.setEmail(updateMeRequest.getEmail());

        }

        //loading other given info into user object only if that info is present in request body
        loggedinUser.setName(Optional.ofNullable(updateMeRequest.getName()).isPresent() ? updateMeRequest.getName() : loggedinUser.getName());
        loggedinUser.setAvatar(Optional.ofNullable(updateMeRequest.getAvatar()).isPresent() ? updateMeRequest.getAvatar() : loggedinUser.getAvatar());
        loggedinUser.setMobile(Optional.ofNullable(updateMeRequest.getMobile()).isPresent() ? updateMeRequest.getMobile() : loggedinUser.getMobile());
        loggedinUser.setGender(Optional.ofNullable(updateMeRequest.getGender()).isPresent() ? updateMeRequest.getGender() : loggedinUser.getGender());

        try {
            //UPDATING USER
            User updatedUser = userService.updateUser(loggedinUser);

            //DTO CONVERSION
            UserWithRoleNameDto userDto = userService.userToUserDtoWithRoleName(updatedUser);

            //RESPONSE
            RBody rbody = new RBody("success", userDto);
            return ResponseEntity.status(HttpStatus.OK).body(rbody);
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    //-----------------------------------------------------------------------------------------
    //---updateMe-------------
    @PatchMapping("/user-protected/users/make-me-author")
    public ResponseEntity<Object> updateMeAsAuthor(@Valid @RequestBody AuthorRequest reqBody, Authentication auth) {

        // GETTING LOGGED IN USER
        String email = auth.getName();

        Optional<User> userOptional = userRepository.findByEmail(email);

        User loggedinUser = userOptional.get();


        //FINDING ROLE
        Optional<Role> roleOp = roleRepository.findByRoleName("ROLE_AUTHOR");
        if (!roleOp.isPresent()) {
            throw new CustomException("Role not Found", HttpStatus.NOT_FOUND);
        }

        //Adding role to loggeduser
        loggedinUser.setRole(roleOp.get());
        loggedinUser.setCompany(reqBody.getCompany().trim());
        loggedinUser.setJob(reqBody.getJob().trim());


        //UPDATING USER
        User updatedUser = userService.updateUser(loggedinUser);


        //RESPONSE
        MsgRBody rbody = new MsgRBody("success", "Successfully updated You as Author");
        return ResponseEntity.status(HttpStatus.OK).body(rbody);


    }

    //-----------------------------------------------------------------------------------------
    //--deactivateMe--------------
    @PatchMapping("/user-protected/users/deactivate-me")
    public ResponseEntity<Object> deactivateMe(HttpServletRequest req, HttpServletResponse res, Authentication auth) {

        // GETTING LOGGED IN USER
        String email = auth.getName();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            throw new CustomException("You are not Logged In, Please Login!", HttpStatus.BAD_REQUEST);
        }

        User loggedinUser = userOptional.get();

        try {
            //DEACTIVATING USER
            User user = userService.deactivateUser(loggedinUser);

            //SENDING RESPONSE
            MsgRBody rbody = new MsgRBody("success", "successfully deactivated the account with the email: " + user.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(rbody);
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //-----------------------------------------------------------------------------------------
    //--deleteMe--------------
    @DeleteMapping("/user-protected/users/delete-me")
    public ResponseEntity<Object> deleteMe(HttpServletRequest req, HttpServletResponse res, Authentication auth) {

        // GETTING LOGGED IN USER
        String email = auth.getName();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            throw new CustomException("You are not Logged In, Please Login!", HttpStatus.BAD_REQUEST);
        }

        User loggedinUser = userOptional.get();


        try {
            //DELETING USER temporarily
            User deletedUser = userService.deleteUser(loggedinUser);


            //Logging out the user---------------------------------
            // If the user is authenticated, perform logout
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(req, res, auth);
            }

            String jwtToken = "dummytokenWithoutSpace";

            // Create a cookie with the token
            Cookie jwtCookie = new Cookie("jwt", jwtToken);
            jwtCookie.setMaxAge(2); // Set the cookie's expiration time in seconds
            jwtCookie.setPath("/"); // Set the cookie's path to "/" so it's accessible across the whole app
            jwtCookie.setHttpOnly(true); // HttpOnly cookies cannot be accessed by JavaScript

            // Add the cookie to the response
            res.addCookie(jwtCookie);

            //RESPONSE
            MsgRBody rbody = new MsgRBody("success",
                    "successfully deleted the account with the email: " + loggedinUser.getEmail());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(rbody);
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    //-----------------------------------------------------------------------------------------
    //--permanentlyDeleteMe--------------
    @DeleteMapping("/user-protected/users/permanently-delete-me")
    public ResponseEntity<Object> permanentlyDeleteMe(HttpServletRequest req, HttpServletResponse res, Authentication auth) {

        // GETTING LOGGED IN USER
        String email = auth.getName();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            throw new CustomException("You are not Logged In, Please Login!", HttpStatus.BAD_REQUEST);
        }

        User loggedinUser = userOptional.get();

        //GETTING USERID
        String userId = loggedinUser.getId();

        try {
            //DELETING USER PERMANENTLY
            userService.permanentlyDeleteUser(userId);

            //Logging out the user
            // If the user is authenticated, perform logout
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(req, res, auth);
            }

            String jwtToken = "dummytokenWithoutSpace";

            // Create a cookie with the token
            Cookie jwtCookie = new Cookie("jwt", jwtToken);
            jwtCookie.setMaxAge(2); // Set the cookie's expiration time in seconds
            jwtCookie.setPath("/"); // Set the cookie's path to "/" so it's accessible across the whole app
            jwtCookie.setHttpOnly(true); // HttpOnly cookies cannot be accessed by JavaScript

            // Add the cookie to the response
            res.addCookie(jwtCookie);


            //RESPONSE
            MsgRBody rbody = new MsgRBody("success",
                    "Permanenlty deleted the account with the email: " + loggedinUser.getEmail());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(rbody);
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    //*****************  ADMIN PROTECTED  ******************************************


    //-----------------------------------------------------------------------------------------
    //--Users Search by Admin--------------
    @GetMapping("/admin-protected/users/search")
    public ResponseEntity<Object> searchUsers(@RequestParam(required = false) String name,
                                              @RequestParam(required = false) String email,
                                              @RequestParam(required = false) String gender,
                                              @RequestParam(required = false) String mobile,
                                              @RequestParam(required = false) String role,
                                              @RequestParam(defaultValue = "createdAt") List<String> sort,
                                              @RequestParam(required = false) List<String> fields,
                                              @RequestParam(defaultValue = "0") Integer page,
                                              @RequestParam(defaultValue = "5") Integer size) {


        try {


//--------------for single sort field-----------------
//            Sort sortByProperty=sort.startsWith("-")?Sort.by(Sort.Direction.DESC,sort.substring(1)):Sort.by(Sort.Direction.ASC,sort);

//--------------for MULTIPLE sort fieldS-----------------
            // MAKING SORT LIST
            List<Sort.Order> ordersA = new ArrayList<>();
            for (String field : sort) {
                if (field.startsWith("-")) {
                    ordersA.add(new Sort.Order(Sort.Direction.DESC, field.substring(1)));
                } else {
                    ordersA.add(new Sort.Order(Sort.Direction.ASC, field));
                }
            }

//        //----------------using stream and map-----------------------
//            List<Sort.Order> ordersB= sort.stream()
//                    .map(field -> {
//                        Sort.Direction direction = field.startsWith("-") ? Sort.Direction.DESC : Sort.Direction.ASC;
//                        String fieldName = field.startsWith("-") ? field.substring(1) : field;
//                        return new Sort.Order(direction, fieldName);
//                    })
//                    .collect(Collectors.toList());

            Sort sortProps = Sort.by(ordersA);


            // PAGEABLE
            Pageable pageable = PageRequest.of(page, size, sortProps);


            Page<User> usersPage = userService.search(name, email, gender, mobile, fields, role, pageable);

//            //DTO CONVERSION
//            Page<UserWithRoleNameDto> usersDtoPage=usersPage.map(user -> userService.userToUserDtoWithRoleName(user));


            //RESPONSE
            RBody rbody = new RBody("success", usersPage);
            return ResponseEntity.status(HttpStatus.OK).body(rbody);
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    //-----------------------------------------------------------------------------------------
    //--getSingleUser--------------
    @GetMapping("/admin-protected/users/{userId}")
    @Order(2)
    public ResponseEntity<Object> getSingleUser(@PathVariable("userId") String userId) {


        Optional<User> user = userService.getSingleUser(userId);

        if (!user.isPresent()) {
            throw new CustomException("User Not Found", HttpStatus.NOT_FOUND);
        }
//            UserWithRoleNameDto userDto = userService.userToUserDtoWithRoleName(user.get());

        RBody rbody = new RBody("success", user);
        return ResponseEntity.status(HttpStatus.OK).body(rbody);

    }


    //-----------------------------------------------------------------------------------------
    //--updateUser by admin (name,email,avatar,gender,mobile)--------------
    @PatchMapping("/admin-protected/users/update/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable("userId") String userId, @Valid @RequestBody UpdateMeRequest updateMeRequest) {

        //1.verifying userId
        Optional<User> userOptional1 = userService.getSingleUser(userId);
        if (!userOptional1.isPresent()) {
            throw new CustomException("User Not Found!", HttpStatus.NOT_FOUND);
        }

        User foundUser = userOptional1.get();


        //VERIFYING GIVEN EMAIL FOR UNIQUENESS
        Optional<String> inputEmailOptional = Optional.ofNullable(updateMeRequest.getEmail());

        //check if email given by user present in request body
        if (inputEmailOptional.isPresent()) {

            //if email given by user is same as existing his own account's email then generate an exception
            if (foundUser.getEmail().equals(inputEmailOptional.get())) {
                throw new CustomException("Provide different email ID other than  Existing email", HttpStatus.BAD_REQUEST);
            }
            //if email given by user  has an account already in db  then generate an exception
            Optional<User> verifyUserOptional = userService.getSingleUserByEmailId(inputEmailOptional.get());
            if (verifyUserOptional.isPresent()) {
                throw new CustomException("User Already exists with this email! Provide different Email", HttpStatus.BAD_REQUEST);
            }

            //if email given by user is present and its unique then loading given email into user object
            foundUser.setEmail(updateMeRequest.getEmail());

        }

        //loading other given info into user object only if that info is present in request body
        foundUser.setName(Optional.ofNullable(updateMeRequest.getName()).isPresent() ? updateMeRequest.getName() : foundUser.getName());
        foundUser.setAvatar(Optional.ofNullable(updateMeRequest.getAvatar()).isPresent() ? updateMeRequest.getAvatar() : foundUser.getAvatar());
        foundUser.setMobile(Optional.ofNullable(updateMeRequest.getMobile()).isPresent() ? updateMeRequest.getMobile() : foundUser.getMobile());
        foundUser.setGender(Optional.ofNullable(updateMeRequest.getGender()).isPresent() ? updateMeRequest.getGender() : foundUser.getGender());

        try {
            //UPDATING USER
            User updatedUser = userService.updateUser(foundUser);

            //DTO CONVERSION
            UserWithRoleNameDto userDto = userService.userToUserDtoWithRoleName(updatedUser);

            //RESPONSE
            RBody rbody = new RBody("success", userDto);
            return ResponseEntity.status(HttpStatus.OK).body(rbody);
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    //-----------------------------------------------------------------------------------------
    //--updateUser by admin (name,email,avatar,gender,mobile)--------------
    @PatchMapping("/admin-protected/users/active-update/{userId}")
    public ResponseEntity<Object> updateActiveUser(@PathVariable("userId") String userId, @Valid @RequestBody UpdateMeRequest updateMeRequest) {

        //1.verifying userId
        Optional<User> userOptional1 = userService.getSingleUser(userId);
        if (!userOptional1.isPresent()) {
            throw new CustomException("User Not Found!", HttpStatus.NOT_FOUND);
        }


        User foundUser = userOptional1.get();

        if (!foundUser.isActive()) {
            throw new CustomException("User Not Active!", HttpStatus.NOT_FOUND);
        }

        //VERIFYING GIVEN EMAIL FOR UNIQUENESS
        Optional<String> inputEmailOptional = Optional.ofNullable(updateMeRequest.getEmail());

        //check if email given by user present in request body
        if (inputEmailOptional.isPresent()) {

            //if email given by user is same as existing his own account's email then generate an exception
            if (foundUser.getEmail().equals(inputEmailOptional.get())) {
                throw new CustomException("Provide different email ID other than  Existing email", HttpStatus.BAD_REQUEST);
            }
            //if email given by user  has an account already in db  then generate an exception
            Optional<User> verifyUserOptional = userService.getSingleUserByEmailId(inputEmailOptional.get());
            if (verifyUserOptional.isPresent()) {
                throw new CustomException("User Already exists with this email! Provide different Email", HttpStatus.BAD_REQUEST);
            }

            //if email given by user is present and its unique then loading given email into user object
            foundUser.setEmail(updateMeRequest.getEmail());

        }

        //loading other given info into user object only if that info is present in request body
        foundUser.setName(Optional.ofNullable(updateMeRequest.getName()).isPresent() ? updateMeRequest.getName() : foundUser.getName());
        foundUser.setAvatar(Optional.ofNullable(updateMeRequest.getAvatar()).isPresent() ? updateMeRequest.getAvatar() : foundUser.getAvatar());
        foundUser.setMobile(Optional.ofNullable(updateMeRequest.getMobile()).isPresent() ? updateMeRequest.getMobile() : foundUser.getMobile());
        foundUser.setGender(Optional.ofNullable(updateMeRequest.getGender()).isPresent() ? updateMeRequest.getGender() : foundUser.getGender());

        try {
            //UPDATING USER
            User updatedUser = userService.updateUser(foundUser);

            //DTO CONVERSION
            UserWithRoleNameDto userDto = userService.userToUserDtoWithRoleName(updatedUser);

            //RESPONSE
            RBody rbody = new RBody("success", userDto);
            return ResponseEntity.status(HttpStatus.OK).body(rbody);
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    //-----------------------------------------------------------------------------------------
    //--deactivateUser--------------
    @PatchMapping("/admin-protected/users/deactivate/{userId}")
    public ResponseEntity<Object> deactivateUser(@PathVariable("userId") String userId) {
        //1.verifying userId
        Optional<User> userOptional1 = userService.getSingleUser(userId);
        if (!userOptional1.isPresent()) {
            throw new CustomException("User Not Found!", HttpStatus.NOT_FOUND);
        }

        User foundUser = userOptional1.get();

        try {
            userService.deactivateUser(foundUser);

            MsgRBody rbody = new MsgRBody("success",
                    "successfully deleted the account with the email: " + foundUser.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(rbody);
        } catch (Exception ex) {

            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }


    //-----------------------------------------------------------------------------------------
    //--deleteUser by admin--------------
    @PatchMapping("/admin-protected/users/delete/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable("userId") String userId) {


        //1.verifying userId
        Optional<User> userOptional1 = userService.getSingleUser(userId);
        if (!userOptional1.isPresent()) {
            throw new CustomException("User Not Found!", HttpStatus.NOT_FOUND);
        }

        User foundUser = userOptional1.get();

        try {
            User deletedUser = userService.deleteUser(foundUser);
            MsgRBody rbody = new MsgRBody("success",
                    "successfully deleted the account with the email: " + foundUser.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(rbody);
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    //-----------------------------------------------------------------------------------------
    //--permanentlyDeleteUser by Admin--------------
    @DeleteMapping("/admin-protected/users/delete/{userId}")
    public ResponseEntity<Object> permanentlyDeleteUser(@PathVariable("userId") String userId) {


        //1.verifying userId
        Optional<User> userOptional1 = userService.getSingleUser(userId);
        if (!userOptional1.isPresent()) {
            throw new CustomException("User Not Found!", HttpStatus.NOT_FOUND);
        }

        User foundUser = userOptional1.get();

        try {
            userService.permanentlyDeleteUser(userId);

            MsgRBody rbody = new MsgRBody("success",
                    "successfully permanently deleted the account with the email: " + foundUser.getEmail());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(rbody);
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    //-----------------------------------------------------------------------------------------
    //--activateUser by admin--------------
    @PatchMapping("/admin-protected/users/activate/{userId}")
    public ResponseEntity<Object> activateUser(@PathVariable("userId") String userId) {
        //1.verifying userId
        Optional<User> userOptional1 = userService.getSingleUser(userId);
        if (!userOptional1.isPresent()) {
            throw new CustomException("User Not Found!", HttpStatus.NOT_FOUND);
        }

        User foundUser = userOptional1.get();

        try {
            User activatedUser = userService.activateUser(foundUser);

            MsgRBody rbody = new MsgRBody("success",
                    "successfully activated the account with the email: " + foundUser.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(rbody);
        } catch (Exception ex) {

            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }


    //-----------------------------------------------------------------------------------------
    //--UndeleteUser by admin--------------
    @PatchMapping("/admin-protected/users/undelete/{userId}")
    public ResponseEntity<Object> undeleteUserButNotActive(@PathVariable("userId") String userId) {


        //1.verifying userId
        Optional<User> userOptional1 = userService.getSingleUser(userId);
        if (!userOptional1.isPresent()) {
            throw new CustomException("User Not Found!", HttpStatus.NOT_FOUND);
        }

        User foundUser = userOptional1.get();

        try {
            User deletedUser = userService.undeleteUserButNotActive(foundUser);


            MsgRBody rbody = new MsgRBody("success",
                    "successfully activated the account with the email: " + foundUser.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(rbody);
        } catch (Exception ex) {
            throw new CustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
