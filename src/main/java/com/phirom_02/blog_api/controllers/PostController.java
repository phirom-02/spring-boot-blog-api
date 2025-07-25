package com.phirom_02.blog_api.controllers;

import com.phirom_02.blog_api.domain.dtos.*;
import com.phirom_02.blog_api.domain.entities.Post;
import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.mappers.PostMapper;
import com.phirom_02.blog_api.service.PostService;
import com.phirom_02.blog_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final PostMapper postMapper;

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPost(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID tagId
    ) {
        List<Post> posts = postService.getAllPosts(categoryId, tagId);
        List<PostResponse> postResponses = posts.stream()
                .map(postMapper::toPostResponse)
                .toList();

        return ResponseEntity.ok(postResponses);
    }

    @GetMapping(path = "/drafts")
    public ResponseEntity<List<PostResponse>> getAllDraftedPosts(@RequestAttribute UUID userId) {
        List<Post> draftedPostsposts = postService.getAllDraftedPosts(userId);
        List<PostResponse> postResponses = draftedPostsposts.stream()
                .map(postMapper::toPostResponse)
                .toList();

        return ResponseEntity.ok(postResponses);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable UUID id) {
        Post post = postService.getPostById(id);
        PostResponse postResponse = postMapper.toPostResponse(post);

        return ResponseEntity.ok(postResponse);
    }

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

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
