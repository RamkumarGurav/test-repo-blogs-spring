package com.ram.testforgithubandcyclic.validations;

import com.ram.testforgithubandcyclic.annotations.FieldsValueMatch;
import com.ram.testforgithubandcyclic.error.FormValidationException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class FieldsValueMatchValidator implements ConstraintValidator<FieldsValueMatch,Object> {

    private String field;
    private String fieldMatch;

    @Override
    public void initialize(FieldsValueMatch constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldMatch =constraintAnnotation.fieldMatch();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext cxt) throws FormValidationException{
        Object fieldValue = new BeanWrapperImpl(value).getPropertyValue(field);
        Object fieldMatchValue = new BeanWrapperImpl(value).getPropertyValue(fieldMatch);

//        if (fieldValue != null){
//
//            if(fieldValue.toString().startsWith("$2a")){ //to ignore password checking with hashedpassword
//                return true;
//            }else{
//                return fieldValue.equals(fieldMatchValue);
//
//            }
//        }else{
//            return fieldMatchValue != null;
//        }


//--------------------------
// to disable the 2nd time validation by spring data jpa only allowing one time validation by spring security, this
        //is done by "spring.properties.javax.peristence.validation.mode=none;
        if (fieldValue != null){

            if(!fieldValue.equals(fieldMatchValue)) {
                throw new FormValidationException("Passwords must match");
            }

            return fieldValue.equals(fieldMatchValue);

        }else{
//            return fieldMatchValue != null;
                throw new FormValidationException("Passwords should not be empty");
        }

    }


}





//-----------explaination-----------------------------
//ConstraintValidator<FieldsValueMatch, Object>: Specifies that the validator handles the FieldsValueMatch annotation and validates objects.
//        initialize(FieldsValueMatch constraintAnnotation): The method to initialize
//        the validator. It extracts the field names to compare from the annotation.
//        isValid(Object value, ConstraintValidatorContext context): The method where the actual validation logic is implemented.
//        It compares the values of the specified fields using BeanWrapperImpl.


//This method is part of the ConstraintValidator interface implementation and is responsible for performing
// the actual validation logic defined by your custom annotation. Let's break down the code step by step:
//
// Object value: This parameter represents the object that is being validated, typically an instance of the class
// where the FieldsValueMatch annotation is applied. In your case, it's an instance of the User class.
//
//ConstraintValidatorContext context: This parameter provides contextual information and allows you to customize the validation
// process. It's often used to build custom error messages or control the validation behavior.
//
//bject fieldValue: This line retrieves the value of the field specified by the field attribute of the FieldsValueMatch annotation. It uses the BeanWrapperImpl class to access the value of the field from the given object.
//
//Object fieldMatchValue: Similarly, this line retrieves the value of the field specified by the fieldMatch attribute
// of the FieldsValueMatch annotation.
// The following if statement checks if the fieldValue is not null. If it's not null, it means
// the field is not empty, and the code checks if the fieldValue is equal to the fieldMatchValue. If they are equal,
// the fields have matching values, and the validation is successful, so the method returns true.
// If the fieldValue is null, it means the field is empty. In this case, the code checks if the fieldMatchValue is also null.
// If it is, the validation is successful, as both fields are empty, and the method returns true.
//If the conditions in the if statement are not met, it means the fields do not match, and the validation fails,
// so the method returns false.
