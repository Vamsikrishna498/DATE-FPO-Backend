package com.farmer.Form.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fpos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String fpoId; // Auto-generated unique FPO ID

    @NotBlank(message = "FPO Name is required")
    @Column(nullable = false)
    private String fpoName;

    @NotBlank(message = "CEO/Contact Person Name is required")
    @Column(nullable = false)
    private String ceoName;

    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    @Column(nullable = false)
    private String phoneNumber;

    @Email(message = "Please provide a valid email address")
    private String email;

    // Address
    @NotBlank(message = "Village is required")
    private String village;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "State is required")
    private String state;

    @Pattern(regexp = "^\\d{6}$", message = "Pincode must be 6 digits")
    @Column(nullable = false)
    private String pincode;

    @NotNull(message = "Join Date is required")
    @Column(nullable = false)
    private LocalDate joinDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationType registrationType;

    @Min(value = 1, message = "Number of members must be at least 1")
    @Column(nullable = false)
    private Integer numberOfMembers;

    // Additional FPO Details
    private String registrationNumber;
    private String panNumber;
    private String gstNumber;
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String branchName;

    // Status and Management
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private FPOStatus status = FPOStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Relationships - will be added after other entities are created
    // @OneToMany(mappedBy = "fpo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<FPOMember> members;

    // @OneToMany(mappedBy = "fpo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<FPOBoardMember> boardMembers;

    // @OneToMany(mappedBy = "fpo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<FPOService> services;

    // @OneToMany(mappedBy = "fpo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<FPOCrop> crops;

    // @OneToMany(mappedBy = "fpo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<FPOTurnover> turnovers;

    // @OneToMany(mappedBy = "fpo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<FPOProduct> products;

    // @OneToMany(mappedBy = "fpo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<FPONotification> notifications;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (fpoId == null) {
            fpoId = generateFPOId();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String generateFPOId() {
        // Generate FPO ID in format: FPO + YYYY + 6-digit sequence
        String year = String.valueOf(LocalDate.now().getYear());
        String sequence = String.format("%06d", System.currentTimeMillis() % 1000000);
        return "FPO" + year + sequence;
    }

    public enum RegistrationType {
        COMPANY,
        COOPERATIVE,
        SOCIETY
    }

    public enum FPOStatus {
        ACTIVE,
        INACTIVE,
        SUSPENDED,
        UNDER_REVIEW
    }
}
