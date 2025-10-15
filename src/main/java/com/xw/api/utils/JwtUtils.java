package com.xw.api.utils;

import java.util.HashMap;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

  @Value("${jwt.secret}")
  private String jwtSecret;

  private SecretKey getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
    return Keys.hmacShaKeyFor(keyBytes);
  }
  
  public String generateToken(UserDetails userDetails) {
    return Jwts.builder()
    .claims(new HashMap<>())
    .subject(userDetails.getUsername())
    .issuedAt(new java.util.Date(System.currentTimeMillis()))
    .expiration(new java.util.Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
    .signWith(getSignInKey(), Jwts.SIG.HS256)
    .compact();
  }

  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  public String extractUsername(String token) {
    return Jwts.parser()
    .verifyWith(getSignInKey())
    .build()
    .parseSignedClaims(token)
    .getPayload()
    .getSubject();
  }

  private boolean isTokenExpired(String token) {
    java.util.Date expiration = Jwts.parser()
    .verifyWith(getSignInKey())
    .build()
    .parseSignedClaims(token)
    .getPayload()
    .getExpiration();
    return expiration.before(new java.util.Date());
  }
}
