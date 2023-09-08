package com.ram.testforgithubandcyclic.service;

import com.ram.testforgithubandcyclic.collection.Category;
import com.ram.testforgithubandcyclic.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Category category) {
        category.setUpdated(true);
        return categoryRepository.save(category);
    }

    @Override
    public Optional<Category> getSingleCategory(String id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Optional<Category> getSingleCategoryByCategoryName(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName);
    }

    @Override
    public List<Category> getAllCategorys() {
        return categoryRepository.findAll();
    }

    @Override
    public void permanentlyDeleteCategory(String id) {
        categoryRepository.deleteById(id);
    }
}
