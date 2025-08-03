package com.phirom_02.blog_api.util;

import com.phirom_02.blog_api.domain.PostStatus;
import com.phirom_02.blog_api.domain.entities.Category;
import com.phirom_02.blog_api.domain.entities.Post;
import com.phirom_02.blog_api.domain.entities.Tag;
import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.repository.CategoryRepository;
import com.phirom_02.blog_api.repository.PostRepository;
import com.phirom_02.blog_api.repository.TagRepository;
import com.phirom_02.blog_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TestDataHelper {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(String name, String email) {
        return userRepository.save(User.builder().email(email != null ? email : "john.smith@example.com").name(name != null ? name : "John Smith").password(passwordEncoder.encode("@#password")).build());
    }

    public Category createCategory(String name) {
        return categoryRepository.save(new Category(null, name, new ArrayList<>()));
    }

    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    public Tag getTagByName(String name) {
        return tagRepository.findByName(name);
    }

    public Tag createTag(String name) {
        return tagRepository.save(new Tag(null, name, new ArrayList<>()));
    }

    public Post createPost(String title, String content, PostStatus status, User author, Category category, Set<Tag> tags) {
        Post post = Post.builder().title(title).content(content).status(status != null ? status : PostStatus.PUBLISHED).readingTime(5).author(author).category(category).tags(tags).build();

        for (Tag tag : tags) {
            tag.getPosts().add(post);
        }

        category.getPosts().add(post);

        return postRepository.save(post);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
