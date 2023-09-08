package com.ram.testforgithubandcyclic.service;

import com.ram.testforgithubandcyclic.dto.PostDto;
import com.ram.testforgithubandcyclic.collection.Post;
import com.ram.testforgithubandcyclic.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ModelMapper postMapper;

    @Override
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Post udpatePost(Post post) {
        post.setUpdated(true);
        return postRepository.save(post);
    }

    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }


    @Override
    public PostDto postToPostDto(Post post) {
        return postMapper.map(post, PostDto.class);
    }

    @Override
    public Page<Post> search(String authorId, String title, String subtitle, String categoryName, String tagId, List<String> fields, Pageable pageable) {


        //CREATING QUERY AND ADDING PAGEABLE TO IT
        Query query = new Query().with(pageable);


        //LIMITING FIELDS
        //excluding and including some fields

        if (fields != null && fields.size() > 0 && !fields.isEmpty()) {
            List<String> excludeFields = new ArrayList<>();
            List<String> includeFields = new ArrayList<>();

            for (String field : fields) {
                if (field.startsWith("-")) {
                    excludeFields.add(field.substring(1));
                } else {
                    includeFields.add(field);
                }
            }
            if (excludeFields.size() > 0) {
                //if they are any excludeFields then , add the logic to query
                log.info("********: excludeFields: " + excludeFields.toString());
                query.fields().exclude(excludeFields.toArray(new String[excludeFields.size()]));
            }
            if (includeFields.size() > 0) {
                //if they are any includeFields then , add the logic to query
                log.info("********: includeFields: " + includeFields.toString());
                query.fields().include(includeFields.toArray(new String[includeFields.size()]));
            }
        }


        //CREATING LIST OF CRITERIA
        //here we used ArrayList bcz ArrayList is flexible with the length but Array has fixed length,
        //bcz based on conditions we are adding the criteria to the list,if array we can't do this incremental addition of criteria
        List<Criteria> criteria = new ArrayList<>();

        //ONLY PROVIDING ACTIVE AND NON PARTIALLY DELETED POSTS
        criteria.add(Criteria.where("active").is(true));
        criteria.add(Criteria.where("deleted").is(false));

        //ADDING CRITERIA TO CRETERIA LIST BASED ON CONDTIONS
//         Check if a search term "authorId" is provided and add a authorId-based regex criteria
        if (authorId != null && !authorId.isEmpty()) {
            criteria.add(Criteria.where("author.id").is(authorId));
        }


        // Check if a search term "title" is provided and add a title-based regex criteria
        if (title != null && !title.isEmpty()) {
            criteria.add(Criteria.where("title").regex(title, "i"));
        }

        // Check if a search term "subtitle" is provided and add a subtitle-based regex criteria
        if (subtitle != null && !subtitle.isEmpty()) {
            criteria.add(Criteria.where("subtitle").regex(subtitle, "i"));
        }

        // //HERE WE HAVE USED categoryName bcz we have made Category object in Post entity as Field instead of a reference
        // Check if a search term "categoryName" is provided and add a categoryName-based  criteria
        if (categoryName != null && !categoryName.isEmpty()) {
            criteria.add(Criteria.where("category.categoryName").is(categoryName.toUpperCase().trim()));
        }


        //HERE WE HAVE USED TAG ID INSTEAD OF TAGNAME BCZ IN POST ENTITY WE HAVE MENTIONED IT AS REFERENCE SO
        //DUE TO THIS WE CAN ONLY ACCESS BASED ON ITS ID ONLY
        // Check if a search term "tagId" is provided and add a tagId-based regex criteria
        if (tagId != null && !tagId.isEmpty()) {
            criteria.add(Criteria.where("tag.id").is(tagId));
        }


        //ADDING CRITERA TO QUERY (WHERE IT TAKES SINGLE CRITERIA OR COMBINED CRITERIA OBJECT
        // If any criteria are provided, combine them using an AND operator and add to the query
        if (!criteria.isEmpty()) {
            //here addCriteria method accepts a single criteria or combined criterias here we first
            //conver ArrayList of creterias into Arrays bcz to combine criteria's they must be array items .
            //initailly we create empty creterias
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[criteria.size()])));
            //In this line, you are using the andOperator method of the Criteria class to combine multiple
            // Criteria objects using the logical AND operator. The andOperator method expects an array of Criteria
            // objects as its parameter.
            //
            //Here's why new Criteria[0] is used:
            //
            //Creating an Empty Array: new Criteria[0] creates a new empty array of Criteria objects without any items in it (which means it has zero items in it initially)
            // . This arrayhas a size of 0, meaning it initially contains no elements.
            //Passing the Array: The criteria.toArray(new Criteria[0]) part converts the List of Criteria objects (
            // defined by criteria) into an array of Criteria objects. The usage of new Criteria[0] here is a way to
            // efficiently create an empty array to which the criteria list can be converted.

            //Passing Empty Array to andOperator: The empty array is then passed to the andOperator method, which
            // effectively means that you are asking it to combine the Criteria objects using the logical AND operator.
            // Since the array is empty, it's equivalent to saying "AND nothing," so the resulting combined Criteria is
            // effectively just the union of all the individual Criteria objects.
            //
            //The primary reason for using new Criteria[0] is to create an empty array that can be used as a
            // parameter for the andOperator method, allowing you to programmatically handle the case where there
            // are no criteria to be combined. It's a bit of a workaround to create an empty array as a placeholder when
            // no actual Criteria objects need to be combined.
        }

        //GETTING PAGE FROM PageableExecutionUtils.get()
        Page<Post> postsPage = PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Post.class),//getPage method needs first parameter as list of resultants docs
                pageable,//2nd parameter is pageable object for paging
                () -> mongoTemplate.count(query.skip(0).limit(0), Post.class)
                //3rd paramter is counting all resultant posts
                // the combination of skip(0) and limit(0) with a value of zero is a technique used to optimize the
                // performance of the count operation when you're only interested in the total count of matching
                // documents and not in retrieving the actual data.
        );


        // RETURING PAGE
        return postsPage;
    }

    @Override
    public Page<Post> searchByAdmin(String authorId, String title, String subtitle, String categoryName, String tagId, List<String> fields, Pageable pageable) {


        //CREATING QUERY AND ADDING PAGEABLE TO IT
        Query query = new Query().with(pageable);


        //LIMITING FIELDS
        //excluding and including some fields

        if (fields != null && fields.size() > 0 && !fields.isEmpty()) {
            List<String> excludeFields = new ArrayList<>();
            List<String> includeFields = new ArrayList<>();

            for (String field : fields) {
                if (field.startsWith("-")) {
                    excludeFields.add(field.substring(1));
                } else {
                    includeFields.add(field);
                }
            }
            if (excludeFields.size() > 0) {
                //if they are any excludeFields then , add the logic to query
                log.info("********: excludeFields: " + excludeFields.toString());
                query.fields().exclude(excludeFields.toArray(new String[excludeFields.size()]));
            }
            if (includeFields.size() > 0) {
                //if they are any includeFields then , add the logic to query
                log.info("********: includeFields: " + includeFields.toString());
                query.fields().include(includeFields.toArray(new String[includeFields.size()]));
            }
        }


        //CREATING LIST OF CRITERIA
        //here we used ArrayList bcz ArrayList is flexible with the length but Array has fixed length,
        //bcz based on conditions we are adding the criteria to the list,if array we can't do this incremental addition of criteria
        List<Criteria> criteria = new ArrayList<>();



        //ADDING CRITERIA TO CRETERIA LIST BASED ON CONDTIONS
//         Check if a search term "authorId" is provided and add a authorId-based regex criteria
        if (authorId != null && !authorId.isEmpty()) {
            criteria.add(Criteria.where("author.id").is(authorId));
        }


        // Check if a search term "title" is provided and add a title-based regex criteria
        if (title != null && !title.isEmpty()) {
            criteria.add(Criteria.where("title").regex(title, "i"));
        }

        // Check if a search term "subtitle" is provided and add a subtitle-based regex criteria
        if (subtitle != null && !subtitle.isEmpty()) {
            criteria.add(Criteria.where("subtitle").regex(subtitle, "i"));
        }

        // //HERE WE HAVE USED categoryName bcz we have made Category object in Post entity as Field instead of a reference
        // Check if a search term "categoryName" is provided and add a categoryName-based  criteria
        if (categoryName != null && !categoryName.isEmpty()) {
            criteria.add(Criteria.where("category.categoryName").is(categoryName.toUpperCase().trim()));
        }


        //HERE WE HAVE USED TAG ID INSTEAD OF TAGNAME BCZ IN POST ENTITY WE HAVE MENTIONED IT AS REFERENCE SO
        //DUE TO THIS WE CAN ONLY ACCESS BASED ON ITS ID ONLY
        // Check if a search term "tagId" is provided and add a tagId-based regex criteria
        if (tagId != null && !tagId.isEmpty()) {
            criteria.add(Criteria.where("tag.id").is(tagId));
        }


        //ADDING CRITERA TO QUERY (WHERE IT TAKES SINGLE CRITERIA OR COMBINED CRITERIA OBJECT
        // If any criteria are provided, combine them using an AND operator and add to the query
        if (!criteria.isEmpty()) {
            //here addCriteria method accepts a single criteria or combined criterias here we first
            //conver ArrayList of creterias into Arrays bcz to combine criteria's they must be array items .
            //initailly we create empty creterias
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[criteria.size()])));
            //In this line, you are using the andOperator method of the Criteria class to combine multiple
            // Criteria objects using the logical AND operator. The andOperator method expects an array of Criteria
            // objects as its parameter.
            //
            //Here's why new Criteria[0] is used:
            //
            //Creating an Empty Array: new Criteria[0] creates a new empty array of Criteria objects without any items in it (which means it has zero items in it initially)
            // . This arrayhas a size of 0, meaning it initially contains no elements.
            //Passing the Array: The criteria.toArray(new Criteria[0]) part converts the List of Criteria objects (
            // defined by criteria) into an array of Criteria objects. The usage of new Criteria[0] here is a way to
            // efficiently create an empty array to which the criteria list can be converted.

            //Passing Empty Array to andOperator: The empty array is then passed to the andOperator method, which
            // effectively means that you are asking it to combine the Criteria objects using the logical AND operator.
            // Since the array is empty, it's equivalent to saying "AND nothing," so the resulting combined Criteria is
            // effectively just the union of all the individual Criteria objects.
            //
            //The primary reason for using new Criteria[0] is to create an empty array that can be used as a
            // parameter for the andOperator method, allowing you to programmatically handle the case where there
            // are no criteria to be combined. It's a bit of a workaround to create an empty array as a placeholder when
            // no actual Criteria objects need to be combined.
        }

        //GETTING PAGE FROM PageableExecutionUtils.get()
        Page<Post> postsPage = PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Post.class),//getPage method needs first parameter as list of resultants docs
                pageable,//2nd parameter is pageable object for paging
                () -> mongoTemplate.count(query.skip(0).limit(0), Post.class)
                //3rd paramter is counting all resultant posts
                // the combination of skip(0) and limit(0) with a value of zero is a technique used to optimize the
                // performance of the count operation when you're only interested in the total count of matching
                // documents and not in retrieving the actual data.
        );


        // RETURING PAGE
        return postsPage;
    }
    @Override
    public Optional<Post> getSinglePostByIdAndAuthorId(String id, String authorId) {
        return postRepository.findByIdAndAuthorId(id, authorId);
    }

    @Override
    public void permanentlyDeletePostById(String id) {
        postRepository.deleteById(id);
    }

//    @Override
//    public void permanentlyDeletePostByIdAndAuthorId(String id, String authorId) {
//        postRepository.DeleteByIdAndAuthorId(id,authorId);
//    }
//
//    @Override
//    public void permanentlyDeleteAllByAuthorId(String authorId) {
//        postRepository.DeleteAllByAuthorId(authorId);
//    }

    //-------------------------------------------------------------------------------------------------------------
    @Override
    public Page<Post> getAllPostsWithPage(Pageable pageable) {
        return postRepository.findAll(pageable);
    }


    @Override
    public Optional<Post> getSinglePost(String id) {
        return postRepository.findById(id);
    }

    @Override
    public void permanentlyDeletePost(String id) {
        postRepository.deleteById(id);
    }

    @Override
    public Post partiallyDeletePost(Post post) {
        post.setActive(false);
        post.setDeleted(true);
        post.setUpdated(true);

        return postRepository.save(post);
    }


    @Override
    public Post undeletePostButNotActive(Post post) {
        post.setActive(false);
        post.setDeleted(false);
        post.setUpdated(true);

        return postRepository.save(post);
    }


    @Override
    public Post deactivatePost(Post post) {
        post.setActive(false);
        post.setDeleted(false);
        post.setUpdated(true);
        return postRepository.save(post);
    }

    @Override
    public Post activatePost(Post post) {
        post.setActive(true);
        post.setDeleted(false);
        post.setUpdated(true);
        return postRepository.save(post);
    }

    @Override
    public Optional<Post> getPostByIdAndActiveAndDeleted(String id, boolean active, boolean deleted) {
        return postRepository.findByIdAndActiveAndDeleted(id,active,deleted);
    }


//-------------------------------------------------------------

}



