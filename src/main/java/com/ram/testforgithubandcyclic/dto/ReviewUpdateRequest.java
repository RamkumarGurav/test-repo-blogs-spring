package com.ram.testforgithubandcyclic.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewUpdateRequest {

    @Size(min = 3,max = 5000,message = "Content of the review must be between 3 and 5000 characters")
    private String text;

}
