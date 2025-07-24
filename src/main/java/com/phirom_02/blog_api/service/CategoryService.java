package com.phirom_02.blog_api.service;

import com.phirom_02.blog_api.domain.entities.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getCategories();

    Category createCategory(Category categorytoCreate);
}
