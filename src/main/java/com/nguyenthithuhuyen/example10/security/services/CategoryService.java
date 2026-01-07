package com.nguyenthithuhuyen.example10.security.services;

import com.nguyenthithuhuyen.example10.dto.CategoryDTO;
import com.nguyenthithuhuyen.example10.entity.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(Category category, Long parentId);
    Category updateCategory(Category category, Long parentId);
    void deleteCategory(Long categoryId);
    CategoryDTO getCategoryById(Long id);
    List<CategoryDTO> getAllCategories();
}
