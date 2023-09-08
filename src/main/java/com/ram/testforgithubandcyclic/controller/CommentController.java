package com.ram.testforgithubandcyclic.controller;

import com.ram.testforgithubandcyclic.dto.CommentDto;
import com.ram.testforgithubandcyclic.collection.Comment;
import com.ram.testforgithubandcyclic.dto.RBody;
import com.ram.testforgithubandcyclic.error.CustomException;
import com.ram.testforgithubandcyclic.repository.CommentRepository;
import com.ram.testforgithubandcyclic.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class CommentController {


    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ModelMapper modelMapper;





    //--------------------------------------------------------------------------
    //--searchComments by admin
    @GetMapping("/admin-protected/comments")
    public ResponseEntity<Object> searchCommentsOrGetAll(
                                            @RequestParam(required = false) String text,
                                              @RequestParam(required = false) String commentId,
                                              @RequestParam(required = false) String userId,
                                              @RequestParam(required = false) String postId,
                                              @RequestParam(defaultValue = "createdAt") List<String> sort,
                                              @RequestParam(required = false) List<String> fields,
                                              @RequestParam(defaultValue = "0") Integer page,
                                              @RequestParam(defaultValue = "5") Integer size) {


        try {



//--------------for MULTIPLE sort fieldS-----------------
            // MAKING SORT LIST
            List<Sort.Order> ordersA = new ArrayList<>();
            for(String field: sort){
                if(field.startsWith("-")){
                    ordersA.add(new Sort.Order(Sort.Direction.DESC, field.substring(1)));
                }else {
                    ordersA.add(new Sort.Order(Sort.Direction.ASC, field));
                }
            }

//        //----------------using stream and map-----------------------
//            List<Sort.Order> ordersB= sort.stream()
//                    .map(field -> {
//                        Sort.Direction direction = field.startsWith("-") ? Sort.Direction.DESC : Sort.Direction.ASC;
//                        String fieldName = field.startsWith("-") ? field.substring(1) : field;
//                        return new Sort.Order(direction, fieldName);
//                    })
//                    .collect(Collectors.toList());

            Sort sortProps = Sort.by(ordersA);


            // PAGEABLE
            Pageable pageable = PageRequest.of(page,size,sortProps);


            Page<Comment> commentsPage = commentService.search(text,postId,userId,commentId,fields,pageable);


            //RESPONSE
            RBody rbody = new RBody("success",commentsPage);
            return ResponseEntity.status(HttpStatus.OK).body(rbody);
        }catch (Exception ex){
            throw new CustomException(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }



    //------------------------------------------------------------------------
    //--getSingleComment by admin
    @GetMapping("/admin-protected/comments/{id}")
    public ResponseEntity<Object> getSingleComment(@PathVariable("id") String id){

        Optional<Comment> commentOp = commentRepository.findById(id);



        RBody rBody= new RBody<>("success", commentOp.get());

        return ResponseEntity.ok(rBody);

    }




    //------------------------------------------------------------------------
    //--permanentlyDeleteCommentByAdmin
    @DeleteMapping("/admin-protected/comments/{commentId}/delete")
    public ResponseEntity<Object> permanentlyDeleteCommentByAdmin(@PathVariable("commentId") String commentId){


        Optional<Comment> foundCommentOp=commentService.getSingleCommentById(commentId);
        if(!foundCommentOp.isPresent()){
            throw new CustomException("Comment Not Found",HttpStatus.NOT_FOUND);
        }


        commentService.deleteCommentById(commentId);

        return ResponseEntity.noContent().build();

    }


    //------------------------------------------------------------------------
    //--permanentlyDeleteCommentByAdmin
    @PatchMapping("/admin-protected/comments/{commentId}/delete")
    public ResponseEntity<Object> partiallyDeleteCommentByAdmin(@PathVariable("commentId") String commentId){


        Optional<Comment> foundCommentOp=commentService.getSingleCommentById(commentId);
        if(!foundCommentOp.isPresent()){
            throw new CustomException("Comment Not Found",HttpStatus.NOT_FOUND);
        }


        commentService.partiallyDeleteCommentById(foundCommentOp.get());



        return ResponseEntity.noContent().build();

    }


}
