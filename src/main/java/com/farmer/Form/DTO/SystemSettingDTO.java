package com.farmer.Form.DTO;

import com.farmer.Form.Entity.SystemSetting;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingDTO {
    
    private Long id;
    private SystemSetting.SettingCategory settingCategory;
    private String settingKey;
    private String settingValue;
    private String description;
    private SystemSetting.DataType dataType;
    private Boolean isActive;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static SystemSettingDTO fromEntity(SystemSetting setting) {
        return SystemSettingDTO.builder()
                .id(setting.getId())
                .settingCategory(setting.getSettingCategory())
                .settingKey(setting.getSettingKey())
                .settingValue(setting.getSettingValue())
                .description(setting.getDescription())
                .dataType(setting.getDataType())
                .isActive(setting.getIsActive())
                .createdBy(setting.getCreatedBy())
                .updatedBy(setting.getUpdatedBy())
                .createdAt(setting.getCreatedAt())
                .updatedAt(setting.getUpdatedAt())
                .build();
    }
    
    public SystemSetting toEntity() {
        return SystemSetting.builder()
                .id(this.id)
                .settingCategory(this.settingCategory)
                .settingKey(this.settingKey)
                .settingValue(this.settingValue)
                .description(this.description)
                .dataType(this.dataType)
                .isActive(this.isActive)
                .createdBy(this.createdBy)
                .updatedBy(this.updatedBy)
                .build();
    }
}
