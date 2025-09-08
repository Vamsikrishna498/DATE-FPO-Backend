package com.farmer.Form.DTO;

import com.farmer.Form.Entity.FPO;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPODTO {
    private Long id;
    private String fpoId;
    private String fpoName;
    private String ceoName;
    private String phoneNumber;
    private String email;
    private String village;
    private String district;
    private String state;
    private String pincode;
    private LocalDate joinDate;
    private FPO.RegistrationType registrationType;
    private Integer numberOfMembers;
    private String registrationNumber;
    private String panNumber;
    private String gstNumber;
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String branchName;
    private FPO.FPOStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for dashboard
    private Long totalMembers;
    private Long activeMembers;
    private Long totalServices;
    private Long completedServices;
    private Long totalCrops;
    private Double totalArea;
    private Double totalRevenue;
    private Long totalProducts;
    private Long lowStockProductsCount;
}
