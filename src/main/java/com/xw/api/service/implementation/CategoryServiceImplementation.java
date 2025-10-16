package com.xw.api.service.implementation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.xw.api.dto.CategoryRequest;
import com.xw.api.dto.CategoryResponse;
import com.xw.api.entity.CategoryEntity;
import com.xw.api.exception.CategoryNotEmptyException;
import com.xw.api.repository.CategoryRepository;
import com.xw.api.repository.ItemRepository;
import com.xw.api.service.CategoryService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImplementation implements CategoryService {

  private final CategoryRepository categoryRepository;

  private final ItemRepository itemRepository;

  @Override
  public CategoryResponse createCategory(CategoryRequest request) {
    CategoryEntity newEntity = convertToEntity(request);
    newEntity = categoryRepository.save(newEntity);
    return convertToResponse(newEntity);
  }

  private CategoryResponse convertToResponse(CategoryEntity newEntity) {
    return CategoryResponse.builder()
        .categoryId(newEntity.getCategoryId())
        .name(newEntity.getName())
        .description(newEntity.getDescription())
        .bgColor(newEntity.getBgColor())
        .createdAt(newEntity.getCreatedAt())
        .updatedAt(newEntity.getUpdatedAt())
        .itemsCount(itemRepository.countByCategory(newEntity.getId()))
        .build();
  }

  private CategoryEntity convertToEntity(CategoryRequest request) {
    return CategoryEntity.builder()
        .categoryId(UUID.randomUUID().toString())
        .name(request.getName())
        .description(request.getDescription())
        .bgColor(request.getBgColor())
        .build();
  }

  @Override
  public List<CategoryResponse> getAllCategories() {
    return categoryRepository.findAll()
        .stream()
        .map(this::convertToResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void deleteCategory(String categoryId) {
    CategoryEntity entity = categoryRepository.findByCategoryId(categoryId)
        .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
    // Check if category has any items before deletion
    int itemCount = itemRepository.countByCategory(entity.getId());
    if (itemCount > 0) {
      throw new CategoryNotEmptyException("Cannot delete category with existing items. Please remove all items first.");
    }
    categoryRepository.delete(entity);
  }

  @Override
  @Transactional
  public CategoryResponse updateCategory(String categoryId, CategoryRequest request) {
    CategoryEntity newEntity = categoryRepository.findByCategoryId(categoryId)
        .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

    if (request.getName() != null) {
      newEntity.setName(request.getName());
    }
    if (request.getDescription() != null) {
      newEntity.setDescription(request.getDescription());
    }
    if (request.getBgColor() != null) {
      newEntity.setBgColor(request.getBgColor());
    }

    newEntity = categoryRepository.save(newEntity);
    return convertToResponse(newEntity);
  }

  @Override
  public Optional<CategoryResponse> getCategoryById(String categoryId) {
    Optional<CategoryEntity> entityOpt = categoryRepository.findByCategoryId(categoryId);
    return entityOpt.map(this::convertToResponse);
  }
}
