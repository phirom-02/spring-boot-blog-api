package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.domain.entities.Category;
import com.phirom_02.blog_api.repository.CategoryRepository;
import com.phirom_02.blog_api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }
}
