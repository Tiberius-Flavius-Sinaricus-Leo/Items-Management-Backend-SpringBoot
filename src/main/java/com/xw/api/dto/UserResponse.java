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
public class UserResponse {
  private String userId;
  private String username;
  private String role;
  private String userEmail;
  private Timestamp createdAt;
  private Timestamp updatedAt;
}
