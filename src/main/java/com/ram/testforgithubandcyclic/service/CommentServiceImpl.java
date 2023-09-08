package com.ram.testforgithubandcyclic.service;

import com.ram.testforgithubandcyclic.collection.Comment;
import com.ram.testforgithubandcyclic.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService{

    @Autowired
    private CommentRepository commentRepository;


    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Comment createComment(Comment filteredComment) {
        return commentRepository.save(filteredComment);
    }

    @Override
    public Comment updateComment(Comment filteredComment) {
        filteredComment.setUpdated(true);
        return commentRepository.save(filteredComment) ;
    }

    @Override
    public List<Comment> getAllMyComments(String userId) {
        return commentRepository.findAllByUserId(userId);
    }

    @Override
    public List<Comment> getAllCommentsOnThisPost(String postId) {
        return commentRepository.findAllByPostId(postId);
    }

    @Override
    public Optional<Comment> getSingleCommentById(String id) {
        return commentRepository.findById(id);
    }

    @Override
    public Optional<Comment> getSingleCommentByIdAndUserId(String id, String userId) {
        return commentRepository.findByIdAndUserId(id,userId);
    }

    @Override
    public Optional<Comment> getSingleCommentByIdAndPostId(String id, String postId) {
        return commentRepository.findByIdAndPostId(id,postId);
    }

    @Override
    public Optional<Comment> getSingleCommentByIdAndPostIdAndUserId(String id, String postId, String userId) {
        return commentRepository.findByIdAndPostIdAndUserId(id,postId,userId);
    }

    @Override
    public void deleteCommentByIdAndPostIdandUserId(String id, String postId, String userId) {
        commentRepository.deleteByIdAndPostIdAndUserId(id,postId,userId);
    }

    @Override
    public void deleteMySingleCommentOnThisPost(String id, String postId, String userId) {

        commentRepository.deleteByIdAndPostIdAndUserId(id,postId,userId);

    }

    @Override
    public void deleteCommentById(String id) {
        commentRepository.deleteById(id);

    }

    @Override
    public void deleteAllMyComments(String userId) {
commentRepository.deleteAllByUserId(userId);
    }

    @Override
    public void deleteAllMyCommentsOnThisPost(String userId, String postId) {
commentRepository.deleteAllByPostIdAndUserId(postId,userId);
    }



    @Override
    public Page<Comment> search(String text,String postId, String userId, String commentId, List<String> fields, Pageable pageable) {

        Query query = new Query().with(pageable);

        //excluding and including some fields

        if (fields !=null && fields.size()>0 && !fields.isEmpty()){
            List<String> excludeFields=new ArrayList<>();
            List<String> includeFields=new ArrayList<>();

            for(String field : fields){
                if(field.startsWith("-")){
                    excludeFields.add(field.substring(1));
                }else{
                    includeFields.add(field);
                }
            }
            if (excludeFields.size()>0){
                //if they are any excludeFields then , add the logic to query
                log.info("********: excludeFields: "+excludeFields.toString());
                query.fields().exclude(excludeFields.toArray(new String[excludeFields.size()]));
            }
            if(includeFields.size()>0){
                //if they are any includeFields then , add the logic to query
                log.info("********: includeFields: "+includeFields.toString());
                query.fields().include(includeFields.toArray(new String[includeFields.size()]));
            }
        }




        //here we used ArrayList bcz ArrayList is flexible with the length but Array has fixed length,
        //bcz based on conditions we are adding the criteria to the list,if array we can't do this incremental addition of criteria
        List<Criteria> criteria = new ArrayList<>();


        //ADDING CRITERIA BASED ON CONDITIONS

        if (postId != null && !postId.isEmpty()) {
            criteria.add(Criteria.where("postId").is(postId));
        }


        if (userId != null && !userId.isEmpty()) {
            criteria.add(Criteria.where("user.id").is(userId));
        }

        if (commentId != null && !commentId.isEmpty()) {
            criteria.add(Criteria.where("id").is(commentId));
        }


        if (text != null && !text.isEmpty()) {
            criteria.add(Criteria.where("text").regex(text,"i"));
        }

        // If any criteria are provided, combine them using an AND operator and add to the query
        if (!criteria.isEmpty()) {

            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[criteria.size()])));

        }






        Page<Comment> commentsPage = PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Comment.class),//getPage method needs first parameter as list of resultants docs
                pageable,//2nd parameter is pageable object for paging
                ()->mongoTemplate.count(query.skip(0).limit(0), Comment.class)
                //3rd paramter is counting all resultant posts
                // the combination of skip(0) and limit(0) with a value of zero is a technique used to optimize the
                // performance of the count operation when you're only interested in the total count of matching
                // documents and not in retrieving the actual data.
        );



        return commentsPage;
    }

    @Override
    public void partiallyDeleteCommentById(Comment comment) {
        comment.setDeleted(true);
        comment.setUpdated(true);
        comment.setActive(false);
        commentRepository.save(comment);
    }


}
