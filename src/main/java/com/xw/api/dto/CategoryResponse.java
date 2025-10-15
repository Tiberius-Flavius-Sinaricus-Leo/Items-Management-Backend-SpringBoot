package com.xw.api.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryResponse {

  private String categoryId;
  private String name;
  private String description;
  private String bgColor;
  private Timestamp createdAt;
  private Timestamp updatedAt;
  private Integer itemsCount;
}
