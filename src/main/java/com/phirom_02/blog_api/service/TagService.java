package com.phirom_02.blog_api.service;

import com.phirom_02.blog_api.domain.entities.Tag;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TagService {

    List<Tag> getAllTags();

    List<Tag> createTags(Set<String> tagNames);

    List<Tag> getTagsByIds(Set<UUID> ids);

    Tag getTagById(UUID tagId);

    void deleteTag(UUID id);
}
