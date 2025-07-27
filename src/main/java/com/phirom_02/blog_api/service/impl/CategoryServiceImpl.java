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

/**
 * Implementation of the {@link CategoryService} interface.
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Retrieves a list of all categories, including their associated post count.
     *
     * @return a list of all categories.
     */
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAllWithPostCount();
    }

    /**
     * Retrieves a category by its ID. Throws an {@link EntityNotFoundException} if not found.
     *
     * @param id the ID of the category to retrieve.
     * @return the category associated with the provided ID.
     * @throws EntityNotFoundException if the category with the given ID does not exist.
     */
    @Override
    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
    }


    /**
     * Creates a new category in the system. Checks if a category with the same name already exists.
     * Throws an {@link IllegalArgumentException} if a category with the same name exists.
     *
     * @param categorytoCreate the category to be created.
     * @return the created category.
     * @throws IllegalArgumentException if the category name already exists in the system.
     */
    @Override
    public Category createCategory(Category categorytoCreate) {
        String categoryName = categorytoCreate.getName();
        if (categoryRepository.existsByNameIgnoreCase(categoryName)) {
            throw new IllegalArgumentException("Category name already exists: " + categoryName);
        }

        return categoryRepository.save(categorytoCreate);
    }

    /**
     * Deletes a category by its ID. If the category has associated posts, an {@link IllegalStateException} is thrown.
     *
     * @param id the ID of the category to delete.
     * @throws IllegalStateException if there are posts associated with the category.
     */
    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            // Check if there are posts associated with this category
            if (category.get().getPosts().size() > 0) {
                throw new IllegalStateException("There are posts associated with category: " + id);
            }
            categoryRepository.deleteById(id);
        }
    }
}
