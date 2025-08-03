package com.phirom_02.blog_api.controllers;

import com.phirom_02.blog_api.IntegrationTest;
import com.phirom_02.blog_api.config.TestSecurityConfig;
import com.phirom_02.blog_api.domain.dtos.CreateTagPayload;
import com.phirom_02.blog_api.domain.entities.Tag;
import com.phirom_02.blog_api.repository.TagRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
@Transactional
class TagControllerIntTest extends IntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    TagRepository tagRepository;

    @Autowired
    TestRestTemplate testRestTemplate;

    @BeforeAll
    @Rollback
    void setUp() {
        Tag tag1 = Tag.builder().name("Tag 1").build();
        Tag tag2 = Tag.builder().name("Tag 2").build();
        Tag tag3 = Tag.builder().name("Tag 3").build();
        Tag tag4 = Tag.builder().name("Tag 4").build();

        tagRepository.saveAll(List.of(tag1, tag2, tag3, tag4));
    }

    @Test
    public void getAllTags_shouldReturnAllTags() {
        // Arrange & Act
        ResponseEntity<List> response = testRestTemplate.getForEntity("/api/v1/tags", List.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).size()).isEqualTo(4);
    }

    @Test
    @Rollback
    public void createTags_shouldReturnTagsAfterCreate() {
        // Arrange
        CreateTagPayload payload = CreateTagPayload.builder()
                .names(Set.of("Tag A", "Tag B", "Tag C"))
                .build();

        // Act
        ResponseEntity<List> response = testRestTemplate.postForEntity("/api/v1/tags", payload, List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).size()).isEqualTo(3);
    }

    @Test
    @Rollback
    public void deleteTag_shouldDeleteTag() {
        // Arrange
        String tagId = tagRepository.findByName("Tag 1").getId().toString();

        // Act
        ResponseEntity<Void> response = testRestTemplate.exchange("/api/v1/tags/" + tagId, HttpMethod.DELETE, null, Void.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}