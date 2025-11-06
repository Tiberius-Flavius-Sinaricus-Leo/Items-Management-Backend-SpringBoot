package com.xw.api.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.xw.api.common.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true, nullable = false)
  private String userId;
  @Column(nullable = false)
  private String username;
  private String password;
  @Enumerated(EnumType.STRING)
  private UserRole role;
  @Column(nullable = false, unique = true)
  private String userEmail;
  @CreationTimestamp
  @Column(updatable = false)
  private Timestamp createdAt;
  @UpdateTimestamp
  private Timestamp updatedAt;
  private Timestamp lastLoginAt;
}
