package com.phirom_02.blog_api.mappers;

import com.phirom_02.blog_api.domain.PostStatus;
import com.phirom_02.blog_api.domain.dtos.TagResponse;
import com.phirom_02.blog_api.domain.entities.Post;
import com.phirom_02.blog_api.domain.entities.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Mapper interface responsible for converting between tag-related DTOs and entities.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

    /**
     * Maps a {@link Tag} entity to a {@link TagResponse} DTO.
     * Includes custom logic to calculate the number of published posts associated with the tag.
     *
     * @param tag the tag entity to convert
     * @return the corresponding {@link TagResponse} DTO
     */
    @Mapping(target = "postCount", source = "posts", qualifiedByName = "calculatePostCount")
    TagResponse toResponseTag(Tag tag);

    /**
     * Calculates the number of published posts associated with the tag.
     *
     * @param posts the list of posts related to the tag
     * @return the number of published posts
     */
    @Named("calculatePostCount")
    default long calculatePostCount(List<Post> posts) {
        if (posts == null) return 0;
        return posts.stream().filter(post -> PostStatus.PUBLISHED.equals(post.getStatus())).count();
    }
}
