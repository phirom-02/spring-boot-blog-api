package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.domain.PostStatus;
import com.phirom_02.blog_api.domain.dtos.CreatePostDto;
import com.phirom_02.blog_api.domain.entities.Category;
import com.phirom_02.blog_api.domain.entities.Post;
import com.phirom_02.blog_api.domain.entities.Tag;
import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.repository.PostRepository;
import com.phirom_02.blog_api.service.CategoryService;
import com.phirom_02.blog_api.service.PostService;
import com.phirom_02.blog_api.service.TagService;
import com.phirom_02.blog_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

    private int calculateReadingTime(String content) {
        if (content == null || content.isEmpty()) {
            return 0;
        }

        int wordCount = content.trim().split("\\s+").length;
        return (int) Math.ceil((double) wordCount / WORDS_PER_MINUTE);
    }
}
