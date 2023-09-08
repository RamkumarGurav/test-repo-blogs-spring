package com.ram.testforgithubandcyclic.service;

import com.ram.testforgithubandcyclic.collection.Tag;
import com.ram.testforgithubandcyclic.collection.Tag;
import com.ram.testforgithubandcyclic.error.CustomException;
import com.ram.testforgithubandcyclic.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService{
    
    @Autowired
    private TagRepository tagRepository;

    @Override
    public Tag createTag(Tag tag) {

        Optional<Tag> tagOptional = tagRepository.findByTagName(tag.getTagName());

        if(tagOptional.isPresent()){
            throw new CustomException("This Tag has already Created", HttpStatus.BAD_REQUEST);
        }

        return tagRepository.save(tag);
    }

    @Override
    public Tag updateTag(Tag tag) {
        Optional<Tag> tagOptional = tagRepository.findByTagName(tag.getTagName());

        if(tagOptional.isPresent()){
            throw new CustomException("This Tag has already Created", HttpStatus.BAD_REQUEST);
        }

        tag.setUpdated(true);

        return tagRepository.save(tag);
    }

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Override
    public Optional<Tag> getActiveSingleTag(String id, boolean active) {
        return tagRepository.findByIdAndActive(id,active);
    }

    @Override
    public Optional<Tag> getSingleTag(String id) {
        return tagRepository.findById(id);
    }

    @Override
    public Optional<Tag> getSingleTagByName(String tagName) {
        return tagRepository.findByTagName(tagName);
    }

    @Override
    public void permanentlyDeleteTag(String id) {
        tagRepository.deleteById(id);
    }

  
}
