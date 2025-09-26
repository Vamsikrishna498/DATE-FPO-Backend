package com.farmer.Form.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingUpdateDTO {
    
    private String settingValue;
    private String description;
    private Boolean isActive;
    private String updatedBy;
}
