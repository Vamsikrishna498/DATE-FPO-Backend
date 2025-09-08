package com.farmer.Form.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fpo_services")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fpo_id", nullable = false)
    private FPO fpo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @NotBlank(message = "Service description is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ServiceStatus status = ServiceStatus.REQUESTED;

    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Service details
    private LocalDateTime scheduledAt;
    private LocalDateTime completedAt;
    private String serviceProvider;
    private String serviceProviderContact;
    private Double serviceCost;
    private String paymentStatus;
    private String remarks;
    private String result;
    private String reportFileName;

    @PrePersist
    protected void onCreate() {
        requestedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ServiceType {
        SOIL_TEST,
        WATER_TEST,
        MACHINERY_RENTAL,
        SEED_SUPPLY,
        FERTILIZER_SUPPLY,
        PESTICIDE_SUPPLY,
        TECHNICAL_SUPPORT,
        MARKETING_SUPPORT,
        TRAINING,
        INSURANCE,
        CREDIT_FACILITY,
        OTHER
    }

    public enum ServiceStatus {
        REQUESTED,
        APPROVED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        REJECTED
    }
}
