package com.phirom_02.blog_api.service;

import com.phirom_02.blog_api.domain.entities.Tag;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service interface for managing tags related to blog posts.
 */
public interface TagService {

    /**
     * Retrieves all tags in the system.
     *
     * @return a list of all {@link Tag} objects in the system.
     */
    List<Tag> getAllTags();

    /**
     * Creates a set of tags based on the provided tag names.
     *
     * @param tagNames a set of tag names to be created.
     * @return a list of {@link Tag} objects created from the provided names.
     */
    List<Tag> createTags(Set<String> tagNames);

    /**
     * Retrieves a list of tags based on their unique identifiers.
     *
     * @param ids a set of tag IDs to search for.
     * @return a list of {@link Tag} objects that match the provided IDs.
     */
    List<Tag> getTagsByIds(Set<UUID> ids);

    /**
     * Retrieves a specific tag by its unique identifier.
     *
     * @param tagId the ID of the tag to retrieve.
     * @return the {@link Tag} object matching the provided ID.
     */
    Tag getTagById(UUID tagId);

    /**
     * Deletes a tag by its unique identifier.
     *
     * @param id the ID of the tag to delete.
     */
    void deleteTag(UUID id);
}
