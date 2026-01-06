package com.nguyenthithuhuyen.example10.security.services;

import java.util.List;
import com.nguyenthithuhuyen.example10.entity.Category;

public interface CategoryService {
    Category createCategory(Category category, Long parentId);
    Category updateCategory(Category category, Long parentId);
    Category getCategoryById(Long id);
    List<Category> getAllCategories();
    void deleteCategory(Long id);
}

