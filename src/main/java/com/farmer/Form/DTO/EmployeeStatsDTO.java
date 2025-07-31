package com.farmer.Form.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeStatsDTO {
    private Long id;
    private String name;
    private Long totalAssigned;
    private Long approved;
    private Long pending;
    private Long referBack;
    private Long rejected;
} 