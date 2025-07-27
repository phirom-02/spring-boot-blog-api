package com.phirom_02.blog_api.mappers;

import com.phirom_02.blog_api.domain.PostStatus;
import com.phirom_02.blog_api.domain.dtos.CategoryResponse;
import com.phirom_02.blog_api.domain.dtos.CreateCategoryPayload;
import com.phirom_02.blog_api.domain.entities.Category;
import com.phirom_02.blog_api.domain.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Mapper interface for converting between category-related entities and DTOs.
 */

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    /**
     * Maps a {@link CreateCategoryPayload} to a {@link Category} entity.
     *
     * @param createCategoryPayload the raw data for creating a category
     * @return the corresponding {@link Category} entity
     */
    Category toEntity(CreateCategoryPayload createCategoryPayload);

    /**
     * Maps a {@link Category} entity to a {@link CategoryResponse} DTO.
     * Includes custom logic to calculate the number of published posts associated with the category.
     *
     * @param category the category entity to convert
     * @return the corresponding {@link CategoryResponse} DTO
     */
    @Mapping(target = "postCount", source = "posts", qualifiedByName = "calculatePostCount")
    CategoryResponse toResponseCategory(Category category);

    /**
     * Calculates the number of published posts associated with the category.
     *
     * @param posts the list of posts related to the category
     * @return the number of published posts
     */
    @Named("calculatePostCount")
    default long calculatePostCount(List<Post> posts) {
        if (posts == null) return 0;
        return posts.stream().filter(post -> PostStatus.PUBLISHED.equals(post.getStatus())).count();
    }
}
