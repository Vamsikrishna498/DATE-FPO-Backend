package com.farmer.Form.DTO;

import com.farmer.Form.Entity.CodeFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeFormatDTO {
    
    private Long id;
    private CodeFormat.CodeType codeType;
    private String prefix;
    private Integer startingNumber;
    private Integer currentNumber;
    private String description;
    private Boolean isActive;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static CodeFormatDTO fromEntity(CodeFormat codeFormat) {
        return CodeFormatDTO.builder()
                .id(codeFormat.getId())
                .codeType(codeFormat.getCodeType())
                .prefix(codeFormat.getPrefix())
                .startingNumber(codeFormat.getStartingNumber())
                .currentNumber(codeFormat.getCurrentNumber())
                .description(codeFormat.getDescription())
                .isActive(codeFormat.getIsActive())
                .createdBy(codeFormat.getCreatedBy())
                .updatedBy(codeFormat.getUpdatedBy())
                .createdAt(codeFormat.getCreatedAt())
                .updatedAt(codeFormat.getUpdatedAt())
                .build();
    }
    
    public CodeFormat toEntity() {
        return CodeFormat.builder()
                .id(this.id)
                .codeType(this.codeType)
                .prefix(this.prefix)
                .startingNumber(this.startingNumber)
                .currentNumber(this.currentNumber)
                .description(this.description)
                .isActive(this.isActive)
                .createdBy(this.createdBy)
                .updatedBy(this.updatedBy)
                .build();
    }
}

