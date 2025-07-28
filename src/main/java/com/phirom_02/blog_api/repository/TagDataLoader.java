package com.phirom_02.blog_api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phirom_02.blog_api.domain.entities.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.asm.TypeReference;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TagDataLoader {
    private static final Logger log = LoggerFactory.getLogger(UserDataLoader.class);

    private final ObjectMapper objectMapper;
    private final TagRepository tagRepository;

    public void load() {
        if (tagRepository.count() == 0) {
            String tagsJson = "/data/tags.json";
            log.info("Loading tags data from {}", tagsJson);
            try (InputStream inputStream = TypeReference.class.getResourceAsStream(tagsJson)) {
                CreateTagList createTagList = objectMapper.readValue(inputStream, CreateTagList.class);
                List<Tag> tags = createTagList.tags().stream().map(dto -> {
                    Tag tag = new Tag();
                    tag.setName(dto.name());
                    return tag;
                }).toList();
                tagRepository.saveAll(tags);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load tags from " + tagsJson, e);
            }
        }
    }

    private record CreateTagDto(String name) {
    }

    private record CreateTagList(List<CreateTagDto> tags) {
    }
}