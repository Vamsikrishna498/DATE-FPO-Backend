package com.farmer.Form.DTO;

import com.farmer.Form.Entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleDTO {
    
    private Long id;
    private String roleName;
    private String description;
    private Set<String> allowedModules;
    private Set<String> permissions;
    private Boolean isActive;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static UserRoleDTO fromEntity(UserRole userRole) {
        UserRoleDTO.UserRoleDTOBuilder builder = UserRoleDTO.builder()
                .id(userRole.getId())
                .roleName(userRole.getRoleName())
                .description(userRole.getDescription())
                .isActive(userRole.getIsActive())
                .createdBy(userRole.getCreatedBy())
                .updatedBy(userRole.getUpdatedBy())
                .createdAt(userRole.getCreatedAt())
                .updatedAt(userRole.getUpdatedAt());
        
        // Safely handle lazy-loaded collections
        try {
            Set<String> allowedModules = userRole.getAllowedModules();
            if (allowedModules != null && !allowedModules.isEmpty()) {
                builder.allowedModules(allowedModules);
            } else {
                builder.allowedModules(new java.util.HashSet<>());
            }
        } catch (Exception e) {
            // If lazy loading fails, set empty set
            builder.allowedModules(new java.util.HashSet<>());
        }
        
        try {
            Set<String> permissions = userRole.getPermissions();
            if (permissions != null && !permissions.isEmpty()) {
                builder.permissions(permissions);
            } else {
                builder.permissions(new java.util.HashSet<>());
            }
        } catch (Exception e) {
            // If lazy loading fails, set empty set
            builder.permissions(new java.util.HashSet<>());
        }
        
        return builder.build();
    }
    
    public UserRole toEntity() {
        return UserRole.builder()
                .id(this.id)
                .roleName(this.roleName)
                .description(this.description)
                .allowedModules(this.allowedModules)
                .permissions(this.permissions)
                .isActive(this.isActive)
                .createdBy(this.createdBy)
                .updatedBy(this.updatedBy)
                .build();
    }
}

