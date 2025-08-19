package com.farmer.Form.Controller;

import com.farmer.Form.Entity.User;
import com.farmer.Form.Entity.Role;
import com.farmer.Form.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-management")
@RequiredArgsConstructor
@Slf4j
public class UserManagementController {

    private final UserRepository userRepository;

    // Get current user info
    @GetMapping("/current-user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        if (authentication != null) {
            response.put("username", authentication.getName());
            response.put("authorities", authentication.getAuthorities());
            response.put("principal", authentication.getPrincipal().toString());
            
            // Get user from database
            Optional<User> userOpt = userRepository.findByEmail(authentication.getName());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                response.put("databaseRole", user.getRole());
                response.put("userStatus", user.getStatus());
                response.put("userId", user.getId());
            }
        }
        
        return ResponseEntity.ok(response);
    }

    // Update user role (for testing purposes)
    @PutMapping("/update-role/{email}")
    public ResponseEntity<Map<String, Object>> updateUserRole(
            @PathVariable String email,
            @RequestParam Role newRole) {
        
        Map<String, Object> response = new HashMap<>();
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(newRole);
            userRepository.save(user);
            
            response.put("message", "Role updated successfully");
            response.put("email", email);
            response.put("newRole", newRole);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "User not found");
            return ResponseEntity.notFound().build();
        }
    }

    // List all users with their roles
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        
        Iterable<User> users = userRepository.findAll();
        Map<String, Object> userList = new HashMap<>();
        
        for (User user : users) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("name", user.getName());
            userInfo.put("role", user.getRole());
            userInfo.put("status", user.getStatus());
            userList.put(user.getEmail(), userInfo);
        }
        
        response.put("users", userList);
        return ResponseEntity.ok(response);
    }
}
