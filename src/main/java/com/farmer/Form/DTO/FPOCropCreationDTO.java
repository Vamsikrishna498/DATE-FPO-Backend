package com.farmer.Form.DTO;

import com.farmer.Form.Entity.FPOCrop;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOCropCreationDTO {
    
    private Long farmerId;
    
    @NotBlank(message = "Crop name is required")
    private String cropName;

    @NotBlank(message = "Variety is required")
    private String variety;

    @NotNull(message = "Area is required")
    @DecimalMin(value = "0.1", message = "Area must be greater than 0")
    private Double area;

    @NotNull(message = "Season is required")
    private FPOCrop.Season season;

    @NotNull(message = "Sowing date is required")
    private LocalDate sowingDate;

    private LocalDate expectedHarvestDate;
    private Double expectedYield;
    private Double marketPrice;
    private String soilType;
    private String irrigationMethod;
    private String seedSource;
    private String fertilizerUsed;
    private String pesticideUsed;
    private String remarks;
    private String photoFileName;
}
