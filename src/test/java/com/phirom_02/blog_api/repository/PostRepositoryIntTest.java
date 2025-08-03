package com.phirom_02.blog_api.repository;

import com.phirom_02.blog_api.IntegrationTest;
import com.phirom_02.blog_api.domain.PostStatus;
import com.phirom_02.blog_api.domain.entities.Category;
import com.phirom_02.blog_api.domain.entities.Post;
import com.phirom_02.blog_api.domain.entities.Tag;
import com.phirom_02.blog_api.domain.entities.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.Rollback;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class PostRepositoryIntTest extends IntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16:9");


    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TagRepository tagRepository;


    @BeforeAll
    @Rollback
    void setUp() {

        User user = User.builder()
                .email("john.smith@example.com")
                .name("John Smith")
                .password("@#Password")
                .build();
        userRepository.save(user);

        Category category1 = Category.builder().name("category-1").build();
        Category category2 = Category.builder().name("category-2").build();
        categoryRepository.saveAll(List.of(category1, category2));

        Tag tag1 = tagRepository.save(Tag.builder().name("tag-1").build());
        Tag tag2 = tagRepository.save(Tag.builder().name("tag-2").build());
        Tag tag3 = tagRepository.save(Tag.builder().name("tag-3").build());
        Tag tag4 = tagRepository.save(Tag.builder().name("tag-4").build());

        Set<Tag> tagsForPost1 = new HashSet<>();
        tagsForPost1.add(tag1);
        tagsForPost1.add(tag2);
        tagsForPost1.add(tag4);

        Set<Tag> tagsForPost2 = new HashSet<>();
        tagsForPost2.add(tag3);
        tagsForPost2.add(tag4);

        Post post1 = Post.builder()
                .title("Test 1")
                .content("Test 1 content")
                .status(PostStatus.PUBLISHED)
                .author(user)
                .category(category1)
                .tags(tagsForPost1)
                .readingTime(1)
                .build();
        Post post2 = Post.builder()
                .title("Test 2")
                .content("Test 2 content")
                .status(PostStatus.PUBLISHED)
                .author(user)
                .category(category2)
                .tags(tagsForPost2)
                .readingTime(1)
                .build();
        Post post3 = Post.builder()
                .title("Test 3")
                .content("Test 3 content")
                .status(PostStatus.DRAFT)
                .author(user)
                .category(category1)
                .tags(tagsForPost2)
                .readingTime(1)
                .build();
        postRepository.saveAll(List.of(post1, post2, post3));
    }

    @Test
    public void findAllByStatusAndCategoryAndTagsContaining_shouldRetrieveMatchingPosts() {
        Category category = categoryRepository.findByName("category-1");
        Tag tag = tagRepository.findByName("tag-4");
        PostStatus status = PostStatus.PUBLISHED;

        var results = postRepository.findAllByStatusAndCategoryAndTagsContaining(status, category, tag);

        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(1);
        results.forEach(post -> {
            assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);
            assertThat(post.getCategory()).isEqualTo(category);
            assertThat(post.getTags().stream().toList().contains(tag)).isTrue();
        });
    }

    @Test
    public void findAllByStatusAndCategory_shouldRetrieveMatchingPosts() {
        Category category = categoryRepository.findByName("category-1");
        PostStatus status = PostStatus.PUBLISHED;

        var results = postRepository.findAllByStatusAndCategory(status, category);

        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(1);
        results.forEach(post -> {
            assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);
            assertThat(post.getCategory()).isEqualTo(category);
        });
    }

    @Test
    public void findAllByStatusAndTagsContaining_shouldRetrieveMatchingPosts() {
        Tag tag = tagRepository.findByName("tag-3");
        PostStatus status = PostStatus.PUBLISHED;
        var results = postRepository.findAllByStatusAndTagsContaining(status, tag);
        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(1);
        results.forEach(post -> {
            assertThat(post.getStatus()).isEqualTo(PostStatus.PUBLISHED);
            assertThat(post.getTags().stream().toList().contains(tag)).isTrue();
        });
    }

    @Test
    public void findAllByStatus_shouldRetrievePublishedPosts() {
        PostStatus status = PostStatus.PUBLISHED;
        var results = postRepository.findAllByStatus(status);
        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(2);
        results.forEach(post -> {
            assertThat(post.getStatus()).isEqualTo(status);
        });
    }

    @Test
    public void findAllByStatus_shouldRetrieveDraftPosts() {
        PostStatus status = PostStatus.DRAFT;
        var results = postRepository.findAllByStatus(status);
        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(1);
        results.forEach(post -> {
            assertThat(post.getStatus()).isEqualTo(status);
        });
    }

    @Test
    void findAllByAuthorAndStatus_shouldRetrievePublishedPost() {
        PostStatus status = PostStatus.PUBLISHED;
        Optional<User> user = userRepository.findByEmail("john.smith@example.com");
        User author = user.get();
        var results = postRepository.findAllByAuthorAndStatus(author, status);
        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(2);
        results.forEach(post -> {
            assertThat(post.getStatus()).isEqualTo(status);
            assertThat(author).isEqualTo(post.getAuthor());
        });
    }
}