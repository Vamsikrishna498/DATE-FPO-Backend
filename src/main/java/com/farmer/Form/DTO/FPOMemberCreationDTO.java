package com.farmer.Form.DTO;

import com.farmer.Form.Entity.FPOMember;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOMemberCreationDTO {
    
    @NotNull(message = "Member type is required")
    private FPOMember.MemberType memberType;
    
    private Long farmerId;
    private Long employeeId;
    private Long userId;
    private String shareAmount;
    private String shareCertificateNumber;
    private String remarks;
}
