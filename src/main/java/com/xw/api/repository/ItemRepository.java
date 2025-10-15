package com.xw.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.xw.api.entity.ItemEntity;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {

  @Query("SELECT i FROM ItemEntity i WHERE i.itemId = :itemId")
  Optional<ItemEntity> findByItemId(String itemId);
  
  // Find items by (true) category ID (type Long) instead of categoryId (type String)
  @Query("SELECT i FROM ItemEntity i WHERE i.category.id = :id")
  List<ItemEntity> findAllByCategoryId(Long id);

  // Count items by (true) category ID (type Long) instead of categoryId (type String)
  @Query("SELECT COUNT(i) FROM ItemEntity i WHERE i.category.id = :id")
  Integer countByCategory(Long id);
}
