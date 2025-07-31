package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.domain.PostStatus;
import com.phirom_02.blog_api.domain.dtos.CreatePostDto;
import com.phirom_02.blog_api.domain.dtos.UpdatePostDto;
import com.phirom_02.blog_api.domain.entities.Category;
import com.phirom_02.blog_api.domain.entities.Post;
import com.phirom_02.blog_api.domain.entities.Tag;
import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.repository.PostRepository;
import com.phirom_02.blog_api.service.CategoryService;
import com.phirom_02.blog_api.service.TagService;
import com.phirom_02.blog_api.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @InjectMocks
    PostServiceImpl postService;

    @Mock
    PostRepository postRepository;
    @Mock
    UserService userService;
    @Mock
    CategoryService categoryService;
    @Mock
    TagService tagService;

    UUID categoryId;
    UUID tagId;
    UUID userId;
    UUID post1Id;
    UUID post2Id;

    User user;
    Category category;
    Tag tag;
    List<Post> posts;


    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        tagId = UUID.randomUUID();
        userId = UUID.randomUUID();

        user = new User();
        user.setId(userId);

        category = new Category();
        category.setId(categoryId);

        tag = new Tag();
        tag.setId(tagId);

        post1Id = UUID.randomUUID();
        post2Id = UUID.randomUUID();

        Post post1 = Post.builder()
                .id(post1Id)
                .title("post 1")
                .content("post content 1")
                .author(user)
                .category(category)
                .tags(Set.of(tag))
                .status(PostStatus.PUBLISHED)
                .build();
        Post post2 = Post.builder()
                .id(post2Id)
                .title("post 2")
                .content("post content 2")
                .author(user)
                .category(category)
                .tags(Set.of(tag))
                .status(PostStatus.DRAFT)
                .build();
        posts = List.of(post1, post2);
    }


    @Test
    public void getAllPosts_shouldRetrieveAllPostByCategoryIdAndTagId() {
        // Arrange
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        when(tagService.getTagById(tagId)).thenReturn(tag);
        when(postRepository.findAllByStatusAndCategoryAndTagsContaining(PostStatus.PUBLISHED, category, tag))
                .thenReturn(posts.stream().filter(p -> p.getStatus() == PostStatus.PUBLISHED).toList());

        // Act
        List<Post> result = postService.getAllPosts(categoryId, tagId);

        // Assert
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void getAllPost_shouldRetrieveAllPostByCategoryId() {
        // Arrange
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        when(postRepository.findAllByStatusAndCategory(PostStatus.PUBLISHED, category))
                .thenReturn(posts.stream().filter(p -> p.getStatus() == PostStatus.PUBLISHED).toList());

        // Act
        List<Post> result = postService.getAllPosts(categoryId, null);

        // Assert
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void getAllPost_shouldRetrieveAllPostByTagId() {
        // Arrange
        when(tagService.getTagById(tagId)).thenReturn(tag);
        when(postRepository.findAllByStatusAndTagsContaining(PostStatus.PUBLISHED, tag))
                .thenReturn(posts.stream().filter(p -> p.getStatus() == PostStatus.PUBLISHED).toList());

        // Act
        List<Post> result = postService.getAllPosts(null, tagId);

        // Assert
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void getAllDraftedPosts_shouldRetrieveAllDraftedPostByUserId() {
        // Arrange
        when(userService.findUserById(userId)).thenReturn(user);
        when(postRepository.findAllByAuthorAndStatus(user, PostStatus.DRAFT))
                .thenReturn(posts.stream().filter(p -> p.getStatus() == PostStatus.DRAFT).toList());

        // Act
        List<Post> result = postService.getAllDraftedPosts(userId);

        // Assert
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void getPostById_shouldRetrieveAMatchingPost() {
        // Arrange
        when(postRepository.findById(post1Id)).thenReturn(posts.stream().filter(p -> p.getId() == post1Id).findFirst());

        // Act
        Post result = postService.getPostById(post1Id);

        // Assert
        assertThat(posts.getFirst().getId()).isEqualTo(result.getId());
    }

    @Test
    public void getPostById_shouldThrowsEntityNotFoundException() {
        // Arrange
        UUID postId = UUID.randomUUID();
        Exception exception = new EntityNotFoundException("Post not found with id: " + postId);

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> postService.getPostById(postId));
        assertThat("Post not found with id: " + postId).isEqualTo(exception.getMessage());

    }

    @Test
    public void createPost_shouldReturnAPostAfterCreate() {
        // Arrange
        CreatePostDto dto = CreatePostDto.builder()
                .title("New Post")
                .content("New post content")
                .status(PostStatus.PUBLISHED)
                .categoryId(categoryId)
                .tagIds(Set.of(tag.getId()))
                .build();

        Post post = Post.builder()
                .title("New Post")
                .content("New post content")
                .status(PostStatus.PUBLISHED)
                .category(category)
                .tags(Set.of(tag))
                .build();

        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        when(tagService.getTagsByIds(Set.of(tagId))).thenReturn(List.of(tag));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        // Act
        Post result = postService.createPost(user, dto);

        // Assert
        assertThat(dto.getTitle()).isEqualTo(result.getTitle());
        assertThat(dto.getContent()).isEqualTo(result.getContent());
        assertThat(dto.getStatus()).isEqualTo(result.getStatus());
        assertThat(dto.getCategoryId()).isEqualTo(result.getCategory().getId());
        assertThat(dto.getTagIds().stream().toList().getFirst()).isEqualTo(result.getTags().stream().toList().getFirst().getId());
    }

    @Test
    public void updatePost_shouldReturnAPostAfterUpdate() {
        // Arrange
        UUID postId = post1Id;

        UpdatePostDto dto = new UpdatePostDto();
        dto.setId(postId);
        dto.setTitle("Updated Title");
        dto.setContent("Updated content for testing.");
        dto.setCategoryId(categoryId);
        dto.setStatus(PostStatus.PUBLISHED);
        dto.setTagIds(Set.of(tagId));

        Optional<Post> updatedPost = posts.stream().filter(p -> p.getId() == postId).findFirst();

        updatedPost.get().builder()
                .id(postId)
                .title("Updated Title")
                .content("Updated content for testing.")
                .category(category)
                .status(PostStatus.PUBLISHED)
                .tags(Set.of(tag))
                .build();

        when(postRepository.findById(postId)).thenReturn(posts.stream().filter(p -> p.getId() == postId).findFirst());
        when(postRepository.save(any(Post.class))).thenReturn(updatedPost.get());

        // Act
        Post result = postService.updatePost(postId, dto);

        // Assert
        assertThat(dto.getTitle()).isEqualTo(result.getTitle());
        assertThat(dto.getContent()).isEqualTo(result.getContent());
        assertThat(dto.getStatus()).isEqualTo(result.getStatus());
        assertThat(dto.getCategoryId()).isEqualTo(result.getCategory().getId());
        assertThat(dto.getTagIds().stream().toList().getFirst()).isEqualTo(result.getTags().stream().toList().getFirst().getId());
    }

    @Test
    public void deletePost_shouldDeleteAPost() {
        // Arrange
        when(postRepository.findById(post1Id)).thenReturn(Optional.of(posts.stream().filter(p -> p.getId() == post1Id).toList().getFirst()));

        // Act
        postService.deletePost(post1Id);

        // Assert | Verify
        verify(postRepository, times(1)).deleteById(post1Id);
    }
}