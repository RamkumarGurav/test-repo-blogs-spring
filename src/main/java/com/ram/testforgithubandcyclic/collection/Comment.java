package com.ram.testforgithubandcyclic.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "comments")
@JsonInclude(JsonInclude.Include.NON_NULL) //not including null values in json object
public class Comment extends BaseEntity {



    @NotBlank(message = "Please provide content of the review")
    @Size(min = 3,max = 5000,message = "Content of the review must be between 3 and 5000 characters")
    private String text;

    @DBRef
    private User user;

//    private String userId;

    private String postId;




}
