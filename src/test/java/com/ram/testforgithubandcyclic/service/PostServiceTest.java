package com.ram.testforgithubandcyclic.service;

import com.ram.testforgithubandcyclic.collection.Post;
import com.ram.testforgithubandcyclic.collection.User;
import com.ram.testforgithubandcyclic.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@SpringBootTest
class PostServiceTest {


    @Autowired
    private PostService postService;

    @MockBean
    private PostRepository postRepository;

    @BeforeEach
    void setup() {

        User author = User.builder().name("ram").avatar("avatar.jpg").email("ram@gmail.com").build();

        Post post = Post.builder().title("yoga and its benefits")
                .subtitle("subtitle of the post")
                .description("description of the post")
                .images(new ArrayList<>(Arrays.asList("image1", "image2")))
                .author(author)
                .build();

        Optional<Post> postOp = Optional.ofNullable(post);

        Mockito.when(postRepository.findByTitleIgnoreCase("yoga and its benefits")).thenReturn(postOp);
    }

    @Test
    @DisplayName("Get the data based on valid title")
//    @Disabled
    void getSinglePostByValidTitle() {
        String postTitle = "yoga and its benefits";

        Optional<Post> foundPostOp = postService.findPostByTitle(postTitle);
        Post foundPost = foundPostOp.get();


        assertEquals(postTitle, foundPost.getTitle());

    }
}