package com.phirom_02.blog_api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phirom_02.blog_api.domain.dtos.CreateCategoryPayload;
import com.phirom_02.blog_api.domain.entities.Category;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.asm.TypeReference;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryDataLoader {

    private static final Logger log = LoggerFactory.getLogger(UserDataLoader.class);

    private final ObjectMapper objectMapper;
    private final CategoryRepository categoryRepository;


    public void load() {
        if (categoryRepository.count() == 0) {
            String categoryJson = "/data/categories.json";
            log.info("Loading category data from {}", categoryJson);
            try (InputStream inputStream = TypeReference.class.getResourceAsStream(categoryJson)) {
                CreateCategoryPayloadList createCategoryPayloadList = objectMapper.readValue(inputStream, CreateCategoryPayloadList.class);
                List<Category> categories = createCategoryPayloadList.categories().stream().map(dto -> {
                    Category category = new Category();
                    category.setName(dto.getName());
                    return category;
                }).toList();
                categoryRepository.saveAll(categories);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load categoriesc from " + categoryJson, e);
            }
        }
    }

    private record CreateCategoryPayloadList(List<CreateCategoryPayload> categories) {

    }
}
