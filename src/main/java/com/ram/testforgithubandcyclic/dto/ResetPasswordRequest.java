package com.ram.testforgithubandcyclic.dto;

import com.ram.testforgithubandcyclic.annotations.FieldsValueMatch;
import com.ram.testforgithubandcyclic.annotations.PasswordValidator;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldsValueMatch.List({
        @FieldsValueMatch(
                field ="password",
                fieldMatch = "confirmPassword",
                message = "Passwords do not match"
        )
//        @FieldsValueMatch(
//                field = "email",
//                fieldMatch = "confirmEmail",
//                message = "Emails do not match"
//
//        )

})
public class ResetPasswordRequest {

    @NotBlank(message = "Password must not be blank")
    @PasswordValidator
    private String password;
    private String confirmPassword;

}
