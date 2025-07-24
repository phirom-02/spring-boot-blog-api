package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.domain.entities.Category;
import com.phirom_02.blog_api.repository.CategoryRepository;
import com.phirom_02.blog_api.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAllWithPostCount();
    }

    @Override
    @Transactional
    public Category createCategory(Category categorytoCreate) {
        String categoryName = categorytoCreate.getName();
        if (categoryRepository.existsByNameIgnoreCase(categoryName)) {
            throw new IllegalArgumentException("Category name already exists: " + categoryName);
        }

        return categoryRepository.save(categorytoCreate);
    }
}
