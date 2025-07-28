package com.phirom_02.blog_api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phirom_02.blog_api.domain.PostStatus;
import com.phirom_02.blog_api.domain.entities.Category;
import com.phirom_02.blog_api.domain.entities.Post;
import com.phirom_02.blog_api.domain.entities.Tag;
import com.phirom_02.blog_api.domain.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.asm.TypeReference;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

@Component
@RequiredArgsConstructor
public class PostDataLoader {

    private final ObjectMapper objectMapper;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public void load() {
        Random random = new Random();
        String postsJson = "/data/posts.json";
        List<Post> posts;

        try (InputStream inputStream = TypeReference.class.getResourceAsStream(postsJson)) {
            PostList postList = objectMapper.readValue(inputStream, PostList.class);
            posts = postList.posts.stream().map(dto -> {
                Post post = new Post();
                post.setTitle(dto.title);
                post.setContent(dto.content);
                return post;
            }).toList();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load posts from " + postsJson, e);
        }


        List<User> users = userRepository.findAll();
        List<Category> categories = categoryRepository.findAll();
        List<Tag> tags = tagRepository.findAll();

        posts.forEach(post -> {
            User author = users.get(random.nextInt(users.size()));
            Category category = categories.get(random.nextInt(categories.size()));
            Set<Tag> randomTags = new HashSet<>();
            Collections.shuffle(tags);
            randomTags.addAll(tags.subList(0, 2)); // pick 2 random tags

            post.setAuthor(author);
            post.setCategory(category);
            post.setTags(randomTags);

            // Optional: Add reading time and status
            post.setStatus(PostStatus.PUBLISHED);
            post.setReadingTime(3 + random.nextInt(8)); // 3–10 mins
        });

        postRepository.saveAll(posts);
    }

    private record PostSeedDto(String title, String content) {
    }

    private record PostList(List<PostSeedDto> posts) {
    }
}
