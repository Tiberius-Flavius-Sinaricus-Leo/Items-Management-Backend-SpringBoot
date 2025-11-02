package com.xw.api.controller;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.xw.api.dto.CategoryRequest;
import com.xw.api.dto.CategoryResponse;
import com.xw.api.service.CategoryService;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @PostMapping("/admin/categories")
  @ResponseStatus(HttpStatus.CREATED)
  public CategoryResponse createCategory(@RequestBody CategoryRequest request) { 
    try {
      return categoryService.createCategory(request);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to create category: " + request.getName() + ", error message: " + e.getMessage(), e);
    }
  }

  @PutMapping("/admin/categories/{categoryId}")
  @ResponseStatus(HttpStatus.OK)
  public CategoryResponse updateCategory(@PathVariable String categoryId, @RequestBody CategoryRequest request) {
    try {
      return categoryService.updateCategory(categoryId, request);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to update category: " + e.getMessage(), e);
    }
  }
  
  @GetMapping("/categories/all")
  @ResponseStatus(HttpStatus.OK)
  public List<CategoryResponse> getAllCategories() {
    return categoryService.getAllCategories();
  }

  @GetMapping("/categories/{categoryId}")
  @ResponseStatus(HttpStatus.OK)
  public CategoryResponse getCategoryById(@PathVariable String categoryId) {
    return categoryService.getCategoryById(categoryId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + categoryId));
  }

  @DeleteMapping("/admin/categories/{categoryId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteCategory(@PathVariable String categoryId) {
    try {
      categoryService.deleteCategory(categoryId);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to delete category: " + e.getMessage(), e);
    }
  }
}
