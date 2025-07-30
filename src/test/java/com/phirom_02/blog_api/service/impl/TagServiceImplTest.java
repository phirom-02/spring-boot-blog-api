package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.domain.entities.Post;
import com.phirom_02.blog_api.domain.entities.Tag;
import com.phirom_02.blog_api.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @InjectMocks
    private TagServiceImpl tagService;

    @Mock
    private TagRepository tagRepository;

    List<Tag> tags;

    @BeforeEach
    void setUp() {
        Tag tag1 = Tag.builder().name("tag1").build();
        Tag tag2 = Tag.builder().name("tag2").build();
        Tag tag3 = Tag.builder().name("tag3").build();

        tags = List.of(tag1, tag2, tag3);
    }

    @Test
    public void getAllTags_shouldRetrieveAllTags() {
        // Arrange
        String tag1 = "tag1";
        String tag2 = "tag2";
        String tag3 = "tag3";

        when(tagRepository.findAllWithPostCount()).thenReturn(tags);

        // Act
        List<Tag> result = tagService.getAllTags();

        // Assert
        assertThat(result.get(0).getName()).isEqualTo(tag1);
        assertThat(result.get(1).getName()).isEqualTo(tag2);
        assertThat(result.get(2).getName()).isEqualTo(tag3);
    }

    @Test
    public void createTags_shouldReturnTagsAfterCreate() {
        // Arrange
        Set<String> tagNames = Set.of("tag1", "tag2", "tag3");

        when(tagRepository.saveAll(any(List.class))).thenReturn(tags);

        // Act
        List<Tag> result = tagService.createTags(tagNames);

        // Assert
        assertThat(result.get(0).getName()).isEqualTo(tags.get(0).getName());
        assertThat(result.get(1).getName()).isEqualTo(tags.get(1).getName());
        assertThat(result.get(2).getName()).isEqualTo(tags.get(2).getName());
    }

    @Test
    public void getTagsByIds_shouldRetrieveMatchingTags() {
        // Arrange
        UUID tag1Id = UUID.randomUUID();
        Tag tag1 = new Tag();
        tag1.setId(tag1Id);
        UUID tag2Id = UUID.randomUUID();
        Tag tag2 = new Tag();
        tag2.setId(tag2Id);
        List<Tag> tags = List.of(tag1, tag2);

        Set<UUID> tagIds = Set.of(tag1.getId(), tag2.getId());

        when(tagRepository.findAllByIdIn(any(Set.class))).thenReturn(tags);

        // Act
        List<Tag> result = tagService.getTagsByIds(tagIds);

        // Assert
        assertThat(result.get(0).getId()).isEqualTo(tag1Id);
        assertThat(result.get(1).getId()).isEqualTo(tag2Id);
    }

    @Test
    public void getTagById_shouldRetrieveAMatchingTag() {
        // Arrange
        UUID tagId = UUID.randomUUID();
        Tag tag = Tag.builder().id(tagId).build();

        when(tagRepository.findById(any(UUID.class))).thenReturn(Optional.of(tag));

        // Act
        Tag result = tagService.getTagById(tagId);

        // Assert
        assertThat(result.getId()).isEqualTo(tagId);
    }

    @Test
    public void getTagById_shouldThrowEntityNotFoundException() {
        // Arrange
        UUID tagId = UUID.randomUUID();
        Exception exception = new EntityNotFoundException("Tag not found with id: " + tagId);
        when(tagRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> tagService.getTagById(tagId));
        assertThat("Tag not found with id: " + tagId).isEqualTo(exception.getMessage());
    }

    @Test
    public void deleteTagById_shouldDeleteATag() {
        // Arrange
        UUID tagId = UUID.randomUUID();
        Tag tag = Tag.builder()
                .id(tagId)
                .posts(new ArrayList<>())
                .build();

        when(tagRepository.findById(any(UUID.class))).thenReturn(Optional.of(tag));
        doNothing().when(tagRepository).deleteById(tagId);

        // Act
        tagService.deleteTag(tagId);

        // Assert | Verify
        verify(tagRepository, times(1)).deleteById(tagId);
    }

    @Test
    public void deleteTagById_shouldThrowIllegalStateException() {
        // Arrange
        UUID tagId = UUID.randomUUID();
        Post post = new Post();
        List<Post> posts = List.of(post);
        Tag tag = Tag.builder()
                .id(tagId)
                .posts(posts)
                .build();

        Exception exception = new IllegalStateException("There are posts associated with tag: " + tagId);

        when(tagRepository.findById(any(UUID.class))).thenReturn(Optional.of(tag));

        // Arrange & Assert
        assertThrows(IllegalStateException.class, () -> tagService.deleteTag(tagId));
        assertThat("There are posts associated with tag: " + tagId).isEqualTo(exception.getMessage());
    }
}