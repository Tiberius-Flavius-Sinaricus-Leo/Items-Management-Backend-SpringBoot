package com.xw.api.service.implementation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.xw.api.dto.ItemRequest;
import com.xw.api.dto.ItemResponse;
import com.xw.api.entity.CategoryEntity;
import com.xw.api.entity.ItemEntity;
import com.xw.api.repository.CategoryRepository;
import com.xw.api.repository.ItemRepository;
import com.xw.api.service.ItemService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ItemServiceImplementation implements ItemService {

  private final ItemRepository itemRepository;

  private final CategoryRepository categoryRepository;

  @Override
  public ItemResponse createItem(ItemRequest request) {
    ItemEntity newEntity = convertToEntity(request);
    newEntity = itemRepository.save(newEntity);
    return convertToResponse(newEntity);
  }

  private ItemEntity convertToEntity(ItemRequest request) {
    CategoryEntity category = categoryRepository.findByCategoryId(request.getCategoryId())
        .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
    return ItemEntity.builder()
    .itemId(UUID.randomUUID().toString())
    .name(request.getName())
    .description(request.getDescription())
    .price(request.getPrice())
    .category(category)
    .build();
  }

  private ItemResponse convertToResponse(ItemEntity newEntity) {
    return ItemResponse.builder()
        .itemId(newEntity.getItemId())
        .name(newEntity.getName())
        .description(newEntity.getDescription())
        .price(newEntity.getPrice())
        .categoryId(newEntity.getCategory().getCategoryId())
        .categoryName(newEntity.getCategory().getName())
        .createdAt(newEntity.getCreatedAt())
        .updatedAt(newEntity.getUpdatedAt())
        .build();
  }

  @Override
  public List<ItemResponse> getItemsByCategory(String categoryId) {
    CategoryEntity category = categoryRepository.findByCategoryId(categoryId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
    return itemRepository.findAllByCategoryId(category.getId())
    .stream()
    .map(this::convertToResponse)
    .collect(Collectors.toList());
  }

  @Override
  public Integer countItemsByCategory(String categoryId) {
    CategoryEntity category = categoryRepository.findByCategoryId(categoryId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
    return itemRepository.countByCategory(category.getId());
  }

  @Override
  @Transactional
  public void deleteItem(String itemId) {
    ItemEntity entity = itemRepository.findByItemId(itemId)
        .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));
    itemRepository.delete(entity);
  }

  @Override
  @Transactional
  public ItemResponse updateItem(String itemId, ItemRequest request) {
    ItemEntity entity = itemRepository.findByItemId(itemId)
        .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));
    if (request.getName() != null) {
      entity.setName(request.getName());
    }
    if (request.getDescription() != null) {
      entity.setDescription(request.getDescription());
    }
    if (request.getPrice() != null) {
      entity.setPrice(request.getPrice());
    }
    if (request.getCategoryId() != null) {
      CategoryEntity category = categoryRepository.findByCategoryId(request.getCategoryId())
          .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
      entity.setCategory(category);
    }
    entity = itemRepository.save(entity);
    return convertToResponse(entity);
  }

  @Override
  public Optional<ItemResponse> getItemById(String itemId) {
    return itemRepository.findByItemId(itemId)
        .map(this::convertToResponse);
  }

  @Override
  public List<ItemResponse> getAllItems() {
    return itemRepository.findAll()
        .stream()
        .map(this::convertToResponse)
        .collect(Collectors.toList());
  }
  
}
