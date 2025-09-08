package com.financemanager.webapp.service.impl;

import com.financemanager.webapp.dto.CategoryDTO;
import com.financemanager.webapp.exception.ResourceNotFoundException;
import com.financemanager.webapp.model.Category;
import com.financemanager.webapp.model.User;
import com.financemanager.webapp.repository.CategoryRepository;
import com.financemanager.webapp.repository.UserRepository;
import com.financemanager.webapp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    // Mapper
    private CategoryDTO mapToCategoryDTO(Category category) {
        return new CategoryDTO(category.getId(), category.getName());
    }

    @Override
    @Transactional
    public CategoryDTO addCategory(Long userId, CategoryDTO categoryDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Check for duplicate category name for this user
        if(categoryRepository.existsByNameAndUserId(categoryDTO.getName(), userId)) {
            throw new IllegalArgumentException("Category with name '" + categoryDTO.getName() + "' already exists for this user.");
        }

        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setUser(user); // Associate with the user

        Category savedCategory = categoryRepository.save(category);
        return mapToCategoryDTO(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getCategoriesByUserId(Long userId) {
        // Optional: Check if user exists first
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        List<Category> categories = categoryRepository.findByUserIdOrderByNameAsc(userId);
        return categories.stream()
                .map(this::mapToCategoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryByIdAndUserId(Long categoryId, Long userId) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId + " for user id: " + userId));
        return mapToCategoryDTO(category);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long userId, Long categoryId, CategoryDTO categoryDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Category existingCategory = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId + " for user id: " + userId));

        // Check if new name conflicts with another category of the same user
        if (!existingCategory.getName().equalsIgnoreCase(categoryDTO.getName()) &&
                categoryRepository.existsByNameAndUserId(categoryDTO.getName(), userId)) {
            throw new IllegalArgumentException("Another category with name '" + categoryDTO.getName() + "' already exists for this user.");
        }

        existingCategory.setName(categoryDTO.getName());
        Category updatedCategory = categoryRepository.save(existingCategory);
        return mapToCategoryDTO(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long userId, Long categoryId) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId + " for user id: " + userId));

        // Consider implications: what if transactions use this category?
        // For now, we allow deletion. Could add a check here.
        // Example check: if (!category.getTransactions().isEmpty()) { throw new ... }

        categoryRepository.delete(category);
    }
}