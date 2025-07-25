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

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final TagService tagService;

    private static final int WORDS_PER_MINUTE = 200;

    public List<Post> getAllPosts(UUID categoryId, UUID tagId) {
        if (categoryId != null && tagId != null) {
            Category category = categoryService.getCategoryById(categoryId);
            Tag tag = tagService.getTagById(tagId);

            return postRepository.findAllByStatusAndCategoryAndTagsContaining(
                    PostStatus.PUBLISHED,
                    category,
                    tag
            );
        }

        if (categoryId != null) {
            Category category = categoryService.getCategoryById(categoryId);

            return postRepository.findAllByStatusAndCategory(
                    PostStatus.PUBLISHED,
                    category
            );
        }

        if (tagId != null) {
            Tag tag = tagService.getTagById(tagId);
            return postRepository.findAllByStatusAndTagsContaining(
                    PostStatus.PUBLISHED,
                    tag
            );
        }

        return postRepository.findAllByStatus(PostStatus.PUBLISHED);
    }

    @Override
    public List<Post> getAllDraftedPosts(UUID userId) {
        User author = userService.findUserById(userId);
        return postRepository.findAllByAuthorAndStatus(author, PostStatus.DRAFT);
    }

    @Override
    public Post getPostById(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));
    }

    @Override
    public Post createPost(User user, CreatePostDto createPostDto) {
        Post post = new Post();
        post.setTitle(createPostDto.getTitle());
        post.setContent(createPostDto.getContent());
        post.setStatus(PostStatus.PUBLISHED);
        post.setAuthor(user);
        post.setReadingTime(calculateReadingTime(createPostDto.getContent()));

        Category category = categoryService.getCategoryById(createPostDto.getCategoryId());
        post.setCategory(category);

        Set<UUID> tagIds = createPostDto.getTagIds();
        List<Tag> tags = tagService.getTagsByIds(tagIds);
        post.setTags(new HashSet<>(tags));

        return postRepository.save(post);
    }

    @Override
    public Post updatePost(UUID id, UpdatePostDto dto) {
        Post existingPost = postRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("No"));

        existingPost.setId(dto.getId());
        existingPost.setTitle(dto.getTitle());
        existingPost.setContent(dto.getContent());
        existingPost.setStatus(PostStatus.PUBLISHED);
        existingPost.setReadingTime(calculateReadingTime(dto.getContent()));

        UUID newPostCategoryId = dto.getCategoryId();
        if (!existingPost.getCategory().equals(newPostCategoryId)) {
            Category newCategory = categoryService.getCategoryById(newPostCategoryId);
            existingPost.setCategory(newCategory);
        }

        Set<UUID> existingTagIds = existingPost.getTags()
                .stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
        Set<UUID> newPostTagIds = dto.getTagIds();
        if (!existingTagIds.equals(existingTagIds)) {
            List<Tag> newTags = tagService.getTagsByIds(existingTagIds);
            existingPost.setTags(new HashSet<>(newTags));
        }

        return postRepository.save(null);
    }

    @Override
    public void deletePost(UUID id) {
        getPostById(id);
        postRepository.deleteById(id);
    }

    private int calculateReadingTime(String content) {
        if (content == null || content.isEmpty()) {
            return 0;
        }

        int wordCount = content.trim().split("\\s+").length;
        return (int) Math.ceil((double) wordCount / WORDS_PER_MINUTE);
    }
}
