package com.farmer.Form.Controller;

import com.farmer.Form.DTO.*;
import com.farmer.Form.Service.BulkImportExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bulk")
@RequiredArgsConstructor
@Slf4j
public class BulkImportExportController {

    private final BulkImportExportService bulkService;

    // Bulk Import Endpoints
    @PostMapping("/import/farmers")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<BulkImportResponseDTO> importFarmers(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "assignedEmployeeEmail", required = false) String assignedEmployeeEmail,
            @RequestParam(value = "autoAssign", defaultValue = "false") Boolean autoAssign,
            @RequestParam(value = "assignmentStrategy", defaultValue = "MANUAL") String assignmentStrategy) {
        
        try {
            BulkImportRequestDTO request = BulkImportRequestDTO.builder()
                    .importType("FARMER")
                    .assignedEmployeeEmail(assignedEmployeeEmail)
                    .autoAssign(autoAssign)
                    .assignmentStrategy(assignmentStrategy)
                    .build();

            BulkImportResponseDTO response = bulkService.importFarmersFromFile(file, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error importing farmers: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BulkImportResponseDTO.builder()
                            .status("FAILED")
                            .message("Import failed: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/import/employees")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<BulkImportResponseDTO> importEmployees(
            @RequestParam("file") MultipartFile file) {
        
        try {
            BulkImportRequestDTO request = BulkImportRequestDTO.builder()
                    .importType("EMPLOYEE")
                    .build();

            BulkImportResponseDTO response = bulkService.importEmployeesFromFile(file, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error importing employees: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BulkImportResponseDTO.builder()
                            .status("FAILED")
                            .message("Import failed: " + e.getMessage())
                            .build());
        }
    }

    // Bulk Export Endpoints
    @PostMapping("/export/farmers")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<byte[]> exportFarmers(@RequestBody BulkExportRequestDTO request) {
        try {
            byte[] fileData = bulkService.exportFarmersToFile(request);
            
            String filename = "farmers_export_" + System.currentTimeMillis();
            if ("EXCEL".equalsIgnoreCase(request.getFormat())) {
                filename += ".xlsx";
            } else {
                filename += ".csv";
            }

            HttpHeaders headers = new HttpHeaders();
            if ("EXCEL".equalsIgnoreCase(request.getFormat())) {
                headers.setContentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            } else {
                headers.setContentType(MediaType.parseMediaType("text/csv"));
            }
            headers.setContentDispositionFormData("attachment", filename);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileData);
        } catch (Exception e) {
            log.error("Error exporting farmers: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/export/employees")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<byte[]> exportEmployees(@RequestBody BulkExportRequestDTO request) {
        try {
            byte[] fileData = bulkService.exportEmployeesToFile(request);
            
            String filename = "employees_export_" + System.currentTimeMillis();
            if ("EXCEL".equalsIgnoreCase(request.getFormat())) {
                filename += ".xlsx";
            } else {
                filename += ".csv";
            }

            HttpHeaders headers = new HttpHeaders();
            if ("EXCEL".equalsIgnoreCase(request.getFormat())) {
                headers.setContentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            } else {
                headers.setContentType(MediaType.parseMediaType("text/csv"));
            }
            headers.setContentDispositionFormData("attachment", filename);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileData);
        } catch (Exception e) {
            log.error("Error exporting employees: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Template Download Endpoints
    @GetMapping("/template/farmers")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<byte[]> downloadFarmerTemplate() {
        try {
            byte[] templateData = bulkService.downloadFarmerTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "farmer_import_template.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(templateData);
        } catch (Exception e) {
            log.error("Error downloading farmer template: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/template/employees")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<byte[]> downloadEmployeeTemplate() {
        try {
            byte[] templateData = bulkService.downloadEmployeeTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "employee_import_template.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(templateData);
        } catch (Exception e) {
            log.error("Error downloading employee template: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Bulk Assignment Endpoints
    @PostMapping("/assign/farmers-by-location")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> assignFarmersByLocation(
            @RequestParam("location") String location,
            @RequestParam(value = "employeeId", required = false) Long employeeId,
            @RequestParam(value = "employeeEmail", required = false) String employeeEmail) {
        try {
            if (employeeEmail != null && !employeeEmail.isBlank()) {
                bulkService.bulkAssignFarmersByLocationToEmail(location, employeeEmail);
            } else if (employeeId != null) {
                bulkService.bulkAssignFarmersByLocation(location, employeeId);
            } else {
                return ResponseEntity.badRequest().body("Provide employeeEmail or employeeId");
            }
            return ResponseEntity.ok("Farmers assigned by location successfully");
        } catch (Exception e) {
            log.error("Error assigning farmers by location: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Assignment failed: " + e.getMessage());
        }
    }

    // New: Assign by farmer names and employee email
    @PostMapping("/assign/farmers-by-names")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> assignFarmersByNames(@RequestBody Map<String, Object> body) {
        try {
            @SuppressWarnings("unchecked")
            List<String> farmerNames = (List<String>) body.get("farmerNames");
            String employeeEmail = String.valueOf(body.get("employeeEmail"));
            bulkService.bulkAssignFarmersByNames(farmerNames, employeeEmail);
            return ResponseEntity.ok("Farmers assigned by names successfully");
        } catch (Exception e) {
            log.error("Error assigning farmers by names: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Assignment failed: " + e.getMessage());
        }
    }

    // Test endpoint to check user role
    @GetMapping("/test-role")
    public ResponseEntity<Map<String, Object>> testRole(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toList()));
        response.put("principal", authentication.getPrincipal().toString());
        return ResponseEntity.ok(response);
    }

    // Status and History Endpoints
    @GetMapping("/import/status/{importId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<BulkImportResponseDTO> getImportStatus(@PathVariable String importId) {
        try {
            BulkImportResponseDTO status = bulkService.getImportStatus(importId);
            if (status != null) {
                return ResponseEntity.ok(status);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error getting import status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/import/history")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<BulkImportResponseDTO>> getImportHistory(
            @RequestParam("userEmail") String userEmail) {
        
        try {
            List<BulkImportResponseDTO> history = bulkService.getImportHistory(userEmail);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Error getting import history: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
