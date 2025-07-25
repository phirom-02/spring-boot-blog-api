package com.phirom_02.blog_api.service;

import com.phirom_02.blog_api.domain.dtos.CreatePostDto;
import com.phirom_02.blog_api.domain.entities.Post;
import com.phirom_02.blog_api.domain.entities.User;

import java.util.List;
import java.util.UUID;

public interface PostService {

    List<Post> getAllPosts(UUID categoryId, UUID tagId);

    List<Post> getAllDraftedPosts(UUID tagId);

    Post createPost(User user, CreatePostDto dto);
}
