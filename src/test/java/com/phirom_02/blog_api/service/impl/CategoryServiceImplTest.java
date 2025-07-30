package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.domain.entities.Category;
import com.phirom_02.blog_api.domain.entities.Post;
import com.phirom_02.blog_api.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @InjectMocks
    CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    List<Category> categories;

    @BeforeEach
    void setUp() {
        Category category1 = Category.builder().name("category1").build();
        Category category2 = Category.builder().name("category2").build();
        Category category3 = Category.builder().name("category3").build();
        categories = List.of(category1, category2, category3);
    }

    @Test
    public void getAllCategories_shouldRetrieveAllCategories() {
        // Arrange
        when(categoryRepository.findAllWithPostCount()).thenReturn(categories);

        // Act
        List<Category> result = categoryService.getAllCategories();

        // Assert
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void getCategoryById_shouldRetrieveAMatchingCategory() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        Category category = Category.builder().id(categoryId).build();

        when(categoryRepository.findById(any(UUID.class))).thenReturn(Optional.of(category));

        // Act
        Category result = categoryService.getCategoryById(categoryId);

        // Assert
        assertThat(result.getId()).isEqualTo(categoryId);
    }

    @Test
    public void getCategoryById_shouldThrowEntityNotFoundException() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        Exception exception = new EntityNotFoundException("Category not found with id: " + categoryId);
        when(categoryRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> categoryService.getCategoryById(categoryId));
        assertThat("Category not found with id: " + categoryId).isEqualTo(exception.getMessage());
    }

    @Test
    public void createCategory_shouldReturnACategoryAfterCreate() {
        // Arrange
        Category category = Category.builder().name("test").build();

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        Category result = categoryService.createCategory(category);

        // Assert
        assertThat(result.getName()).isEqualTo("test");
    }

    @Test
    public void createCategory_shouldThrowsIllegalArgumentException() {
        // Arrange
        String categoryName = "test";
        Category category = Category.builder().name(categoryName).build();
        Exception exception = new IllegalArgumentException("Category name already exists: " + categoryName);

        when(categoryRepository.existsByNameIgnoreCase(any(String.class))).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> categoryService.createCategory(category));
        assertThat("Category name already exists: " + categoryName).isEqualTo(exception.getMessage());
    }

    @Test
    public void deleteCategory_shouldDeleteACategory() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        Category category = Category.builder()
                .id(categoryId)
                .posts(new ArrayList<>())
                .build();

        when(categoryRepository.findById(any(UUID.class))).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).deleteById(categoryId);

        // Act
        categoryService.deleteCategory(categoryId);

        // Assert | Verify
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    public void deleteCategory_shouldThrowIllegalStateException() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        Post post = new Post();
        List<Post> posts = List.of(post);
        Category category = Category.builder()
                .id(categoryId)
                .posts(posts)
                .build();

        Exception exception = new IllegalStateException("There are posts associated with category: " + categoryId);

        when(categoryRepository.findById(any(UUID.class))).thenReturn(Optional.of(category));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> categoryService.deleteCategory(categoryId));
        assertThat("There are posts associated with category: " + categoryId).isEqualTo(exception.getMessage());
    }
}