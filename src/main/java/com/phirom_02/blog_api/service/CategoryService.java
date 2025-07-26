package com.phirom_02.blog_api.service;

import com.phirom_02.blog_api.domain.entities.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    List<Category> getAllCategories();

    Category getCategoryById(UUID id);

    Category createCategory(Category categoryToCreate);

    void deleteCategory(UUID id);
}
