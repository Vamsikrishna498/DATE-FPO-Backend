package com.farmer.Form.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fpo_products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fpo_id", nullable = false)
    private FPO fpo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private FPOProductCategory category;

    @NotBlank(message = "Product name is required")
    @Column(nullable = false)
    private String productName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Unit is required")
    private String unit; // kg, liter, piece, etc.

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Column(nullable = false)
    private Double price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be non-negative")
    @Column(nullable = false)
    private Integer stockQuantity;

    @Min(value = 0, message = "Minimum stock must be non-negative")
    private Integer minimumStock;

    @NotBlank(message = "Supplier is required")
    private String supplier;

    private String supplierContact;
    private String supplierAddress;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProductStatus status = ProductStatus.AVAILABLE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Additional details
    private String batchNumber;
    private java.time.LocalDate expiryDate;
    private String photoFileName;
    private String remarks;
    private Double discountPercentage;
    private Double taxPercentage;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ProductStatus {
        AVAILABLE,
        OUT_OF_STOCK,
        DISCONTINUED,
        UNDER_REVIEW
    }
}
