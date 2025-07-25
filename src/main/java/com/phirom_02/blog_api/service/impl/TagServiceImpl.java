package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.domain.entities.Tag;
import com.phirom_02.blog_api.repository.TagRepository;
import com.phirom_02.blog_api.service.TagService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAllWithPostCount();
    }

    @Override
    public List<Tag> createTags(Set<String> tagNames) {
        List<Tag> existingTags = tagRepository.findAllByNameIn(tagNames);

        Set<String> existingTagNames = existingTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        List<Tag> newTags = tagNames.stream()
                .filter(name -> !existingTagNames.contains(name))
                .map(name -> Tag.builder()
                        .name(name)
                        .posts(new ArrayList<>())
                        .build())
                .toList();

        List<Tag> createdTags = new ArrayList<>();
        if (!newTags.isEmpty()) {
            createdTags = tagRepository.saveAll(newTags);
        }

        return createdTags;
    }

    @Override
    public List<Tag> getTagsByIds(Set<UUID> ids) {
        return tagRepository.findAllByIdContaining(ids);
    }

    @Override
    public Tag getTagById(UUID id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteTag(UUID id) {
        tagRepository.findById(id).ifPresent(tag -> {
            if (!tag.getPosts().isEmpty()) {
                throw new IllegalStateException("There are posts associated with tag: " + id);
            }
        });
        tagRepository.deleteById(id);
    }
}
