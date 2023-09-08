package com.ram.testforgithubandcyclic.dto;

import lombok.Data;

@Data
public class CommentDto {

    private String id;
    private String text;
    private UserCommentDto user;
    private String postId;


}
