package com.phirom_02.blog_api.controllers;

import com.phirom_02.blog_api.domain.dtos.CreateCategoryPayload;
import com.phirom_02.blog_api.domain.dtos.ResponseCategoryDto;
import com.phirom_02.blog_api.domain.entities.Category;
import com.phirom_02.blog_api.mappers.CategoryMapper;
import com.phirom_02.blog_api.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public ResponseEntity<List<ResponseCategoryDto>> getCategories() {
        List<ResponseCategoryDto> categories = categoryService.getCategories()
                .stream().map(categoryMapper::toResponseCategoryDto).toList();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<ResponseCategoryDto> createCategory(@RequestBody @Valid CreateCategoryPayload payload) {
        Category categoryToCreate = categoryMapper.toEntity(payload);
        Category createdCategory = categoryService.createCategory(categoryToCreate);
        ResponseCategoryDto responseCategoryDto = categoryMapper.toResponseCategoryDto(createdCategory);

        return new ResponseEntity<>(
                responseCategoryDto,
                HttpStatus.CREATED
        );
    }
}
