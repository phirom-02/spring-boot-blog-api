package com.phirom_02.blog_api.repository;

import com.phirom_02.blog_api.domain.entities.Tag;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.Rollback;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class TagsRepositoryIntTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16:9");

    @Autowired
    private TagRepository tagRepository;

    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    Tag savedTag;

    @BeforeEach
    @Rollback
    void setUp() {
        savedTag = Tag.builder()
                .name("Spring boot")
                .build();

        tagRepository.save(savedTag);
    }

    @Test
    public void findAllWithPostCount_shouldFindAllWithPostCount() {
        List<Tag> tags = tagRepository.findAllWithPostCount();
        assertThat(tags.size()).isEqualTo(1);
    }

    @Test
    public void findAllByNameIn_shouldRetrieveAllWithNameIn() {
        Set<String> tagNames = Set.of("Spring boot");

        List<Tag> tags = tagRepository.findAllByNameIn(tagNames);
        assertThat(tags.size()).isEqualTo(1);
        assertThat(tags.getFirst().getName()).isEqualTo("Spring boot");
    }

    @Test
    public void findAllByIdIn_shouldRetrieveAllWithIdIn() {
        UUID tagId = savedTag.getId();
        Set<UUID> ids = Set.of(tagId);
        List<Tag> tags = tagRepository.findAllByIdIn(ids);

        assertThat(tags.size()).isEqualTo(1);
        assertThat(tags.getFirst().getId()).isEqualTo(tagId);
    }
}