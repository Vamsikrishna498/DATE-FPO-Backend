package com.farmer.Form.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private Long totalFarmers;
    private Long totalEmployees;
    private Long pendingUsers;
    private Long approvedUsers;
    private Long totalFPO;
    private Long pendingEmployees;
    private Long pendingFarmers;
    private Long approvedEmployees;
    private Long approvedFarmers;
    private Long kycApprovedFarmers;
} 