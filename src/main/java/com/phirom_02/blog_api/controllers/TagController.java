package com.phirom_02.blog_api.controllers;

import com.phirom_02.blog_api.domain.dtos.CreateTagPayload;
import com.phirom_02.blog_api.domain.dtos.TagResponse;
import com.phirom_02.blog_api.domain.entities.Tag;
import com.phirom_02.blog_api.mappers.TagMapper;
import com.phirom_02.blog_api.service.TagService;
import com.phirom_02.blog_api.swagger.SwaggerTag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.phirom_02.blog_api.swagger.SwaggerTags.POSTS;

/**
 * REST controller for managing blog tags.
 */
@RestController
@RequestMapping(path = "/api/v1/tags")
@RequiredArgsConstructor
@SwaggerTag(
        name = POSTS,
        description = "Operations for managing blog tags including retrieval, creation, and deletion"
)
public class TagController {

    private final TagService tagService;
    private final TagMapper tagMapper;

    /**
     * Retrieves a list of all tags.
     *
     * @return a {@link ResponseEntity} containing a list of {@link TagResponse} objects and HTTP status 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<TagResponse>> getAllTags() {
        List<TagResponse> tags = tagService.getAllTags()
                .stream().map(tagMapper::toResponseTag).toList();
        return ResponseEntity.ok(tags);
    }

    /**
     * Creates new tags based on the provided tag names.
     *
     * @param payload the payload containing a list of tag names to be created
     * @return a {@link ResponseEntity} containing the list of created {@link TagResponse} objects
     * and HTTP status 201 (Created)
     */
    @PostMapping
    public ResponseEntity<List<TagResponse>> createTag(@RequestBody @Valid CreateTagPayload payload) {
        List<Tag> createdTags = tagService.createTags(payload.getNames());
        List<TagResponse> createdTagsResponse = createdTags.stream()
                .map(tagMapper::toResponseTag)
                .toList();
        return new ResponseEntity<>(createdTagsResponse, HttpStatus.CREATED);
    }

    /**
     * Deletes a tag by its ID.
     *
     * @param id the UUID of the tag to be deleted
     * @return a {@link ResponseEntity} with HTTP status 204 (No Content) if deletion is successful
     */
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<TagResponse> deleteTag(@PathVariable UUID id) {
        tagService.deleteTag(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
