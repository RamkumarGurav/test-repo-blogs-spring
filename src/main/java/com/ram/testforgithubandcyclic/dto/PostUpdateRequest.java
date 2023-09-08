package com.ram.testforgithubandcyclic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;

@Data
public class PostUpdateRequest {
    @Size(min = 3,max = 200,message = "Post name must be between 3 and 200 characters in length")
    private String title;

    @Size(min = 3,max = 200,message = "Post name must be between 3 and 200 characters in length")
    private String subtitle;

    @Size(min = 10,message = "Post name must be between 10 and 5000 characters in length")
    private String description;

    private String tagName;

    private String categoryName;


    @Size(min = 0, max = 10, message = "List size must be between 0 and 10")
    private ArrayList<String> images;
}
