package com.farmer.Form.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fpo_crops")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOCrop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fpo_id", nullable = false)
    private FPO fpo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;

    @NotBlank(message = "Crop name is required")
    @Column(nullable = false)
    private String cropName;

    @NotBlank(message = "Variety is required")
    private String variety;

    @NotNull(message = "Area is required")
    @DecimalMin(value = "0.1", message = "Area must be greater than 0")
    private Double area; // in acres

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Season season;

    @NotNull(message = "Sowing date is required")
    private java.time.LocalDate sowingDate;

    private java.time.LocalDate expectedHarvestDate;
    private java.time.LocalDate actualHarvestDate;

    @DecimalMin(value = "0", message = "Expected yield must be non-negative")
    private Double expectedYield; // in quintals

    @DecimalMin(value = "0", message = "Actual yield must be non-negative")
    private Double actualYield; // in quintals

    @DecimalMin(value = "0", message = "Market price must be non-negative")
    private Double marketPrice; // per quintal

    @DecimalMin(value = "0", message = "Total revenue must be non-negative")
    private Double totalRevenue;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CropStatus status = CropStatus.PLANNED;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Additional details
    private String soilType;
    private String irrigationMethod;
    private String seedSource;
    private String fertilizerUsed;
    private String pesticideUsed;
    private String remarks;
    private String photoFileName;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Season {
        KHARIF,
        RABI,
        ZAID,
        YEAR_ROUND
    }

    public enum CropStatus {
        PLANNED,
        SOWED,
        GROWING,
        READY_FOR_HARVEST,
        HARVESTED,
        SOLD,
        FAILED
    }
}
