package com.farmer.Form.security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtutil;

    public JwtAuthenticationFilter(JwtUtil jwtutil) {
        this.jwtutil = jwtutil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Log all incoming requests for debugging
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        log.debug("JWT Filter processing: {} {}", method, requestUri);

        // Define a list of public URLs (no token required)
        String[] publicUrls = {
            "/api/auth",
            "/api/public",
            "/api/super-admin/dashboard",
            "/api/admin/farmers-with-kyc",
            "/error"
        };

        // Check if the requested URL is public
        boolean isPublicUrl = false;
        for (String url : publicUrls) {
            if (requestUri.startsWith(url)) {
                isPublicUrl = true;
                log.debug("Public URL detected: {} matches {}", requestUri, url);
                break;
            }
        }

        // If it's a public URL, skip token validation
        if (isPublicUrl) {
            log.debug("Skipping JWT validation for public URL: {}", requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        // Get the JWT token from the Authorization header
        String authorizationHeader = request.getHeader("Authorization");
        String jwtToken = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
        }

        if (jwtToken == null) {
            log.info("No token found in request for URL: {}", requestUri);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token is missing");
            return;
        }

        try {
            // Validate token
            if (!jwtutil.validateToken(jwtToken)) {
                log.info("Invalid token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token is invalid or expired");
                return;
            }

            // Extract claims
            Claims claims = jwtutil.extractClaims(jwtToken);
            String username = claims.get("sub", String.class);

            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);

            // ✅ Convert roles to Spring Security authorities with ROLE_ prefix
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    username,
                    "",
                    roles.stream()
                         .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                         .collect(Collectors.toList())
            );

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token is expired");
            return;
        } catch (Exception e) {
            log.error("JWT processing failed: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token is invalid");
            return;
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}
