package com.farmer.Form.DTO;

import com.farmer.Form.Entity.Template;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDTO {
    
    private Long id;
    private String templateName;
    private Template.TemplateType templateType;
    private Template.ModuleType moduleType;
    private String subject;
    private String content;
    private String placeholders;
    private Boolean isActive;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static TemplateDTO fromEntity(Template template) {
        return TemplateDTO.builder()
                .id(template.getId())
                .templateName(template.getTemplateName())
                .templateType(template.getTemplateType())
                .moduleType(template.getModuleType())
                .subject(template.getSubject())
                .content(template.getContent())
                .placeholders(template.getPlaceholders())
                .isActive(template.getIsActive())
                .createdBy(template.getCreatedBy())
                .updatedBy(template.getUpdatedBy())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
    
    public Template toEntity() {
        return Template.builder()
                .id(this.id)
                .templateName(this.templateName)
                .templateType(this.templateType)
                .moduleType(this.moduleType)
                .subject(this.subject)
                .content(this.content)
                .placeholders(this.placeholders)
                .isActive(this.isActive)
                .createdBy(this.createdBy)
                .updatedBy(this.updatedBy)
                .build();
    }
}

