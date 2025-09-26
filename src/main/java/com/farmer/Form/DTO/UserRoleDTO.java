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
        return UserRoleDTO.builder()
                .id(userRole.getId())
                .roleName(userRole.getRoleName())
                .description(userRole.getDescription())
                .allowedModules(userRole.getAllowedModules())
                .permissions(userRole.getPermissions())
                .isActive(userRole.getIsActive())
                .createdBy(userRole.getCreatedBy())
                .updatedBy(userRole.getUpdatedBy())
                .createdAt(userRole.getCreatedAt())
                .updatedAt(userRole.getUpdatedAt())
                .build();
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

