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
        Tag tag1 = new Tag();
        tag1.setName("tag1");
        Tag tag2 = new Tag();
        tag2.setName("tag2");
        Tag tag3 = new Tag();
        tag3.setName("tag3");

        tags = List.of(tag1, tag2, tag3);
    }

    @Test
    public void getAllTags_shouldReturnAllTags() {
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
        Set<String> tagNames = new HashSet<>();
        tagNames.add("tag1");
        tagNames.add("tag2");
        tagNames.add("tag3");

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

        Set<UUID> tagIds = new HashSet<>();
        tagIds.add(tag1.getId());
        tagIds.add(tag2.getId());

        when(tagRepository.findAllByIdIn(any(Set.class))).thenReturn(tags);

        // Act
        List<Tag> result = tagService.getTagsByIds(tagIds);


        // Assert
        assertThat(result.get(0).getId()).isEqualTo(tag1Id);
        assertThat(result.get(1).getId()).isEqualTo(tag2Id);
    }

    @Test
    public void getTagById_shouldReturnMatchingTag() {
        // Arrange
        UUID tagId = UUID.randomUUID();
        Tag tag = new Tag();
        tag.setId(tagId);

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
        Exception exception = new EntityNotFoundException("There are posts associated with tag: " + tagId);
        when(tagRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> tagService.getTagById(tagId));
        assertThat("There are posts associated with tag: " + tagId).isEqualTo(exception.getMessage());
    }

    @Test
    public void deleteTagById_shouldDeleteTag() {
        // Arrange
        UUID tagId = UUID.randomUUID();
        Tag tag = new Tag();
        tag.setId(tagId);

        doNothing().when(tagRepository).deleteById(tagId);

        // Arrange
        tagService.deleteTag(tagId);

        // Assert | Verify
        verify(tagRepository, times(1)).deleteById(tagId);
    }

    @Test
    public void deleteTagById_shouldThrowIllegalStateException() {
        // Arrange
        UUID tagId = UUID.randomUUID();
        Tag tag = new Tag();
        tag.setId(tagId);
        Post post = new Post();
        List<Post> posts = List.of(post);
        tag.setPosts(posts);

        Exception exception = new IllegalStateException("There are posts associated with tag: " + tagId);

        when(tagRepository.findById(any(UUID.class))).thenReturn(Optional.of(tag));

        // Arrange & Assert
        assertThrows(IllegalStateException.class, () -> tagService.deleteTag(tagId));
        assertThat("There are posts associated with tag: " + tagId).isEqualTo(exception.getMessage());
    }
}