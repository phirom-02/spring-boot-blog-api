package com.phirom_02.blog_api.controllers;

import com.phirom_02.blog_api.domain.dtos.ResponseCategoryDto;
import com.phirom_02.blog_api.mappers.CategoryMapper;
import com.phirom_02.blog_api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
