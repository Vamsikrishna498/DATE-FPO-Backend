package com.farmer.Form.DTO;

import com.farmer.Form.Entity.FPOService;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOServiceDTO {
    private Long id;
    private Long fpoId;
    private String fpoName;
    private Long farmerId;
    private String farmerName;
    private FPOService.ServiceType serviceType;
    private String description;
    private FPOService.ServiceStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime scheduledAt;
    private LocalDateTime completedAt;
    private String serviceProvider;
    private String serviceProviderContact;
    private Double serviceCost;
    private String paymentStatus;
    private String remarks;
    private String result;
    private String reportFileName;
}
