package com.phirom_02.blog_api.service;

import com.phirom_02.blog_api.domain.dtos.CreatePostDto;
import com.phirom_02.blog_api.domain.dtos.UpdatePostDto;
import com.phirom_02.blog_api.domain.entities.Post;
import com.phirom_02.blog_api.domain.entities.User;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for handling operations related to blog posts.
 */
public interface PostService {

    /**
     * Fetches a list of posts filtered by category and/or tag.
     * Retrieves all posts within the specified category and/or with the specified tag.
     *
     * @param categoryId the ID of the category to filter by (optional)
     * @param tagId      the ID of the tag to filter by (optional)
     * @return a list of {@link Post} objects matching the filters
     */
    List<Post> getAllPosts(UUID categoryId, UUID tagId);

    /**
     * Fetches a list of all draft posts filtered by tag.
     * Retrieves all posts that are in the 'draft' status and match the specified tag.
     *
     * @param tagId the ID of the tag to filter by
     * @return a list of {@link Post} objects in draft status and matching the tag
     */
    List<Post> getAllDraftedPosts(UUID tagId);

    /**
     * Retrieves a specific post by its unique identifier.
     *
     * @param postId the ID of the post to retrieve
     * @return the {@link Post} object with the specified ID
     */
    Post getPostById(UUID postId);

    /**
     * Creates a new blog post based on the provided data transfer object (DTO) and user information.
     *
     * @param user the user creating the post
     * @param dto  the data transfer object containing the post details
     * @return the newly created {@link Post} object
     */
    Post createPost(User user, CreatePostDto dto);

    /**
     * Updates an existing post with new data.
     *
     * @param id  the ID of the post to update
     * @param dto the data transfer object containing the updated post details
     * @return the updated {@link Post} object
     */
    Post updatePost(UUID id, UpdatePostDto dto);

    /**
     * Deletes a specific post by its unique identifier.
     *
     * @param id the ID of the post to delete
     */
    void deletePost(UUID id);
}
