package com.farmer.Form.DTO;

import com.farmer.Form.Entity.SystemPreference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemPreferenceDTO {
    
    private Long id;
    private String preferenceKey;
    private String preferenceValue;
    private String description;
    private SystemPreference.PreferenceType preferenceType;
    private Boolean isActive;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static SystemPreferenceDTO fromEntity(SystemPreference preference) {
        return SystemPreferenceDTO.builder()
                .id(preference.getId())
                .preferenceKey(preference.getPreferenceKey())
                .preferenceValue(preference.getPreferenceValue())
                .description(preference.getDescription())
                .preferenceType(preference.getPreferenceType())
                .isActive(preference.getIsActive())
                .createdBy(preference.getCreatedBy())
                .updatedBy(preference.getUpdatedBy())
                .createdAt(preference.getCreatedAt())
                .updatedAt(preference.getUpdatedAt())
                .build();
    }
    
    public SystemPreference toEntity() {
        return SystemPreference.builder()
                .id(this.id)
                .preferenceKey(this.preferenceKey)
                .preferenceValue(this.preferenceValue)
                .description(this.description)
                .preferenceType(this.preferenceType)
                .isActive(this.isActive)
                .createdBy(this.createdBy)
                .updatedBy(this.updatedBy)
                .build();
    }
}

