package com.ram.testforgithubandcyclic.controller;

import com.ram.testforgithubandcyclic.collection.Photo;
import com.ram.testforgithubandcyclic.dto.RBody;
import com.ram.testforgithubandcyclic.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class PhotoController {


    @Autowired
    private PhotoService photoService;


    //---store photo in binary format in mongodb
    // we upload photo as request param which has type of MultipartFile
    @PostMapping("/public/photos/upload")
    public ResponseEntity<Object> uploadPhoto(@RequestParam("image") MultipartFile image) throws IOException {

        //-----dont forget to spring configuration in ymlfile
        //spring:
        //  servlet:
        //    multipart:
        //      max-file-size: 256MB
        //      max-request-size: 256MB
        //      enabled: true

        Photo newPhoto = photoService.uploadPhoto(image.getOriginalFilename(),image);

        RBody rbody = new RBody("success", newPhoto);
        return ResponseEntity.status(HttpStatus.CREATED).body(rbody);
    }

    @GetMapping("/public/photos/download/{photoId}")
    public ResponseEntity<Object> downloadPhoto(@PathVariable("photoId") String photoId){

        Photo photo = photoService.downloadPhoto(photoId);

        //Bi

        Resource resource = new ByteArrayResource(photo.getPhoto().getData());

        //directly putting resource in the body so that whenever user hits the above url it automatically
        // downloads the file
        //by using MediaType.APPLICATION_OCTET_STREAM (ie "application/octet-stream"), you're correctly indicating to the browser that the
        // content you're serving is a binary stream of data,and you're instructing it to handle the content as
        // an attachment for download using HttpHeader Content-disposition as `attachment; filename="new-tour-1.jpg" `
        //When you set the Content-Disposition header to "attachment" in an HTTP response, it indicates that the
        // content being served is intended to be treated as a downloadable file and also we give the filename along with it(to add " inside
        // string we need escape it like this \". This header informs the browser to
        // prompt the user to download the content as a file attachment rather than displaying it directly in the
        // browser window.
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+photo.getTitle()+"\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
    }

    //The line Resource resource = new ByteArrayResource(photo.getPhoto().getData()); is used to convert the byte
    // array data of a Photo object into a Spring Resource. This conversion is important when you're serving
    // binary data like images, files, or other non-text content in a Spring application, especially in scenarios
    // where you want to provide these resources for download or viewing.
    //
    //Here's why you need to do this conversion and how it's useful:
    //
    //Serve Binary Data: In your code, it looks like you're dealing with photos. Photos, like any binary data
    // (images, audio, videos, etc.), are essentially sequences of bytes. To serve these bytes as a resource to
    // the user, you need to encapsulate them in a way that's suitable for HTTP responses.
    //
    //Spring Resource Abstraction: Spring provides the Resource abstraction, which is designed to handle various
    // types of resources, including binary data. The ByteArrayResource implementation of Resource is particularly
    // useful for wrapping byte arrays.
    //
    //Response Handling: When a user accesses your endpoint and initiates a download, you're sending back an HTTP
    // response. The Resource abstraction helps you manage the response by encapsulating the binary data in a way
    // that allows you to control how the browser or client interprets and handles the data.
    //
    //Content-Disposition Header:  the Content-Disposition header is used to
    // instruct the browser on how to handle the response content. In this case, you're suggesting that the content
    // should be treated as an attachment with a specific filename. To fulfill this requirement, you need to
    // provide a downloadable resource, and that's where ByteArrayResource comes in.
    //
    //HTTP Response: Using ByteArrayResource, you can set the response's content type, headers, and the actual
    // content data, all of which are necessary to properly serve binary data for download.
    //
    //In summary, converting the byte array data of your Photo object into a ByteArrayResource allows you to
    // encapsulate the photo's binary data as a Spring resource, which is then included in the HTTP response to properly handle the download of the photo.
    // This approach ensures that the response is properly formatted for the specific content you're serving,
    // whether it's an image, file, or any other binary data.

}
