package com.phirom_02.blog_api.service;

import com.phirom_02.blog_api.domain.entities.Post;

import java.util.List;
import java.util.UUID;

public interface PostService {

    List<Post> getAllPosts(UUID categoryId, UUID tagId);
}
