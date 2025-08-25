package com.farmer.Form.Service.Impl;

import com.farmer.Form.DTO.FarmerDTO;
import com.farmer.Form.DTO.FarmerDashboardDTO;
import com.farmer.Form.DTO.EmployeeAssignmentDTO;
import com.farmer.Form.Entity.Farmer;
import com.farmer.Form.Entity.Employee;
import com.farmer.Form.Mapper.FarmerMapper;
import com.farmer.Form.Repository.FarmerRepository;
import com.farmer.Form.Repository.EmployeeRepository;
import com.farmer.Form.Service.FarmerService;
import com.farmer.Form.Service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FarmerServiceImpl implements FarmerService {

    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private com.farmer.Form.Repository.EmployeeRepository employeeRepository;

    @Override
    public FarmerDTO createFarmer(FarmerDTO dto, MultipartFile photo, MultipartFile passbookPhoto,
                                  MultipartFile aadhaar, MultipartFile soilTestCertificate) {
        try {
            String photoFile = (photo != null && !photo.isEmpty())
                    ? fileStorageService.storeFile(photo, "photos") : null;
            String passbookFile = (passbookPhoto != null && !passbookPhoto.isEmpty())
                    ? fileStorageService.storeFile(passbookPhoto, "passbooks") : null;
            String aadhaarFile = (aadhaar != null && !aadhaar.isEmpty())
                    ? fileStorageService.storeFile(aadhaar, "documents") : null;
            String soilTestFile = (soilTestCertificate != null && !soilTestCertificate.isEmpty())
                    ? fileStorageService.storeFile(soilTestCertificate, "soil-tests") : null;

            Farmer farmer = FarmerMapper.toEntity(dto, photoFile, passbookFile, aadhaarFile, soilTestFile);
            Farmer saved = farmerRepository.save(farmer);
            return FarmerMapper.toDto(saved);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store uploaded files", e);
        }
    }

    @Override
    public FarmerDTO getFarmerById(Long id) {
        return farmerRepository.findById(id)
                .map(FarmerMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
    }

    @Override
    public List<FarmerDTO> getAllFarmers() {
        return farmerRepository.findAll().stream()
                .map(FarmerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public FarmerDTO updateFarmer(Long id, FarmerDTO dto,
                                  MultipartFile photo, MultipartFile passbookPhoto,
                                  MultipartFile aadhaar, MultipartFile soilTestCertificate) {

        Farmer existing = farmerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        try {
            String photoFile = (photo != null && !photo.isEmpty())
                    ? fileStorageService.storeFile(photo, "photos")
                    : existing.getPhotoFileName();

            String passbookFile = (passbookPhoto != null && !passbookPhoto.isEmpty())
                    ? fileStorageService.storeFile(passbookPhoto, "passbooks")
                    : existing.getPassbookFileName();

            String aadhaarFile = (aadhaar != null && !aadhaar.isEmpty())
                    ? fileStorageService.storeFile(aadhaar, "documents")
                    : existing.getDocumentFileName();

            String soilTestFile = (soilTestCertificate != null && !soilTestCertificate.isEmpty())
                    ? fileStorageService.storeFile(soilTestCertificate, "soil-tests")
                    : existing.getSoilTestCertificateFileName();

            Farmer updated = FarmerMapper.toEntity(dto, photoFile, passbookFile, aadhaarFile, soilTestFile);
            updated.setId(existing.getId());

            Farmer saved = farmerRepository.save(updated);
            return FarmerMapper.toDto(saved);

        } catch (IOException e) {
            throw new RuntimeException("Failed to update files", e);
        }
    }

    @Override
    public FarmerDTO updateFarmer(Long id, FarmerDTO dto) {
        Farmer existing = farmerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        Farmer updated = FarmerMapper.toEntity(dto,
                existing.getPhotoFileName(),
                existing.getPassbookFileName(),
                existing.getDocumentFileName(),
                existing.getSoilTestCertificateFileName());

        updated.setId(existing.getId());
        return FarmerMapper.toDto(farmerRepository.save(updated));
    }

    @Override
    public void deleteFarmer(Long id) {
        farmerRepository.deleteById(id);
    }

    @Override
    public long getFarmerCount() {
        return farmerRepository.count();
    }

    // --- SUPER ADMIN RAW CRUD ---
    @Override
    public List<Farmer> getAllFarmersRaw() {
        return farmerRepository.findAll();
    }

    @Override
    public Farmer getFarmerRawById(Long id) {
        return farmerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Farmer not found with ID: " + id));
    }

    @Override
    public Farmer createFarmerBySuperAdmin(Farmer farmer) {
        return farmerRepository.save(farmer);
    }

    @Override
    public Farmer updateFarmerBySuperAdmin(Long id, Farmer updatedFarmer) {
        Farmer farmer = getFarmerRawById(id);
        // Update fields as needed
        farmer.setFirstName(updatedFarmer.getFirstName());
        farmer.setLastName(updatedFarmer.getLastName());
        farmer.setDateOfBirth(updatedFarmer.getDateOfBirth());
        farmer.setGender(updatedFarmer.getGender());
        farmer.setContactNumber(updatedFarmer.getContactNumber());
        farmer.setCountry(updatedFarmer.getCountry());
        farmer.setState(updatedFarmer.getState());
        farmer.setDistrict(updatedFarmer.getDistrict());
        farmer.setBlock(updatedFarmer.getBlock());
        farmer.setVillage(updatedFarmer.getVillage());
        farmer.setPincode(updatedFarmer.getPincode());
        // ... update other fields as needed
        return farmerRepository.save(farmer);
    }

    @Override
    public void deleteFarmerBySuperAdmin(Long id) {
        farmerRepository.deleteById(id);
    }

    @Override
    public void assignFarmerToEmployee(Long farmerId, Long employeeId) {
        System.out.println("🔍 Assigning farmer " + farmerId + " to employee " + employeeId);
        
        Farmer farmer = farmerRepository.findById(farmerId)
            .orElseThrow(() -> new RuntimeException("Farmer not found"));
        System.out.println("🔍 Found farmer: " + farmer.getFirstName() + " " + farmer.getLastName());
        
        com.farmer.Form.Entity.Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        System.out.println("🔍 Found employee: " + employee.getFirstName() + " " + employee.getLastName());
        
        farmer.setAssignedEmployee(employee);
        System.out.println("🔍 Set assignedEmployee on farmer");
        
        Farmer savedFarmer = farmerRepository.save(farmer);
        System.out.println("🔍 Saved farmer with assignedEmployee: " + (savedFarmer.getAssignedEmployee() != null ? savedFarmer.getAssignedEmployee().getEmail() : "null"));
        
        // Verify the assignment was saved
        Farmer verifyFarmer = farmerRepository.findById(farmerId).orElse(null);
        if (verifyFarmer != null && verifyFarmer.getAssignedEmployee() != null) {
            System.out.println("✅ Assignment verified: Farmer " + verifyFarmer.getId() + " assigned to " + verifyFarmer.getAssignedEmployee().getEmail());
        } else {
            System.err.println("❌ Assignment verification failed: Farmer " + farmerId + " has no assigned employee");
        }
    }

    @Override
    public List<Farmer> getFarmersByEmployeeEmail(String email) {
        return farmerRepository.findByAssignedEmployee_Email(email);
    }

    @Override
    public void approveKyc(Long farmerId) {
        Farmer farmer = farmerRepository.findById(farmerId)
            .orElseThrow(() -> new RuntimeException("Farmer not found"));
        farmer.setKycApproved(true);
        farmerRepository.save(farmer);
    }
    
    // Enhanced KYC Management Methods
    @Override
    public void approveKycByEmployee(Long farmerId, String employeeEmail) {
        Farmer farmer = farmerRepository.findById(farmerId)
            .orElseThrow(() -> new RuntimeException("Farmer not found"));
        
        // Verify the farmer is assigned to this employee
        if (farmer.getAssignedEmployee() == null || !farmer.getAssignedEmployee().getEmail().equals(employeeEmail)) {
            throw new RuntimeException("Farmer is not assigned to this employee");
        }
        
        farmer.setKycStatus(Farmer.KycStatus.APPROVED);
        farmer.setKycApproved(true);
        farmer.setKycReviewedDate(LocalDate.now());
        farmer.setKycReviewedBy(employeeEmail);
        farmer.setKycRejectionReason(null);
        farmer.setKycReferBackReason(null);
        
        farmerRepository.save(farmer);
    }
    
    @Override
    public void referBackKyc(Long farmerId, String employeeEmail, String reason) {
        Farmer farmer = farmerRepository.findById(farmerId)
            .orElseThrow(() -> new RuntimeException("Farmer not found"));
        
        // Verify the farmer is assigned to this employee
        if (farmer.getAssignedEmployee() == null || !farmer.getAssignedEmployee().getEmail().equals(employeeEmail)) {
            throw new RuntimeException("Farmer is not assigned to this employee");
        }
        
        farmer.setKycStatus(Farmer.KycStatus.REFER_BACK);
        farmer.setKycApproved(false);
        farmer.setKycReviewedDate(LocalDate.now());
        farmer.setKycReviewedBy(employeeEmail);
        farmer.setKycReferBackReason(reason);
        farmer.setKycRejectionReason(null);
        
        farmerRepository.save(farmer);
    }
    
    @Override
    public void rejectKyc(Long farmerId, String employeeEmail, String reason) {
        Farmer farmer = farmerRepository.findById(farmerId)
            .orElseThrow(() -> new RuntimeException("Farmer not found"));
        
        // Verify the farmer is assigned to this employee
        if (farmer.getAssignedEmployee() == null || !farmer.getAssignedEmployee().getEmail().equals(employeeEmail)) {
            throw new RuntimeException("Farmer is not assigned to this employee");
        }
        
        farmer.setKycStatus(Farmer.KycStatus.REJECTED);
        farmer.setKycApproved(false);
        farmer.setKycReviewedDate(LocalDate.now());
        farmer.setKycReviewedBy(employeeEmail);
        farmer.setKycRejectionReason(reason);
        farmer.setKycReferBackReason(null);
        
        farmerRepository.save(farmer);
    }

    @Override
    public FarmerDashboardDTO getFarmerDashboardData(Long farmerId) {
        Farmer farmer = farmerRepository.findById(farmerId)
            .orElseThrow(() -> new RuntimeException("Farmer not found"));
        
        return buildFarmerDashboardDTO(farmer);
    }

    @Override
    public FarmerDashboardDTO getFarmerDashboardDataByEmail(String email) {
        // This would need a repository method to find farmer by email
        // For now, we'll need to implement this based on your user-farmer relationship
        throw new UnsupportedOperationException("Method not implemented yet");
    }

    private FarmerDashboardDTO buildFarmerDashboardDTO(Farmer farmer) {
        EmployeeAssignmentDTO assignedEmployee = null;
        if (farmer.getAssignedEmployee() != null) {
            Employee emp = farmer.getAssignedEmployee();
            assignedEmployee = EmployeeAssignmentDTO.builder()
                    .employeeId(emp.getId())
                    .employeeName(emp.getFirstName() + " " + emp.getLastName())
                    .employeeEmail(emp.getEmail())
                    .employeeContactNumber(emp.getContactNumber())
                    .employeeDesignation(emp.getRole())
                    .employeeRole(emp.getRole())
                    .assignmentStatus("ACTIVE")
                    .build();
        }

        return FarmerDashboardDTO.builder()
                // Personal Information
                .salutation(farmer.getSalutation())
                .firstName(farmer.getFirstName())
                .middleName(farmer.getMiddleName())
                .lastName(farmer.getLastName())
                .fullName(farmer.getSalutation() + " " + farmer.getFirstName() + " " + 
                         (farmer.getMiddleName() != null ? farmer.getMiddleName() + " " : "") + 
                         farmer.getLastName())
                .dateOfBirth(farmer.getDateOfBirth())
                .gender(farmer.getGender())
                .fatherName(farmer.getFatherName())
                .contactNumber(farmer.getContactNumber())
                .alternativeRelationType(farmer.getAlternativeRelationType())
                .alternativeContactNumber(farmer.getAlternativeContactNumber())
                .nationality(farmer.getNationality())
                
                // Address Information
                .country(farmer.getCountry())
                .state(farmer.getState())
                .district(farmer.getDistrict())
                .block(farmer.getBlock())
                .village(farmer.getVillage())
                .pincode(farmer.getPincode())
                
                // Professional Information
                .education(farmer.getEducation())
                .experience(farmer.getExperience())
                
                // Current Crop Information
                .currentSurveyNumber(farmer.getCurrentSurveyNumber())
                .currentLandHolding(farmer.getCurrentLandHolding())
                .currentGeoTag(farmer.getCurrentGeoTag())
                .currentCrop(farmer.getCurrentCrop())
                .currentNetIncome(farmer.getCurrentNetIncome())
                .currentSoilTest(farmer.getCurrentSoilTest())
                .currentWaterSource(farmer.getCurrentWaterSource())
                .currentDischargeLPH(farmer.getCurrentDischargeLPH())
                .currentSummerDischarge(farmer.getCurrentSummerDischarge())
                .currentBorewellLocation(farmer.getCurrentBorewellLocation())
                
                // Proposed Crop Information
                .proposedSurveyNumber(farmer.getProposedSurveyNumber())
                .proposedLandHolding(farmer.getProposedLandHolding())
                .proposedGeoTag(farmer.getProposedGeoTag())
                .proposedCrop(farmer.getProposedCrop())
                .proposedNetIncome(farmer.getProposedNetIncome())
                .proposedSoilTest(farmer.getProposedSoilTest())
                .proposedWaterSource(farmer.getProposedWaterSource())
                .proposedDischargeLPH(farmer.getProposedDischargeLPH())
                .proposedSummerDischarge(farmer.getProposedSummerDischarge())
                .proposedBorewellLocation(farmer.getProposedBorewellLocation())
                
                // Bank Details
                .bankName(farmer.getBankName())
                .accountNumber(farmer.getAccountNumber())
                .branchName(farmer.getBranchName())
                .ifscCode(farmer.getIfscCode())
                
                // Document Information
                .documentType(farmer.getDocumentType())
                .documentNumber(farmer.getDocumentNumber())
                
                // Portal Information
                .portalRole(farmer.getPortalRole())
                .portalAccess(farmer.getPortalAccess())
                
                // KYC Information
                .kycApproved(farmer.getKycApproved())
                .kycStatus(farmer.getKycStatus() != null ? farmer.getKycStatus().name() : "PENDING")
                .kycRejectionReason(farmer.getKycRejectionReason())
                .kycReferBackReason(farmer.getKycReferBackReason())
                .kycSubmittedDate(farmer.getKycSubmittedDate())
                .kycReviewedDate(farmer.getKycReviewedDate())
                .kycReviewedBy(farmer.getKycReviewedBy())
                
                // Assigned Employee Information
                .assignedEmployee(assignedEmployee)
                
                // File Information
                .photoFileName(farmer.getPhotoFileName())
                .passbookFileName(farmer.getPassbookFileName())
                .documentFileName(farmer.getDocumentFileName())
                .soilTestCertificateFileName(farmer.getSoilTestCertificateFileName())
                
                // Statistics (placeholder values - would need to be calculated)
                .totalCrops(calculateTotalCrops(farmer))
                .pendingDocuments(calculatePendingDocuments(farmer))
                .totalBenefitsReceived(0.0) // Would need to be calculated from benefits table
                .registrationDate(LocalDate.now()) // Would need to be stored in farmer entity
                .lastUpdatedDate(LocalDate.now()) // Would need to be stored in farmer entity
                .build();
    }

    private Integer calculateTotalCrops(Farmer farmer) {
        int count = 0;
        if (farmer.getCurrentCrop() != null && !farmer.getCurrentCrop().isEmpty()) count++;
        if (farmer.getProposedCrop() != null && !farmer.getProposedCrop().isEmpty()) count++;
        return count;
    }

    private Integer calculatePendingDocuments(Farmer farmer) {
        int count = 0;
        if (farmer.getPhotoFileName() == null) count++;
        if (farmer.getPassbookFileName() == null) count++;
        if (farmer.getDocumentFileName() == null) count++;
        if (farmer.getSoilTestCertificateFileName() == null) count++;
        return count;
    }
}
