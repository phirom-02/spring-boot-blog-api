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
 * DTO for updating an existing blog post.
 * This object is used to pass updated post data to the service layer for processing.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePostDto {

    private UUID id;

    private String title;

    private String content;

    private UUID categoryId;

    @Builder.Default
    private Set<UUID> tagIds = new HashSet<>();

    private PostStatus status;
}
