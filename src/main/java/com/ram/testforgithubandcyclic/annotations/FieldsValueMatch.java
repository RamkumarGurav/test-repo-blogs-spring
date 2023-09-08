package com.ram.testforgithubandcyclic.annotations;

import com.ram.testforgithubandcyclic.validations.FieldsValueMatchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FieldsValueMatchValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldsValueMatch {

    String message() default "Fields Value don't match";
    Class<?>[] groups() default  {};
    Class<? extends Payload>[] payload() default  {};

    String field();
    String fieldMatch();

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {

        FieldsValueMatch[] value();
    }


}




//-------------------explaination-----------------------
//This part defines a custom annotation named FieldsValueMatch that checks
// if two fields in a class have matching values. Here's what each part means:
//
//@Constraint(validatedBy = FieldsValueMatchValidator.class): Specifies that the annotation is validated by
// the FieldsValueMatchValidator class.
//@Target({ ElementType.TYPE }): Specifies that the annotation can be applied to class-level elements.
//@Retention(RetentionPolicy.RUNTIME): Specifies that the annotation should be retained at runtime.
//        message(): A method that provides a default error message when the validation fails.
//        field(): A method that specifies the name of the first field to compare.
//        fieldMatch(): A method that specifies the name of the second field to compare.
//@interface List: Nested annotation used to allow multiple FieldsValueMatch annotations in a list.
