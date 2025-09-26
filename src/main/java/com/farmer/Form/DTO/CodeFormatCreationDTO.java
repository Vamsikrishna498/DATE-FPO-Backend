package com.farmer.Form.DTO;

import com.farmer.Form.Entity.CodeFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeFormatCreationDTO {
    
    private CodeFormat.CodeType codeType;
    private String prefix;
    private Integer startingNumber;
    private String description;
    private String createdBy;
}
