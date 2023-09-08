package com.ram.testforgithubandcyclic.validations;

import com.ram.testforgithubandcyclic.annotations.EnumChecker;
import com.ram.testforgithubandcyclic.collection.Gender;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class EnumCheckerValidator implements ConstraintValidator<EnumChecker, String> {
    private List<String> validEnumList;

    @Override
    public void initialize(EnumChecker constraintAnnotation) {
        validEnumList = Arrays.stream(constraintAnnotation.enumClass().getEnumConstants())
                .map(eachEnumValue -> eachEnumValue.name())
                .collect(Collectors.toList());
        log.info("**********:inside EnumCheckerValidator "+validEnumList.toString());
    }



    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        log.info("*********:value "+value);
        if(value==null){
           return true;
        }
        return validEnumList.contains(value);
    }


}
