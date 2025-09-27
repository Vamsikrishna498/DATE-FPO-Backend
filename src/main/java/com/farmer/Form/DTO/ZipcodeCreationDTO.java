package com.farmer.Form.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZipcodeCreationDTO {
    
    @NotBlank(message = "Zipcode is required")
    @Pattern(regexp = "^[0-9]+$", message = "Zipcode must contain only numeric characters")
    private String code;
    
    @NotNull(message = "Village ID is required")
    private Long villageId;
}
