package com.ram.testforgithubandcyclic.dto;

import com.ram.testforgithubandcyclic.collection.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class PostDto {

    private String id;
    private String title;
    private String subtitle;
    private String description;
    private TagDto tag;
    private CategoryDto category;
    private List<String> images;
    private int numberOfLikes;
    private Date publishedAt;
    private UserCommentDto author;

}
