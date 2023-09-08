package com.ram.testforgithubandcyclic.repository;

import com.ram.testforgithubandcyclic.collection.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {


    Optional<Post> findByIdAndAuthorId(String id, String authorId);


    List<Post> findAllByAuthorId(String authorId);

    //pagination
    Page<Post> findAll(Pageable pageable);

    Optional<Post> findByIdAndActive(String id, boolean active);

    Optional<Post> findByIdAndActiveAndDeleted(String id, boolean active, boolean deleted);

    void deleteAllByAuthorId(String authorId);

//    void DeleteByIdAndAuthorId(String id, String authorId);
//


}

