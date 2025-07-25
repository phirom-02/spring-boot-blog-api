package com.phirom_02.blog_api.controllers;

import com.phirom_02.blog_api.domain.dtos.CreateCategoryPayload;
import com.phirom_02.blog_api.domain.dtos.ResponseCategory;
import com.phirom_02.blog_api.domain.entities.Category;
import com.phirom_02.blog_api.mappers.CategoryMapper;
import com.phirom_02.blog_api.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public ResponseEntity<List<ResponseCategory>> getCategories() {
        List<ResponseCategory> categories = categoryService.getCategories()
                .stream().map(categoryMapper::toResponseCategoryDto).toList();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<ResponseCategory> createCategory(@RequestBody @Valid CreateCategoryPayload payload) {
        Category categoryToCreate = categoryMapper.toEntity(payload);
        Category createdCategory = categoryService.createCategory(categoryToCreate);
        ResponseCategory responseCategory = categoryMapper.toResponseCategoryDto(createdCategory);

        return new ResponseEntity<>(
                responseCategory,
                HttpStatus.CREATED
        );
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
