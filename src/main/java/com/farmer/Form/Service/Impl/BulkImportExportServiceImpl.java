package com.farmer.Form.Service.Impl;

import com.farmer.Form.DTO.*;
import com.farmer.Form.Entity.Farmer;
import com.farmer.Form.Entity.Employee;
import com.farmer.Form.Repository.FarmerRepository;
import com.farmer.Form.Repository.EmployeeRepository;
import com.farmer.Form.Service.BulkImportExportService;
import com.farmer.Form.Service.FarmerService;
import com.farmer.Form.Service.EmployeeService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class BulkImportExportServiceImpl implements BulkImportExportService {

    private final FarmerRepository farmerRepository;
    private final EmployeeRepository employeeRepository;
    private final FarmerService farmerService;
    private final EmployeeService employeeService;
    
    private final Map<String, BulkImportResponseDTO> importStatusMap = new ConcurrentHashMap<>();
    
    // Farmer template headers
    private static final String[] FARMER_HEADERS = {
        "Salutation", "First Name", "Middle Name", "Last Name", "Date of Birth (YYYY-MM-DD)", 
        "Gender", "Father Name", "Contact Number", "Alternative Relation Type", 
        "Alternative Contact Number", "Nationality", "Country", "State", "District", 
        "Block", "Village", "Pincode", "Education", "Experience", "Current Survey Number",
        "Current Land Holding (acres)", "Current Geo Tag", "Current Crop", "Current Net Income",
        "Current Soil Test (true/false)", "Current Water Source", "Current Discharge LPH",
        "Current Summer Discharge", "Current Borewell Location", "Proposed Survey Number",
        "Proposed Land Holding (acres)", "Proposed Geo Tag", "Proposed Crop", "Proposed Net Income",
        "Proposed Soil Test (true/false)", "Proposed Water Source", "Proposed Discharge LPH",
        "Proposed Summer Discharge", "Proposed Borewell Location", "Bank Name", "Account Number",
        "Branch Name", "IFSC Code", "Document Type", "Document Number"
    };
    
    // Employee template headers
    private static final String[] EMPLOYEE_HEADERS = {
        "Salutation", "First Name", "Middle Name", "Last Name", "Gender", "Nationality",
        "Date of Birth (YYYY-MM-DD)", "Contact Number", "Email", "Relation Type", "Relation Name",
        "Alternative Number", "Alternative Number Type", "Country", "State", "District", "Block",
        "Village", "Zipcode", "Sector", "Education", "Experience", "Bank Name", "Account Number",
        "Branch Name", "IFSC Code", "Document Type", "Document Number", "Role", "Access Status"
    };

    @Override
    public BulkImportResponseDTO importFarmersFromFile(MultipartFile file, BulkImportRequestDTO request) {
        String importId = UUID.randomUUID().toString();
        BulkImportResponseDTO response = BulkImportResponseDTO.builder()
                .importId(importId)
                .importType("FARMER")
                .importDate(LocalDateTime.now())
                .status("PROCESSING")
                .build();
        
        importStatusMap.put(importId, response);
        
        // Process asynchronously
        CompletableFuture.runAsync(() -> {
            try {
                List<String[]> data = readFileData(file);
                List<ImportErrorDTO> errors = validateFarmerData(data);
                
                int totalRecords = data.size() - 1; // Exclude header
                int successfulImports = 0;
                int failedImports = errors.size();
                int skippedRecords = 0;
                
                if (errors.isEmpty()) {
                    // Process valid records
                    for (int i = 1; i < data.size(); i++) {
                        try {
                            String[] row = data.get(i);
                            Farmer farmer = createFarmerFromRow(row, request);
                            farmerRepository.save(farmer);
                            successfulImports++;
                        } catch (Exception e) {
                            log.error("Error importing farmer at row {}: {}", i + 1, e.getMessage());
                            failedImports++;
                        }
                    }
                }
                
                response.setTotalRecords(totalRecords);
                response.setSuccessfulImports(successfulImports);
                response.setFailedImports(failedImports);
                response.setSkippedRecords(skippedRecords);
                response.setStatus("COMPLETED");
                response.setErrors(errors);
                response.setMessage("Import completed successfully");
                
            } catch (Exception e) {
                log.error("Error during bulk import: {}", e.getMessage());
                response.setStatus("FAILED");
                response.setMessage("Import failed: " + e.getMessage());
            }
        });
        
        return response;
    }

    @Override
    public BulkImportResponseDTO importEmployeesFromFile(MultipartFile file, BulkImportRequestDTO request) {
        String importId = UUID.randomUUID().toString();
        BulkImportResponseDTO response = BulkImportResponseDTO.builder()
                .importId(importId)
                .importType("EMPLOYEE")
                .importDate(LocalDateTime.now())
                .status("PROCESSING")
                .build();
        
        importStatusMap.put(importId, response);
        
        // Process asynchronously
        CompletableFuture.runAsync(() -> {
            try {
                List<String[]> data = readFileData(file);
                List<ImportErrorDTO> errors = validateEmployeeData(data);
                
                int totalRecords = data.size() - 1; // Exclude header
                int successfulImports = 0;
                int failedImports = errors.size();
                int skippedRecords = 0;
                
                if (errors.isEmpty()) {
                    // Process valid records
                    for (int i = 1; i < data.size(); i++) {
                        try {
                            String[] row = data.get(i);
                            Employee employee = createEmployeeFromRow(row);
                            employeeRepository.save(employee);
                            successfulImports++;
                        } catch (Exception e) {
                            log.error("Error importing employee at row {}: {}", i + 1, e.getMessage());
                            failedImports++;
                        }
                    }
                }
                
                response.setTotalRecords(totalRecords);
                response.setSuccessfulImports(successfulImports);
                response.setFailedImports(failedImports);
                response.setSkippedRecords(skippedRecords);
                response.setStatus("COMPLETED");
                response.setErrors(errors);
                response.setMessage("Import completed successfully");
                
            } catch (Exception e) {
                log.error("Error during bulk import: {}", e.getMessage());
                response.setStatus("FAILED");
                response.setMessage("Import failed: " + e.getMessage());
            }
        });
        
        return response;
    }

    @Override
    public byte[] exportFarmersToFile(BulkExportRequestDTO request) {
        try {
            List<Farmer> farmers = getFilteredFarmers(request);
            
            if ("EXCEL".equalsIgnoreCase(request.getFormat())) {
                return exportToExcel(farmers, FARMER_HEADERS, "Farmers");
            } else {
                return exportToCsv(farmers, FARMER_HEADERS);
            }
        } catch (Exception e) {
            log.error("Error exporting farmers: {}", e.getMessage());
            throw new RuntimeException("Export failed", e);
        }
    }

    @Override
    public byte[] exportEmployeesToFile(BulkExportRequestDTO request) {
        try {
            List<Employee> employees = getFilteredEmployees(request);
            
            if ("EXCEL".equalsIgnoreCase(request.getFormat())) {
                return exportToExcel(employees, EMPLOYEE_HEADERS, "Employees");
            } else {
                return exportToCsv(employees, EMPLOYEE_HEADERS);
            }
        } catch (Exception e) {
            log.error("Error exporting employees: {}", e.getMessage());
            throw new RuntimeException("Export failed", e);
        }
    }

    @Override
    public byte[] downloadFarmerTemplate() {
        return createTemplate(FARMER_HEADERS, "Farmer_Template");
    }

    @Override
    public byte[] downloadEmployeeTemplate() {
        return createTemplate(EMPLOYEE_HEADERS, "Employee_Template");
    }

    @Override
    public void bulkAssignFarmersToEmployee(List<Long> farmerIds, Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        for (Long farmerId : farmerIds) {
            try {
                farmerService.assignFarmerToEmployee(farmerId, employeeId);
            } catch (Exception e) {
                log.error("Error assigning farmer {} to employee {}: {}", farmerId, employeeId, e.getMessage());
            }
        }
    }

    @Override
    public void bulkAssignFarmersByLocation(String location, Long employeeId) {
        List<Farmer> farmers = farmerRepository.findByDistrictContainingIgnoreCase(location);
        for (Farmer farmer : farmers) {
            try {
                farmerService.assignFarmerToEmployee(farmer.getId(), employeeId);
            } catch (Exception e) {
                log.error("Error assigning farmer {} to employee {}: {}", farmer.getId(), employeeId, e.getMessage());
            }
        }
    }

    @Override
    public void bulkAssignFarmersByLocationToEmail(String location, String employeeEmail) {
        Employee employee = employeeRepository.findByEmail(employeeEmail)
            .orElseThrow(() -> new RuntimeException("Employee not found for email: " + employeeEmail));
        bulkAssignFarmersByLocation(location, employee.getId());
    }

    @Override
    public void bulkAssignFarmersRoundRobin(List<Long> farmerIds) {
        List<Employee> employees = employeeRepository.findByRole("FIELD_OFFICER");
        if (employees.isEmpty()) {
            throw new RuntimeException("No field officers found for assignment");
        }
        
        int employeeIndex = 0;
        for (Long farmerId : farmerIds) {
            Employee employee = employees.get(employeeIndex % employees.size());
            try {
                farmerService.assignFarmerToEmployee(farmerId, employee.getId());
            } catch (Exception e) {
                log.error("Error assigning farmer {} to employee {}: {}", farmerId, employee.getId(), e.getMessage());
            }
            employeeIndex++;
        }
    }

    @Override
    public void bulkAssignFarmersByNames(List<String> farmerNames, String employeeEmail) {
        Employee employee = employeeRepository.findByEmail(employeeEmail)
            .orElseThrow(() -> new RuntimeException("Employee not found for email: " + employeeEmail));

        if (farmerNames == null || farmerNames.isEmpty()) {
            throw new RuntimeException("No farmer names provided");
        }

        // naive match by first + last name ignoring case and extra spaces
        List<Farmer> all = farmerRepository.findAll();
        for (String name : farmerNames) {
            String normalized = name == null ? "" : name.trim().toLowerCase();
            if (normalized.isEmpty()) continue;
            Farmer match = all.stream().filter(f -> {
                String full = ((f.getFirstName() == null ? "" : f.getFirstName()) + " " + (f.getLastName() == null ? "" : f.getLastName())).trim().toLowerCase();
                return full.equals(normalized);
            }).findFirst().orElse(null);

            if (match != null) {
                try {
                    farmerService.assignFarmerToEmployee(match.getId(), employee.getId());
                } catch (Exception e) {
                    log.error("Failed to assign farmer {} to {}: {}", name, employeeEmail, e.getMessage());
                }
            } else {
                log.warn("No farmer found for name: {}", name);
            }
        }
    }

    @Override
    public BulkImportResponseDTO getImportStatus(String importId) {
        return importStatusMap.get(importId);
    }

    @Override
    public List<BulkImportResponseDTO> getImportHistory(String userEmail) {
        // This would typically query a database table for import history
        // For now, return recent imports from memory
        return new ArrayList<>(importStatusMap.values());
    }

    @Override
    public List<ImportErrorDTO> validateFarmerData(List<String[]> data) {
        List<ImportErrorDTO> errors = new ArrayList<>();
        
        for (int i = 1; i < data.size(); i++) {
            String[] row = data.get(i);
            int rowNumber = i + 1;
            
            // Validate required fields
            if (isEmpty(row[0]) || isEmpty(row[2]) || isEmpty(row[4]) || isEmpty(row[5]) || 
                isEmpty(row[7]) || isEmpty(row[10]) || isEmpty(row[11])) {
                errors.add(ImportErrorDTO.builder()
                        .rowNumber(rowNumber)
                        .fieldName("Required Fields")
                        .errorMessage("Required fields cannot be empty")
                        .errorType("REQUIRED")
                        .build());
            }
            
            // Validate contact number format
            if (!isEmpty(row[7]) && !row[7].matches("^\\d{10}$")) {
                errors.add(ImportErrorDTO.builder()
                        .rowNumber(rowNumber)
                        .fieldName("Contact Number")
                        .fieldValue(row[7])
                        .errorMessage("Contact number must be 10 digits")
                        .errorType("FORMAT")
                        .build());
            }
            
            // Validate pincode format
            if (!isEmpty(row[16]) && !row[16].matches("^\\d{6}$")) {
                errors.add(ImportErrorDTO.builder()
                        .rowNumber(rowNumber)
                        .fieldName("Pincode")
                        .fieldValue(row[16])
                        .errorMessage("Pincode must be 6 digits")
                        .errorType("FORMAT")
                        .build());
            }
            
            // Validate date format
            if (!isEmpty(row[4]) && !isValidDate(row[4])) {
                errors.add(ImportErrorDTO.builder()
                        .rowNumber(rowNumber)
                        .fieldName("Date of Birth")
                        .fieldValue(row[4])
                        .errorMessage("Date must be in YYYY-MM-DD format")
                        .errorType("FORMAT")
                        .build());
            }
        }
        
        return errors;
    }

    @Override
    public List<ImportErrorDTO> validateEmployeeData(List<String[]> data) {
        List<ImportErrorDTO> errors = new ArrayList<>();
        
        for (int i = 1; i < data.size(); i++) {
            String[] row = data.get(i);
            int rowNumber = i + 1;
            
            // Validate required fields
            if (isEmpty(row[0]) || isEmpty(row[2]) || isEmpty(row[7]) || isEmpty(row[8])) {
                errors.add(ImportErrorDTO.builder()
                        .rowNumber(rowNumber)
                        .fieldName("Required Fields")
                        .errorMessage("Required fields cannot be empty")
                        .errorType("REQUIRED")
                        .build());
            }
            
            // Validate email format
            if (!isEmpty(row[8]) && !row[8].matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                errors.add(ImportErrorDTO.builder()
                        .rowNumber(rowNumber)
                        .fieldName("Email")
                        .fieldValue(row[8])
                        .errorMessage("Invalid email format")
                        .errorType("FORMAT")
                        .build());
            }
            
            // Validate contact number format
            if (!isEmpty(row[7]) && !row[7].matches("^\\d{10}$")) {
                errors.add(ImportErrorDTO.builder()
                        .rowNumber(rowNumber)
                        .fieldName("Contact Number")
                        .fieldValue(row[7])
                        .errorMessage("Contact number must be 10 digits")
                        .errorType("FORMAT")
                        .build());
            }
        }
        
        return errors;
    }

    // Helper methods
    private List<String[]> readFileData(MultipartFile file) throws IOException {
        List<String[]> data = new ArrayList<>();
        
        if (file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
                String[] line;
                while ((line = reader.readNext()) != null) {
                    data.add(line);
                }
            } catch (CsvValidationException e) {
                throw new IOException("Invalid CSV format", e);
            }
        } else {
            try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);
                for (Row row : sheet) {
                    String[] rowData = new String[row.getLastCellNum()];
                    for (int i = 0; i < row.getLastCellNum(); i++) {
                        Cell cell = row.getCell(i);
                        rowData[i] = cell == null ? "" : getCellValueAsString(cell);
                    }
                    data.add(rowData);
                }
            }
        }
        
        return data;
    }

    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    private Farmer createFarmerFromRow(String[] row, BulkImportRequestDTO request) {
        return Farmer.builder()
                .salutation(row[0])
                .firstName(row[1])
                .middleName(row[2])
                .lastName(row[3])
                .dateOfBirth(LocalDate.parse(row[4]))
                .gender(row[5])
                .fatherName(row[6])
                .contactNumber(row[7])
                .alternativeRelationType(row[8])
                .alternativeContactNumber(row[9])
                .nationality(row[10])
                .country(row[11])
                .state(row[12])
                .district(row[13])
                .block(row[14])
                .village(row[15])
                .pincode(row[16])
                .education(row[17])
                .experience(row[18])
                .currentSurveyNumber(row[19])
                .currentLandHolding(parseDouble(row[20]))
                .currentGeoTag(row[21])
                .currentCrop(row[22])
                .currentNetIncome(parseDouble(row[23]))
                .currentSoilTest(parseBoolean(row[24]))
                .currentWaterSource(row[25])
                .currentDischargeLPH(row[26])
                .currentSummerDischarge(row[27])
                .currentBorewellLocation(row[28])
                .proposedSurveyNumber(row[29])
                .proposedLandHolding(parseDouble(row[30]))
                .proposedGeoTag(row[31])
                .proposedCrop(row[32])
                .proposedNetIncome(parseDouble(row[33]))
                .proposedSoilTest(parseBoolean(row[34]))
                .proposedWaterSource(row[35])
                .proposedDischargeLPH(row[36])
                .proposedSummerDischarge(row[37])
                .proposedBorewellLocation(row[38])
                .bankName(row[39])
                .accountNumber(row[40])
                .branchName(row[41])
                .ifscCode(row[42])
                .documentType(row[43])
                .documentNumber(row[44])
                .kycStatus(Farmer.KycStatus.PENDING)
                .build();
    }

    private Employee createEmployeeFromRow(String[] row) {
        return Employee.builder()
                .salutation(row[0])
                .firstName(row[1])
                .middleName(row[2])
                .lastName(row[3])
                .gender(row[4])
                .nationality(row[5])
                .dob(LocalDate.parse(row[6]))
                .contactNumber(row[7])
                .email(row[8])
                .relationType(row[9])
                .relationName(row[10])
                .altNumber(row[11])
                .altNumberType(row[12])
                .country(row[13])
                .state(row[14])
                .district(row[15])
                .block(row[16])
                .village(row[17])
                .zipcode(row[18])
                .sector(row[19])
                .education(row[20])
                .experience(row[21])
                .bankName(row[22])
                .accountNumber(row[23])
                .branchName(row[24])
                .ifscCode(row[25])
                .documentType(row[26])
                .documentNumber(row[27])
                .role(row[28])
                .accessStatus(row[29])
                .build();
    }

    private byte[] exportToExcel(List<?> data, String[] headers, String sheetName) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);
            
            // Header
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            // Data rows (support Farmer and Employee)
            int rowIdx = 1;
            for (Object item : data) {
                Row row = sheet.createRow(rowIdx++);
                if (item instanceof com.farmer.Form.Entity.Farmer) {
                    com.farmer.Form.Entity.Farmer f = (com.farmer.Form.Entity.Farmer) item;
                    int c = 0;
                    row.createCell(c++).setCellValue(nz(f.getSalutation()));
                    row.createCell(c++).setCellValue(nz(f.getFirstName()));
                    row.createCell(c++).setCellValue(nz(f.getMiddleName()));
                    row.createCell(c++).setCellValue(nz(f.getLastName()));
                    row.createCell(c++).setCellValue(f.getDateOfBirth() != null ? f.getDateOfBirth().toString() : "");
                    row.createCell(c++).setCellValue(nz(f.getGender()));
                    row.createCell(c++).setCellValue(nz(f.getFatherName()));
                    row.createCell(c++).setCellValue(nz(f.getContactNumber()));
                    row.createCell(c++).setCellValue(nz(f.getAlternativeRelationType()));
                    row.createCell(c++).setCellValue(nz(f.getAlternativeContactNumber()));
                    row.createCell(c++).setCellValue(nz(f.getNationality()));
                    row.createCell(c++).setCellValue(nz(f.getCountry()));
                    row.createCell(c++).setCellValue(nz(f.getState()));
                    row.createCell(c++).setCellValue(nz(f.getDistrict()));
                    row.createCell(c++).setCellValue(nz(f.getBlock()));
                    row.createCell(c++).setCellValue(nz(f.getVillage()));
                    row.createCell(c++).setCellValue(nz(f.getPincode()));
                    row.createCell(c++).setCellValue(nz(f.getEducation()));
                    row.createCell(c++).setCellValue(nz(f.getExperience()));
                    row.createCell(c++).setCellValue(nz(f.getCurrentSurveyNumber()));
                    row.createCell(c++).setCellValue(f.getCurrentLandHolding() != null ? f.getCurrentLandHolding() : 0);
                    row.createCell(c++).setCellValue(nz(f.getCurrentGeoTag()));
                    row.createCell(c++).setCellValue(nz(f.getCurrentCrop()));
                    row.createCell(c++).setCellValue(f.getCurrentNetIncome() != null ? f.getCurrentNetIncome() : 0);
                    row.createCell(c++).setCellValue(f.getCurrentSoilTest() != null ? f.getCurrentSoilTest() : false);
                    row.createCell(c++).setCellValue(nz(f.getCurrentWaterSource()));
                    row.createCell(c++).setCellValue(nz(f.getCurrentDischargeLPH()));
                    row.createCell(c++).setCellValue(nz(f.getCurrentSummerDischarge()));
                    row.createCell(c++).setCellValue(nz(f.getCurrentBorewellLocation()));
                    row.createCell(c++).setCellValue(nz(f.getProposedSurveyNumber()));
                    row.createCell(c++).setCellValue(f.getProposedLandHolding() != null ? f.getProposedLandHolding() : 0);
                    row.createCell(c++).setCellValue(nz(f.getProposedGeoTag()));
                    row.createCell(c++).setCellValue(nz(f.getProposedCrop()));
                    row.createCell(c++).setCellValue(f.getProposedNetIncome() != null ? f.getProposedNetIncome() : 0);
                    row.createCell(c++).setCellValue(f.getProposedSoilTest() != null ? f.getProposedSoilTest() : false);
                    row.createCell(c++).setCellValue(nz(f.getProposedWaterSource()));
                    row.createCell(c++).setCellValue(nz(f.getProposedDischargeLPH()));
                    row.createCell(c++).setCellValue(nz(f.getProposedSummerDischarge()));
                    row.createCell(c++).setCellValue(nz(f.getProposedBorewellLocation()));
                    row.createCell(c++).setCellValue(nz(f.getBankName()));
                    row.createCell(c++).setCellValue(nz(f.getAccountNumber()));
                    row.createCell(c++).setCellValue(nz(f.getBranchName()));
                    row.createCell(c++).setCellValue(nz(f.getIfscCode()));
                    row.createCell(c++).setCellValue(nz(f.getDocumentType()));
                    row.createCell(c++).setCellValue(nz(f.getDocumentNumber()));
                } else if (item instanceof com.farmer.Form.Entity.Employee) {
                    com.farmer.Form.Entity.Employee e = (com.farmer.Form.Entity.Employee) item;
                    int c = 0;
                    row.createCell(c++).setCellValue(nz(e.getSalutation()));
                    row.createCell(c++).setCellValue(nz(e.getFirstName()));
                    row.createCell(c++).setCellValue(nz(e.getMiddleName()));
                    row.createCell(c++).setCellValue(nz(e.getLastName()));
                    row.createCell(c++).setCellValue(nz(e.getGender()));
                    row.createCell(c++).setCellValue(nz(e.getNationality()));
                    row.createCell(c++).setCellValue(e.getDob() != null ? e.getDob().toString() : "");
                    row.createCell(c++).setCellValue(nz(e.getContactNumber()));
                    row.createCell(c++).setCellValue(nz(e.getEmail()));
                    row.createCell(c++).setCellValue(nz(e.getRelationType()));
                    row.createCell(c++).setCellValue(nz(e.getRelationName()));
                    row.createCell(c++).setCellValue(nz(e.getAltNumber()));
                    row.createCell(c++).setCellValue(nz(e.getAltNumberType()));
                    row.createCell(c++).setCellValue(nz(e.getCountry()));
                    row.createCell(c++).setCellValue(nz(e.getState()));
                    row.createCell(c++).setCellValue(nz(e.getDistrict()));
                    row.createCell(c++).setCellValue(nz(e.getBlock()));
                    row.createCell(c++).setCellValue(nz(e.getVillage()));
                    row.createCell(c++).setCellValue(nz(e.getZipcode()));
                    row.createCell(c++).setCellValue(nz(e.getSector()));
                    row.createCell(c++).setCellValue(nz(e.getEducation()));
                    row.createCell(c++).setCellValue(nz(e.getExperience()));
                    row.createCell(c++).setCellValue(nz(e.getBankName()));
                    row.createCell(c++).setCellValue(nz(e.getAccountNumber()));
                    row.createCell(c++).setCellValue(nz(e.getBranchName()));
                    row.createCell(c++).setCellValue(nz(e.getIfscCode()));
                    row.createCell(c++).setCellValue(nz(e.getDocumentType()));
                    row.createCell(c++).setCellValue(nz(e.getDocumentNumber()));
                    row.createCell(c++).setCellValue(nz(e.getRole()));
                    row.createCell(c++).setCellValue(nz(e.getAccessStatus()));
                }
            }

            // Autosize for readability
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private String nz(String s) { return s == null ? "" : s; }

    private byte[] exportToCsv(List<?> data, String[] headers) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream))) {
            // Write headers
            writer.println(String.join(",", headers));
            
            // Write data rows
            // Implementation would depend on the data type (Farmer or Employee)
        }
        return outputStream.toByteArray();
    }

    private byte[] createTemplate(String[] headers, String templateName) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(templateName);
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error creating template", e);
        }
    }

    private List<Farmer> getFilteredFarmers(BulkExportRequestDTO request) {
        // Implementation would filter farmers based on request criteria
        return farmerRepository.findAll();
    }

    private List<Employee> getFilteredEmployees(BulkExportRequestDTO request) {
        // Implementation would filter employees based on request criteria
        return employeeRepository.findAll();
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Double parseDouble(String value) {
        try {
            return isEmpty(value) ? null : Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Boolean parseBoolean(String value) {
        return "true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value);
    }
}
