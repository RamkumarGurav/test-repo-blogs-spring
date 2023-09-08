package com.ram.testforgithubandcyclic.collection;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "posts")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Post extends BaseEntity{


    @NotBlank(message = "Please provide product name")
    @Size(min = 3,max = 200,message = "Post name must be between 3 and 200 characters in length")
    private String title;

    @NotBlank(message = "Please provide product name")
    @Size(min = 3,max = 200,message = "Post name must be between 3 and 200 characters in length")
    private String subtitle;

    @NotBlank(message = "Please provide product description")
    @Size(min = 10,message = "Post name must be between 10 and 5000 characters in length")
    private String description;


    private List<String> likes=new ArrayList<>(0);

    private int numberOfLikes=0;


    @Indexed
    @DBRef
    private Tag tag;

    @Indexed
    @Field("category")
    private Category category;



    @Size(min = 1, max = 10, message = "images list size must be between 1 and 10")
    private ArrayList<String> images=new ArrayList<>(0);

    @Indexed
    @DBRef
    private User author;

//    private String authorId;

    private Date publishedAt = new Date();


//    @DBRef
//    private List<Comment> comment=new ArrayList<>(0);


    public void updateNumOfLikes(){
        this.setNumberOfLikes(this.getLikes()==null?0:this.getLikes().size());

    }


}
