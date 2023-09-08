package com.ram.testforgithubandcyclic.service;

import com.ram.testforgithubandcyclic.collection.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category createCategory(Category category);

    Optional<Category> getSingleCategory(String id);

    Optional<Category> getSingleCategoryByCategoryName(String categoryName);

    List<Category> getAllCategorys();

    void permanentlyDeleteCategory(String id);

    Category updateCategory(Category category);
}
