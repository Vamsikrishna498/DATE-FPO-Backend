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
import com.farmer.Form.Service.IdCardService;
import com.farmer.Form.Service.EmailService;
import com.farmer.Form.Repository.IdCardRepository;
import com.farmer.Form.Repository.FPOMemberRepository;
import com.farmer.Form.Repository.FPOServiceRepository;
import com.farmer.Form.Repository.FPOCropRepository;
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

    @Autowired
    private IdCardService idCardService;

    @Autowired
    private IdCardRepository idCardRepository;

    @Autowired
    private FPOMemberRepository fpoMemberRepository;

    @Autowired
    private FPOServiceRepository fpoServiceRepository;

    @Autowired
    private FPOCropRepository fpoCropRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public FarmerDTO createFarmer(FarmerDTO dto, MultipartFile photo, MultipartFile passbookPhoto,
                                  MultipartFile aadhaar, MultipartFile soilTestCertificate) {
        try {
            // Check for existing email - TEMPORARILY DISABLED FOR TESTING
            // TODO: Implement proper duplicate email handling (update vs create)
            /*
            if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
                farmerRepository.findByEmail(dto.getEmail()).ifPresent(farmer -> {
                    throw new RuntimeException("Email already registered: " + dto.getEmail());
                });
            }
            */
            System.out.println("🔍 Email validation temporarily disabled for testing phone number issue");
            
            String photoFile = (photo != null && !photo.isEmpty())
                    ? fileStorageService.storeFile(photo, "photos") : null;
            String passbookFile = (passbookPhoto != null && !passbookPhoto.isEmpty())
                    ? fileStorageService.storeFile(passbookPhoto, "passbooks") : null;
            String aadhaarFile = (aadhaar != null && !aadhaar.isEmpty())
                    ? fileStorageService.storeFile(aadhaar, "documents") : null;
            String soilTestFile = (soilTestCertificate != null && !soilTestCertificate.isEmpty())
                    ? fileStorageService.storeFile(soilTestCertificate, "soil-tests") : null;

            // Debug logging
            System.out.println("🔍 Service received farmer DTO:");
            System.out.println("  - Contact Number: " + dto.getContactNumber());
            System.out.println("  - Contact Number Type: " + (dto.getContactNumber() != null ? dto.getContactNumber().getClass().getSimpleName() : "null"));
            
            Farmer farmer = FarmerMapper.toEntity(dto, photoFile, passbookFile, aadhaarFile, soilTestFile);
            
            // Debug logging for entity
            System.out.println("🔍 Farmer entity before save:");
            System.out.println("  - Contact Number: " + farmer.getContactNumber());
            System.out.println("  - ID: " + farmer.getId());
            
            Farmer saved = farmerRepository.save(farmer);
            
            // Debug logging for saved entity
            System.out.println("🔍 Farmer entity after save:");
            System.out.println("  - Contact Number: " + saved.getContactNumber());
            System.out.println("  - ID: " + saved.getId());
            
            // Generate ID card for newly created farmers
            try {
                System.out.println("🔄 Generating ID card for newly created farmer: " + saved.getId());
                idCardService.generateFarmerIdCard(saved);
                System.out.println("✅ ID card generated successfully for farmer: " + saved.getId());
            } catch (Exception e) {
                System.err.println("❌ Failed to generate ID card for farmer " + saved.getId() + ": " + e.getMessage());
                // Don't throw exception - farmer creation should still succeed even if ID card generation fails
            }
            
            return FarmerMapper.toDto(saved);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store uploaded files", e);
        }
    }

    @Override
    public FarmerDTO updateFarmerPhoto(Long id, MultipartFile photo) {
        Farmer existing = farmerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
        try {
            String photoFile = (photo != null && !photo.isEmpty())
                    ? fileStorageService.storeFile(photo, "photos")
                    : existing.getPhotoFileName();

            existing.setPhotoFileName(photoFile);
            Farmer saved = farmerRepository.save(existing);
            try {
                List<com.farmer.Form.Entity.IdCard> cards = idCardRepository.findByHolderId(String.valueOf(saved.getId()));
                com.farmer.Form.Entity.IdCard latestActive = cards.stream()
                        .filter(c -> c.getStatus() == com.farmer.Form.Entity.IdCard.CardStatus.ACTIVE)
                        .findFirst().orElse(cards.isEmpty() ? null : cards.get(0));
                if (latestActive != null) {
                    idCardService.regenerateIdCard(latestActive.getCardId());
                }
            } catch (Exception ignore) {}
            return FarmerMapper.toDto(saved);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update farmer photo", e);
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
        // Check for existing email
        if (farmer.getEmail() != null && !farmer.getEmail().trim().isEmpty()) {
            farmerRepository.findByEmail(farmer.getEmail()).ifPresent(existingFarmer -> {
                throw new RuntimeException("Email already registered: " + farmer.getEmail());
            });
        }
        
        Farmer savedFarmer = farmerRepository.save(farmer);
        
        // Generate ID card immediately for Super Admin created farmers
        try {
            System.out.println("🔄 Generating ID card for Super Admin created farmer: " + savedFarmer.getId());
            idCardService.generateFarmerIdCard(savedFarmer);
            System.out.println("✅ ID card generated successfully for farmer: " + savedFarmer.getId());
        } catch (Exception e) {
            System.err.println("❌ Failed to generate ID card for farmer " + savedFarmer.getId() + ": " + e.getMessage());
            // Don't throw exception - farmer creation should still succeed even if ID card generation fails
        }
        
        return savedFarmer;
    }

    @Override
    public Farmer updateFarmerBySuperAdmin(Long id, Farmer updatedFarmer) {
        Farmer farmer = getFarmerRawById(id);
        
        // Update all relevant fields with null checks and validation
        if (updatedFarmer.getFirstName() != null) {
            farmer.setFirstName(updatedFarmer.getFirstName());
        }
        if (updatedFarmer.getMiddleName() != null) {
            farmer.setMiddleName(updatedFarmer.getMiddleName());
        }
        if (updatedFarmer.getLastName() != null && !updatedFarmer.getLastName().trim().isEmpty()) {
            farmer.setLastName(updatedFarmer.getLastName());
        }
        if (updatedFarmer.getSalutation() != null && !updatedFarmer.getSalutation().trim().isEmpty()) {
            farmer.setSalutation(updatedFarmer.getSalutation());
        }
        if (updatedFarmer.getDateOfBirth() != null) {
            farmer.setDateOfBirth(updatedFarmer.getDateOfBirth());
        }
        if (updatedFarmer.getGender() != null && !updatedFarmer.getGender().trim().isEmpty()) {
            farmer.setGender(updatedFarmer.getGender());
        }
        if (updatedFarmer.getFatherName() != null) {
            farmer.setFatherName(updatedFarmer.getFatherName());
        }
        if (updatedFarmer.getContactNumber() != null) {
            farmer.setContactNumber(updatedFarmer.getContactNumber());
        }
        if (updatedFarmer.getAlternativeContactNumber() != null) {
            farmer.setAlternativeContactNumber(updatedFarmer.getAlternativeContactNumber());
        }
        if (updatedFarmer.getAlternativeRelationType() != null) {
            farmer.setAlternativeRelationType(updatedFarmer.getAlternativeRelationType());
        }
        if (updatedFarmer.getEmail() != null) {
            farmer.setEmail(updatedFarmer.getEmail());
        }
        if (updatedFarmer.getNationality() != null && !updatedFarmer.getNationality().trim().isEmpty()) {
            farmer.setNationality(updatedFarmer.getNationality());
        }
        if (updatedFarmer.getCountry() != null && !updatedFarmer.getCountry().trim().isEmpty()) {
            farmer.setCountry(updatedFarmer.getCountry());
        }
        if (updatedFarmer.getState() != null) {
            farmer.setState(updatedFarmer.getState());
        }
        if (updatedFarmer.getDistrict() != null) {
            farmer.setDistrict(updatedFarmer.getDistrict());
        }
        if (updatedFarmer.getBlock() != null) {
            farmer.setBlock(updatedFarmer.getBlock());
        }
        if (updatedFarmer.getVillage() != null) {
            farmer.setVillage(updatedFarmer.getVillage());
        }
        if (updatedFarmer.getPincode() != null) {
            farmer.setPincode(updatedFarmer.getPincode());
        }
        if (updatedFarmer.getEducation() != null) {
            farmer.setEducation(updatedFarmer.getEducation());
        }
        if (updatedFarmer.getExperience() != null) {
            farmer.setExperience(updatedFarmer.getExperience());
        }
        
        // Update professional and crop information
        if (updatedFarmer.getCurrentSurveyNumber() != null) {
            farmer.setCurrentSurveyNumber(updatedFarmer.getCurrentSurveyNumber());
        }
        if (updatedFarmer.getCurrentLandHolding() != null) {
            farmer.setCurrentLandHolding(updatedFarmer.getCurrentLandHolding());
        }
        if (updatedFarmer.getCurrentCrop() != null) {
            farmer.setCurrentCrop(updatedFarmer.getCurrentCrop());
        }
        if (updatedFarmer.getCurrentNetIncome() != null) {
            farmer.setCurrentNetIncome(updatedFarmer.getCurrentNetIncome());
        }
        if (updatedFarmer.getCurrentSoilTest() != null) {
            farmer.setCurrentSoilTest(updatedFarmer.getCurrentSoilTest());
        }
        
        // Handle assignedEmployee field carefully to avoid foreign key constraint violations
        if (updatedFarmer.getAssignedEmployee() != null) {
            // Verify the employee exists before assigning
            try {
                Employee employee = employeeRepository.findById(updatedFarmer.getAssignedEmployee().getId()).orElse(null);
                if (employee != null) {
                    farmer.setAssignedEmployee(employee);
                    System.out.println("✅ Assigned farmer to employee: " + employee.getEmail());
                } else {
                    System.out.println("⚠️ Employee not found, keeping existing assignment: " + farmer.getAssignedEmployee());
                }
            } catch (Exception e) {
                System.err.println("❌ Error handling assignedEmployee: " + e.getMessage());
                // Keep existing assignment if there's an error
            }
        }
        
        // Ensure required fields have default values if they're null/empty
        if (farmer.getSalutation() == null || farmer.getSalutation().trim().isEmpty()) {
            farmer.setSalutation("Mr"); // Default value
        }
        if (farmer.getLastName() == null || farmer.getLastName().trim().isEmpty()) {
            farmer.setLastName("Unknown"); // Default value
        }
        if (farmer.getGender() == null || farmer.getGender().trim().isEmpty()) {
            farmer.setGender("Male"); // Default value
        }
        if (farmer.getNationality() == null || farmer.getNationality().trim().isEmpty()) {
            farmer.setNationality("Indian"); // Default value
        }
        if (farmer.getCountry() == null || farmer.getCountry().trim().isEmpty()) {
            farmer.setCountry("India"); // Default value
        }
        
        // Clear invalid pattern fields to avoid validation errors
        if (farmer.getAlternativeContactNumber() != null && 
            !farmer.getAlternativeContactNumber().matches("^\\d{10}$")) {
            System.out.println("⚠️ Clearing invalid alternative contact number: " + farmer.getAlternativeContactNumber());
            farmer.setAlternativeContactNumber(null);
        }
        
        if (farmer.getPincode() != null && 
            !farmer.getPincode().matches("^\\d{6}$")) {
            System.out.println("⚠️ Clearing invalid pincode: " + farmer.getPincode());
            farmer.setPincode(null);
        }
        
        try {
            return farmerRepository.save(farmer);
        } catch (Exception e) {
            System.err.println("❌ FarmerService: Database save error: " + e.getMessage());
            System.err.println("❌ Error type: " + e.getClass().getSimpleName());
            
            // Handle specific constraint violations
            if (e.getMessage() != null) {
                if (e.getMessage().contains("constraint") || e.getMessage().contains("validation")) {
                    throw new RuntimeException("Validation error: " + e.getMessage(), e);
                }
                if (e.getMessage().contains("foreign key") || e.getMessage().contains("FK_")) {
                    throw new RuntimeException("Foreign key constraint violation: " + e.getMessage(), e);
                }
                if (e.getMessage().contains("unique") || e.getMessage().contains("duplicate")) {
                    throw new RuntimeException("Duplicate data error: " + e.getMessage(), e);
                }
            }
            
            // Log the full exception for debugging
            e.printStackTrace();
            throw new RuntimeException("Database error while saving farmer: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFarmerBySuperAdmin(Long id) {
        try {
            // Remove any linked ID cards first to satisfy FK constraints
            java.util.List<com.farmer.Form.Entity.IdCard> cards = idCardRepository.findByHolderId(String.valueOf(id));
            if (cards != null && !cards.isEmpty()) {
                idCardRepository.deleteAll(cards);
            }
            // Remove FPO memberships that reference this farmer
            try {
                java.util.List<com.farmer.Form.Entity.FPOMember> memberships = fpoMemberRepository.findByFarmerId(id);
                if (memberships != null && !memberships.isEmpty()) {
                    fpoMemberRepository.deleteAll(memberships);
                }
            } catch (Exception ignore) {}
            // Remove FPO services rows that reference this farmer
            try {
                java.util.List<com.farmer.Form.Entity.FPOService> services = fpoServiceRepository.findByFarmerId(id);
                if (services != null && !services.isEmpty()) {
                    fpoServiceRepository.deleteAll(services);
                }
            } catch (Exception ignore) {}
            // Remove FPO crops rows that reference this farmer
            try {
                java.util.List<com.farmer.Form.Entity.FPOCrop> crops = fpoCropRepository.findByFarmerId(id);
                if (crops != null && !crops.isEmpty()) {
                    fpoCropRepository.deleteAll(crops);
                }
            } catch (Exception ignore) {}
        } catch (Exception ignore) {}
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
            try {
                String empEmail = verifyFarmer.getAssignedEmployee().getEmail();
                String empName = (verifyFarmer.getAssignedEmployee().getFirstName() != null ? verifyFarmer.getAssignedEmployee().getFirstName() : "") +
                        (verifyFarmer.getAssignedEmployee().getLastName() != null ? (" " + verifyFarmer.getAssignedEmployee().getLastName()) : "");
                String farmerName = (verifyFarmer.getFirstName() != null ? verifyFarmer.getFirstName() : "") +
                        (verifyFarmer.getLastName() != null ? (" " + verifyFarmer.getLastName()) : "");
                emailService.sendFarmerAssignedToEmployee(empEmail, empName.trim(), farmerName.trim(), verifyFarmer.getId());
            } catch (Exception e) {
                System.err.println("❌ Failed to send assignment email: " + e.getMessage());
            }
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
        farmer.setKycStatus(Farmer.KycStatus.APPROVED);
        farmerRepository.save(farmer);
        
        // Note: ID card generation is now handled in UserService during user approval
        // No need to generate ID card here as it's done automatically when user is approved
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
        
        // Note: ID card generation is now handled in UserService during user approval
        // No need to generate ID card here as it's done automatically when user is approved
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
        // Find farmer by email
        Farmer farmer = farmerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Farmer not found with email: " + email));
        
        return buildFarmerDashboardDTO(farmer);
    }

    @Override
    public FarmerDTO getFarmerByEmail(String email) {
        Farmer farmer = farmerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Farmer not found with email: " + email));
        
        return FarmerMapper.toDto(farmer);
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
                .id(farmer.getId())
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
                .email(farmer.getEmail())
                
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

    // FPO KYC Management Methods
    @Override
    public void approveKycByFPO(Long farmerId, String fpoUserEmail) {
        Farmer farmer = farmerRepository.findById(farmerId)
            .orElseThrow(() -> new RuntimeException("Farmer not found with ID: " + farmerId));
        
        // For FPO users, we don't need to check assignment since FPO users can approve any farmer
        farmer.setKycStatus(Farmer.KycStatus.APPROVED);
        farmer.setKycApproved(true);
        farmer.setKycReviewedDate(LocalDate.now());
        farmer.setKycReviewedBy(fpoUserEmail);
        farmer.setKycRejectionReason(null);
        farmer.setKycReferBackReason(null);
        
        farmerRepository.save(farmer);
    }

    @Override
    public void referBackKycByFPO(Long farmerId, String fpoUserEmail, String reason) {
        Farmer farmer = farmerRepository.findById(farmerId)
            .orElseThrow(() -> new RuntimeException("Farmer not found with ID: " + farmerId));
        
        farmer.setKycStatus(Farmer.KycStatus.PENDING);
        farmer.setKycApproved(false);
        farmer.setKycReviewedDate(LocalDate.now());
        farmer.setKycReviewedBy(fpoUserEmail);
        farmer.setKycReferBackReason(reason);
        farmer.setKycRejectionReason(null);
        
        farmerRepository.save(farmer);
    }

    @Override
    public void rejectKycByFPO(Long farmerId, String fpoUserEmail, String reason) {
        Farmer farmer = farmerRepository.findById(farmerId)
            .orElseThrow(() -> new RuntimeException("Farmer not found with ID: " + farmerId));
        
        farmer.setKycStatus(Farmer.KycStatus.REJECTED);
        farmer.setKycApproved(false);
        farmer.setKycReviewedDate(LocalDate.now());
        farmer.setKycReviewedBy(fpoUserEmail);
        farmer.setKycRejectionReason(reason);
        farmer.setKycReferBackReason(null);
        
        farmerRepository.save(farmer);
    }
}
