package com.ram.testforgithubandcyclic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthorRequest {
    @NotBlank(message = "Provide Job name")
    @Size(min=2, message="job name must be at least 2 characters long")
    private String job;

    @NotBlank(message = "Please Provide Company Name where you worked")
    @Size(min=2, message="company name must be at least 2 characters long")
    private String company;

}
