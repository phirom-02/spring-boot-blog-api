package com.phirom_02.blog_api.service;

import com.phirom_02.blog_api.domain.entities.Category;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for handling category-related operations such as fetching, creating, and deleting categories.
 */
public interface CategoryService {

    /**
     * Retrieves all categories from the database.
     * This method fetches a list of all categories available in the system.
     *
     * @return a list of all {@link Category} entities
     */
    List<Category> getAllCategories();

    /**
     * Retrieves a category by its unique identifier (ID).
     * This method allows fetching a category based on its ID for viewing or processing.
     *
     * @param id the unique identifier of the category to retrieve
     * @return the {@link Category} entity with the given ID
     */
    Category getCategoryById(UUID id);

    /**
     * Creates a new category in the database.
     * This method processes the given category data and persists it to the database.
     *
     * @param categoryToCreate the category entity containing the data to be persisted
     * @return the created {@link Category} entity with an assigned ID
     */
    Category createCategory(Category categoryToCreate);

    /**
     * Deletes an existing category from the database.
     * This method deletes a category based on the provided ID.
     *
     * @param id the unique identifier of the category to delete
     */
    void deleteCategory(UUID id);
}
