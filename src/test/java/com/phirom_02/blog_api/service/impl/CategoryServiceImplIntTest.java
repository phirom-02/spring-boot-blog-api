package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.IntegrationTest;
import com.phirom_02.blog_api.domain.PostStatus;
import com.phirom_02.blog_api.domain.entities.Category;
import com.phirom_02.blog_api.domain.entities.Tag;
import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.repository.CategoryRepository;
import com.phirom_02.blog_api.repository.TagRepository;
import com.phirom_02.blog_api.service.CategoryService;
import com.phirom_02.blog_api.util.TestDataHelper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
@Import(TestDataHelper.class)
@Transactional
class CategoryServiceImplIntTest extends IntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16:9");

    @Autowired
    private TestDataHelper testDataHelper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TagRepository tagRepository;

    @BeforeEach
    @Rollback
    void setUp() {
        Category category1 = Category.builder().name("category-1").posts(new ArrayList<>()).build();
        Category category2 = Category.builder().name("category-2").posts(new ArrayList<>()).build();
        Category category3 = Category.builder().name("category-3").posts(new ArrayList<>()).build();
        Category category4 = Category.builder().name("category-4").posts(new ArrayList<>()).build();
        List<Category> categories = List.of(category1, category2, category3, category4);
        categoryRepository.saveAll(categories);

        User user = testDataHelper.createUser("John Smitht", "john.smith@example.com");
        Tag tag = testDataHelper.createTag("tag-A");
        Set<Tag> postTags = Set.of(tag);
        testDataHelper.createPost(
                "test1",
                "test1 content",
                PostStatus.PUBLISHED,
                user,
                category1,
                postTags
        );
    }

    @Test
    public void getAllCategories_shouldRetrieveAllCategories() {
        // Arrange & Act
        List<Category> categories = categoryService.getAllCategories();

        // Assert
        assertThat(categories.size()).isEqualTo(4);
    }

    @Test
    public void getCategoryById_shouldRetrieveAMatchingCategory() {
        // Arrange
        UUID categoryId = testDataHelper.getCategoryByName("category-1").getId();

        // Act
        Category category = categoryService.getCategoryById(categoryId);

        // Assert
        assertThat(category.getId()).isEqualTo(categoryId);
    }

    @Test
    public void getCategoryById_shouldThrowEntityNotFoundException() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        Exception exception = new EntityNotFoundException("Category not found with id: " + categoryId);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> categoryService.getCategoryById(categoryId));
        assertThat("Category not found with id: " + categoryId).isEqualTo(exception.getMessage());
    }

    @Test
    @Rollback
    public void createCategory_shouldReturnACategoryAfterCreate() {
        // Arrange
        Category categoryToCreate = Category.builder().name("category-A").posts(new ArrayList<>()).build();

        // Act
        Category category = categoryService.createCategory(categoryToCreate);

        // Assert
        assertThat(category.getName()).isEqualTo(categoryToCreate.getName());
    }

    @Test
    public void createCategory_shouldThrowsIllegalArgumentException() {
        // Arrange
        String categoryName = "category-1";
        Category categoryToCreate = Category.builder().name(categoryName).posts(new ArrayList<>()).build();
        Exception exception = new IllegalArgumentException("Category name already exists: " + categoryName);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> categoryService.createCategory(categoryToCreate));
        assertThat("Category name already exists: " + categoryName).isEqualTo(exception.getMessage());
    }

    @Test
    @Rollback
    public void deleteCategory_shouldDeleteACategory() {
        // Arrange
        UUID categoryId = testDataHelper.getCategoryByName("category-2").getId();

        // Act
        categoryService.deleteCategory(categoryId);

        // Assert
        assertThat(tagRepository.findById(categoryId)).isNotPresent();
    }

    @Test
    public void deleteCategory_shouldThrowIllegalStateException() {
        // Arrange
        UUID categoryId = testDataHelper.getCategoryByName("category-1").getId();
        Exception exception = new IllegalStateException("There are posts associated with category: " + categoryId);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> categoryService.deleteCategory(categoryId));
        assertThat("There are posts associated with category: " + categoryId).isEqualTo(exception.getMessage());
    }
}