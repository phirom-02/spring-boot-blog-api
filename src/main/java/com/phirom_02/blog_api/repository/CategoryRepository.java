package com.phirom_02.blog_api.repository;

import com.phirom_02.blog_api.domain.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing {@link Category} entities.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    /**
     * Fetches all categories along with their associated posts.
     * This query uses a LEFT JOIN FETCH to eagerly load the related posts for each category.
     *
     * @return a list of categories with their associated posts
     */
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.posts")
    List<Category> findAllWithPostCount();

    /**
     * Checks if a category with the given name exists, ignoring case.
     *
     * @param name the name of the category to check
     * @return true if a category with the given name exists, false otherwise
     */
    boolean existsByNameIgnoreCase(String name);
}
