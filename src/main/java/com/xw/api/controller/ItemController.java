package com.xw.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.xw.api.dto.ItemRequest;
import com.xw.api.dto.ItemResponse;
import com.xw.api.service.ItemService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
public class ItemController {

  private final ItemService itemService;

  @PostMapping("/admin/items")
  @ResponseStatus(HttpStatus.CREATED)
  public ItemResponse createItem(@RequestBody ItemRequest request) {
    try {
      return itemService.createItem(request);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create item: " + request.getName() + ", error message: " + e.getMessage(), e);
    }
  }

  @GetMapping("/items/{itemId}")
  @ResponseStatus(HttpStatus.OK)
  public ItemResponse getItemById(@PathVariable String itemId) {
    return itemService.getItemById(itemId).orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
  }

  @GetMapping("/items/all")
  @ResponseStatus(HttpStatus.OK)
  public List<ItemResponse> getAllItems() {
    return itemService.getAllItems();
  }

  @GetMapping("/items/{categoryId}/all")
  @ResponseStatus(HttpStatus.OK)
  public List<ItemResponse> getItemsByCategory(@PathVariable String categoryId) {
    return itemService.getItemsByCategory(categoryId);
  }

  @GetMapping("/items/{categoryId}/count")
  @ResponseStatus(HttpStatus.OK)
  public Integer countItemsByCategory(@RequestParam String categoryId) {
    return itemService.countItemsByCategory(categoryId);
  }

  @DeleteMapping("/admin/items/{itemId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteItem(@PathVariable String itemId) {
    try {
      itemService.deleteItem(itemId);
    } catch (Exception e) {
      throw new RuntimeException("Failed to delete item: " + itemId + ", error message: " + e.getMessage(), e);
    }
  }

  @PutMapping("/admin/items/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ItemResponse updateItem(@PathVariable("id") String itemId, @RequestBody ItemRequest request) {
    try {
      return itemService.updateItem(itemId, request);
    } catch (Exception e) {
      throw new RuntimeException("Failed to update item: " + itemId + ", error message: " + e.getMessage(), e);
    }
  }
  
}
