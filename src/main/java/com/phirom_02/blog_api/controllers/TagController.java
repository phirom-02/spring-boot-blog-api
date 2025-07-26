package com.phirom_02.blog_api.controllers;

import com.phirom_02.blog_api.domain.dtos.CreateTagPayload;
import com.phirom_02.blog_api.domain.dtos.TagResponse;
import com.phirom_02.blog_api.domain.entities.Tag;
import com.phirom_02.blog_api.mappers.TagMapper;
import com.phirom_02.blog_api.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final TagMapper tagMapper;

    @GetMapping
    public ResponseEntity<List<TagResponse>> getAllTags() {
        List<TagResponse> tags = tagService.getAllTags()
                .stream().map(tagMapper::toResponseTag).toList();
        return ResponseEntity.ok(tags);
    }

    @PostMapping
    public ResponseEntity<List<TagResponse>> createTag(@RequestBody @Valid CreateTagPayload payload) {
        List<Tag> createdTags = tagService.createTags(payload.getNames());
        List<TagResponse> createdTagsResponse = createdTags.stream()
                .map(tagMapper::toResponseTag)
                .toList();
        return new ResponseEntity<>(createdTagsResponse, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<TagResponse> deleteTag(@PathVariable UUID id) {
        tagService.deleteTag(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
