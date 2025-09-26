package com.farmer.Form.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateUpdateDTO {
    
    private String templateName;
    private String subject;
    private String content;
    private String placeholders;
    private Boolean isActive;
    private String updatedBy;
}
