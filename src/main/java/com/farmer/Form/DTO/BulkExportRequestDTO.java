package com.farmer.Form.DTO;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkExportRequestDTO {
    private String exportType; // "FARMER" or "EMPLOYEE"
    private String format; // "EXCEL" or "CSV"
    private String assignedEmployeeEmail;
    private String location; // district, state, etc.
    private LocalDate fromDate;
    private LocalDate toDate;
    private String kycStatus;
    private List<String> includeFields; // Specific fields to include
    private Boolean includeHeaders;
}
