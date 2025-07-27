package com.phirom_02.blog_api.mappers;

import com.phirom_02.blog_api.domain.dtos.*;
import com.phirom_02.blog_api.domain.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper interface for converting between different representations of a post,
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    /**
     * Maps a {@link Post} entity to a {@link PostResponse} DTO.
     * Converts a post entity to a response format, including author, category, and tags.
     *
     * @param post the post entity to convert
     * @return the corresponding {@link PostResponse} DTO
     */
    @Mapping(target = "author", source = "author")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "tags", source = "tags")
    PostResponse toPostResponse(Post post);

    /**
     * Maps a {@link CreatePostPayload} to a {@link CreatePostDto}.
     * Converts the raw post creation data from the client into a DTO suitable for further processing.
     *
     * @param payload the raw post creation data
     * @return the corresponding {@link CreatePostDto}
     */
    CreatePostDto toCreatePostDto(CreatePostPayload payload);

    /**
     * Maps an {@link UpdatePostPayload} to an {@link UpdatePostDto}.
     * Converts the post update data from the client to a DTO for updating an existing post.
     *
     * @param payload the post update data
     * @return the corresponding {@link UpdatePostDto}
     */
    UpdatePostDto toUpdatePostDto(UpdatePostPayload payload);
}
