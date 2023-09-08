package com.ram.testforgithubandcyclic.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @Value("${welcomeMsg}")
    String welcomeMsg;


    @GetMapping("/")
    public ResponseEntity<Object> welcome(){

        return ResponseEntity.status(HttpStatus.OK).body(welcomeMsg);
    }
}
