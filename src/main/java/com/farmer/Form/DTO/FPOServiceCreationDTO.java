package com.farmer.Form.DTO;

import com.farmer.Form.Entity.FPOService;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOServiceCreationDTO {
    
    private Long farmerId;
    
    @NotNull(message = "Service type is required")
    private FPOService.ServiceType serviceType;

    @NotBlank(message = "Service description is required")
    private String description;

    private LocalDateTime scheduledAt;
    private String serviceProvider;
    private String serviceProviderContact;
    private Double serviceCost;
    private String paymentStatus;
    private String remarks;
}
