package com.farmer.Form.DTO;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportErrorDTO {
    private Integer rowNumber;
    private String fieldName;
    private String fieldValue;
    private String errorMessage;
    private String errorType; // "VALIDATION", "DUPLICATE", "FORMAT", "REQUIRED"
}
