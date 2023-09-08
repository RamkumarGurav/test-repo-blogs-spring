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
@FieldsValueMatch.List(
        {
                @FieldsValueMatch(
                        field = "newPassword",
                        fieldMatch = "confirmNewPassword",
                        message = "password do not match"

                )
        }
)
public class UpdatePasswordRequest {

    @NotBlank(message = "please provide your old password")
    private String oldPassword;

    @NotBlank(message = "please provide newPassword")
    @PasswordValidator
    private String newPassword;

    @NotBlank(message = "please provide confirmNewPassword")
    private String confirmNewPassword;
}
