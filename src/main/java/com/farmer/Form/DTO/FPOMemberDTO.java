package com.farmer.Form.DTO;

import com.farmer.Form.Entity.FPOMember;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOMemberDTO {
    private Long id;
    private Long fpoId;
    private String fpoName;
    private Long farmerId;
    private String farmerName;
    private Long employeeId;
    private String employeeName;
    private Long userId;
    private String userName;
    private FPOMember.MemberType memberType;
    private FPOMember.MemberStatus status;
    private LocalDateTime joinedAt;
    private LocalDateTime updatedAt;
    private String memberId;
    private String shareAmount;
    private String shareCertificateNumber;
    private String remarks;
}
