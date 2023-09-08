package com.ram.testforgithubandcyclic.repository;

import com.ram.testforgithubandcyclic.collection.Category;
import com.ram.testforgithubandcyclic.collection.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends MongoRepository<Category,String> {
    Optional<Category> findByCategoryName(String string);
    Optional<Tag> findByIdAndActive(String id, boolean active);
}
