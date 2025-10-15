package com.xw.api.service.implementation;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.xw.api.entity.UserEntity;
import com.xw.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImplementation implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserEntity userFound = userRepository.findByUserEmail(email)
      .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + email));

    return new User(userFound.getUserEmail(), userFound.getPassword(), Collections.singleton(new SimpleGrantedAuthority(userFound.getRole().name())));
  }
  
}