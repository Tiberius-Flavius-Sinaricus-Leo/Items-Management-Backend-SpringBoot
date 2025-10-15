package com.xw.api.service;

import java.util.List;
import java.util.Optional;

import com.xw.api.dto.CategoryRequest;
import com.xw.api.dto.CategoryResponse;

public interface CategoryService {

  public CategoryResponse createCategory(CategoryRequest request);

  public List<CategoryResponse> getAllCategories();

  public void deleteCategory(String categoryId);

  public CategoryResponse updateCategory(String categoryId, CategoryRequest request);

  public Optional<CategoryResponse> getCategoryById(String categoryId);

}
