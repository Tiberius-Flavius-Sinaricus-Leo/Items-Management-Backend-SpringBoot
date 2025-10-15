package com.xw.api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.xw.api.utils.JwtUtils;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

  private final UserDetailsService userDetailsService;
  private final JwtUtils jwtUtils;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws IOException, ServletException {
    
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      filterChain.doFilter(request, response);
      return;
    }
    try {
      String jwt = readCookie(request, "token");
      if (jwt == null) {
        String authz = request.getHeader("Authorization");
        if (authz != null && authz.startsWith("Bearer ")) {
          jwt = authz.substring(7);
        }
      }
      if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        String email = jwtUtils.extractUsername(jwt);

        if (email != null) {
          UserDetails userDetails = userDetailsService.loadUserByUsername(email);

          boolean valid = jwtUtils.validateToken(jwt, userDetails);
          if (valid) {
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
          }
        }
      }
    } catch (Exception ignored) {
    }

    filterChain.doFilter(request, response);
  }

  private String readCookie(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) return null;
    return Arrays.stream(cookies)
        .filter(c -> name.equals(c.getName()))
        .findFirst()
        .map(Cookie::getValue)
        .orElse(null);
  }

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      return true; // also skip via shouldNotFilter to be explicit
    }
    String path = request.getRequestURI();
    return path.startsWith("/login")
        || path.startsWith("/auth/refresh")
        || path.startsWith("/public/")
        || path.startsWith("/assets/")
        || path.startsWith("/health");
  }
}

