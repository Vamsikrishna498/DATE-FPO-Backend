package com.farmer.Form.Service;

import com.farmer.Form.DTO.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface BulkImportExportService {
    
    // Bulk Import Operations
    BulkImportResponseDTO importFarmersFromFile(MultipartFile file, BulkImportRequestDTO request);
    BulkImportResponseDTO importEmployeesFromFile(MultipartFile file, BulkImportRequestDTO request);
    
    // Bulk Export Operations
    byte[] exportFarmersToFile(BulkExportRequestDTO request);
    byte[] exportEmployeesToFile(BulkExportRequestDTO request);
    
    // Template Operations
    byte[] downloadFarmerTemplate();
    byte[] downloadEmployeeTemplate();
    
    // Assignment Operations
    void bulkAssignFarmersToEmployee(List<Long> farmerIds, Long employeeId);
    void bulkAssignFarmersByLocation(String location, Long employeeId);
    void bulkAssignFarmersRoundRobin(List<Long> farmerIds);
    
    // Status Operations
    BulkImportResponseDTO getImportStatus(String importId);
    List<BulkImportResponseDTO> getImportHistory(String userEmail);

    // New: assignment by names and email
    void bulkAssignFarmersByNames(java.util.List<String> farmerNames, String employeeEmail);
    void bulkAssignFarmersByLocationToEmail(String location, String employeeEmail);
    
    // Validation Operations
    List<ImportErrorDTO> validateFarmerData(List<String[]> data);
    List<ImportErrorDTO> validateEmployeeData(List<String[]> data);
}
