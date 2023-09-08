//package com.ram.ecommongo.mapper;
//
//import com.ram.ecommongo.collection.Post;
//import com.ram.ecommongo.collection.Comment;
//import com.ram.ecommongo.dto.PostDto;
//import com.ram.ecommongo.dto.CommentDto;
//import com.ram.ecommongo.dto.UserWithRoleNameDto;
//import io.swagger.models.auth.In;
//import lombok.extern.slf4j.Slf4j;
//import org.modelmapper.AbstractConverter;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//
//@Slf4j
//@Component
//public class ProductWithNoOfReviewsConverter extends AbstractConverter<Post, PostDto> {
//
//    @Autowired
//    private ModelMapper modelMapper;
//
//    @Override
//    protected PostDto convert(Post product) {
//
//        //**********this method has been implemented inside updateAverageRatings method*********
//
//        //First checking whether there are Reviews on the product (if reviews are null then you can't perform size() method on the null)
//        Optional<List<Comment>> reviews =Optional.ofNullable(product.getReviews());
//        //if there are reviews then getting size otherwise its 0
//        Integer noOfReviews = reviews.isPresent()?reviews.get().size():0;
//
//
////        List<CommentDto> reviewDtos=reviews.get().stream().map(review->modelMapper.map(review,CommentDto.class)).collect(Collectors.toList());
//
//        //creating new empty productDto
//         PostDto productDto = new PostDto();
//
//        //setting NumberOfReviews to productDto
//        productDto.setNumberOfReviews(noOfReviews);
//
//        //mapping remaining fields of user using  modelmapper
//        modelMapper.map(product, productDto);
//
//        //**************************************************
//
//        //finally returning productDto
//        return productDto;
//
//    }
//}
