package com.xw.api.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemResponse {
  private String itemId;
  private String name;
  private String description;
  private BigDecimal price;
  private String categoryId;
  private String categoryName;
  private Timestamp createdAt;
  private Timestamp updatedAt;
}
