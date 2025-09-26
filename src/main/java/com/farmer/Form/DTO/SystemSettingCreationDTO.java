package com.farmer.Form.DTO;

import com.farmer.Form.Entity.SystemSetting;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingCreationDTO {
    
    private SystemSetting.SettingCategory settingCategory;
    private String settingKey;
    private String settingValue;
    private String description;
    private SystemSetting.DataType dataType;
    private String createdBy;
}
