package com.phirom_02.blog_api.controllers;

import com.phirom_02.blog_api.domain.dtos.*;
import com.phirom_02.blog_api.domain.entities.Post;
import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.mappers.PostMapper;
import com.phirom_02.blog_api.service.PostService;
import com.phirom_02.blog_api.service.UserService;
import com.phirom_02.blog_api.swagger.SwaggerTag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.phirom_02.blog_api.swagger.SwaggerTags.POSTS;


/**
 * REST controller for managing blog posts.
 */
@RestController
@RequestMapping(path = "/api/v1/posts")
@RequiredArgsConstructor
@SwaggerTag(
        name = POSTS,
        description = "Operations for managing blog posts including retrieval, creation, modification, and deletion"
)
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final PostMapper postMapper;

    /**
     * Retrieves a list of all blog posts. Optionally filters by category or tag.
     *
     * @param categoryId the UUID of the category to filter by (optional)
     * @param tagId      the UUID of the tag to filter by (optional)
     * @return a {@link ResponseEntity} containing a list of {@link PostResponse} objects and HTTP status 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID tagId
    ) {
        List<Post> posts = postService.getAllPosts(categoryId, tagId);
        List<PostResponse> postResponses = posts.stream()
                .map(postMapper::toPostResponse)
                .toList();

        return ResponseEntity.ok(postResponses);
    }

    /**
     * Retrieves a list of all drafted blog posts for a specific user.
     *
     * @param userId the UUID of the user whose drafted posts are to be retrieved
     * @return a {@link ResponseEntity} containing a list of {@link PostResponse} objects and HTTP status 200 (OK)
     */
    @GetMapping(path = "/drafts")
    public ResponseEntity<List<PostResponse>> getAllDraftedPosts(@RequestAttribute UUID userId) {
        List<Post> draftedPostsposts = postService.getAllDraftedPosts(userId);
        List<PostResponse> postResponses = draftedPostsposts.stream()
                .map(postMapper::toPostResponse)
                .toList();

        return ResponseEntity.ok(postResponses);
    }

    /**
     * Retrieves a single blog post by its ID.
     *
     * @param id the UUID of the post to retrieve
     * @return a {@link ResponseEntity} containing the {@link PostResponse} object and HTTP status 200 (OK)
     */
    @GetMapping(path = "/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable UUID id) {
        Post post = postService.getPostById(id);
        PostResponse postResponse = postMapper.toPostResponse(post);

        return ResponseEntity.ok(postResponse);
    }

    /**
     * Creates a new blog post.
     *
     * @param payload the payload containing the post data to be created
     * @param userId  the UUID of the user creating the post
     * @return a {@link ResponseEntity} containing the created {@link PostResponse} object and HTTP status 201 (Created)
     */
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestBody @Valid CreatePostPayload payload,
            @RequestAttribute UUID userId
    ) {
        User user = userService.findUserById(userId);
        CreatePostDto dto = postMapper.toCreatePostDto(payload);
        Post post = postService.createPost(user, dto);
        PostResponse postResponse = postMapper.toPostResponse(post);

        return new ResponseEntity<>(postResponse, HttpStatus.CREATED);
    }

    /**
     * Updates an existing blog post.
     *
     * @param id      the UUID of the post to update
     * @param payload the payload containing the updated post data
     * @return a {@link ResponseEntity} containing the updated {@link PostResponse} object and HTTP status 200 (OK)
     */
    @PutMapping(path = "/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable UUID id,
            @RequestBody @Valid UpdatePostPayload payload
    ) {
        UpdatePostDto dto = postMapper.toUpdatePostDto(payload);
        Post post = postService.updatePost(id, dto);
        PostResponse postResponse = postMapper.toPostResponse(post);

        return ResponseEntity.ok(postResponse);
    }


    /**
     * Deletes a blog post by its ID.
     *
     * @param id the UUID of the post to delete
     * @return a {@link ResponseEntity} with HTTP status 204 (No Content) if the deletion is successful
     */
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
