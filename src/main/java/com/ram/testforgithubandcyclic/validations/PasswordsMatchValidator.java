package com.ram.testforgithubandcyclic.validations;

import com.ram.testforgithubandcyclic.annotations.PasswordsMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch,Object> {

    private String field;
    private String fieldMatch;

    @Override
    public void initialize(PasswordsMatch constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldMatch =constraintAnnotation.fieldMatch();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext cxt){
        Object fieldValue = new BeanWrapperImpl(value).getPropertyValue(field);
        Object fieldMatchValue = new BeanWrapperImpl(value).getPropertyValue(fieldMatch);

        if (fieldValue != null){
            return fieldValue.equals(fieldMatchValue);
        }else{
            return fieldMatchValue != null;
        }



    }

}
