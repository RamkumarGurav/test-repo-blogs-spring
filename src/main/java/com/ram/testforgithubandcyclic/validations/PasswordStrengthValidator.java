package com.ram.testforgithubandcyclic.validations;


import com.ram.testforgithubandcyclic.annotations.PasswordValidator;
import com.ram.testforgithubandcyclic.error.FormValidationException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class PasswordStrengthValidator implements ConstraintValidator<PasswordValidator,String> {

    List<String> weakPasswords;

    @Override
    public void initialize(PasswordValidator passwordValidator) {
        weakPasswords = Arrays.asList("12345678","asdfghjk","11111111","00000000","password","abcdefgh");
    }

    @Override
    public boolean isValid(String passwordField, ConstraintValidatorContext context) {
//        if(passwordField !=null && (!weakPasswords.contains(passwordField))){
//            throw new FormValidationException("Please give a Strong password");
//        }

        return passwordField !=null && (!weakPasswords.contains(passwordField)) ;
    }
}


//---------------explaination----------------------
//This part defines the PasswordStrengthValidator class that implements the custom validation logic
// for password strength. Let's break it down:
//
// ConstraintValidator<PasswordValidator, String>: Specifies that the validator handles
// the PasswordValidator annotation and validates strings.
// initialize(PasswordValidator passwordValidator): The method to initialize the validator.
// In this case, it initializes the list of weak passwords.
// isValid(String passwordField, ConstraintValidatorContext cxt): The method where the
// actual validation logic is implemented. It checks if the password is not null and not in the list of weak passwords.