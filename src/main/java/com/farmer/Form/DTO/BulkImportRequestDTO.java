package com.farmer.Form.DTO;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkImportRequestDTO {
    private String importType; // "FARMER" or "EMPLOYEE"
    private String assignedEmployeeEmail; // For farmer imports, assign to this employee
    private Boolean autoAssign; // Whether to auto-assign farmers to employees
    private String assignmentStrategy; // "ROUND_ROBIN", "BY_LOCATION", "MANUAL"
}
