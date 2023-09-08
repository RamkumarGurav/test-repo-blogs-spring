package com.ram.testforgithubandcyclic.service;

import com.ram.testforgithubandcyclic.collection.Post;
import com.ram.testforgithubandcyclic.dto.PostDto;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Post createPost(Post post);
    Post udpatePost(Post post);


    List<Post> findAll();


    Page<Post> getAllPostsWithPage(Pageable pageable);



    Optional<Post> getSinglePost(String id);

    void permanentlyDeletePost(String id);

    Post partiallyDeletePost(Post post);

    Post undeletePostButNotActive(Post post);
    Post deactivatePost(Post post);
    Post activatePost(Post post);

    Optional<Post> getPostByIdAndActiveAndDeleted(String id, boolean active,boolean deleted);



    PostDto postToPostDto(Post post);

    Page<Post> search(String authorId,String title, String subtitle, String categoryName, String tagId, List<String> fields, Pageable pageable);

    Page<Post> searchByAdmin(String authorId, String title, String subtitle, String categoryName, String tagId, List<String> fields, Pageable pageable);

    Optional<Post> getSinglePostByIdAndAuthorId(String id, String authorId);

    void permanentlyDeletePostById(String id);

//    void permanentlyDeletePostByIdAndAuthorId(String id, String authorId);
//
//    void permanentlyDeleteAllByAuthorId(String authorId);
}
