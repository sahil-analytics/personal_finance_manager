package com.financemanager.webapp.service;

import com.financemanager.webapp.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {
    CategoryDTO addCategory(Long userId, CategoryDTO categoryDTO);
    List<CategoryDTO> getCategoriesByUserId(Long userId);
    CategoryDTO getCategoryByIdAndUserId(Long categoryId, Long userId);
    CategoryDTO updateCategory(Long userId, Long categoryId, CategoryDTO categoryDTO);
    void deleteCategory(Long userId, Long categoryId);
}