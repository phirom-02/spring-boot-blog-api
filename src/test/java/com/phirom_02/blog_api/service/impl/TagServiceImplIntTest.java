package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.IntegrationTest;
import com.phirom_02.blog_api.domain.PostStatus;
import com.phirom_02.blog_api.domain.entities.Category;
import com.phirom_02.blog_api.domain.entities.Tag;
import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.repository.TagRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
@Import(TestDataHelper.class)
@Transactional
class TagServiceImplIntTest extends IntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16:9");

    @Autowired
    private TestDataHelper testDataHelper;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagServiceImpl tagService;

    @BeforeEach
    @Rollback
    void setUp() {
        Tag tag2 = Tag.builder().name("tag-1").posts(new ArrayList<>()).build();
        Tag tag1 = Tag.builder().name("tag-2").posts(new ArrayList<>()).build();
        Tag tag3 = Tag.builder().name("tag-3").posts(new ArrayList<>()).build();
        Tag tag4 = Tag.builder().name("tag-4").posts(new ArrayList<>()).build();
        List<Tag> tags = List.of(tag1, tag2, tag3, tag4);
        tagRepository.saveAll(tags);

        User user = testDataHelper.createUser("John Smith", "john.smith@example.com");
        Category category = testDataHelper.createCategory("Test Category");

        Set<Tag> postTags = Set.of(tag1, tag2);
        testDataHelper.createPost(
                "test1",
                "test1 content",
                PostStatus.PUBLISHED,
                user,
                category,
                postTags
        );
    }

    @Test
    void getAllTags_shouldRetALlTags() {
        // Arrange & Act
        List<Tag> tags = tagService.getAllTags();

        // Assert
        assertThat(tags.size()).isEqualTo(4);
    }

    @Test
    @Rollback
    void createTags_shouldReturnTagsAfterCreate() {
        // Arrange
        Set<String> tagNames = Set.of("tagA", "tagB", "tagC");

        // Act
        List<Tag> tags = tagService.createTags(tagNames);

        // Assert
        assertThat(tags.size()).isEqualTo(3);
    }

    @Test
    void getTagsByIds_shouldRetrieveMatchingTags() {
        // Arrange
        UUID tagId1 = testDataHelper.getTagByName("tag-1").getId();
        UUID tagId2 = testDataHelper.getTagByName("tag-2").getId();
        Set<UUID> tagIds = Set.of(tagId1, tagId2);

        // Act
        List<Tag> tags = tagService.getTagsByIds(tagIds);

        // Assert
        assertThat(tags.size()).isEqualTo(2);
    }

    @Test
    void getTagById_shouldRetrieveAMatchingTag() {
        // Arrange
        UUID tagId = testDataHelper.getTagByName("tag-1").getId();

        // Act
        Tag tag = tagService.getTagById(tagId);

        // Assert
        assertThat(tag.getId()).isEqualTo(tagId);
    }

    @Test
    public void getTagById_shouldThrowEntityNotFoundException() {
        // Arrange
        UUID tagId = UUID.randomUUID();
        Exception exception = new EntityNotFoundException("Tag not found with id: " + tagId);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> tagService.getTagById(tagId));
        assertThat("Tag not found with id: " + tagId).isEqualTo(exception.getMessage());
    }

    @Test
    @Rollback
    public void deleteTagById_shouldDeleteTag() {
        // Arrange
        UUID tagId = testDataHelper.getTagByName("tag-3").getId();

        // Act
        tagService.deleteTag(tagId);

        // Assert
        assertThat(tagRepository.findById(tagId)).isNotPresent();
    }

    @Test
    public void deleteTagById_shouldThrowIllegalStateException() {
        // Arrange
        UUID tagId = testDataHelper.getTagByName("tag-1").getId();
        Exception exception = new IllegalStateException("There are posts associated with tag: " + tagId);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> tagService.deleteTag(tagId));
        assertThat("There are posts associated with tag: " + tagId).isEqualTo(exception.getMessage());
    }
}