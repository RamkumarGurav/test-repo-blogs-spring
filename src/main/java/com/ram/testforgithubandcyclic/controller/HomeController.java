package com.ram.testforgithubandcyclic.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @Value("${welcomeMsg}")
    String welcomeMsg;



    @GetMapping("/")
    public ResponseEntity<String> welcome(){

        return ResponseEntity.status(HttpStatus.OK).body(welcomeMsg);
    }


    @GetMapping("/public/greet")
    public ResponseEntity<String> greet(){

        return ResponseEntity.status(HttpStatus.OK).body("HI");
    }

    @GetMapping("/user-protected/greet")
    public ResponseEntity<String> userGreet(Authentication authentication){

        return ResponseEntity.status(HttpStatus.OK).body("HI  "+authentication.getName());
    }
}
