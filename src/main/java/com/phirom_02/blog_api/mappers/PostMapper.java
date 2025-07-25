package com.phirom_02.blog_api.mappers;

import com.phirom_02.blog_api.domain.dtos.*;
import com.phirom_02.blog_api.domain.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(target = "author", source = "author")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "tags", source = "tags")
    PostResponse toPostResponse(Post post);

    CreatePostDto toCreatePostDto(CreatePostPayload payload);

    UpdatePostDto toUpdatePostDto(UpdatePostPayload payload);
}
