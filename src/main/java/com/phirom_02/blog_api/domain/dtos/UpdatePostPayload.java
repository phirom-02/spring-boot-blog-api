package com.phirom_02.blog_api.domain.dtos;

import com.phirom_02.blog_api.domain.PostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for updating an existing blog post.
 * Used for capturing updated data while ensuring validation on input fields.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePostPayload {

    @NotNull(message = "Post ID is required")
    private UUID id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between {min} and {max} characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 10, max = 50000, message = "Content must be between {min} and {max} characters")
    private String content;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    @Builder.Default
    @Size(max = 10, message = "Maximum {max} tag allowed")
    private Set<UUID> tagIds = new HashSet<>();

    @NotNull(message = "Status is required")
    private PostStatus status;
}
