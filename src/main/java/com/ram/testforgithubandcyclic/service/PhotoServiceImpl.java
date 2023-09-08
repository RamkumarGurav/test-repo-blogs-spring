package com.ram.testforgithubandcyclic.service;

import com.ram.testforgithubandcyclic.collection.Photo;
import com.ram.testforgithubandcyclic.error.CustomException;
import com.ram.testforgithubandcyclic.error.NotFoundException;
import com.ram.testforgithubandcyclic.repository.PhotoRepository;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.Document;
import java.io.IOException;
import java.util.Optional;

@Service
public class PhotoServiceImpl implements PhotoService{

    @Autowired
    private PhotoRepository photoRepository;

    @Override
    public Photo uploadPhoto(String originalFilename, MultipartFile image) throws IOException {

        //we create a photo object with its originalName as title and we store image file as Binary (by converting
        // input image into bytes using these byte a create a Binary object that as input image bytes

        Photo photo = new Photo();
        photo.setTitle(originalFilename);
        photo.setPhoto(new Binary(BsonBinarySubType.BINARY,image.getBytes()));

         Photo newPhoto = photoRepository.save(photo);


        return newPhoto;
    }

    @Override
    public Photo downloadPhoto(String photoId) {

        Optional<Photo> photo = photoRepository.findById(photoId);

        if(!photo.isPresent()){
            throw new CustomException("Photo Not found", HttpStatus.NOT_FOUND);
        }


        return photo.get();
    }
}
