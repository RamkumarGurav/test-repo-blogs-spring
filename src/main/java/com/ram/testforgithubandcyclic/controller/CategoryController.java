package com.ram.testforgithubandcyclic.controller;

import com.ram.testforgithubandcyclic.collection.Category;
import com.ram.testforgithubandcyclic.collection.Tag;
import com.ram.testforgithubandcyclic.dto.MsgRBody;
import com.ram.testforgithubandcyclic.dto.RBody;
import com.ram.testforgithubandcyclic.error.CustomException;
import com.ram.testforgithubandcyclic.repository.CategoryRepository;
import com.ram.testforgithubandcyclic.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;


    @PostMapping("/admin-protected/categories")
    public ResponseEntity<Object> createCategory(@Valid @RequestBody Category category){

        Optional<Category> foundCategoryOp=categoryRepository.findByCategoryName(category.getCategoryName());
        if(foundCategoryOp.isPresent()){
            throw new CustomException("This Category Already Exists", HttpStatus.BAD_REQUEST);
        }

        category.setCategoryName(category.getCategoryName().toUpperCase().trim());

        Category newCategory = categoryService.createCategory(category);


        RBody rbody = new RBody("success", newCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(rbody);
    }


    @GetMapping("/admin-protected/categories/{id}")
    public ResponseEntity<Object> getSingleCategory(@PathVariable("id") String id){

        Optional<Category> foundCategoryOp = categoryService.getSingleCategory(id);

        if (!foundCategoryOp.isPresent()){
            throw new CustomException("Category not found",HttpStatus.NOT_FOUND);
        }


        RBody rbody = new RBody("success", foundCategoryOp.get());
        return ResponseEntity.status(HttpStatus.OK).body(rbody);
    }


    @GetMapping("/admin-protected/categories")
    public ResponseEntity<Object> getAllCategorys(){

        List<Category> categories = categoryService.getAllCategorys();


        RBody rbody = new RBody("success", categories);
        return ResponseEntity.status(HttpStatus.OK).body(rbody);
    }



    @PatchMapping("/admin-protected/categories/{id}")
    public ResponseEntity<Object> updateCategory(@PathVariable("id") String id,@Valid @RequestBody Category category){

        //CHECKING IF THE CATEGORY IS AVAILABLE
        Optional<Category> foundCategoryOp1=categoryService.getSingleCategory(id);
        if(!foundCategoryOp1.isPresent()){
            throw new CustomException("Tag Not Found",HttpStatus.NOT_FOUND);
        }

        Category foundCategory=foundCategoryOp1.get();

        //CHECKING IF THERE IS ANOTHER CATEGORY WITH THE SAME GIVE NAME
        Optional<Category> foundCategoryOp2=categoryRepository.findByCategoryName(category.getCategoryName());
        if(foundCategoryOp2.isPresent()){
            throw new CustomException("This Category Already Exists", HttpStatus.BAD_REQUEST);
        }


        //UPDATING CATEGORY

        foundCategory.setCategoryName(category.getCategoryName().toUpperCase().trim());

        Category updateCategory = categoryService.updateCategory(foundCategory);


        RBody rbody = new RBody("success", updateCategory);
        return ResponseEntity.status(HttpStatus.OK).body(rbody);
    }

    @DeleteMapping("/admin-protected/categories")
    public ResponseEntity<Object> permanentlyDeleteCategory(@PathVariable("id") String id){

        Optional<Category>  foundCategoryOp=categoryService.getSingleCategory(id);
        if (!foundCategoryOp.isPresent()){
            throw new CustomException("Category not found",HttpStatus.NOT_FOUND);
        }


        categoryService.permanentlyDeleteCategory(id);


        MsgRBody rbody = new MsgRBody("success", "successfully deleted category with categoryName: "+foundCategoryOp.get().getCategoryName());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(rbody);
    }

}
