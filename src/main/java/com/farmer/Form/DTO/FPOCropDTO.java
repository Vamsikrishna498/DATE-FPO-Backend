package com.farmer.Form.DTO;

import com.farmer.Form.Entity.FPOCrop;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOCropDTO {
    private Long id;
    private Long fpoId;
    private String fpoName;
    private Long farmerId;
    private String farmerName;
    private String cropName;
    private String variety;
    private Double area;
    private FPOCrop.Season season;
    private LocalDate sowingDate;
    private LocalDate expectedHarvestDate;
    private LocalDate actualHarvestDate;
    private Double expectedYield;
    private Double actualYield;
    private Double marketPrice;
    private Double totalRevenue;
    private FPOCrop.CropStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String soilType;
    private String irrigationMethod;
    private String seedSource;
    private String fertilizerUsed;
    private String pesticideUsed;
    private String remarks;
    private String photoFileName;
}
