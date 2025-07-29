package com.phirom_02.blog_api.repository;

import com.phirom_02.blog_api.IntegrationTest;
import com.phirom_02.blog_api.domain.PostStatus;
import com.phirom_02.blog_api.domain.entities.Category;
import com.phirom_02.blog_api.domain.entities.Tag;
import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.util.TestDataHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestDataHelper.class)
@Transactional
class PostRepositoryIntTest extends IntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16:9");

    @Autowired
    TestDataHelper testDataHelper;

    @Autowired
    private PostRepository postRepository;

    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @BeforeEach
    @Rollback
    void setUp() {
        User user = testDataHelper.createUser("John Smith", "john.smith@example.com");
        Category category1 = testDataHelper.createCategory("category-1");
        Category category2 = testDataHelper.createCategory("category-2");

        Tag tag1 = testDataHelper.createTag("tag-1");
        Tag tag2 = testDataHelper.createTag("tag-2");
        Tag tag3 = testDataHelper.createTag("tag-3");
        Tag tag4 = testDataHelper.createTag("tag-4");

        Set<Tag> tagsForPost1 = new HashSet<>();
        tagsForPost1.add(tag1);
        tagsForPost1.add(tag2);
        tagsForPost1.add(tag4);

        Set<Tag> tagsForPost2 = new HashSet<>();
        tagsForPost2.add(tag3);
        tagsForPost2.add(tag4);

        testDataHelper.createPost(
                "Test1",
                "Test contents 1",
                PostStatus.PUBLISHED,
                user,
                category1,
                tagsForPost1
        );
        testDataHelper.createPost(
                "Test2",
                "Test contents 2",
                PostStatus.PUBLISHED,
                user,
                category2,
                tagsForPost2
        );
        testDataHelper.createPost(
                "Test3",
                "Test contents 3",
                PostStatus.DRAFT,
                user,
                category1,
                tagsForPost2
        );
    }

    @Test
    public void findAllByStatusAndCategoryAndTagsContaining_shouldRetrieveMatchingPosts() {
        Category category = testDataHelper.getCategoryByName("category-1");
        Tag tag = testDataHelper.getTagByName("tag-4");
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
        Category category = testDataHelper.getCategoryByName("category-1");
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
        Tag tag = testDataHelper.getTagByName("tag-3");
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
        Optional<User> user = testDataHelper.getUserByEmail("john.smith@example.com");
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