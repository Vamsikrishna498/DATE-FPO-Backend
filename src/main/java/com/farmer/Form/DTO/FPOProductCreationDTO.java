package com.farmer.Form.DTO;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOProductCreationDTO {
    
    private Long categoryId;
    
    @NotBlank(message = "Product name is required")
    private String productName;

    private String description;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private Double price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be non-negative")
    private Integer stockQuantity;

    @Min(value = 0, message = "Minimum stock must be non-negative")
    private Integer minimumStock;

    @NotBlank(message = "Supplier is required")
    private String supplier;

    private String supplierContact;
    private String supplierAddress;
    private String batchNumber;
    private LocalDate expiryDate;
    private String photoFileName;
    private String remarks;
    private Double discountPercentage;
    private Double taxPercentage;
}
