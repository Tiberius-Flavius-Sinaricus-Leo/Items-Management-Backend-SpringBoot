package com.xw.api.service;

import java.util.List;
import java.util.Optional;

import com.xw.api.dto.ItemRequest;
import com.xw.api.dto.ItemResponse;

public interface ItemService {
  
  public ItemResponse createItem(ItemRequest request);

  public List<ItemResponse> getItemsByCategory(String categoryId);

  public List<ItemResponse> getAllItems();

  public Integer countItemsByCategory(String categoryId);

  public void deleteItem(String itemId);

  public ItemResponse updateItem(String itemId, ItemRequest request);

  public Optional<ItemResponse> getItemById(String itemId);
}
