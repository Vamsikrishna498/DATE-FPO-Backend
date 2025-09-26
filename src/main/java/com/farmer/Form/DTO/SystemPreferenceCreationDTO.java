package com.farmer.Form.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemPreferenceCreationDTO {
    
    private String preferenceKey;
    private String preferenceValue;
    private String description;
    private String preferenceType;
    private Boolean isActive;
    private String createdBy;
}
