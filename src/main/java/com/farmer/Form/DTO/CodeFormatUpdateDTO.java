package com.farmer.Form.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeFormatUpdateDTO {
    
    private String prefix;
    private Integer startingNumber;
    
    private Integer currentNumber; // Optional - will preserve existing if not provided
    
    private String description;
    
    private Boolean isActive;
    
    private String updatedBy;
}
