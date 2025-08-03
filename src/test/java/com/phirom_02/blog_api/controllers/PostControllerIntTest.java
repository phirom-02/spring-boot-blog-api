package com.phirom_02.blog_api.controllers;

import com.phirom_02.blog_api.IntegrationTest;
import com.phirom_02.blog_api.config.TestSecurityConfig;
import com.phirom_02.blog_api.domain.PostStatus;
import com.phirom_02.blog_api.domain.dtos.AuthResponse;
import com.phirom_02.blog_api.domain.dtos.LoginPayload;
import com.phirom_02.blog_api.domain.entities.Category;
import com.phirom_02.blog_api.domain.entities.Post;
import com.phirom_02.blog_api.domain.entities.Tag;
import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.util.TestDataHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestSecurityConfig.class, TestDataHelper.class})
@Transactional
class PostControllerIntTest extends IntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    TestDataHelper testDataHelper;

    UUID postId;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeAll
    void setUp() {
        User user = testDataHelper.createUser("John Smith", "john.smith@example.com");
        Category category1 = testDataHelper.createCategory("category-1");
        Category category2 = testDataHelper.createCategory("category-2");

        Tag tag1 = testDataHelper.createTag("tag-1");
        Tag tag2 = testDataHelper.createTag("tag-2");
        Tag tag3 = testDataHelper.createTag("tag-3");
        Tag tag4 = testDataHelper.createTag("tag-4");

        Set<Tag> tagsForPost1 = new HashSet<>();
        tagsForPost1.add(tag1);
        tagsForPost1.add(tag2);
        tagsForPost1.add(tag4);

        Set<Tag> tagsForPost2 = new HashSet<>();
        tagsForPost2.add(tag3);
        tagsForPost2.add(tag4);

        Post post = testDataHelper.createPost(
                "Test1",
                "Test contents 1",
                PostStatus.PUBLISHED,
                user,
                category1,
                tagsForPost1
        );
        postId = post.getId();
        testDataHelper.createPost(
                "Test2",
                "Test contents 2",
                PostStatus.PUBLISHED,
                user,
                category2,
                tagsForPost2
        );
        testDataHelper.createPost(
                "Test3",
                "Test contents 3",
                PostStatus.DRAFT,
                user,
                category1,
                tagsForPost2
        );
    }

    @Test
    public void getAllPosts_shouldReturnAllPostsWithMatchingCategoryIdAndTagId() {
        // Arrange
        String categoryId = testDataHelper.getCategoryByName("category-1").getId().toString();
        String tagId = testDataHelper.getTagByName("tag-1").getId().toString();

        // Act
        String url = "/api/v1/posts?categoryId={categoryId}&tagId={tagId}";
        ResponseEntity<List> response = testRestTemplate.getForEntity(url, List.class, categoryId, tagId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).size()).isEqualTo(1);
    }

    @Test
    public void getAllPosts_shouldReturnAllPostsWithMatchingCategory() {
        // Arrange
        String categoryId = testDataHelper.getCategoryByName("category-1").getId().toString();

        // Act
        String url = "/api/v1/posts?categoryId={categoryId}";
        ResponseEntity<List> response = testRestTemplate.getForEntity(url, List.class, categoryId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).size()).isEqualTo(1);
    }

    @Test
    public void getAllPosts_shouldReturnAllPostsWithMatchingTagId() {
        // Arrange
        // token
        String tagId = testDataHelper.getTagByName("tag-4").getId().toString();

        // Act
        String url = "/api/v1/posts?tagId={tagId}";
        ResponseEntity<List> response = testRestTemplate.getForEntity(url, List.class, tagId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).size()).isEqualTo(2);
    }

    @Test
    public void getAllDraftedPosts_shouldReturnAllDraftedPosts() {
        // Arrange & Act
        LoginPayload payload = LoginPayload.builder()
                .email("john.smith@example.com")
                .password("@#password")
                .build();

        ResponseEntity<AuthResponse> loginResponse = testRestTemplate.postForEntity("/api/v1/auth/login", payload, AuthResponse.class);

        String token = Objects.requireNonNull(loginResponse.getBody()).getToken();


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List> response = testRestTemplate.exchange("/api/v1/posts/drafts", HttpMethod.GET, entity, List.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).size()).isEqualTo(1);
    }
}