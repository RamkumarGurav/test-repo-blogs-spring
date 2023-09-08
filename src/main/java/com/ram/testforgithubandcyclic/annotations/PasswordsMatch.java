package com.ram.testforgithubandcyclic.annotations;

import com.ram.testforgithubandcyclic.validations.PasswordsMatchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordsMatchValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordsMatch {

    String message() default "Passwords do not match";

    Class<?>[]  groups() default  {};
    Class<? extends Payload>[] payload() default {};

    String field();
    String fieldMatch();


}
