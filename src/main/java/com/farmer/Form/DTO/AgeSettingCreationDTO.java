package com.farmer.Form.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgeSettingCreationDTO {
    
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;
    
    @NotNull(message = "Minimum value is required")
    @Min(value = 1, message = "Minimum value must be at least 1")
    @Max(value = 100, message = "Minimum value must not exceed 100")
    private Integer minValue;
    
    @NotNull(message = "Maximum value is required")
    @Min(value = 1, message = "Maximum value must be at least 1")
    @Max(value = 100, message = "Maximum value must not exceed 100")
    private Integer maxValue;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @NotBlank(message = "User type is required")
    private String userType;
    
    @Builder.Default
    private Boolean isActive = true;
    
    private String createdBy;
}
