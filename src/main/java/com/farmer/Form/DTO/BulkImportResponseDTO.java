package com.farmer.Form.DTO;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkImportResponseDTO {
    private String importId;
    private String importType;
    private LocalDateTime importDate;
    private Integer totalRecords;
    private Integer successfulImports;
    private Integer failedImports;
    private Integer skippedRecords;
    private String status; // "PROCESSING", "COMPLETED", "FAILED"
    private String message;
    private List<ImportErrorDTO> errors;
    private String downloadUrl; // URL to download error report
    private String templateUrl; // URL to download template
}
