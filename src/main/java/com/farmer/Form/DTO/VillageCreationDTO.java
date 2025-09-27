package com.farmer.Form.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VillageCreationDTO {
    
    @NotBlank(message = "Village name is required")
    @Size(max = 100, message = "Village name must not exceed 100 characters")
    private String name;
    
    @NotNull(message = "Block ID is required")
    private Long blockId;
}
