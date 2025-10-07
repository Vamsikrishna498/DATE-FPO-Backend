package com.farmer.Form.security;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.farmer.Form.Entity.FPOUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.expiration:86400000}")
    private long expirationTime;

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
 
    // Generate JWT token
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
 
        // Get roles from authorities and remove ROLE_ prefix for storage
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replace("ROLE_", ""))
                .toList();
 
        // Create claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
 
        return Jwts.builder().claims(claims) // Add custom claims
                .subject(username) // Set the subject (username)
                .issuedAt(new Date()) // Set issue date
                .expiration(new Date(System.currentTimeMillis() + expirationTime)) // Set expiration date
                .signWith(getSigningKey()) // FIX: Removed MacAlgorithm parameter
                .compact();
    }
 
    // Extract claims from token
    public Claims extractClaims(String token) {
        return Jwts.parser() // Use the new parser method
                .verifyWith(getSigningKey()) // Corrected method for signature verification
                .build().parseSignedClaims(token) // New method for parsing signed claims
                .getPayload(); // Retrieve the claims
    }
 
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }
 
    // Method to validate JWT (optional)
    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
 
    // Method to check if token is expired
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date(System.currentTimeMillis()));
    }

    // Generate JWT token for FPO user
    public String generateTokenForFPOUser(FPOUser fpoUser) {
        try {
            String username = fpoUser.getEmail();
            
            // Create claims
            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", java.util.Arrays.asList("FPO"));
            claims.put("userId", fpoUser.getId());
            claims.put("fpoId", fpoUser.getFpo() != null ? fpoUser.getFpo().getId() : null);
            
            String token = Jwts.builder()
                    .claims(claims)
                    .subject(username)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + expirationTime))
                    .signWith(getSigningKey())
                    .compact();
            
            log.info("JWT Generation - Username: {}", username);
            log.debug("JWT Generation - Token length: {}", (token != null ? token.length() : "null"));
            
            return token;
        } catch (Exception e) {
            log.error("JWT Generation Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate JWT token for FPO user", e);
        }
    }

}
