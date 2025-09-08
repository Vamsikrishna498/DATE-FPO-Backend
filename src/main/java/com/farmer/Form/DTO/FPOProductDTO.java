package com.farmer.Form.DTO;

import com.farmer.Form.Entity.FPOProduct;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOProductDTO {
    private Long id;
    private Long fpoId;
    private String fpoName;
    private Long categoryId;
    private String categoryName;
    private String productName;
    private String description;
    private String brand;
    private String unit;
    private Double price;
    private Integer stockQuantity;
    private Integer minimumStock;
    private String supplier;
    private String supplierContact;
    private String supplierAddress;
    private FPOProduct.ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String batchNumber;
    private LocalDate expiryDate;
    private String photoFileName;
    private String remarks;
    private Double discountPercentage;
    private Double taxPercentage;
}
