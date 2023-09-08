package com.ram.testforgithubandcyclic.controller;


import com.ram.testforgithubandcyclic.collection.Category;
import com.ram.testforgithubandcyclic.collection.Tag;
import com.ram.testforgithubandcyclic.collection.Post;
import com.ram.testforgithubandcyclic.collection.User;
import com.ram.testforgithubandcyclic.collection.Comment;
import com.ram.testforgithubandcyclic.dto.*;
import com.ram.testforgithubandcyclic.error.CustomException;
import com.ram.testforgithubandcyclic.repository.*;
import com.ram.testforgithubandcyclic.service.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class PostController {

    @Autowired
    private ModelMapper postMapper;

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;


    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagService tagService;


    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;


    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;


    @Autowired
    private ModelMapper modelMapper;

    //-------------------------------------------------------------------------
    //---createPostByAuthor
    @PostMapping("/author-protected/posts")
    public ResponseEntity<Object> createPostByAuthor(@Valid @RequestBody PostRequest reqBody, Authentication auth) {


        //getting author
        String authorEmail = auth.getName();
        Optional<User> authorOp = userService.getSingleActiveUserByEmail(authorEmail, true);
        User author = authorOp.get();

        //FINDING CATEGORY AND TAG AND ADDING IT TO THE POST OBJECT
        String categoryName = reqBody.getCategoryName().toUpperCase().trim();
        Optional<Category> foundCategoryOp = categoryRepository.findByCategoryName(categoryName);
        if (!foundCategoryOp.isPresent()) {
            throw new CustomException("Please provide valid Category", HttpStatus.BAD_REQUEST);
        }

        Category foundCategory = foundCategoryOp.get();

        String tagName = reqBody.getTagName().toUpperCase().trim();
        Optional<Tag> foundTagOp = tagRepository.findByTagName(tagName);
        if (!foundTagOp.isPresent()) {
            throw new CustomException("Please provide valid Tag", HttpStatus.BAD_REQUEST);
        }

        Tag foundTag = foundTagOp.get();

        //BUILDING POST OBJECT WITH FILTERED INPUTS
        Post filteredPost = Post.builder()
                .title(reqBody.getTitle().trim())
                .subtitle(reqBody.getSubtitle())
                .description(reqBody.getDescription())
                .images(reqBody.getImages() != null ? reqBody.getImages() : new ArrayList<>(0))
                .category(foundCategory)
                .tag(foundTag)
                .likes(new ArrayList<>(0))
                .publishedAt(new Date())
                .author(author)
                .build();


        Post newPost = postService.createPost(filteredPost);


        PostDto postDto = postMapper.map(newPost, PostDto.class);


        RBody rbody = new RBody("success", postDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(rbody);

    }


    //----------paging and searching using Criteria----------
    //---postsSearchByPublic
    @GetMapping("/public/posts")
    public ResponseEntity<Object> postsSearchOrGetAllByPublic(@RequestParam(required = false) String title,
                                                      @RequestParam(required = false) String subtitle,
                                                      @RequestParam(required = false) String categoryName,
                                                      @RequestParam(required = false) String tagName,
                                                      @RequestParam(required = false) String authorName,
                                                      @RequestParam(required = false) List<String> fields,
                                                      @RequestParam(defaultValue = "createdAt") List<String> sort,
                                                      @RequestParam(defaultValue = "0") Integer page,
                                                      @RequestParam(defaultValue = "5") Integer size) {


//--------------for single sort field-----------------
//            Sort sortByProperty=sort.startsWith("-")?Sort.by(Sort.Direction.DESC,sort.substring(1)):Sort.by(Sort.Direction.ASC,sort);

//--------------for MULTIPLE sort fieldS-----------------
        // MAKING SORT LIST
        List<Sort.Order> ordersA = new ArrayList<>();
        //FILTERING BASED ON "-" SYMBOL
        for (String field : sort) {
            if (field.startsWith("-")) {
                ordersA.add(new Sort.Order(Sort.Direction.DESC, field.substring(1)));
            } else {
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

        //CREATING SORT OBJECT WHICH TAKES STREAMABLE SORT.ORDER OBJECTS
        Sort sortProps = Sort.by(ordersA);

        //PAGEABLE
        Pageable pageable = PageRequest.of(page, size, sortProps);


        //getting tag id
        String tagId = tagName == null ? null : tagRepository.findByTagName(tagName.toUpperCase().trim()).get().getId();

        //getting author id
        String authorId = authorName == null ? null : userRepository.findByName(authorName).get().getId();


        //GETTING POSTS PAGE
        Page<Post> postsPage = postService.search(authorId, title, subtitle, categoryName, tagId, fields, pageable);


        //CONVERTING POSTS PAGE INTO POSTS DTO PAGE
        //DTO CONVERSION
        Page<PostDto> postDtosPage = postsPage.map(post -> postService.postToPostDto(post));

        RBody rbody = new RBody("success", postDtosPage);
        return ResponseEntity.status(HttpStatus.OK).body(rbody);
    }

    //-----------------------------------------------------------------------------------------------
    //---postsSearchByAdmin
    @GetMapping("/admin-protected/posts")
    public ResponseEntity<Object> postsSearchOrGetAllByAdmin(@RequestParam(required = false) String title,
                                                     @RequestParam(required = false) String subtitle,
                                                     @RequestParam(required = false) String categoryName,
                                                     @RequestParam(required = false) String tagName,
                                                     @RequestParam(required = false) String authorId,
                                                     @RequestParam(required = false) String authorName,
                                                     @RequestParam(required = false) List<String> fields,
                                                     @RequestParam(defaultValue = "createdAt") List<String> sort,
                                                     @RequestParam(defaultValue = "0") Integer page,
                                                     @RequestParam(defaultValue = "5") Integer size) {


//--------------for single sort field-----------------
//            Sort sortByProperty=sort.startsWith("-")?Sort.by(Sort.Direction.DESC,sort.substring(1)):Sort.by(Sort.Direction.ASC,sort);

//--------------for MULTIPLE sort fieldS-----------------
        // MAKING SORT LIST
        List<Sort.Order> ordersA = new ArrayList<>();
        //FILTERING BASED ON "-" SYMBOL
        for (String field : sort) {
            if (field.startsWith("-")) {
                ordersA.add(new Sort.Order(Sort.Direction.DESC, field.substring(1)));
            } else {
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

        //CREATING SORT OBJECT WHICH TAKES STREAMABLE SORT.ORDER OBJECTS
        Sort sortProps = Sort.by(ordersA);

        //PAGEABLE
        Pageable pageable = PageRequest.of(page, size, sortProps);

        //getting tag id
        String tagId = tagName == null ? null : tagRepository.findByTagName(tagName.toUpperCase().trim()).get().getId();

        //getting author id
        String authorIdFinal = authorId != null ? authorId : (authorName == null ? null : userRepository.findByName(authorName).get().getId());


        //GETTING POSTS PAGE
        Page<Post> postsPage = postService.searchByAdmin(authorIdFinal, title, subtitle, categoryName, tagId, fields, pageable);


        //GETTING POSTS PAGE


//        //CONVERTING POSTS PAGE INTO POSTS DTO PAGE
//        //DTO CONVERSION
//        Page<PostDto> postDtosPage = postsPage.map(post -> postService.postToPostDto(post));

        RBody rbody = new RBody("success", postsPage);
        return ResponseEntity.status(HttpStatus.OK).body(rbody);
    }

    //-----------------------------------------------------------------------------------------------
    //---myPostsSearchOrGetAllByAuthor
    @GetMapping("/author-protected/posts")
    public ResponseEntity<Object> myPostsSearchOrGetAllByAuthor(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String subtitle,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String tagName,
            @RequestParam(required = false) List<String> fields,
            @RequestParam(defaultValue = "createdAt") List<String> sort,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            Authentication auth) {


        String authorEmail=auth.getName();
        String authorId=userService.getSingleActiveUserByEmail(authorEmail,true).get().getId();


//--------------for single sort field-----------------
//            Sort sortByProperty=sort.startsWith("-")?Sort.by(Sort.Direction.DESC,sort.substring(1)):Sort.by(Sort.Direction.ASC,sort);

//--------------for MULTIPLE sort fieldS-----------------
        // MAKING SORT LIST
        List<Sort.Order> ordersA = new ArrayList<>();
        //FILTERING BASED ON "-" SYMBOL
        for (String field : sort) {
            if (field.startsWith("-")) {
                ordersA.add(new Sort.Order(Sort.Direction.DESC, field.substring(1)));
            } else {
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

        //CREATING SORT OBJECT WHICH TAKES STREAMABLE SORT.ORDER OBJECTS
        Sort sortProps = Sort.by(ordersA);

        //PAGEABLE
        Pageable pageable = PageRequest.of(page, size, sortProps);

        //getting tag id
        String tagId = tagName == null ? null : tagRepository.findByTagName(tagName.toUpperCase().trim()).get().getId();



        //GETTING POSTS PAGE
        Page<Post> postsPage = postService.search(authorId, title, subtitle, categoryName, tagId, fields, pageable);


        //GETTING POSTS PAGE


//        //CONVERTING POSTS PAGE INTO POSTS DTO PAGE
//        //DTO CONVERSION
        Page<PostDto> postDtosPage = postsPage.map(post -> postService.postToPostDto(post));

        RBody rbody = new RBody("success", postDtosPage);
        return ResponseEntity.status(HttpStatus.OK).body(rbody);
    }

    //----------------------------------------------------------------------------------
    //-- getSinglePostByPublic
    @GetMapping("/public/posts/{id}")
    public ResponseEntity<Object> getSinglePostByPublic(@PathVariable("id") String id) {

        Optional<Post> foundPostOp = postService.getPostByIdAndActiveAndDeleted(id,true,false);
        if (!foundPostOp.isPresent()) {
            throw new CustomException("Post Not Found!", HttpStatus.NOT_FOUND);
        }

        Post foundPost = foundPostOp.get();

        //Dto conversion

        PostDto postDto=postService.postToPostDto(foundPost);


        RBody rBody = new RBody<>("success",postDto );

        return ResponseEntity.status(HttpStatus.OK).body(rBody);
    }
    //----------------------------------------------------------------------------------
    //-- getSinglePostByAdmin
    @GetMapping("/admin-protected/posts/{id}")
    public ResponseEntity<Object> getSinglePostByAdmin(@PathVariable("id") String id) {

        Optional<Post> foundPostOp = postService.getSinglePost(id);
        if (!foundPostOp.isPresent()) {
            throw new CustomException("Post Not Found!", HttpStatus.NOT_FOUND);
        }

        Post foundPost = foundPostOp.get();


        RBody rBody = new RBody<>("success", foundPost);

        return ResponseEntity.status(HttpStatus.OK).body(rBody);
    }

    //----------------------------------------------------------------------------------
    //-- getSinglePostByAdmin
    @GetMapping("/author-protected/posts/{id}")
    public ResponseEntity<Object> getSinglePostByAuthor(@PathVariable("id") String id) {

        Optional<Post> foundPostOp = postService.getSinglePost(id);
        if (!foundPostOp.isPresent()) {
            throw new CustomException("Post Not Found!", HttpStatus.NOT_FOUND);
        }

        Post foundPost = foundPostOp.get();


        RBody rBody = new RBody<>("success", postService.postToPostDto(foundPost));

        return ResponseEntity.status(HttpStatus.OK).body(rBody);
    }


    //----------------------------------------------------------------------------------
    //-- addOrRemoveLikeOfPostByUser
    @PatchMapping("/user-protected/posts/{id}/like")
    public ResponseEntity<Object> addOrRemoveLikeOfPostByUser(@PathVariable("id") String id, Authentication auth) {

        // CHECKING IF THE GIVE POST ID IS VALID
        Optional<Post> foundPostOp = postService.getSinglePost(id);
        if (!foundPostOp.isPresent()) {
            throw new CustomException("Post Not Found!", HttpStatus.NOT_FOUND);
        }

        Post foundPost = foundPostOp.get();


        //GETTING LOGGED USER ID
        String loggedUserEmail = auth.getName();
        Optional<User> loggedUserOp = userService.getSingleActiveUserByEmail(loggedUserEmail, true);

        String userId = loggedUserOp.get().getId();

        //CHECKING IF THE THE USER ALREADY LIKED THE POST
//      Optional<String> likeIdOp=foundPost.getLikes().stream().filter(likeId->likeId.equals(userId)).findFirst();

        if (foundPost.getLikes().indexOf(userId) > -1) {
            foundPost.getLikes().remove(userId);
//           foundPost.setLikes(foundPost.getLikes().stream().filter(likeId->!likeId.equals(userId)).collect(Collectors.toList()));
        } else {
            foundPost.getLikes().add(userId);
        }

        foundPost.updateNumOfLikes();

        Post updatedPost = postService.udpatePost(foundPost);

        PostDto postDto = postMapper.map(updatedPost, PostDto.class);

        RBody rBody = new RBody<>("success", postDto);

        return ResponseEntity.status(HttpStatus.OK).body(rBody);
    }


    //    ------------------------------------------------------------------------------------
//    -- permanentlyDeleteHisSinglePost by Author
    @DeleteMapping("/author-protected/posts/{id}")
    public ResponseEntity<Object> permanentlyDeleteHisSinglePostByAuthor(@PathVariable("id") String id, Authentication auth) {

        //GETTING LOGGED USER
        String loggedUserEmail = auth.getName();
        Optional<User> loggedUserOp = userService.getSingleActiveUserByEmail(loggedUserEmail, true);
        if (!loggedUserOp.isPresent()) {
            throw new CustomException("User Not Active", HttpStatus.BAD_REQUEST);
        }

        String authorId = loggedUserOp.get().getId();

        Optional<Post> foundPostOp = postService.getSinglePostByIdAndAuthorId(id, authorId);
        if (!foundPostOp.isPresent()) {
            throw new CustomException("You are Not Allowed to perform this action!", HttpStatus.FORBIDDEN);
        }

        Post foundPost = foundPostOp.get();

        // permanently deleting the POST
        postService.permanentlyDeletePostById(id);

        //PERMANENTLY DELETING THE COMMENTS THAT ARE ON THIS POST
        commentRepository.deleteAllByPostId(id);

        MsgRBody rBody = new MsgRBody<>("success", "Successfully deleted the post and its associated comments with post id: " + id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(rBody);
    }

    //------------------------------------------------------------------------------------
    //    -- permanentlyDeleteAllHisPostsByAuthor by Author
    @DeleteMapping("/author-protected/posts")
    public ResponseEntity<Object> permanentlyDeleteAllHisPostsByAuthor(Authentication auth) {

        //GETTING LOGGED USER
        String loggedUserEmail=auth.getName();
        Optional<User> loggedUserOp=userService.getSingleActiveUserByEmail(loggedUserEmail,true);
        String authorId=loggedUserOp.get().getId();

        //
        List<Post> authorPosts=postRepository.findAllByAuthorId(authorId);


        //PERMANENTLY DELETING All THE POSTS OF THE AUTHOR
        postRepository.deleteAllByAuthorId(authorId);


        try{
            //PERMANENTLY DELETING THE COMMENTS THAT ARE ON AUTHOR POSTS
            for(Post post : authorPosts){
                commentRepository.deleteAllByPostId(post.getId());

            }
        }catch (Exception ex){
            throw new CustomException("Error while deleting comments of the author posts",HttpStatus.INTERNAL_SERVER_ERROR);
        }



        MsgRBody rBody = new MsgRBody<>("success", "Successfully deleted all the posts and associated comments of the author wit id: "+authorId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(rBody);
    }


    //----------------------------------------------------------------------------------
    //-- deleteSinglePost by admin
    @PatchMapping("/admin-protected/posts/delete/{id}")
    public ResponseEntity<Object> partiallyDeleteSinglePost(@PathVariable("id") String id) {

        Optional<Post> foundPostOp = postService.getSinglePost(id);
        if (!foundPostOp.isPresent()) {
            throw new CustomException("Post Not Found!", HttpStatus.NOT_FOUND);
        }

        Post foundPost = foundPostOp.get();

        Post deletedPost = postService.partiallyDeletePost(foundPost);


        MsgRBody rBody = new MsgRBody<>("success", "Successfully deleted the post with id: " + id);

        return ResponseEntity.status(HttpStatus.OK).body(rBody);
    }

    //----------------------------------------------------------------------------------
    //-- undeleteSinglePost by admin
    @PatchMapping("/admin-protected/posts/undelete/{id}")
    public ResponseEntity<Object> undeletePostButNotActive(@PathVariable("id") String id) {

        Optional<Post> foundPostOp = postService.getSinglePost(id);
        if (!foundPostOp.isPresent()) {
            throw new CustomException("Post Not Found!", HttpStatus.NOT_FOUND);
        }

        Post foundPost = foundPostOp.get();

        Post undeletedPost = postService.undeletePostButNotActive(foundPost);


        MsgRBody rBody = new MsgRBody<>("success", "Successfully undeleted the post with id: " + id);

        return ResponseEntity.status(HttpStatus.OK).body(rBody);
    }

    //----------------------------------------------------------------------------------
    //-- permanentlyDeleteSinglePost by admin
    @DeleteMapping("/admin-protected/posts/delete/{id}")
    public ResponseEntity<Object> permanentlyDeleteSinglePost(@PathVariable("id") String id) {

        Optional<Post> foundPostOp = postService.getSinglePost(id);
        if (!foundPostOp.isPresent()) {
            throw new CustomException("Post Not Found!", HttpStatus.NOT_FOUND);
        }

        Post foundPost = foundPostOp.get();

        postService.permanentlyDeletePost(id);


        MsgRBody rBody = new MsgRBody<>("success", "Successfully deleted the post with id: " + id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(rBody);
    }


    //----------------------------------------------------------------------------------
    //-- deactivateSinglePost
    @PatchMapping("/admin-protected/posts/deactivate/{id}")
    public ResponseEntity<Object> deactivateSinglePost(@PathVariable("id") String id) {

        Optional<Post> foundPostOp = postService.getSinglePost(id);
        if (!foundPostOp.isPresent()) {
            throw new CustomException("Post Not Found!", HttpStatus.NOT_FOUND);
        }

        Post foundPost = foundPostOp.get();

        Post deactivatedPost = postService.deactivatePost(foundPost);


        MsgRBody rBody = new MsgRBody<>("success", "Successfully deactivated the post with id: " + id);

        return ResponseEntity.status(HttpStatus.OK).body(rBody);
    }


    //----------------------------------------------------------------------------------
    //-- activateSinglePost by admin
    @PatchMapping("/admin-protected/posts/activate/{id}")
    public ResponseEntity<Object> activateSinglePost(@PathVariable("id") String id) {

        Optional<Post> foundPostOp = postService.getSinglePost(id);
        if (!foundPostOp.isPresent()) {
            throw new CustomException("Post Not Found!", HttpStatus.NOT_FOUND);
        }

        Post foundPost = foundPostOp.get();

        Post deactivatedPost = postService.activatePost(foundPost);


        MsgRBody rBody = new MsgRBody<>("success", "Successfully activated the post with id: " + id);

        return ResponseEntity.status(HttpStatus.OK).body(rBody);
    }


    // -------------COMMENTS ON POSTS ------------------
    // -- createComment on post------
    @PostMapping("/user-protected/posts/{postId}/comments")
    public ResponseEntity<Object> createComment(@PathVariable("postId") String postId,
                                                @Valid @RequestBody Comment comment, Authentication auth) {
        //FIND THE POST
        Optional<Post> foundPostOp = postService.getPostByIdAndActiveAndDeleted(postId, true,false);
        if (!foundPostOp.isPresent()) {
            throw new CustomException("Post Not Found!", HttpStatus.NOT_FOUND);
        }

        Post foundPost = foundPostOp.get();

        //FIND THE LOGGED USER
        String loggedEmail = auth.getName();
        Optional<User> loggedUserOp = userService.getSingleUserByEmailId(loggedEmail);

        User loggedinUser = loggedUserOp.get();

//***************making user to comment on the posts multiple times instead of one time******************

//        //CHECKING IF THE USER ALREADY COMMENTED ON THIS POST FOR MAKING USER COMMENT ON THE POST FOR ONE TIME ONLY
//        Optional<Comment> foundComment = commentRepository.findByPostIdAndUserId(postId, loggedinUser.getId());
//        if (foundComment.isPresent()) {
//            throw new CustomException("You have already commented this post", HttpStatus.BAD_REQUEST);
//        }


        //BUILD FILTERED COMMENT
        Comment filteredComment = Comment.builder()
                .text(comment.getText())
                .user(loggedinUser).postId(postId).build();

        //CREATE COMMENT
        Comment newComment = commentService.createComment(filteredComment);


        RBody rBody = new RBody<>("success", "Successfully created the comment on the post with id: " + postId);

        return ResponseEntity.status(HttpStatus.OK).body(rBody);
    }

    //------------------------------------------
    // -- updateMyCommentOnThisPost on post------
    @PatchMapping("/user-protected/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Object> updateMyCommentOnThisPost(@PathVariable("postId") String postId,
                                                            @PathVariable("commentId") String commentId,
                                                            @Valid @RequestBody CommentUpdateRequest commentUpdateRequest,
                                                            Authentication auth) {
        //FIND THE POST
        Optional<Post> foundPostOp = postService.getPostByIdAndActiveAndDeleted(postId, true,false);
        if (!foundPostOp.isPresent()) {
            throw new CustomException("Post Not Found!", HttpStatus.NOT_FOUND);
        }

        Post foundPost = foundPostOp.get();

        //FIND THE LOGGED USER
        String loggedEmail = auth.getName();
        Optional<User> loggedUserOp = userService.getSingleUserByEmailId(loggedEmail);

        User loggedinUser = loggedUserOp.get();


        //CHECKING IF THE USER BELONGS TO THIS COMMENT AND POST
        Optional<Comment> foundCommentOp = commentRepository.findByIdAndPostIdAndUserIdAndActive(commentId, postId, loggedinUser.getId(), true);
        if (!foundCommentOp.isPresent()) {
            throw new CustomException("You are not Allowed to perform this action", HttpStatus.UNAUTHORIZED);
        }

        Comment foundComment = foundCommentOp.get();

        //UPDATING COMMENT object
        //only update text if user has given the text
        if (Optional.ofNullable(commentUpdateRequest.getText()).isPresent()) {
            foundComment.setText(commentUpdateRequest.getText());
        }


        //FINALLY UPDATING COMMENT
        Comment updatedComment = commentService.updateComment(foundComment);


        MsgRBody rBody = new MsgRBody<>("success", "Successfully updated the comment with Id: " + commentId);

        return ResponseEntity.status(HttpStatus.OK).body(rBody);
    }


    //--------------------------------------------------------------------------------------------
    // -- getAllCommentsOfThisPost on post------
    @GetMapping("/public/posts/{postId}/comments")
    public ResponseEntity<Object> getAllCommentsOfThisPost(@PathVariable("postId") String postId,
                                                           @RequestParam(required = false) String userId,
                                                           @RequestParam(required = false) String commentId,
                                                           @RequestParam(required = false) List<String> fields,
                                                           @RequestParam(defaultValue = "-createdAt") List<String> sort,
                                                           @RequestParam(defaultValue = "0") Integer page,
                                                           @RequestParam(defaultValue = "5") Integer size) {

        //FIND THE POST
        Optional<Post> foundPostOp = postService.getPostByIdAndActiveAndDeleted(postId, true,false);
        if (!foundPostOp.isPresent()) {
            throw new CustomException("Post Not Found!", HttpStatus.NOT_FOUND);
        }

        Post foundPost = foundPostOp.get();


//--------------for single sort field-----------------
//            Sort sortByProperty=sort.startsWith("-")?Sort.by(Sort.Direction.DESC,sort.substring(1)):Sort.by(Sort.Direction.ASC,sort);

//--------------for MULTIPLE sort fieldS-----------------
        // MAKING SORT LIST
        List<Sort.Order> ordersA = new ArrayList<>();
        //FILTERING BASED ON "-" SYMBOL
        for (String field : sort) {
            if (field.startsWith("-")) {
                ordersA.add(new Sort.Order(Sort.Direction.DESC, field.substring(1)));
            } else {
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

        //CREATING SORT OBJECT WHICH TAKES STREAMABLE SORT.ORDER OBJECTS
        Sort sortProps = Sort.by(ordersA);

        //PAGEABLE
        Pageable pageable = PageRequest.of(page, size, sortProps);


        //GETTING POSTS PAGE
        Page<Comment> commentsPage = commentService.search(null, postId, userId, commentId, fields, pageable);


//        //CONVERTING POSTS PAGE INTO POSTS DTO PAGE
//        //DTO CONVERSION
        Page<CommentDto> commentDtosPage = commentsPage.map(comment -> modelMapper.map(comment, CommentDto.class));


        RBody rBody = new RBody<>("success", commentDtosPage);

        return ResponseEntity.status(HttpStatus.OK).body(rBody);
    }

    //--------------------------------------------------------------------------------------------

    // -- deleteComment on post ------
    @DeleteMapping("/user-protected/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Object> permanentlyDeleteMyCommentOnThisPost(@PathVariable("postId") String postId,
                                                                       @PathVariable("commentId") String commentId, Authentication auth) {
        //FIND THE POST
        Optional<Post> foundPostOp = postService.getPostByIdAndActiveAndDeleted(postId, true,false);
        if (!foundPostOp.isPresent()) {
            throw new CustomException("Post Not Found!", HttpStatus.NOT_FOUND);
        }

        Post foundPost = foundPostOp.get();

        //FIND THE LOGGED USER
        String loggedEmail = auth.getName();
        Optional<User> loggedUserOp = userService.getSingleUserByEmailId(loggedEmail);

        User loggedinUser = loggedUserOp.get();


        //CHECKING IF THE USER BELONGS THIS COMMENT
        Optional<Comment> foundCommentOp = commentService.getSingleCommentByIdAndPostIdAndUserId(commentId, postId, loggedinUser.getId());
        if (!foundCommentOp.isPresent()) {
            throw new CustomException("You are not allowed to perform this action", HttpStatus.FORBIDDEN);
        }

        Comment foundComment = foundCommentOp.get();

        //DELETE COMMENT

        commentService.deleteCommentById(commentId);


        RBody rBody = new RBody<>("success", "Successfully deleted the comment");

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(rBody);
    }


}
