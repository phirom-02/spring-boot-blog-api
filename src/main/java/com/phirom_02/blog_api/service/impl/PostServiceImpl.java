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
import com.phirom_02.blog_api.service.PostService;
import com.phirom_02.blog_api.service.TagService;
import com.phirom_02.blog_api.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link PostService} interface for managing blog posts.
 */
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final TagService tagService;

    private static final int WORDS_PER_MINUTE = 200;

    /**
     * Fetches all published posts, optionally filtered by category and/or tag.
     * If neither filter is provided, returns all published posts.
     *
     * @param categoryId the UUID of the category to filter by (optional)
     * @param tagId      the UUID of the tag to filter by (optional)
     * @return a list of {@link Post} entities matching the specified filters
     */
    public List<Post> getAllPosts(UUID categoryId, UUID tagId) {
        // If both category and tag are provided, filter by both
        if (categoryId != null && tagId != null) {
            Category category = categoryService.getCategoryById(categoryId);
            Tag tag = tagService.getTagById(tagId);

            return postRepository.findAllByStatusAndCategoryAndTagsContaining(
                    PostStatus.PUBLISHED,
                    category,
                    tag
            );
        }

        // If only category is provided, filter by category
        if (categoryId != null) {
            Category category = categoryService.getCategoryById(categoryId);

            return postRepository.findAllByStatusAndCategory(
                    PostStatus.PUBLISHED,
                    category
            );
        }

        // If only tag is provided, filter by tag
        if (tagId != null) {
            Tag tag = tagService.getTagById(tagId);
            return postRepository.findAllByStatusAndTagsContaining(
                    PostStatus.PUBLISHED,
                    tag
            );
        }

        // If neither category nor tag is provided, return all published posts
        return postRepository.findAllByStatus(PostStatus.PUBLISHED);
    }

    /**
     * Fetches all drafted posts authored by a user.
     *
     * @param userId the UUID of the user whose drafted posts should be fetched
     * @return a list of drafted {@link Post} entities authored by the user
     */
    @Override
    public List<Post> getAllDraftedPosts(UUID userId) {
        // Find the user by their ID
        User author = userService.findUserById(userId);
        // Fetch and return the drafted posts by the user
        return postRepository.findAllByAuthorAndStatus(author, PostStatus.DRAFT);
    }

    /**
     * Fetches a post by its ID.
     *
     * @param postId the UUID of the post to fetch
     * @return the {@link Post} entity with the specified ID
     * @throws EntityNotFoundException if the post with the given ID does not exist
     */
    @Override
    public Post getPostById(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));
    }

    /**
     * Creates a new post.
     *
     * @param user          the user who is creating the post
     * @param createPostDto the data transfer object containing the post details
     * @return the created {@link Post} entity
     * @throws IllegalArgumentException if the post title already exists
     */
    @Override
    public Post createPost(User user, CreatePostDto createPostDto) {
        // Create a new Post object and set its properties based on the DTO
        Post post = new Post();
        post.setTitle(createPostDto.getTitle());
        post.setContent(createPostDto.getContent());
        post.setStatus(createPostDto.getStatus());
        post.setAuthor(user); // Set the author of the post
        post.setReadingTime(calculateReadingTime(createPostDto.getContent())); // Calculate reading time based on content

        // Set the category of the post
        Category category = categoryService.getCategoryById(createPostDto.getCategoryId());
        post.setCategory(category);

        // Set the tags for the post based on tag IDs provided in the DTO
        Set<UUID> tagIds = createPostDto.getTagIds();
        List<Tag> tags = tagService.getTagsByIds(tagIds);
        post.setTags(new HashSet<>(tags));

        // Save the post to the repository
        return postRepository.save(post);
    }

    /**
     * Updates an existing post.
     *
     * @param id  the UUID of the post to update
     * @param dto the data transfer object containing the updated post details
     * @return the updated {@link Post} entity
     * @throws EntityNotFoundException if the post with the given ID does not exist
     */
    @Override
    public Post updatePost(UUID id, UpdatePostDto dto) {
        // Find the existing post by ID
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No"));

        // Update post properties
        existingPost.setId(dto.getId());
        existingPost.setTitle(dto.getTitle());
        existingPost.setContent(dto.getContent());
        existingPost.setStatus(PostStatus.PUBLISHED);
        existingPost.setReadingTime(calculateReadingTime(dto.getContent()));

        // Update the category of the post if it has changed
        UUID newPostCategoryId = dto.getCategoryId();
        if (!existingPost.getCategory().getId().equals(newPostCategoryId)) {
            Category newCategory = categoryService.getCategoryById(newPostCategoryId);
            existingPost.setCategory(newCategory);
        }

        // Update the tags of the post if they have changed
        Set<UUID> existingTagIds = existingPost.getTags()
                .stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
        Set<UUID> newPostTagIds = dto.getTagIds();
        if (!existingTagIds.equals(newPostTagIds)) {
            List<Tag> newTags = tagService.getTagsByIds(existingTagIds);
            existingPost.setTags(new HashSet<>(newTags));
        }

        // Save and return the updated post
        return postRepository.save(existingPost);
    }

    /**
     * Deletes a post by its ID.
     *
     * @param id the UUID of the post to delete
     * @throws EntityNotFoundException if the post with the given ID does not exist
     */
    @Override
    public void deletePost(UUID id) {
        // Ensure the post exists before attempting to delete it
        getPostById(id);
        // Delete the post from the repository
        postRepository.deleteById(id);
    }

    /**
     * Calculates the reading time for a given content based on an average reading speed of 200 words per minute.
     *
     * @param content the content of the post
     * @return the estimated reading time in minutes
     */
    private int calculateReadingTime(String content) {
        // If content is empty, return 0 reading time
        if (content == null || content.isEmpty()) {
            return 0;
        }

        // Split the content into words and calculate the word count
        int wordCount = content.trim().split("\\s+").length;
        // Calculate and return the reading time (rounded up to the nearest minute)
        return (int) Math.ceil((double) wordCount / WORDS_PER_MINUTE);
    }
}
