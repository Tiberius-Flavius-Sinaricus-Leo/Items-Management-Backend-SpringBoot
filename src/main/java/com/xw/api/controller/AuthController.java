package com.xw.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RestController;

import com.xw.api.dto.AuthRequest;
import com.xw.api.dto.AuthResponse;
import com.xw.api.exception.AuthenticationException;
import com.xw.api.service.UserService;
import com.xw.api.utils.JwtUtils;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequiredArgsConstructor
public class AuthController {

  private final UserDetailsService userDetailsService;

  private final UserService userService;

  private final AuthenticationManager authenticationManager;

  private final JwtUtils jwtUtils;

  // @PostMapping("/logout")
  // public ResponseEntity<Void> logout(HttpServletResponse response) {
  //   // Invalidate the JWT cookie by setting maxAge to 0
  //   ResponseCookie cookie = ResponseCookie.from("token", "")
  //       .httpOnly(true)
  //       .secure(true)
  //       .path("/")
  //       .sameSite("None")
  //       .maxAge(0)
  //       .build();
  //   response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  //   return ResponseEntity.noContent().build();
  // }

  @GetMapping("/check-login")
  public ResponseEntity<AuthResponse> checkLogin(@AuthenticationPrincipal UserDetails userDetails) {
    if (userDetails == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    String userEmail = userDetails.getUsername();
    String role = userService.getUserRole(userEmail);
    return ResponseEntity.ok(new AuthResponse(userEmail, role));
  }
  

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request, HttpServletResponse response){
    String userEmail = request.getUserEmail();
    String password = request.getPassword();
    Boolean rememberMe = request.getRememberMe();
    try {
      // Authenticate the user
      authenticationManager
      .authenticate(new UsernamePasswordAuthenticationToken(userEmail, password));
      // If authentication is successful, load user details
      final UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
      // Generate a token
      final String token = jwtUtils.generateToken(userDetails);
      final String role = userService.getUserRole(userEmail);
      // Set rememberMe cookie if requested
      Integer maxAge = rememberMe != null && rememberMe ? 7 * 24 * 60 * 60 : 24 * 60 * 60;
      ResponseCookie cookie = ResponseCookie.from("token", token)
          .httpOnly(true)
          .secure(true)
          .path("/")
          .sameSite("None")
          .maxAge(maxAge)
          .build();

      response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

      return ResponseEntity.ok(new AuthResponse(userEmail, role));
    }
    catch (DisabledException e) {
      throw new AuthenticationException("USER_DISABLED", e);
    }
    catch (BadCredentialsException e) {
      throw new AuthenticationException("Invalid credentials", e);
    }
  }
}
