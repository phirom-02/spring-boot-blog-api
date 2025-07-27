package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.domain.entities.Tag;
import com.phirom_02.blog_api.repository.TagRepository;
import com.phirom_02.blog_api.service.TagService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for tag-related business logic.
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    /**
     * Retrieves all tags from the repository including their associated posts
     * for calculating post counts.
     *
     * @return a list of all tags
     */
    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAllWithPostCount();
    }

    /**
     * Creates new tags if they do not already exist in the database.
     * Existing tag names will be ignored.
     *
     * @param tagNames the set of tag names to create
     * @return a list of newly created tags
     */
    @Override
    public List<Tag> createTags(Set<String> tagNames) {
        // Fetch existing tags from the database
        List<Tag> existingTags = tagRepository.findAllByNameIn(tagNames);

        // Extract existing tag names for filtering
        Set<String> existingTagNames = existingTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        // Filter and map only new tag names to Tag entities
        List<Tag> newTags = tagNames.stream()
                .filter(name -> !existingTagNames.contains(name))
                .map(name -> Tag.builder()
                        .name(name)
                        .posts(new ArrayList<>())
                        .build())
                .toList();

        // Save new tags if any and return them
        List<Tag> createdTags = new ArrayList<>();
        if (!newTags.isEmpty()) {
            createdTags = tagRepository.saveAll(newTags);
        }

        return createdTags;
    }

    /**
     * Retrieves a list of tags based on their UUIDs.
     *
     * @param ids the set of tag IDs
     * @return the list of corresponding tag entities
     */
    @Override
    public List<Tag> getTagsByIds(Set<UUID> ids) {
        return tagRepository.findAllByIdIn(ids);
    }

    /**
     * Retrieves a tag by its ID.
     *
     * @param id the tag's UUID
     * @return the found tag entity
     * @throws EntityNotFoundException if no tag is found
     */
    @Override
    public Tag getTagById(UUID id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with id: " + id));
    }

    /**
     * Deletes a tag by its ID only if no posts are associated with it.
     *
     * @param id the tag's UUID
     * @throws IllegalStateException if the tag has associated posts
     */
    @Override
    @Transactional
    public void deleteTag(UUID id) {
        tagRepository.findById(id).ifPresent(tag -> {
            if (!tag.getPosts().isEmpty()) {
                throw new IllegalStateException("There are posts associated with tag: " + id);
            }
        });
        tagRepository.deleteById(id);
    }
}
