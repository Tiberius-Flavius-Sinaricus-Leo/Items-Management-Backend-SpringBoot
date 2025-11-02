package com.xw.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.xw.api.dto.UserRequest;
import com.xw.api.dto.UserResponse;
import com.xw.api.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponse createUser(@RequestBody UserRequest request) {
    try {
      return userService.createUser(request);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to create user: " + request.getUserEmail() + ", error message: " + e.getMessage(), e);
    }
  }
  
  @GetMapping("/users/all")
  @ResponseStatus(HttpStatus.OK)  
  public List<UserResponse> getAllUsers() {
    return userService.getAllUsers();
  }

  @DeleteMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@PathVariable String userId) {
    try {
      userService.deleteUser(userId);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to delete user: "+ userId+ ", error message: " + e.getMessage(), e);
    }
  }

  @GetMapping("/users/{userEmail}")
  @ResponseStatus(HttpStatus.OK)
  public UserResponse getUserByEmail(@PathVariable String userEmail) {
    return userService.getUserByEmail(userEmail)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + userEmail));
  }

  @PutMapping("/users/{userEmail}")
  @ResponseStatus(HttpStatus.OK)
  public UserResponse updateUser(@PathVariable String userEmail, @RequestBody UserRequest request) {
    try {
      return userService.updateUser(userEmail, request);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to update user: " + userEmail + ", error message: " + e.getMessage(), e);
    }
  }
}
