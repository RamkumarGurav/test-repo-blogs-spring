package com.ram.testforgithubandcyclic.dto;

import com.ram.testforgithubandcyclic.collection.Gender;
import com.ram.testforgithubandcyclic.collection.User;
import lombok.Data;

@Data
public class UserRequestDto {

    private String name;
    private String email;
    private Gender gender;
    private String avatar;
    private String mobile;
    private String password;
    private String confirmPassword;
    private String job;
    private String company;
}
