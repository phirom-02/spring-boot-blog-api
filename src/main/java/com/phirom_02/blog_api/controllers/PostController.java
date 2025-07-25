package com.phirom_02.blog_api.controllers;

import com.phirom_02.blog_api.domain.dtos.PostResponse;
import com.phirom_02.blog_api.domain.entities.Post;
import com.phirom_02.blog_api.mappers.PostMapper;
import com.phirom_02.blog_api.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
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
}
