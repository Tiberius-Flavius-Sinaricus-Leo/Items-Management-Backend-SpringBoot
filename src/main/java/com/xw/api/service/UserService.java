package com.xw.api.service;

import java.util.List;
import java.util.Optional;

import com.xw.api.dto.UserRequest;
import com.xw.api.dto.UserResponse;

public interface UserService {

  public UserResponse createUser(UserRequest request);

  public UserResponse createInitialSuperUser(UserRequest request);

  public String getUserRole(String userEmail);

  public List<UserResponse> getAllUsers();

  public void deleteUser(String userId);

  public UserResponse updateUser(String userEmail, UserRequest request);

  public Optional<UserResponse> getUserByEmail(String userEmail);
}