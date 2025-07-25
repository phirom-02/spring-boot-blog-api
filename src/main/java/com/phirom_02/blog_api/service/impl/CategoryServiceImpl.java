package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.domain.entities.Category;
import com.phirom_02.blog_api.repository.CategoryRepository;
import com.phirom_02.blog_api.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAllWithPostCount();
    }

    @Override
    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
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

    @Override
    public void deleteCategory(UUID id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            if (category.get().getPosts().size() > 0) {
                throw new IllegalStateException("There are posts associated with category: " + id);
            }
            categoryRepository.deleteById(id);
        }
    }
}
