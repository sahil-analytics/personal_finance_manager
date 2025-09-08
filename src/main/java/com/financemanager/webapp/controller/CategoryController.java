package com.financemanager.webapp.controller;

import com.financemanager.webapp.dto.CategoryDTO;
import com.financemanager.webapp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/categories") // Categories are user-specific
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDTO> addCategory(@PathVariable Long userId, @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO createdCategory = categoryService.addCategory(userId, categoryDTO);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getUserCategories(@PathVariable Long userId) {
        List<CategoryDTO> categories = categoryService.getCategoriesByUserId(userId);
        return ResponseEntity.ok(categories);
    }

    // Get a single category (might be useful)
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long userId, @PathVariable Long categoryId) {
        CategoryDTO category = categoryService.getCategoryByIdAndUserId(categoryId, userId);
        if (category != null) {
            return ResponseEntity.ok(category);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Edit category (Optional - wasn't explicitly requested but good to have)
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long userId, @PathVariable Long categoryId, @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(userId, categoryId, categoryDTO);
        if (updatedCategory != null) {
            return ResponseEntity.ok(updatedCategory);
        } else {
            return ResponseEntity.notFound().build(); // Or bad request if data invalid
        }
    }


    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long userId, @PathVariable Long categoryId) {
        categoryService.deleteCategory(userId, categoryId);
        return ResponseEntity.noContent().build(); // Standard practice for successful DELETE
    }
}