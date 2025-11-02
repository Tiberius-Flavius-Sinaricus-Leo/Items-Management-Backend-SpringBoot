package com.xw.api.service.implementation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  private boolean isValidRole(String role) {
    if (role == null) return false;
    try {
      UserRole.valueOf(role.toUpperCase());
      return true;
    } catch (IllegalArgumentException ex) {
      return false;
    }
  }

  private String getRequesterEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
    }
    Object principal = authentication.getPrincipal();
    if (principal instanceof UserDetails userDetails) {
      return userDetails.getUsername(); // usually email
    } else {
      return authentication.getName();
    }
  }

  private UserEntity getRequesterEntity() {
    String email = getRequesterEmail();
    return userRepository.findByUserEmail(email)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found in database"));
  }

  private UserResponse convertToResponse(UserEntity entity) {
    return UserResponse.builder()
        .userId(entity.getUserId())
        .userEmail(entity.getUserEmail())
        .username(entity.getUsername())
        .role(entity.getRole().name())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
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
  @Transactional
  public UserResponse createUser(UserRequest request) {
    UserEntity requester = getRequesterEntity();

    // Validate input
    if (request.getUserEmail() == null || request.getUserEmail().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User email cannot be empty");
    }
    if (!request.getUserEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
    }
    if (userRepository.findByUserEmail(request.getUserEmail()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists with email: " + request.getUserEmail());
    }
    if (request.getUsername() == null || request.getUsername().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be empty");
    }
    if (request.getPassword() == null || request.getPassword().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be empty");
    }
    if (!isValidRole(request.getRole())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role: " + request.getRole());
    }

    UserRole targetRole = UserRole.valueOf(request.getRole().toUpperCase());
    UserRole requesterRole = requester.getRole();

    // Authorization logic
    if (targetRole == UserRole.ROLE_ROOT) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot create another root user");
    }
    if (targetRole == UserRole.ROLE_ADMIN && requesterRole != UserRole.ROLE_ROOT) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only root user can create admins");
    }
    if (targetRole == UserRole.ROLE_USER && requesterRole == UserRole.ROLE_USER) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Regular users cannot create other users");
    }

    // Create user
    UserEntity newUser = convertToEntity(request);
    newUser = userRepository.save(newUser);
    return convertToResponse(newUser);
  }

  @Override
  public String getUserRole(String userEmail) {
    return userRepository.findRoleByUserEmail(userEmail);
  }

  @Override
  public List<UserResponse> getAllUsers() {
    UserEntity requester = getRequesterEntity();
    if (requester.getRole() == UserRole.ROLE_USER) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Regular users cannot query user list");
    }

    return userRepository.findAll()
        .stream()
        .map(this::convertToResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void deleteUser(String userId) {
    UserEntity requester = getRequesterEntity();
    UserEntity target = userRepository.findByUserId(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId));

    // Root user cannot be deleted
    if (target.getRole() == UserRole.ROLE_ROOT) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot delete root user");
    }

    // Only root can delete admins
    if (target.getRole() == UserRole.ROLE_ADMIN && requester.getRole() != UserRole.ROLE_ROOT) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only root can delete admins");
    }

    // Root can delete both admin and user, admin can delete only user
    if (requester.getRole() == UserRole.ROLE_ROOT || 
    (requester.getRole() == UserRole.ROLE_ADMIN && target.getRole() == UserRole.ROLE_USER)) {
      userRepository.delete(target);
    } else {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to delete this user");
    }
  }

  @Override
  @Transactional
  public UserResponse updateUser(String userEmail, UserRequest request) {
    UserEntity requester = getRequesterEntity();
    UserEntity target = userRepository.findByUserEmail(userEmail)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + userEmail));

    if (target.getRole() == UserRole.ROLE_ROOT) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot modify root user");
    }

    // Check modification permissions
    if (target.getRole() == UserRole.ROLE_ADMIN && requester.getRole() != UserRole.ROLE_ROOT) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only root can modify admins");
    }
    if (target.getRole() == UserRole.ROLE_USER && requester.getRole() == UserRole.ROLE_USER) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Regular users cannot modify other users");
    }

    // Role updates allowed only by root
    if (request.getRole() != null && !request.getRole().isEmpty() && 
        !request.getRole().equalsIgnoreCase(target.getRole().name())) {
      if (requester.getRole() != UserRole.ROLE_ROOT) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only root can change user roles");
      }
      if (!isValidRole(request.getRole())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role: " + request.getRole());
      }
      UserRole newRole = UserRole.valueOf(request.getRole().toUpperCase());
      if (newRole == UserRole.ROLE_ROOT) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot promote user to root");
      }
      target.setRole(newRole);
    }

    // Update other fields
    if (request.getUsername() != null && !request.getUsername().isEmpty() && 
        !request.getUsername().equals(target.getUsername())) {
      target.setUsername(request.getUsername());
    }
    if (request.getPassword() != null && !request.getPassword().isEmpty() && 
        !passwordEncoder.matches(request.getPassword(), target.getPassword())) {
      target.setPassword(passwordEncoder.encode(request.getPassword()));
    }
    if (request.getUserEmail() != null && !request.getUserEmail().isEmpty() && 
        !request.getUserEmail().equals(target.getUserEmail())) {
      target.setUserEmail(request.getUserEmail());
    }

    target = userRepository.save(target);
    return convertToResponse(target);
  }

  @Override
  public Optional<UserResponse> getUserByEmail(String userEmail) {
    UserEntity requester = getRequesterEntity();
    if (requester.getRole() == UserRole.ROLE_USER) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Regular users cannot query other users");
    }

    return userRepository.findByUserEmail(userEmail).map(this::convertToResponse);
  }

  @Override
  @Transactional
  public UserResponse createInitialSuperUser(UserRequest request) {
    // Validate input
    if (request.getUserEmail() == null || request.getUserEmail().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User email cannot be empty");
    }
    if (!request.getUserEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
    }
    if (request.getUsername() == null || request.getUsername().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be empty");
    }
    if (request.getPassword() == null || request.getPassword().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be empty");
    }

    // Create root user
    UserEntity newUser = UserEntity.builder()
        .userId(UUID.randomUUID().toString())
        .username(request.getUsername())
        .userEmail(request.getUserEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(UserRole.ROLE_ROOT)
        .build();

    newUser = userRepository.save(newUser);
    return convertToResponse(newUser);
  }
}