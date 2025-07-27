package com.phirom_02.blog_api.controllers;

import com.phirom_02.blog_api.domain.dtos.CategoryResponse;
import com.phirom_02.blog_api.domain.dtos.CreateCategoryPayload;
import com.phirom_02.blog_api.domain.entities.Category;
import com.phirom_02.blog_api.mappers.CategoryMapper;
import com.phirom_02.blog_api.service.CategoryService;
import com.phirom_02.blog_api.swagger.SwaggerTag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.phirom_02.blog_api.swagger.SwaggerTags.CATEGORIES;


/**
 * REST controller for managing categories in the blog application.
 */
@RestController
@RequestMapping(path = "/api/v1/categories")
@RequiredArgsConstructor
@SwaggerTag(
        name = CATEGORIES,
        description = "Operations for managing blog categories including retrieval, creation, and deletion"
)
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    /**
     * Retrieves a list of all categories.
     *
     * @return a {@link ResponseEntity} containing a list of {@link CategoryResponse} objects and HTTP status 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories()
                .stream().map(categoryMapper::toResponseCategory).toList();
        return ResponseEntity.ok(categories);
    }

    /**
     * Creates a new category.
     *
     * @param payload the payload containing the category data to be created
     * @return a {@link ResponseEntity} containing the created {@link CategoryResponse} object and HTTP status 201 (Created)
     */
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody @Valid CreateCategoryPayload payload) {
        Category categoryToCreate = categoryMapper.toEntity(payload);
        Category createdCategory = categoryService.createCategory(categoryToCreate);
        CategoryResponse categoryResponse = categoryMapper.toResponseCategory(createdCategory);
        return new ResponseEntity<>(categoryResponse, HttpStatus.CREATED);
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id the UUID of the category to be deleted
     * @return a {@link ResponseEntity} with HTTP status 204 (No Content) if deletion is successful
     */
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
