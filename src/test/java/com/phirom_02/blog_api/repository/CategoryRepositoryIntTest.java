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
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestDataHelper.class)
@Transactional
class CategoryRepositoryIntTest extends IntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16:9");

    @Autowired
    TestDataHelper testDataHelper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @BeforeEach
    @Rollback
    void setUp() {
        User user = testDataHelper.createUser(null, null);
        Category category = testDataHelper.createCategory("Java");
        Tag tag = testDataHelper.createTag("Spring boot");
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        testDataHelper.createPost(
                "Test",
                "Test contents",
                PostStatus.PUBLISHED,
                user,
                category,
                tags
        );
    }

    @Test
    public void findAllWithPostCount_shouldRetrieveAllWithPostCount() {
        List<Category> categories = categoryRepository.findAllWithPostCount();
        assertThat(categories.size()).isEqualTo(1);
    }
}