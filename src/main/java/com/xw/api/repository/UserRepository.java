package com.xw.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.xw.api.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

  @Query("SELECT u FROM UserEntity u WHERE u.userEmail = :userEmail")
  Optional<UserEntity> findByUserEmail(String userEmail); 

  @Query("SELECT u FROM UserEntity u WHERE u.userId = :userId")
  Optional<UserEntity> findByUserId(String userId);

  @Query("SELECT u FROM UserEntity u WHERE u.username = :name")
  Optional<UserEntity> findByUsername(String name);
  
  @Query("SELECT u.role FROM UserEntity u WHERE u.userEmail = :userEmail")
  String findRoleByUserEmail(String userEmail);
}
