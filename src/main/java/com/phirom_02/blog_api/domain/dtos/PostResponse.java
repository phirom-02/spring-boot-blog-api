package com.phirom_02.blog_api.domain.dtos;

import com.phirom_02.blog_api.domain.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse {
    private UUID id;
    private String title;
    private String content;
    private PostAuthor author;
    private CategoryResponse category;
    private Set<TagResponse> tags;
    private PostStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
