package com.nguyenthithuhuyen.example10.security.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import com.nguyenthithuhuyen.example10.entity.Category;
import com.nguyenthithuhuyen.example10.repository.CategoryRepository;
import com.nguyenthithuhuyen.example10.security.services.CategoryService;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    // ================= CREATE =================
    @Override
public Category createCategory(Category category, Long parentId) {
    if (parentId != null) {
        Category parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent category not found"));
        category.setParent(parent);
    }
    return categoryRepository.save(category);
}

@Override
public Category updateCategory(Category category, Long parentId) {
    Category existing = categoryRepository.findById(category.getId())
            .orElseThrow(() -> new RuntimeException("Category not found"));

    existing.setName(category.getName());
    existing.setDescription(category.getDescription());
    existing.setImageUrl(category.getImageUrl());

    if (parentId != null) {
        Category parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent category not found"));
        existing.setParent(parent);
    } else {
        existing.setParent(null);
    }

    return categoryRepository.save(existing);
}

    // ================= GET BY ID =================
    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
    }

    // ================= GET ALL =================
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // ================= DELETE =================
    @Override
    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
