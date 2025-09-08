package com.farmer.Form.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOProductCategoryCreationDTO {
    
    @NotBlank(message = "Category name is required")
    private String categoryName;

    private String description;
}
