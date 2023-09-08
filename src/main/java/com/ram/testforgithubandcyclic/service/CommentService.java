package com.ram.testforgithubandcyclic.service;

import com.ram.testforgithubandcyclic.collection.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Comment createComment(Comment filteredComment);

    Comment updateComment(Comment filteredComment);

    List<Comment> getAllMyComments(String userId);

    List<Comment> getAllCommentsOnThisPost(String postId);

    Optional<Comment> getSingleCommentById(String id);

    Optional<Comment> getSingleCommentByIdAndUserId(String id,String userId);

    Optional<Comment> getSingleCommentByIdAndPostId(String id,String postId);
    Optional<Comment> getSingleCommentByIdAndPostIdAndUserId(String id,String postId,String userId);

    void deleteCommentByIdAndPostIdandUserId(String id,String postId,String userId);

    void deleteMySingleCommentOnThisPost(String id,String postId,String userId);

    void deleteCommentById(String id);

    void deleteAllMyComments(String userId);

    void deleteAllMyCommentsOnThisPost(String userId,String postId);


    Page<Comment> search(String text, String postId, String userId, String commentId, List<String> fields, Pageable pageable);

    void partiallyDeleteCommentById(Comment comment);
}
