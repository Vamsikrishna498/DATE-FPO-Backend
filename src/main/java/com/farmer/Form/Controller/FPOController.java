package com.farmer.Form.Controller;

import com.farmer.Form.DTO.*;
import com.farmer.Form.Service.FPOService;
import com.farmer.Form.Service.FarmerService;
import com.farmer.Form.Entity.FPOMember;
import com.farmer.Form.Entity.Farmer;
import com.farmer.Form.Entity.FPO;
import com.farmer.Form.Repository.FPOMemberRepository;
import com.farmer.Form.Repository.FPORepository;
import com.farmer.Form.Repository.FPOUserRepository;
import com.farmer.Form.Repository.FarmerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/fpo")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FPOController {

    private final FPOService fpoService;
    private final FarmerService farmerService;
    private final FPOMemberRepository fpoMemberRepository;
    private final FPORepository fpoRepository;
    private final FPOUserRepository fpoUserRepository;
    private final FarmerRepository farmerRepository;

    // FPO CRUD Operations
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<FPODTO> createFPO(@Valid @RequestBody FPOCreationDTO fpoCreationDTO) {
        log.info("Creating new FPO: {}", fpoCreationDTO.getFpoName());
        FPODTO createdFPO = fpoService.createFPO(fpoCreationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFPO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO')")
    public ResponseEntity<FPODTO> updateFPO(@PathVariable Long id, @Valid @RequestBody FPOCreationDTO fpoCreationDTO) {
        log.info("Updating FPO with ID: {}", id);
        FPODTO updatedFPO = fpoService.updateFPO(id, fpoCreationDTO);
        return ResponseEntity.ok(updatedFPO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO') or hasRole('FARMER') or hasRole('EMPLOYEE')")
    public ResponseEntity<FPODTO> getFPOById(@PathVariable Long id) {
        FPODTO fpo = fpoService.getFPOById(id);
        return ResponseEntity.ok(fpo);
    }

    @GetMapping("/fpo-id/{fpoId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO') or hasRole('FARMER') or hasRole('EMPLOYEE')")
    public ResponseEntity<FPODTO> getFPOByFpoId(@PathVariable String fpoId) {
        FPODTO fpo = fpoService.getFPOByFpoId(fpoId);
        return ResponseEntity.ok(fpo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteFPO(@PathVariable Long id) {
        log.info("Deleting FPO with ID: {}", id);
        fpoService.deleteFPO(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateFPO(@PathVariable Long id) {
        log.info("Deactivating FPO with ID: {}", id);
        fpoService.deactivateFPO(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Void> activateFPO(@PathVariable Long id) {
        log.info("Activating FPO with ID: {}", id);
        fpoService.activateFPO(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<Void> updateFPOStatus(@PathVariable Long id, @RequestParam String status) {
        log.info("Updating FPO status. id={}, status={}", id, status);
        if ("ACTIVE".equalsIgnoreCase(status)) {
            fpoService.activateFPO(id);
        } else if ("INACTIVE".equalsIgnoreCase(status)) {
            fpoService.deactivateFPO(id);
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    // FPO List and Search
    @PostMapping("/search")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<Page<FPODTO>> searchFPOs(@RequestBody FPOListRequestDTO request) {
        log.info("Searching FPOs with filters: {}", request);
        Page<FPODTO> fpos = fpoService.getAllFPOs(request);
        return ResponseEntity.ok(fpos);
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<Page<FPODTO>> getAllFPOs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String status) {
        
        FPOListRequestDTO request = FPOListRequestDTO.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .searchTerm(searchTerm)
                .state(state)
                .district(district)
                .status(status != null ? com.farmer.Form.Entity.FPO.FPOStatus.valueOf(status) : null)
                .build();
        
        Page<FPODTO> fpos = fpoService.getAllFPOs(request);
        return ResponseEntity.ok(fpos);
    }

    @GetMapping("/state/{state}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<FPODTO>> getFPOsByState(@PathVariable String state) {
        List<FPODTO> fpos = fpoService.getFPOsByState(state);
        return ResponseEntity.ok(fpos);
    }

    @GetMapping("/district/{district}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<FPODTO>> getFPOsByDistrict(@PathVariable String district) {
        List<FPODTO> fpos = fpoService.getFPOsByDistrict(district);
        return ResponseEntity.ok(fpos);
    }

    @GetMapping("/states")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<String>> getDistinctStates() {
        List<String> states = fpoService.getDistinctStates();
        return ResponseEntity.ok(states);
    }

    @GetMapping("/districts/{state}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<String>> getDistinctDistrictsByState(@PathVariable String state) {
        List<String> districts = fpoService.getDistinctDistrictsByState(state);
        return ResponseEntity.ok(districts);
    }

    // FPO Dashboard
    @GetMapping("/{id}/dashboard")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO')")
    public ResponseEntity<FPODashboardDTO> getFPODashboard(@PathVariable Long id) {
        FPODashboardDTO dashboard = fpoService.getFPODashboard(id);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/fpo-id/{fpoId}/dashboard")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO')")
    public ResponseEntity<FPODashboardDTO> getFPODashboardByFpoId(@PathVariable String fpoId) {
        FPODashboardDTO dashboard = fpoService.getFPODashboardByFpoId(fpoId);
        return ResponseEntity.ok(dashboard);
    }

    // FPO Statistics
    @GetMapping("/stats/total")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<Long> getTotalFPOsCount() {
        Long count = fpoService.getTotalFPOsCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/active")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<Long> getActiveFPOsCount() {
        Long count = fpoService.getActiveFPOsCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/status/{status}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Long> getFPOsCountByStatus(@PathVariable String status) {
        Long count = fpoService.getFPOsCountByStatus(com.farmer.Form.Entity.FPO.FPOStatus.valueOf(status));
        return ResponseEntity.ok(count);
    }

    // FPO KYC Management Endpoints
    @PutMapping("/kyc/approve/{farmerId}")
    @PreAuthorize("hasRole('FPO') or hasRole('EMPLOYEE')")
    public ResponseEntity<String> approveKyc(@PathVariable Long farmerId, Authentication authentication) {
        try {
            String fpoUserEmail = authentication.getName();
            log.info("FPO KYC approval request for farmer {} by FPO user {}", farmerId, fpoUserEmail);
            farmerService.approveKycByFPO(farmerId, fpoUserEmail);
            return ResponseEntity.ok("KYC approved successfully by FPO");
        } catch (Exception e) {
            log.error("Error approving KYC for farmer {} by FPO user {}: {}", farmerId, authentication.getName(), e.getMessage());
            return ResponseEntity.badRequest().body("Error approving KYC: " + e.getMessage());
        }
    }

    @PutMapping("/kyc/reject/{farmerId}")
    @PreAuthorize("hasRole('FPO') or hasRole('EMPLOYEE')")
    public ResponseEntity<String> rejectKyc(@PathVariable Long farmerId, 
                                          @RequestBody Map<String, String> request,
                                          Authentication authentication) {
        try {
            String fpoUserEmail = authentication.getName();
            String reason = request.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Reason is required for rejection");
            }
            log.info("FPO KYC rejection request for farmer {} by FPO user {} with reason: {}", farmerId, fpoUserEmail, reason);
            farmerService.rejectKycByFPO(farmerId, fpoUserEmail, reason);
            return ResponseEntity.ok("KYC rejected successfully by FPO");
        } catch (Exception e) {
            log.error("Error rejecting KYC for farmer {} by FPO user {}: {}", farmerId, authentication.getName(), e.getMessage());
            return ResponseEntity.badRequest().body("Error rejecting KYC: " + e.getMessage());
        }
    }

    @PutMapping("/kyc/refer-back/{farmerId}")
    @PreAuthorize("hasRole('FPO') or hasRole('EMPLOYEE')")
    public ResponseEntity<String> referBackKyc(@PathVariable Long farmerId, 
                                             @RequestBody Map<String, String> request,
                                             Authentication authentication) {
        try {
            String fpoUserEmail = authentication.getName();
            String reason = request.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Reason is required for refer back");
            }
            log.info("FPO KYC refer-back request for farmer {} by FPO user {} with reason: {}", farmerId, fpoUserEmail, reason);
            farmerService.referBackKycByFPO(farmerId, fpoUserEmail, reason);
            return ResponseEntity.ok("KYC referred back successfully by FPO");
        } catch (Exception e) {
            log.error("Error referring back KYC for farmer {} by FPO user {}: {}", farmerId, authentication.getName(), e.getMessage());
            return ResponseEntity.badRequest().body("Error referring back KYC: " + e.getMessage());
        }
    }

    // FPO-specific Farmer Management
    @PostMapping("/{fpoId}/farmers")
    @PreAuthorize("hasRole('FPO') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> createFPOFarmer(@PathVariable Long fpoId, @RequestBody Map<String, Object> farmerData, Authentication authentication) {
        try {
            log.info("Creating FPO-specific farmer for FPO ID: {}", fpoId);

            // Enforce that only users tied to this FPO can add its farmers
            String requesterEmail = authentication != null ? authentication.getName() : null;
            if (requesterEmail != null) {
                var fpoUserOpt = fpoUserRepository.findByEmail(requesterEmail);
                if (fpoUserOpt.isPresent()) {
                    Long usersFpoId = fpoUserOpt.get().getFpo() != null ? fpoUserOpt.get().getFpo().getId() : null;
                    if (usersFpoId == null || !usersFpoId.equals(fpoId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                                "error", "You are not authorized to add farmers for this FPO"
                        ));
                    }
                } else {
                    // If not an FPO user, deny (preAuthorize may allow global roles, but we scope by membership here)
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                            "error", "Only FPO-linked users can add farmers for this FPO"
                    ));
                }
            }
            
            // Get FPO entity
            FPO fpo = fpoRepository.findById(fpoId)
                .orElseThrow(() -> new RuntimeException("FPO not found with ID: " + fpoId));
            
            // Create farmer in global system
            FarmerDTO farmerDTO = new FarmerDTO();
            // Personal details required by validation
            farmerDTO.setSalutation((String) farmerData.getOrDefault("salutation", "Mr"));
            farmerDTO.setFirstName((String) farmerData.get("firstName"));
            farmerDTO.setLastName((String) farmerData.get("lastName"));
            farmerDTO.setGender((String) farmerData.getOrDefault("gender", "Male"));
            farmerDTO.setNationality((String) farmerData.getOrDefault("nationality", "Indian"));
            Object dobRaw = farmerData.get("dateOfBirth");
            if (dobRaw instanceof String && !((String) dobRaw).isBlank()) {
                try {
                    farmerDTO.setDateOfBirth(LocalDate.parse((String) dobRaw));
                } catch (Exception e) {
                    log.warn("Invalid dateOfBirth format, expected yyyy-MM-dd: {}", dobRaw);
                }
            }
            farmerDTO.setEmail((String) farmerData.get("email"));
            farmerDTO.setContactNumber((String) farmerData.get("contactNumber"));
            // Address
            farmerDTO.setCountry((String) farmerData.getOrDefault("country", "India"));
            farmerDTO.setVillage((String) farmerData.get("village"));
            farmerDTO.setDistrict((String) farmerData.get("district"));
            farmerDTO.setState((String) farmerData.get("state"));
            farmerDTO.setPincode((String) farmerData.get("pincode"));
            farmerDTO.setDocumentNumber((String) farmerData.get("aadharNumber")); // Using documentNumber for Aadhar
            farmerDTO.setAccountNumber((String) farmerData.get("bankAccountNumber"));
            farmerDTO.setIfscCode((String) farmerData.get("ifscCode"));
            farmerDTO.setBankName((String) farmerData.get("bankName"));
            farmerDTO.setBranchName((String) farmerData.get("branchName"));
            
            // Create farmer (without file uploads for now)
            FarmerDTO createdFarmerDTO = farmerService.createFarmer(farmerDTO, null, null, null, null);
            
            // Create FPOMember record linking farmer to FPO
            // We need to get the actual Farmer entity from the created farmer ID
            Farmer farmer = farmerRepository.findById(createdFarmerDTO.getId())
                .orElseThrow(() -> new RuntimeException("Farmer not found with ID: " + createdFarmerDTO.getId()));
            
            FPOMember fpoMember = FPOMember.builder()
                .fpo(fpo)
                .farmer(farmer)
                .memberType(FPOMember.MemberType.FARMER)
                .status(FPOMember.MemberStatus.ACTIVE)
                .build();
            
            FPOMember savedMember = fpoMemberRepository.save(fpoMember);
            
            log.info("Successfully created FPO farmer: {} for FPO: {}", createdFarmerDTO.getId(), fpoId);
            
            return ResponseEntity.ok(Map.of(
                "message", "FPO farmer created successfully",
                "farmerId", createdFarmerDTO.getId(),
                "memberId", savedMember.getId(),
                "fpoId", fpoId
            ));
            
        } catch (Exception e) {
            log.error("Error creating FPO farmer for FPO {}: {}", fpoId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to create FPO farmer: " + e.getMessage()));
        }
    }

    // FPO-specific Employee Management
    @PostMapping("/{fpoId}/employees")
    @PreAuthorize("hasRole('FPO')")
    public ResponseEntity<Map<String, Object>> createFPOEmployee(@PathVariable Long fpoId, @RequestBody Map<String, Object> employeeData) {
        try {
            log.info("Creating FPO-specific employee for FPO ID: {}", fpoId);
            
            // Get FPO entity
            FPO fpo = fpoRepository.findById(fpoId)
                .orElseThrow(() -> new RuntimeException("FPO not found with ID: " + fpoId));
            
            // Check for existing email
            String email = (String) employeeData.get("email");
            if (email != null && !email.trim().isEmpty()) {
                fpoUserRepository.findByEmail(email).ifPresent(existingUser -> {
                    throw new RuntimeException("Email already registered: " + email);
                });
            }
            
            // Create FPOUser (FPO-specific employee)
            com.farmer.Form.Entity.FPOUser fpoUser = com.farmer.Form.Entity.FPOUser.builder()
                .fpo(fpo)
                .firstName((String) employeeData.get("firstName"))
                .lastName((String) employeeData.get("lastName"))
                .email(email)
                .phoneNumber((String) employeeData.get("phoneNumber"))
                .role(com.farmer.Form.Entity.Role.EMPLOYEE)
                .status(com.farmer.Form.Entity.UserStatus.APPROVED)
                .passwordHash("Temp@123") // Temporary password - should be changed on first login
                .build();
            
            // Save FPOUser
            com.farmer.Form.Entity.FPOUser savedFpoUser = fpoUserRepository.save(fpoUser);
            
            // Create FPOMember record linking employee to FPO
            FPOMember fpoMember = FPOMember.builder()
                .fpo(fpo)
                .memberType(FPOMember.MemberType.EMPLOYEE)
                .status(FPOMember.MemberStatus.ACTIVE)
                .build();
            
            FPOMember savedMember = fpoMemberRepository.save(fpoMember);
            
            log.info("Successfully created FPO employee: {} for FPO: {}", savedFpoUser.getId(), fpoId);
            
            return ResponseEntity.ok(Map.of(
                "message", "FPO employee created successfully",
                "employeeId", savedFpoUser.getId(),
                "memberId", savedMember.getId(),
                "fpoId", fpoId
            ));
            
        } catch (Exception e) {
            log.error("Error creating FPO employee for FPO {}: {}", fpoId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to create FPO employee: " + e.getMessage()));
        }
    }
}
