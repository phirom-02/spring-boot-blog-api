package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.IntegrationTest;
import com.phirom_02.blog_api.domain.PostStatus;
import com.phirom_02.blog_api.domain.dtos.CreatePostDto;
import com.phirom_02.blog_api.domain.dtos.UpdatePostDto;
import com.phirom_02.blog_api.domain.entities.Category;
import com.phirom_02.blog_api.domain.entities.Post;
import com.phirom_02.blog_api.domain.entities.Tag;
import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.repository.PostRepository;
import com.phirom_02.blog_api.util.TestDataHelper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
@Import(TestDataHelper.class)
@Transactional
class PostServiceImplIntTest extends IntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16:9");

    @Autowired
    private TestDataHelper testDataHelper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    PostServiceImpl postService;

    UUID postId;

    @BeforeEach
    @Rollback
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
    public void getAllPosts_shouldRetrieveAllPostByCategoryIdAndTagId() {
        // Arrange
        UUID categoryId = testDataHelper.getCategoryByName("category-1").getId();
        UUID tagId = testDataHelper.getTagByName("tag-1").getId();

        // Act
        List<Post> posts = postService.getAllPosts(categoryId, tagId);

        // Assert
        assertThat(posts.size()).isEqualTo(1);
    }

    @Test
    public void getAllPost_shouldRetrieveAllPostByCategoryId() {
        // Arrange
        UUID categoryId = testDataHelper.getCategoryByName("category-1").getId();

        // Act
        List<Post> posts = postService.getAllPosts(categoryId, null);

        // Assert
        assertThat(posts.size()).isEqualTo(1);
    }

    @Test
    public void getAllPost_shouldRetrieveAllPostByTagId() {
        // Arrange
        UUID tagId = testDataHelper.getTagByName("tag-4").getId();

        // Act
        List<Post> posts = postService.getAllPosts(null, tagId);

        // Assert
        assertThat(posts.size()).isEqualTo(2);
    }

    @Test
    public void getAllDraftedPosts_shouldRetrieveAllDraftedPostByUserId() {
        // Arrange
        UUID userId = testDataHelper.getUserByEmail("john.smith@example.com").get().getId();

        // Act
        List<Post> draftedPost = postService.getAllDraftedPosts(userId);

        // Assert
        draftedPost.forEach(p -> {
            assertThat(p.getStatus()).isEqualTo(PostStatus.DRAFT);
        });
    }

    @Test
    public void getPostById_shouldRetrieveAMatchingPost() {
        // Arrange & Act
        Post post = postService.getPostById(postId);

        // Assert
        assertThat(post.getId()).isEqualTo(postId);
    }

    @Test
    public void getPostById_shouldThrowsEntityNotFoundException() {
        // Arrange
        UUID postId = UUID.randomUUID();
        Exception exception = new EntityNotFoundException("Post not found with id: " + postId);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> postService.getPostById(postId));
        assertThat("Post not found with id: " + postId).isEqualTo(exception.getMessage());
    }

    @Test
    @Rollback
    public void createPost_shouldReturnAPostAfterCreate() {
        // Arrange
        User user = testDataHelper.getUserByEmail("john.smith@example.com").get();
        UUID categoryId = testDataHelper.getCategoryByName("category-1").getId();
        UUID tagId = testDataHelper.getTagByName("tag-1").getId();
        Set<UUID> tags = Set.of(tagId);
        CreatePostDto createPostDto = CreatePostDto.builder()
                .title("new post")
                .content("new post content")
                .categoryId(categoryId)
                .tagIds(tags)
                .status(PostStatus.PUBLISHED
                ).build();

        // Act
        Post post = postService.createPost(user, createPostDto);

        // Assert
        assertThat(post.getTitle()).isEqualTo(createPostDto.getTitle());
        assertThat(post.getContent()).isEqualTo(createPostDto.getContent());
        assertThat(post.getCategory().getId()).isEqualTo(categoryId);
        assertThat(post.getTags().stream().toList().getFirst().getId()).isEqualTo(tagId);
        assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);
    }

    @Test
    @Rollback
    public void updatePost_shouldReturnAPostAfterUpdate() {
        // Arrange
        Post post = postService.getPostById(postId);

        UpdatePostDto updatePostDto = UpdatePostDto.builder()
                .id(postId)
                .title("Updated title")
                .content(post.getContent())
                .categoryId(post.getCategory().getId())
                .tagIds(post.getTags().stream().map(Tag::getId).collect(Collectors.toSet()))
                .build();

        // Act
        Post updatedPost = postService.updatePost(postId, updatePostDto);

        // Assert
        assertThat(updatedPost.getTitle()).isEqualTo("Updated title");
    }

    @Test
    @Rollback
    public void deletePost_shouldDeleteAPost() {
        // Arrange
        Exception exception = new EntityNotFoundException("Post not found with id: " + postId);

        // Act
        postService.deletePost(postId);

        // Assert
        assertThrows(EntityNotFoundException.class, () -> postService.getPostById(postId));
        assertThat("Post not found with id: " + postId).isEqualTo(exception.getMessage());
    }
}