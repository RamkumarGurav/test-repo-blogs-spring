package com.ram.testforgithubandcyclic.dto;

import com.ram.testforgithubandcyclic.collection.Gender;
import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String name;
    private String email;
    private Gender gender;
    private String avatar;
    private String mobile;
    private String job;
    private String company;
}
