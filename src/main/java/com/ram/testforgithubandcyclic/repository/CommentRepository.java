package com.ram.testforgithubandcyclic.repository;

import com.ram.testforgithubandcyclic.collection.Comment;
import com.ram.testforgithubandcyclic.collection.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends MongoRepository<Comment,String> {

    List<Comment> findAllByPostId(String postId);

    List<Comment> findAllByUserId(String userId);
    Optional<Comment> findByPostIdAndUserId(String postId, String userId);


    Optional<Comment> findByIdAndActive(String id, boolean active);

    Optional<Comment> findByIdAndPostIdAndUserId(String id, String postId, String userId);

    Optional<Comment> findByIdAndPostIdAndUserIdAndActive(String id, String postId, String userId, boolean active);

    void deleteByIdAndPostIdAndUserId(String id, String postId, String userId);

    void deleteAllByUserId(String userId);

    void deleteAllByPostIdAndUserId(String postId, String userId);

    Optional<Comment> findByIdAndUserId(String id, String userId);

    Optional<Comment> findByIdAndPostId(String id, String postId);

    void deleteAllByPostId(String id);
}
