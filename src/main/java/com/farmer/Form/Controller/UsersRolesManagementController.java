package com.farmer.Form.Controller;

import com.farmer.Form.DTO.UserRoleCreationDTO;
import com.farmer.Form.DTO.UserRoleDTO;
import com.farmer.Form.DTO.UserRoleUpdateDTO;
import com.farmer.Form.Service.ConfigurationService;
import com.farmer.Form.Service.UserService;
import com.farmer.Form.Entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users-roles-management")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UsersRolesManagementController {

    private final ConfigurationService configurationService;
    private final UserService userService;

    /**
     * Maps UserRole names to Role enum values
     */
    private com.farmer.Form.Entity.Role mapUserRoleToEnum(String roleName) {
        if (roleName == null) {
            return com.farmer.Form.Entity.Role.FARMER; // Default role
        }
        
        // Direct mapping for exact matches
        try {
            return com.farmer.Form.Entity.Role.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            // Handle custom role names by mapping to closest enum value
            switch (roleName.toUpperCase()) {
                case "SUPER_ADMIN":
                case "SUPERADMIN":
                    return com.farmer.Form.Entity.Role.SUPER_ADMIN;
                case "ADMIN":
                case "ADMINISTRATOR":
                    return com.farmer.Form.Entity.Role.ADMIN;
                case "EMPLOYEE":
                case "STAFF":
                case "WORKER":
                    return com.farmer.Form.Entity.Role.EMPLOYEE;
                case "FARMER":
                case "FARMER_USER":
                    return com.farmer.Form.Entity.Role.FARMER;
                case "FPO":
                case "FPO_USER":
                case "FPO_ADMIN":
                case "FPO_EMPLOYEE":
                    return com.farmer.Form.Entity.Role.FPO;
                default:
                    log.warn("Unknown role name: {}, defaulting to FARMER", roleName);
                    return com.farmer.Form.Entity.Role.FARMER;
            }
        }
    }

    // --- Role Management Endpoints (using existing UserRole system) ---

    @PostMapping("/roles")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<UserRoleDTO> createRole(
            @Valid @RequestBody UserRoleCreationDTO creationDTO,
            Authentication authentication) {
        creationDTO.setCreatedBy(authentication.getName());
        UserRoleDTO createdRole = configurationService.createUserRole(creationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }

    @PutMapping("/roles/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<UserRoleDTO> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UserRoleUpdateDTO updateDTO,
            Authentication authentication) {
        updateDTO.setUpdatedBy(authentication.getName());
        UserRoleDTO updatedRole = configurationService.updateUserRole(id, updateDTO);
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/roles/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> deleteRole(@PathVariable Long id) {
        configurationService.deleteUserRole(id);
        return ResponseEntity.ok(Map.of("message", "Role deleted successfully"));
    }

    @GetMapping("/debug/check-tables")
    public ResponseEntity<Map<String, Object>> checkTables() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Try to get count of user_roles table
            long roleCount = configurationService.getAllUserRoles().size();
            response.put("user_roles_table_exists", true);
            response.put("user_roles_count", roleCount);
            response.put("status", "success");
        } catch (Exception e) {
            response.put("user_roles_table_exists", false);
            response.put("error", e.getMessage());
            response.put("status", "error");
            log.error("Error checking tables: {}", e.getMessage(), e);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<UserRoleDTO>> getAllRoles() {
        try {
            List<UserRoleDTO> roles = configurationService.getAllUserRoles();
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            log.error("Error getting all roles: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/roles/active")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<UserRoleDTO>> getActiveRoles() {
        List<UserRoleDTO> roles = configurationService.getAllUserRoles().stream()
                .filter(role -> role.getIsActive())
                .toList();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/roles/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<UserRoleDTO> getRoleById(@PathVariable Long id) {
        UserRoleDTO role = configurationService.getUserRoleById(id);
        return ResponseEntity.ok(role);
    }

    @GetMapping("/roles/search")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<UserRoleDTO>> searchRoles(@RequestParam String searchTerm) {
        List<UserRoleDTO> roles = configurationService.getAllUserRoles().stream()
                .filter(role -> role.getRoleName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                               (role.getDescription() != null && role.getDescription().toLowerCase().contains(searchTerm.toLowerCase())))
                .toList();
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/roles/{id}/activate")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<UserRoleDTO> activateRole(@PathVariable Long id, Authentication authentication) {
        UserRoleDTO role = configurationService.getUserRoleById(id);
        UserRoleUpdateDTO updateDTO = UserRoleUpdateDTO.builder()
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .allowedModules(role.getAllowedModules())
                .permissions(role.getPermissions())
                .isActive(true)
                .updatedBy(authentication.getName())
                .build();
        UserRoleDTO activatedRole = configurationService.updateUserRole(id, updateDTO);
        return ResponseEntity.ok(activatedRole);
    }

    @PostMapping("/roles/{id}/deactivate")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<UserRoleDTO> deactivateRole(@PathVariable Long id, Authentication authentication) {
        UserRoleDTO role = configurationService.getUserRoleById(id);
        UserRoleUpdateDTO updateDTO = UserRoleUpdateDTO.builder()
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .allowedModules(role.getAllowedModules())
                .permissions(role.getPermissions())
                .isActive(false)
                .updatedBy(authentication.getName())
                .build();
        UserRoleDTO deactivatedRole = configurationService.updateUserRole(id, updateDTO);
        return ResponseEntity.ok(deactivatedRole);
    }

    // --- User Management Endpoints ---

    @GetMapping("/users")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<User> users = userService.getAllUsersRaw();
        List<Map<String, Object>> userList = users.stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("name", user.getName() != null ? user.getName() : "N/A");
                    userMap.put("email", user.getEmail());
                    userMap.put("phoneNumber", user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A");
                    userMap.put("role", user.getRole() != null ? user.getRole().toString() : "N/A");
                    userMap.put("status", user.getStatus() != null ? user.getStatus().toString() : "N/A");
                    userMap.put("kycStatus", user.getKycStatus() != null ? user.getKycStatus().toString() : "N/A");
                    return userMap;
                })
                .toList();
        return ResponseEntity.ok(userList);
    }

    @PostMapping("/assign-role")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> assignRoleToUser(
            @RequestBody Map<String, Object> assignmentData,
            Authentication authentication) {
        try {
            log.info("Received assignment data: {}", assignmentData);
            
            // Validate input data
            if (assignmentData == null || assignmentData.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Assignment data is required"));
            }
            
            Object userIdObj = assignmentData.get("userId");
            Object roleIdObj = assignmentData.get("roleId");
            
            if (userIdObj == null || roleIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "userId and roleId are required"));
            }
            
            Long userId = Long.valueOf(userIdObj.toString());
            Long roleId = Long.valueOf(roleIdObj.toString());
            
            log.info("Processing assignment: userId={}, roleId={}", userId, roleId);
            
            // Get user and role
            User user = userService.getUserRawById(userId);
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found with id: " + userId));
            }
            
            UserRoleDTO role = configurationService.getUserRoleById(roleId);
            if (role == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Role not found with id: " + roleId));
            }
            
            log.info("Found user: {} and role: {}", user.getEmail(), role.getRoleName());
            
            // Update user's role - map UserRole names to Role enum values
            com.farmer.Form.Entity.Role mappedRole = mapUserRoleToEnum(role.getRoleName());
            user.setRole(mappedRole);
            userService.updateUserBySuperAdmin(userId, user);
            
            log.info("Successfully assigned role {} (mapped to {}) to user {} by {}", 
                    role.getRoleName(), mappedRole, user.getEmail(), authentication.getName());
            
            return ResponseEntity.ok(Map.of(
                    "message", "Role assigned successfully",
                    "user", user.getName(),
                    "role", role.getRoleName(),
                    "mappedRole", mappedRole.toString()
            ));
        } catch (NumberFormatException e) {
            log.error("Invalid number format in assignment data: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid userId or roleId format"));
        } catch (Exception e) {
            log.error("Failed to assign role: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to assign role: " + e.getMessage()));
        }
    }

    // --- Dashboard Data ---

    @GetMapping("/dashboard-data")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        List<UserRoleDTO> allRoles = configurationService.getAllUserRoles();
        long activeRolesCount = allRoles.stream().mapToLong(role -> role.getIsActive() ? 1 : 0).sum();
        long totalRolesCount = allRoles.size();

        Map<String, Object> dashboardData = Map.of(
                "totalRoles", totalRolesCount,
                "activeRoles", activeRolesCount,
                "inactiveRoles", totalRolesCount - activeRolesCount
        );

        return ResponseEntity.ok(dashboardData);
    }
}
