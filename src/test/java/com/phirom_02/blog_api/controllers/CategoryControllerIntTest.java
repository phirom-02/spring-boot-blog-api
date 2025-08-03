package com.phirom_02.blog_api.controllers;

import com.phirom_02.blog_api.IntegrationTest;
import com.phirom_02.blog_api.config.TestSecurityConfig;
import com.phirom_02.blog_api.domain.dtos.CategoryResponse;
import com.phirom_02.blog_api.domain.dtos.CreateCategoryPayload;
import com.phirom_02.blog_api.domain.entities.Category;
import com.phirom_02.blog_api.repository.CategoryRepository;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
@Transactional
class CategoryControllerIntTest extends IntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TestRestTemplate testRestTemplate;

    @BeforeAll
    @Rollback
    void setUp() {
        Category category1 = Category.builder().name("Category 1").build();
        Category category2 = Category.builder().name("Category 2").build();
        Category category3 = Category.builder().name("Category 3").build();

        categoryRepository.saveAll(List.of(category1, category2, category3));
    }

    @Test
    public void getCategories_shouldReturnAllCategories() {
        // Arrange & Act
        ResponseEntity<List> response = testRestTemplate.getForEntity("/api/v1/categories", List.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).size()).isEqualTo(3);
    }

    @Test
    @Rollback
    public void createCategory_shouldReturnCategoryAfterCreate() {
        // Arrange
        CreateCategoryPayload payload = CreateCategoryPayload.builder()
                .name("Java")
                .build();

        // Act
        ResponseEntity<CategoryResponse> response = testRestTemplate.postForEntity("/api/v1/categories", payload, CategoryResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(Objects.requireNonNull(response.getBody()).getName()).isEqualTo("Java");
    }

    @Test
    @Rollback
    public void deleteCategory_shouldDeleteACategory() {
        // Arrange
        String categoryId = categoryRepository.findByName("Category 1").getId().toString();

        // Act
        ResponseEntity<Void> response = testRestTemplate.exchange("/api/v1/categories/" + categoryId, HttpMethod.DELETE, null, Void.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}