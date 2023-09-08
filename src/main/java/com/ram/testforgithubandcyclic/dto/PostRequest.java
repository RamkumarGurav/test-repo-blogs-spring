package com.ram.testforgithubandcyclic.dto;

import com.ram.testforgithubandcyclic.collection.Category;
import com.ram.testforgithubandcyclic.collection.Comment;
import com.ram.testforgithubandcyclic.collection.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
public class PostRequest {
    @NotBlank(message = "Please provide product name")
    @Size(min = 3,max = 200,message = "Post name must be between 3 and 200 characters in length")
    private String title;

    @NotBlank(message = "Please provide product name")
    @Size(min = 3,max = 200,message = "Post name must be between 3 and 200 characters in length")
    private String subtitle;

    @NotBlank(message = "Please provide product description")
    @Size(min = 10,message = "Post name must be between 10 and 5000 characters in length")
    private String description;

    @NotBlank(message = "Please provide tag name")
    private String tagName;

    @NotBlank(message = "Please provide Category name")
    private String categoryName;


    @Size(min = 1, max = 10, message = "Images List size must be between 1 and 10")
    private ArrayList<String> images;

}
