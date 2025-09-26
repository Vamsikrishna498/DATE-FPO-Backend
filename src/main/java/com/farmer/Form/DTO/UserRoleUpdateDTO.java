package com.farmer.Form.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleUpdateDTO {
    
    private String roleName;
    private String description;
    private Set<String> allowedModules;
    private Set<String> permissions;
    private Boolean isActive;
    private String updatedBy;
}
