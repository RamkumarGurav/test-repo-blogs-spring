package com.ram.testforgithubandcyclic.dto;

import com.ram.testforgithubandcyclic.annotations.EnumChecker;
import com.ram.testforgithubandcyclic.annotations.PasswordValidator;
import com.ram.testforgithubandcyclic.collection.Gender;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

@Data
public class UpdateMeRequest {

    @Size(min = 2,message = "Name must contain atleast 2 characters")
    private String name;



    @Email(message = "Please enter valid email")
    @Size(min=2, message="Please provide valid email")
    private String email;


    @EnumChecker(message = "Provide Valid Gender",enumClass = Gender.class)
    private String gender;

    @Size(min=2, message="avatar must be at least 2 characters long")
    private String avatar;

    @Pattern(regexp = "(^[0-9]{10}$)",message = "Please Enter valid mobile number")
    private String mobile;

}
