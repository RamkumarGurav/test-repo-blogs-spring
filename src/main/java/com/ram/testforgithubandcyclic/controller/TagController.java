package com.ram.testforgithubandcyclic.controller;

import com.ram.testforgithubandcyclic.collection.Tag;
import com.ram.testforgithubandcyclic.collection.Tag;
import com.ram.testforgithubandcyclic.dto.MsgRBody;
import com.ram.testforgithubandcyclic.dto.RBody;
import com.ram.testforgithubandcyclic.error.CustomException;
import com.ram.testforgithubandcyclic.repository.TagRepository;
import com.ram.testforgithubandcyclic.service.TagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class TagController {
    @Autowired
    private TagService tagService;
    
    @Autowired
    private TagRepository tagRepository;


    @PostMapping("/admin-protected/tags")
    public ResponseEntity<Object> createTag(@Valid @RequestBody Tag tag){

        Optional<Tag> foundTagOp=tagRepository.findByTagName(tag.getTagName());
        if(foundTagOp.isPresent()){
            throw new CustomException("This Tag is Already Exists", HttpStatus.BAD_REQUEST);
        }

        tag.setTagName(tag.getTagName().toUpperCase().trim());

        Tag newTag = tagService.createTag(tag);


        RBody rbody = new RBody("success", newTag);
        return ResponseEntity.status(HttpStatus.CREATED).body(rbody);
    }


    @GetMapping("/admin-protected/tags/{id}")
    public ResponseEntity<Object> getSingleTag(@PathVariable("id") String id){

        Optional<Tag> foundTagOp = tagService.getSingleTag(id);

        if (!foundTagOp.isPresent()){
            throw new CustomException("Tag not found",HttpStatus.NOT_FOUND);
        }


        RBody rbody = new RBody("success", foundTagOp.get());
        return ResponseEntity.status(HttpStatus.OK).body(rbody);
    }


    @GetMapping("/admin-protected/tags")
    public ResponseEntity<Object> getAllTags(){

        List<Tag> tags = tagService.getAllTags();


        RBody rbody = new RBody("success", tags);
        return ResponseEntity.status(HttpStatus.OK).body(rbody);
    }



    @PatchMapping("/admin-protected/tags/{id}")
    public ResponseEntity<Object> updateTag(@PathVariable("id") String id,@Valid @RequestBody Tag tag){

        //CHECKING IF THE TAG IS AVAILABLE
        Optional<Tag> foundTagOp1=tagService.getSingleTag(id);
        if(!foundTagOp1.isPresent()){
            throw new CustomException("Tag Not Found",HttpStatus.NOT_FOUND);
        }

        Tag foundTag=foundTagOp1.get();



        //checking if the given tagname has a tag IN DB
        Optional<Tag> foundTagOp2=tagRepository.findByTagName(tag.getTagName());
        if(foundTagOp2.isPresent()){
            throw new CustomException("This Tag Already Exists", HttpStatus.BAD_REQUEST);
        }


        //updating tag
        foundTag.setTagName(tag.getTagName().toUpperCase().trim());

        Tag updatedTag = tagService.updateTag(foundTag);


        RBody rbody = new RBody("success", updatedTag);
        return ResponseEntity.status(HttpStatus.OK).body(rbody);
    }


    @DeleteMapping("/admin-protected/tags")
    public ResponseEntity<Object> permanentlyDeleteTag(@PathVariable("id") String id){

        Optional<Tag>  foundTagOp=tagService.getSingleTag(id);
        if (!foundTagOp.isPresent()){
            throw new CustomException("Tag not found",HttpStatus.NOT_FOUND);
        }


        tagService.permanentlyDeleteTag(id);


        MsgRBody rbody = new MsgRBody("success", "successfully deleted tag with tagName: "+foundTagOp.get().getTagName());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(rbody);
    }

}
