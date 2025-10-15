package com.xw.api.service.implementation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xw.api.common.UserRole;
import com.xw.api.dto.UserRequest;
import com.xw.api.dto.UserResponse;
import com.xw.api.entity.UserEntity;
import com.xw.api.repository.UserRepository;
import com.xw.api.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {

  private boolean isValidRole(String role) {
    if (role == null) return false;
    try {
      UserRole.valueOf(role.toUpperCase());
      return true;
    } catch (IllegalArgumentException ex) {
      return false;
    }
  }

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  @Override
  public UserResponse createUser(UserRequest request) {
    String userEmail = request.getUserEmail();
    if (userRepository.findByUserEmail(userEmail).isPresent()) {
      throw new RuntimeException("User already exists with email: " + userEmail);
    }
    else if (!isValidRole(request.getRole())) {
      throw new RuntimeException("Invalid role: " + request.getRole() + ". Allowed roles are: " + java.util.Arrays.toString(UserRole.values()));
    }
    else if (request.getPassword() == null || request.getPassword().isEmpty()) {
      throw new RuntimeException("Password cannot be empty");
    }
    else if (request.getUsername() == null || request.getUsername().isEmpty()) {
      throw new RuntimeException("Username cannot be empty");
    }
    else if (request.getUserEmail() == null || request.getUserEmail().isEmpty()) {
      throw new RuntimeException("User email cannot be empty");
    }
    else if (!request.getUserEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
      throw new RuntimeException("Invalid email format: " + request.getUserEmail());
    }
    else {
      UserEntity newEntity = convertToEntity(request);
      newEntity = userRepository.save(newEntity);
      return convertToResponse(newEntity);
    }
  }

  private UserResponse convertToResponse(UserEntity newEntity) {
    return UserResponse.builder()
        .userId(newEntity.getUserId())
        .userEmail(newEntity.getUserEmail())
        .username(newEntity.getUsername())
        .role(newEntity.getRole().name())
        .createdAt(newEntity.getCreatedAt())
        .updatedAt(newEntity.getUpdatedAt())
        .build();
  }

  private UserEntity convertToEntity(UserRequest request) {
    return UserEntity.builder()
        .userId(UUID.randomUUID().toString())
        .username(request.getUsername())
        .userEmail(request.getUserEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(UserRole.valueOf(request.getRole().toUpperCase()))
        .build();
  }

  @Override
  public String getUserRole(String userEmail) {
    return userRepository.findRoleByUserEmail(userEmail);
  }

  @Override
  public List<UserResponse> getAllUsers() {
    return userRepository.findAll()
        .stream()
        .map(this::convertToResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void deleteUser(String userId) {
    UserEntity user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    userRepository.delete(user);
  }

  @Override
  @Transactional
  public UserResponse updateUser(String userEmail, UserRequest request) {
    UserEntity existingUser = userRepository.findByUserEmail(userEmail)
        .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
    
    // Update fields if they are provided in the request
    if (request.getPassword() != null && !request.getPassword().isEmpty()) {
      existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
    }
  
    if (request.getRole() != null && !request.getRole().isEmpty()) {
      existingUser.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
    }
    if (request.getUsername() != null && !request.getUsername().isEmpty()) {
      existingUser.setUsername(request.getUsername());
    }
    if (request.getUserEmail() != null && !request.getUserEmail().isEmpty()) {
      existingUser.setUserEmail(request.getUserEmail());
    }
    
    existingUser = userRepository.save(existingUser);
    return convertToResponse(existingUser);
  }

  @Override
  public Optional<UserResponse> getUserByEmail(String userEmail) {
    Optional<UserEntity> entityOpt = userRepository.findByUserEmail(userEmail);
    return entityOpt.map(this::convertToResponse);
  }
}
