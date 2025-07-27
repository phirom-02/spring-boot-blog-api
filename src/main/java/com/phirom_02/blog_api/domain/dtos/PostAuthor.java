package com.phirom_02.blog_api.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO representing the author of a blog post.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostAuthor {

    private UUID id;
    private String name;
}
