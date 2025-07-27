package com.phirom_02.blog_api.domain.dtos;

import com.phirom_02.blog_api.domain.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * DTO representing the data for creating a new post.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePostDto {
    private String title;

    private String content;

    private UUID categoryId;

    @Builder.Default
    private Set<UUID> tagIds = new HashSet<>();

    private PostStatus status;
}
