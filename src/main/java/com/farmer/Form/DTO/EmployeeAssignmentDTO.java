package com.farmer.Form.DTO;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAssignmentDTO {
    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    private String employeeContactNumber;
    private String employeeDesignation;
    private String employeeRole;
    private String assignmentDate;
    private String assignmentStatus; // "ACTIVE", "INACTIVE", "PENDING"
}
