package com.farmer.Form.DTO;

import com.farmer.Form.Entity.Template;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateCreationDTO {
    
    private String templateName;
    private Template.TemplateType templateType;
    private Template.ModuleType moduleType;
    private String subject;
    private String content;
    private String placeholders;
    private String createdBy;
}
