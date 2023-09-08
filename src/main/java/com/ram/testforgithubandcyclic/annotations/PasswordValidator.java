package com.ram.testforgithubandcyclic.annotations;

import com.ram.testforgithubandcyclic.validations.PasswordStrengthValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordStrengthValidator.class)
@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordValidator {

    String message() default "Please choose a strong password";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}


// ---------------explaination--------------------------------------------------------------
//    This part defines a custom annotation named PasswordValidator that is used for validating
//    password strength. Here's what each part means:
//
//@Documented: Indicates that elements using this annotation should be documented by JavaDoc and similar tools.
//@Constraint(validatedBy = PasswordStrengthValidator.class): Specifies that the annotation is validated by the PasswordStrengthValidator class.
//@Target({ ElementType.METHOD, ElementType.FIELD }): Defines where the annotation can be applied – in this case,
// methods and fields.
//@Retention(RetentionPolicy.RUNTIME): Specifies that the annotation should be retained at runtime.

//        message(): A method that provides a default error message when the validation fails.
//        groups(): A method that can specify validation groups.
//        payload(): A method that can define custom payload objects.
