package com.farmer.Form.Service.Impl;

import com.farmer.Form.DTO.*;
import com.farmer.Form.Entity.*;
import com.farmer.Form.Repository.*;
import com.farmer.Form.Service.FPOService;
import com.farmer.Form.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FPOServiceImpl implements FPOService {

    private final FPORepository fpoRepository;
    private final FPOMemberRepository fpoMemberRepository;
    private final FPOBoardMemberRepository fpoBoardMemberRepository;
    private final FPOServiceRepository fpoServiceRepository;
    private final FPOCropRepository fpoCropRepository;
    private final FPOTurnoverRepository fpoTurnoverRepository;
    private final FPOProductRepository fpoProductRepository;
    private final FPOProductCategoryRepository fpoProductCategoryRepository;
    private final FPONotificationRepository fpoNotificationRepository;
    private final com.farmer.Form.Repository.FPOInputShopRepository fpoInputShopRepository;
    private final FPOUserRepository fpoUserRepository;
    private final FarmerRepository farmerRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    @Override
    public FPODTO createFPO(FPOCreationDTO fpoCreationDTO) {
        log.info("Creating new FPO: {}", fpoCreationDTO.getFpoName());
        
        FPO fpo = FPO.builder()
                .fpoName(fpoCreationDTO.getFpoName())
                .ceoName(fpoCreationDTO.getCeoName())
                .phoneNumber(fpoCreationDTO.getPhoneNumber())
                .email(fpoCreationDTO.getEmail())
                .village(fpoCreationDTO.getVillage())
                .district(fpoCreationDTO.getDistrict())
                .state(fpoCreationDTO.getState())
                .pincode(fpoCreationDTO.getPincode())
                .joinDate(fpoCreationDTO.getJoinDate())
                .registrationType(fpoCreationDTO.getRegistrationType())
                .numberOfMembers(fpoCreationDTO.getNumberOfMembers())
                .registrationNumber(fpoCreationDTO.getRegistrationNumber())
                .panNumber(fpoCreationDTO.getPanNumber())
                .gstNumber(fpoCreationDTO.getGstNumber())
                .bankName(fpoCreationDTO.getBankName())
                .accountNumber(fpoCreationDTO.getAccountNumber())
                .ifscCode(fpoCreationDTO.getIfscCode())
                .branchName(fpoCreationDTO.getBranchName())
                .status(FPO.FPOStatus.ACTIVE)
                .build();

        FPO savedFPO = fpoRepository.save(fpo);
        log.info("FPO created successfully with ID: {}", savedFPO.getId());
        
        return convertToFPODTO(savedFPO);
    }

    @Override
    public FPODTO updateFPO(Long id, FPOCreationDTO fpoCreationDTO) {
        log.info("Updating FPO with ID: {}", id);
        
        FPO fpo = fpoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FPO not found with ID: " + id));

        fpo.setFpoName(fpoCreationDTO.getFpoName());
        fpo.setCeoName(fpoCreationDTO.getCeoName());
        fpo.setPhoneNumber(fpoCreationDTO.getPhoneNumber());
        fpo.setEmail(fpoCreationDTO.getEmail());
        fpo.setVillage(fpoCreationDTO.getVillage());
        fpo.setDistrict(fpoCreationDTO.getDistrict());
        fpo.setState(fpoCreationDTO.getState());
        fpo.setPincode(fpoCreationDTO.getPincode());
        fpo.setJoinDate(fpoCreationDTO.getJoinDate());
        fpo.setRegistrationType(fpoCreationDTO.getRegistrationType());
        fpo.setNumberOfMembers(fpoCreationDTO.getNumberOfMembers());
        fpo.setRegistrationNumber(fpoCreationDTO.getRegistrationNumber());
        fpo.setPanNumber(fpoCreationDTO.getPanNumber());
        fpo.setGstNumber(fpoCreationDTO.getGstNumber());
        fpo.setBankName(fpoCreationDTO.getBankName());
        fpo.setAccountNumber(fpoCreationDTO.getAccountNumber());
        fpo.setIfscCode(fpoCreationDTO.getIfscCode());
        fpo.setBranchName(fpoCreationDTO.getBranchName());

        FPO updatedFPO = fpoRepository.save(fpo);
        log.info("FPO updated successfully with ID: {}", updatedFPO.getId());
        
        return convertToFPODTO(updatedFPO);
    }

    @Override
    @Transactional(readOnly = true)
    public FPODTO getFPOById(Long id) {
        FPO fpo = fpoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FPO not found with ID: " + id));
        return convertToFPODTO(fpo);
    }

    @Override
    @Transactional(readOnly = true)
    public FPODTO getFPOByFpoId(String fpoId) {
        FPO fpo = fpoRepository.findByFpoId(fpoId)
                .orElseThrow(() -> new ResourceNotFoundException("FPO not found with FPO ID: " + fpoId));
        return convertToFPODTO(fpo);
    }

    @Override
    public void deleteFPO(Long id) {
        log.info("Deleting FPO with ID: {}", id);
        
        FPO fpo = fpoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FPO not found with ID: " + id));
        
        // Delete all related data first to avoid foreign key constraint violations
        try {
            // Delete FPO Users
            fpoUserRepository.deleteByFpoId(id);
            log.info("Deleted FPO Users for FPO ID: {}", id);
            
            // Delete FPO Members
            fpoMemberRepository.deleteByFpoId(id);
            log.info("Deleted FPO Members for FPO ID: {}", id);
            
            // Delete FPO Board Members
            fpoBoardMemberRepository.deleteByFpoId(id);
            log.info("Deleted FPO Board Members for FPO ID: {}", id);
            
            // Delete FPO Services
            fpoServiceRepository.deleteByFpoId(id);
            log.info("Deleted FPO Services for FPO ID: {}", id);
            
            // Delete FPO Crops
            fpoCropRepository.deleteByFpoId(id);
            log.info("Deleted FPO Crops for FPO ID: {}", id);
            
            // Delete FPO Turnovers
            fpoTurnoverRepository.deleteByFpoId(id);
            log.info("Deleted FPO Turnovers for FPO ID: {}", id);
            
            // Delete FPO Input Shops
            fpoInputShopRepository.deleteByFpoId(id);
            log.info("Deleted FPO Input Shops for FPO ID: {}", id);
            
            // Delete FPO Product Categories
            fpoProductCategoryRepository.deleteByFpoId(id);
            log.info("Deleted FPO Product Categories for FPO ID: {}", id);
            
            // Delete FPO Products
            fpoProductRepository.deleteByFpoId(id);
            log.info("Deleted FPO Products for FPO ID: {}", id);
            
            // Delete FPO Notifications
            fpoNotificationRepository.deleteByFpoId(id);
            log.info("Deleted FPO Notifications for FPO ID: {}", id);
            
        } catch (Exception e) {
            log.error("Error deleting related data for FPO ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to delete FPO due to related data: " + e.getMessage());
        }
        
        // Finally delete the FPO itself
        fpoRepository.delete(fpo);
        log.info("FPO deleted successfully with ID: {}", id);
    }

    @Override
    public void deactivateFPO(Long id) {
        log.info("Deactivating FPO with ID: {}", id);
        
        FPO fpo = fpoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FPO not found with ID: " + id));
        
        fpo.setStatus(FPO.FPOStatus.INACTIVE);
        fpoRepository.save(fpo);
        log.info("FPO deactivated successfully with ID: {}", id);
    }

    @Override
    public void activateFPO(Long id) {
        log.info("Activating FPO with ID: {}", id);
        
        FPO fpo = fpoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FPO not found with ID: " + id));
        
        fpo.setStatus(FPO.FPOStatus.ACTIVE);
        fpoRepository.save(fpo);
        log.info("FPO activated successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FPODTO> getAllFPOs(FPOListRequestDTO request) {
        log.info("Fetching FPOs with filters: {}", request);
        
        Sort sort = Sort.by(
                Sort.Direction.fromString(request.getSortDirection()),
                request.getSortBy()
        );
        
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        
        Page<FPO> fpoPage = fpoRepository.findFPOsWithFilters(
                request.getSearchTerm(),
                request.getState(),
                request.getDistrict(),
                request.getStatus(),
                request.getStartDate(),
                request.getEndDate(),
                pageable
        );
        
        return fpoPage.map(this::convertToFPODTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FPODTO> getFPOsByState(String state) {
        List<FPO> fpos = fpoRepository.findByState(state);
        return fpos.stream().map(this::convertToFPODTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FPODTO> getFPOsByDistrict(String district) {
        List<FPO> fpos = fpoRepository.findByDistrict(district);
        return fpos.stream().map(this::convertToFPODTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getDistinctStates() {
        return fpoRepository.findDistinctStates();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getDistinctDistrictsByState(String state) {
        return fpoRepository.findDistinctDistrictsByState(state);
    }

    @Override
    @Transactional(readOnly = true)
    public FPODashboardDTO getFPODashboard(Long fpoId) {
        log.info("Fetching FPO dashboard for FPO ID: {}", fpoId);
        
        FPO fpo = fpoRepository.findById(fpoId)
                .orElseThrow(() -> new ResourceNotFoundException("FPO not found with ID: " + fpoId));
        
        return buildFPODashboard(fpo);
    }

    @Override
    @Transactional(readOnly = true)
    public FPODashboardDTO getFPODashboardByFpoId(String fpoId) {
        log.info("Fetching FPO dashboard for FPO ID: {}", fpoId);
        
        FPO fpo = fpoRepository.findByFpoId(fpoId)
                .orElseThrow(() -> new ResourceNotFoundException("FPO not found with FPO ID: " + fpoId));
        
        return buildFPODashboard(fpo);
    }

    // Helper methods
    private FPODTO convertToFPODTO(FPO fpo) {
        return FPODTO.builder()
                .id(fpo.getId())
                .fpoId(fpo.getFpoId())
                .fpoName(fpo.getFpoName())
                .ceoName(fpo.getCeoName())
                .phoneNumber(fpo.getPhoneNumber())
                .email(fpo.getEmail())
                .village(fpo.getVillage())
                .district(fpo.getDistrict())
                .state(fpo.getState())
                .pincode(fpo.getPincode())
                .joinDate(fpo.getJoinDate())
                .registrationType(fpo.getRegistrationType())
                .numberOfMembers(fpo.getNumberOfMembers())
                .registrationNumber(fpo.getRegistrationNumber())
                .panNumber(fpo.getPanNumber())
                .gstNumber(fpo.getGstNumber())
                .bankName(fpo.getBankName())
                .accountNumber(fpo.getAccountNumber())
                .ifscCode(fpo.getIfscCode())
                .branchName(fpo.getBranchName())
                .status(fpo.getStatus())
                .createdAt(fpo.getCreatedAt())
                .updatedAt(fpo.getUpdatedAt())
                .build();
    }

    private FPODashboardDTO buildFPODashboard(FPO fpo) {
        // Get statistics
        Long totalMembers = fpoMemberRepository.countByFpoIdAndStatus(fpo.getId(), FPOMember.MemberStatus.ACTIVE);
        Long totalServices = fpoServiceRepository.countByFpoIdAndStatus(fpo.getId(), null);
        Long completedServices = fpoServiceRepository.countByFpoIdAndStatus(fpo.getId(), com.farmer.Form.Entity.FPOService.ServiceStatus.COMPLETED);
        Long totalCrops = fpoCropRepository.countByFpoIdAndStatus(fpo.getId(), null);
        Double totalArea = fpoCropRepository.sumAreaByFpoIdAndStatus(fpo.getId(), FPOCrop.CropStatus.HARVESTED);
        Long totalProducts = fpoProductRepository.countByFpoIdAndStatus(fpo.getId(), null);
        Long lowStockProductsCount = fpoProductRepository.countLowStockProductsByFpoId(fpo.getId());

        // Get financial data
        Integer currentYear = java.time.LocalDate.now().getYear();
        Double currentYearRevenue = fpoTurnoverRepository.sumRevenueByFpoIdAndFinancialYear(fpo.getId(), currentYear);
        Double currentYearExpenses = fpoTurnoverRepository.sumExpensesByFpoIdAndFinancialYear(fpo.getId(), currentYear);
        Double currentYearProfit = fpoTurnoverRepository.sumProfitByFpoIdAndFinancialYear(fpo.getId(), currentYear);

        return FPODashboardDTO.builder()
                .id(fpo.getId())
                .fpoId(fpo.getFpoId())
                .fpoName(fpo.getFpoName())
                .ceoName(fpo.getCeoName())
                .phoneNumber(fpo.getPhoneNumber())
                .email(fpo.getEmail())
                .address(fpo.getVillage() + ", " + fpo.getDistrict() + ", " + fpo.getState() + " - " + fpo.getPincode())
                .joinDate(fpo.getJoinDate())
                .status(fpo.getStatus().toString())
                .totalMembers(totalMembers)
                .activeMembers(totalMembers)
                .totalServices(totalServices)
                .completedServices(completedServices)
                .pendingServices(totalServices - completedServices)
                .totalCrops(totalCrops)
                .totalArea(totalArea != null ? totalArea : 0.0)
                .totalProducts(totalProducts)
                .lowStockProductsCount(lowStockProductsCount)
                .currentYearRevenue(currentYearRevenue != null ? currentYearRevenue : 0.0)
                .currentYearExpenses(currentYearExpenses != null ? currentYearExpenses : 0.0)
                .currentYearProfit(currentYearProfit != null ? currentYearProfit : 0.0)
                .build();
    }

    // FPO Statistics Methods
    @Override
    @Transactional(readOnly = true)
    public Long getTotalFPOsCount() {
        return fpoRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getActiveFPOsCount() {
        return fpoRepository.countByStatus(FPO.FPOStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getFPOsCountByStatus(FPO.FPOStatus status) {
        return fpoRepository.countByStatus(status);
    }

    // FPO Members Management
    @Override
    @Transactional(readOnly = true)
    public List<FPOMemberDTO> getFPOMembers(Long fpoId) {
        List<FPOMember> members = fpoMemberRepository.findByFpoId(fpoId);
        return members.stream().map(this::convertToFPOMemberDTO).collect(Collectors.toList());
    }

    @Override
    public FPOMemberDTO addMemberToFPO(Long fpoId, FPOMemberCreationDTO memberDTO) {
        FPO fpo = fpoRepository.findById(fpoId)
                .orElseThrow(() -> new ResourceNotFoundException("FPO not found with ID: " + fpoId));

        FPOMember.FPOMemberBuilder builder = FPOMember.builder()
                .fpo(fpo)
                .memberType(memberDTO.getMemberType())
                .status(FPOMember.MemberStatus.ACTIVE)
                .shareAmount(memberDTO.getShareAmount())
                .shareCertificateNumber(memberDTO.getShareCertificateNumber())
                .remarks(memberDTO.getRemarks());

        if (memberDTO.getFarmerId() != null) {
            com.farmer.Form.Entity.Farmer farmer = farmerRepository.findById(memberDTO.getFarmerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Farmer not found with ID: " + memberDTO.getFarmerId()));
            builder.farmer(farmer);
        }
        if (memberDTO.getEmployeeId() != null) {
            com.farmer.Form.Entity.Employee employee = employeeRepository.findById(memberDTO.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + memberDTO.getEmployeeId()));
            builder.employee(employee);
        }
        if (memberDTO.getUserId() != null) {
            com.farmer.Form.Entity.User user = userRepository.findById(memberDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + memberDTO.getUserId()));
            builder.user(user);
        }

        FPOMember savedMember = fpoMemberRepository.save(builder.build());
        return convertToFPOMemberDTO(savedMember);
    }

    @Override
    public void removeMemberFromFPO(Long fpoId, Long memberId) {
        FPOMember member = fpoMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + memberId));
        if (member.getFpo() == null || !member.getFpo().getId().equals(fpoId)) {
            throw new ResourceNotFoundException("Member not found with ID: " + memberId + " in FPO: " + fpoId);
        }
        fpoMemberRepository.delete(member);
    }

    @Override
    public void updateMemberStatus(Long fpoId, Long memberId, FPOMember.MemberStatus status) {
        FPOMember member = fpoMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + memberId));
        if (member.getFpo() == null || !member.getFpo().getId().equals(fpoId)) {
            throw new ResourceNotFoundException("Member not found with ID: " + memberId + " in FPO: " + fpoId);
        }
        member.setStatus(status);
        fpoMemberRepository.save(member);
    }

    // FPO Board Members Management
    @Override
    @Transactional(readOnly = true)
    public List<FPOBoardMemberDTO> getFPOBoardMembers(Long fpoId) {
        List<FPOBoardMember> boardMembers = fpoBoardMemberRepository.findByFpoId(fpoId);
        return boardMembers.stream().map(this::convertToFPOBoardMemberDTO).collect(Collectors.toList());
    }

    @Override
    public FPOBoardMemberDTO addBoardMember(Long fpoId, FPOBoardMemberCreationDTO boardMemberDTO) {
        FPO fpo = fpoRepository.findById(fpoId)
                .orElseThrow(() -> new ResourceNotFoundException("FPO not found with ID: " + fpoId));

        FPOBoardMember boardMember = FPOBoardMember.builder()
                .fpo(fpo)
                .name(boardMemberDTO.getName())
                .role(boardMemberDTO.getRole())
                .phoneNumber(boardMemberDTO.getPhoneNumber())
                .email(boardMemberDTO.getEmail())
                .address(boardMemberDTO.getAddress())
                .qualification(boardMemberDTO.getQualification())
                .experience(boardMemberDTO.getExperience())
                .photoFileName(boardMemberDTO.getPhotoFileName())
                .documentFileName(boardMemberDTO.getDocumentFileName())
                .remarks(boardMemberDTO.getRemarks())
                .status(FPOBoardMember.BoardMemberStatus.ACTIVE)
                .build();

        FPOBoardMember savedBoardMember = fpoBoardMemberRepository.save(boardMember);
        return convertToFPOBoardMemberDTO(savedBoardMember);
    }

    @Override
    public FPOBoardMemberDTO updateBoardMember(Long fpoId, Long boardMemberId, FPOBoardMemberCreationDTO boardMemberDTO) {
        FPOBoardMember boardMember = fpoBoardMemberRepository.findById(boardMemberId)
                .orElseThrow(() -> new ResourceNotFoundException("Board member not found with ID: " + boardMemberId));
        if (boardMember.getFpo() == null || !boardMember.getFpo().getId().equals(fpoId)) {
            throw new ResourceNotFoundException("Board member not found with ID: " + boardMemberId + " in FPO: " + fpoId);
        }

        boardMember.setName(boardMemberDTO.getName());
        boardMember.setRole(boardMemberDTO.getRole());
        boardMember.setPhoneNumber(boardMemberDTO.getPhoneNumber());
        boardMember.setEmail(boardMemberDTO.getEmail());
        boardMember.setAddress(boardMemberDTO.getAddress());
        boardMember.setQualification(boardMemberDTO.getQualification());
        boardMember.setExperience(boardMemberDTO.getExperience());
        boardMember.setPhotoFileName(boardMemberDTO.getPhotoFileName());
        boardMember.setDocumentFileName(boardMemberDTO.getDocumentFileName());
        boardMember.setRemarks(boardMemberDTO.getRemarks());
        
        // Update status if provided in the DTO
        if (boardMemberDTO.getStatus() != null) {
            boardMember.setStatus(boardMemberDTO.getStatus());
        }

        FPOBoardMember updatedBoardMember = fpoBoardMemberRepository.save(boardMember);
        return convertToFPOBoardMemberDTO(updatedBoardMember);
    }

    @Override
    public void removeBoardMember(Long fpoId, Long boardMemberId) {
        FPOBoardMember boardMember = fpoBoardMemberRepository.findById(boardMemberId)
                .orElseThrow(() -> new ResourceNotFoundException("Board member not found with ID: " + boardMemberId));
        if (boardMember.getFpo() == null || !boardMember.getFpo().getId().equals(fpoId)) {
            throw new ResourceNotFoundException("Board member not found with ID: " + boardMemberId + " in FPO: " + fpoId);
        }
        fpoBoardMemberRepository.delete(boardMember);
    }

    // FPO Services Management
    @Override
    @Transactional(readOnly = true)
    public List<FPOServiceDTO> getFPOServices(Long fpoId) {
        List<com.farmer.Form.Entity.FPOService> services = fpoServiceRepository.findByFpoId(fpoId);
        return services.stream().map(this::convertToFPOServiceDTO).collect(Collectors.toList());
    }

    @Override
    public FPOServiceDTO createService(Long fpoId, FPOServiceCreationDTO serviceDTO) {
        FPO fpo = fpoRepository.findById(fpoId)
                .orElseThrow(() -> new ResourceNotFoundException("FPO not found with ID: " + fpoId));

        com.farmer.Form.Entity.FPOService.FPOServiceBuilder builder = com.farmer.Form.Entity.FPOService.builder()
                .fpo(fpo)
                .serviceType(serviceDTO.getServiceType())
                .description(serviceDTO.getDescription())
                .scheduledAt(serviceDTO.getScheduledAt())
                .serviceProvider(serviceDTO.getServiceProvider())
                .serviceProviderContact(serviceDTO.getServiceProviderContact())
                .serviceCost(serviceDTO.getServiceCost())
                .paymentStatus(serviceDTO.getPaymentStatus())
                .remarks(serviceDTO.getRemarks())
                .status(com.farmer.Form.Entity.FPOService.ServiceStatus.REQUESTED);

        if (serviceDTO.getFarmerId() != null) {
            com.farmer.Form.Entity.Farmer farmer = farmerRepository.findById(serviceDTO.getFarmerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Farmer not found with ID: " + serviceDTO.getFarmerId()));
            builder.farmer(farmer);
        }

        com.farmer.Form.Entity.FPOService service = builder.build();

        com.farmer.Form.Entity.FPOService savedService = fpoServiceRepository.save(service);
        return convertToFPOServiceDTO(savedService);
    }

    @Override
    public FPOServiceDTO updateService(Long fpoId, Long serviceId, FPOServiceCreationDTO serviceDTO) {
        com.farmer.Form.Entity.FPOService service = fpoServiceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + serviceId));
        if (service.getFpo() == null || !service.getFpo().getId().equals(fpoId)) {
            throw new ResourceNotFoundException("Service not found with ID: " + serviceId + " in FPO: " + fpoId);
        }

        service.setServiceType(serviceDTO.getServiceType());
        service.setDescription(serviceDTO.getDescription());
        service.setScheduledAt(serviceDTO.getScheduledAt());
        service.setServiceProvider(serviceDTO.getServiceProvider());
        service.setServiceProviderContact(serviceDTO.getServiceProviderContact());
        service.setServiceCost(serviceDTO.getServiceCost());
        service.setPaymentStatus(serviceDTO.getPaymentStatus());
        service.setRemarks(serviceDTO.getRemarks());

        com.farmer.Form.Entity.FPOService updatedService = fpoServiceRepository.save(service);
        return convertToFPOServiceDTO(updatedService);
    }

    @Override
    public void updateServiceStatus(Long fpoId, Long serviceId, com.farmer.Form.Entity.FPOService.ServiceStatus status) {
        com.farmer.Form.Entity.FPOService service = fpoServiceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + serviceId));
        if (service.getFpo() == null || !service.getFpo().getId().equals(fpoId)) {
            throw new ResourceNotFoundException("Service not found with ID: " + serviceId + " in FPO: " + fpoId);
        }
        
        service.setStatus(status);
        fpoServiceRepository.save(service);
    }

    @Override
    public void removeService(Long fpoId, Long serviceId) {
        log.info("Deleting service {} from FPO with ID: {}", serviceId, fpoId);
        com.farmer.Form.Entity.FPOService service = fpoServiceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + serviceId));
        if (service.getFpo() == null || !service.getFpo().getId().equals(fpoId)) {
            throw new ResourceNotFoundException("Service not found with ID: " + serviceId + " in FPO: " + fpoId);
        }
        
        fpoServiceRepository.delete(service);
        log.info("Service {} deleted successfully from FPO with ID: {}", serviceId, fpoId);
    }

    // FPO Crops Management
    @Override
    @Transactional(readOnly = true)
    public List<FPOCropDTO> getFPOCrops(Long fpoId) {
        List<FPOCrop> crops = fpoCropRepository.findByFpoId(fpoId);
        return crops.stream().map(this::convertToFPOCropDTO).collect(Collectors.toList());
    }

    @Override
    public FPOCropDTO createCrop(Long fpoId, FPOCropCreationDTO cropDTO) {
        FPO fpo = fpoRepository.findById(fpoId)
                .orElseThrow(() -> new ResourceNotFoundException("FPO not found with ID: " + fpoId));

        FPOCrop crop = FPOCrop.builder()
                .fpo(fpo)
                .cropName(cropDTO.getCropName())
                .variety(cropDTO.getVariety())
                .area(cropDTO.getArea())
                .season(cropDTO.getSeason())
                .sowingDate(cropDTO.getSowingDate())
                .expectedHarvestDate(cropDTO.getExpectedHarvestDate())
                .expectedYield(cropDTO.getExpectedYield())
                .status(FPOCrop.CropStatus.PLANNED)
                .build();

        FPOCrop savedCrop = fpoCropRepository.save(crop);
        return convertToFPOCropDTO(savedCrop);
    }

    @Override
    public FPOCropDTO updateCrop(Long fpoId, Long cropId, FPOCropCreationDTO cropDTO) {
        FPOCrop crop = fpoCropRepository.findById(cropId)
                .orElseThrow(() -> new ResourceNotFoundException("Crop not found with ID: " + cropId));
        if (crop.getFpo() == null || !crop.getFpo().getId().equals(fpoId)) {
            throw new ResourceNotFoundException("Crop not found with ID: " + cropId + " in FPO: " + fpoId);
        }

        crop.setCropName(cropDTO.getCropName());
        crop.setVariety(cropDTO.getVariety());
        crop.setArea(cropDTO.getArea());
        crop.setSeason(cropDTO.getSeason());
        crop.setSowingDate(cropDTO.getSowingDate());
        crop.setExpectedHarvestDate(cropDTO.getExpectedHarvestDate());
        crop.setExpectedYield(cropDTO.getExpectedYield());

        FPOCrop updatedCrop = fpoCropRepository.save(crop);
        return convertToFPOCropDTO(updatedCrop);
    }

    @Override
    public void updateCropStatus(Long fpoId, Long cropId, FPOCrop.CropStatus status) {
        FPOCrop crop = fpoCropRepository.findById(cropId)
                .orElseThrow(() -> new ResourceNotFoundException("Crop not found with ID: " + cropId));
        if (crop.getFpo() == null || !crop.getFpo().getId().equals(fpoId)) {
            throw new ResourceNotFoundException("Crop not found with ID: " + cropId + " in FPO: " + fpoId);
        }
        
        crop.setStatus(status);
        fpoCropRepository.save(crop);
    }

    @Override
    public void deleteCrop(Long fpoId, Long cropId) {
        FPOCrop crop = fpoCropRepository.findById(cropId)
                .orElseThrow(() -> new ResourceNotFoundException("Crop not found with ID: " + cropId));
        if (crop.getFpo() == null || !crop.getFpo().getId().equals(fpoId)) {
            throw new ResourceNotFoundException("Crop not found with ID: " + cropId + " in FPO: " + fpoId);
        }
        
        fpoCropRepository.delete(crop);
    }

    // Input Shop
    @Override
    public java.util.List<com.farmer.Form.DTO.FPOInputShopDTO> getFPOInputShops(Long fpoId) {
        FPO fpo = fpoRepository.findById(fpoId).orElseThrow(() -> new ResourceNotFoundException("FPO not found with id: " + fpoId));
        var list = fpoInputShopRepository.findByFpo(fpo);
        return list.stream().map(this::convertToFPOInputShopDTO).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public com.farmer.Form.DTO.FPOInputShopDTO createInputShop(Long fpoId, com.farmer.Form.DTO.FPOInputShopCreationDTO dto) {
        FPO fpo = fpoRepository.findById(fpoId).orElseThrow(() -> new ResourceNotFoundException("FPO not found with id: " + fpoId));
        var entity = com.farmer.Form.Entity.FPOInputShop.builder()
                .fpo(fpo)
                .shopName(dto.getShopName())
                .seedLicense(dto.getSeedLicense())
                .pesticideLicense(dto.getPesticideLicense())
                .fertiliserLicense(dto.getFertiliserLicense())
                .build();
        var saved = fpoInputShopRepository.save(entity);
        return convertToFPOInputShopDTO(saved);
    }

    @Override
    public com.farmer.Form.DTO.FPOInputShopDTO updateInputShop(Long fpoId, Long shopId, com.farmer.Form.DTO.FPOInputShopCreationDTO dto) {
        var shop = fpoInputShopRepository.findById(shopId).orElseThrow(() -> new ResourceNotFoundException("Input shop not found with id: " + shopId));
        if (shop.getFpo() == null || !shop.getFpo().getId().equals(fpoId)) {
            throw new ResourceNotFoundException("Input shop not found with id: " + shopId + " in FPO: " + fpoId);
        }
        shop.setShopName(dto.getShopName());
        shop.setSeedLicense(dto.getSeedLicense());
        shop.setPesticideLicense(dto.getPesticideLicense());
        shop.setFertiliserLicense(dto.getFertiliserLicense());
        var updated = fpoInputShopRepository.save(shop);
        return convertToFPOInputShopDTO(updated);
    }

    @Override
    public void deleteInputShop(Long fpoId, Long shopId) {
        var shop = fpoInputShopRepository.findById(shopId).orElseThrow(() -> new ResourceNotFoundException("Input shop not found with id: " + shopId));
        if (shop.getFpo() == null || !shop.getFpo().getId().equals(fpoId)) {
            throw new ResourceNotFoundException("Input shop not found with id: " + shopId + " in FPO: " + fpoId);
        }
        fpoInputShopRepository.delete(shop);
    }

    private com.farmer.Form.DTO.FPOInputShopDTO convertToFPOInputShopDTO(com.farmer.Form.Entity.FPOInputShop shop) {
        return com.farmer.Form.DTO.FPOInputShopDTO.builder()
                .id(shop.getId())
                .fpoId(shop.getFpo() != null ? shop.getFpo().getId() : null)
                .fpoName(shop.getFpo() != null ? shop.getFpo().getFpoName() : null)
                .shopName(shop.getShopName())
                .seedLicense(shop.getSeedLicense())
                .pesticideLicense(shop.getPesticideLicense())
                .fertiliserLicense(shop.getFertiliserLicense())
                .createdAt(shop.getCreatedAt())
                .updatedAt(shop.getUpdatedAt())
                .build();
    }

    // FPO Turnover Management
    @Override
    @Transactional(readOnly = true)
    public List<FPOTurnoverDTO> getFPOTurnovers(Long fpoId) {
        List<FPOTurnover> turnovers = fpoTurnoverRepository.findByFpoId(fpoId);
        return turnovers.stream().map(this::convertToFPOTurnoverDTO).collect(Collectors.toList());
    }

    @Override
    public FPOTurnoverDTO createTurnover(Long fpoId, FPOTurnoverCreationDTO turnoverDTO) {
        FPO fpo = fpoRepository.findById(fpoId)
                .orElseThrow(() -> new ResourceNotFoundException("FPO not found with ID: " + fpoId));

        FPOTurnover turnover = FPOTurnover.builder()
                .fpo(fpo)
                .financialYear(turnoverDTO.getFinancialYear())
                .quarter(turnoverDTO.getQuarter())
                .month(turnoverDTO.getMonth())
                .revenue(turnoverDTO.getRevenue())
                .expenses(turnoverDTO.getExpenses())
                .profit(turnoverDTO.getRevenue() - turnoverDTO.getExpenses())
                .turnoverType(turnoverDTO.getTurnoverType())
                .description(turnoverDTO.getDescription())
                .remarks(turnoverDTO.getRemarks())
                .documentFileName(turnoverDTO.getDocumentFileName())
                .enteredBy(turnoverDTO.getEnteredBy())
                .build();

        FPOTurnover savedTurnover = fpoTurnoverRepository.save(turnover);
        return convertToFPOTurnoverDTO(savedTurnover);
    }

    @Override
    public FPOTurnoverDTO updateTurnover(Long fpoId, Long turnoverId, FPOTurnoverCreationDTO turnoverDTO) {
        FPOTurnover turnover = fpoTurnoverRepository.findById(turnoverId)
                .orElseThrow(() -> new ResourceNotFoundException("Turnover not found with ID: " + turnoverId));
        if (turnover.getFpo() == null || !turnover.getFpo().getId().equals(fpoId)) {
            throw new ResourceNotFoundException("Turnover not found with ID: " + turnoverId + " in FPO: " + fpoId);
        }

        turnover.setFinancialYear(turnoverDTO.getFinancialYear());
        turnover.setQuarter(turnoverDTO.getQuarter());
        turnover.setMonth(turnoverDTO.getMonth());
        turnover.setRevenue(turnoverDTO.getRevenue());
        turnover.setExpenses(turnoverDTO.getExpenses());
        turnover.setProfit(turnoverDTO.getRevenue() - turnoverDTO.getExpenses());
        turnover.setTurnoverType(turnoverDTO.getTurnoverType());
        turnover.setDescription(turnoverDTO.getDescription());
        turnover.setRemarks(turnoverDTO.getRemarks());
        turnover.setDocumentFileName(turnoverDTO.getDocumentFileName());
        turnover.setEnteredBy(turnoverDTO.getEnteredBy());

        FPOTurnover updatedTurnover = fpoTurnoverRepository.save(turnover);
        return convertToFPOTurnoverDTO(updatedTurnover);
    }

    @Override
    public void deleteTurnover(Long fpoId, Long turnoverId) {
        FPOTurnover turnover = fpoTurnoverRepository.findById(turnoverId)
                .orElseThrow(() -> new ResourceNotFoundException("Turnover not found with ID: " + turnoverId));
        if (turnover.getFpo() == null || !turnover.getFpo().getId().equals(fpoId)) {
            throw new ResourceNotFoundException("Turnover not found with ID: " + turnoverId + " in FPO: " + fpoId);
        }
        
        fpoTurnoverRepository.delete(turnover);
    }

    // FPO Products Management
    @Override
    @Transactional(readOnly = true)
    public List<FPOProductDTO> getFPOProducts(Long fpoId) {
        List<FPOProduct> products = fpoProductRepository.findByFpoId(fpoId);
        return products.stream().map(this::convertToFPOProductDTO).collect(Collectors.toList());
    }

    @Override
    public FPOProductDTO createProduct(Long fpoId, FPOProductCreationDTO productDTO) {
        FPO fpo = fpoRepository.findById(fpoId)
                .orElseThrow(() -> new ResourceNotFoundException("FPO not found with ID: " + fpoId));

        FPOProduct product = FPOProduct.builder()
                .fpo(fpo)
                .productName(productDTO.getProductName())
                .description(productDTO.getDescription())
                .brand(productDTO.getBrand())
                .unit(productDTO.getUnit())
                .price(productDTO.getPrice())
                .stockQuantity(productDTO.getStockQuantity())
                .minimumStock(productDTO.getMinimumStock())
                .supplier(productDTO.getSupplier())
                .supplierContact(productDTO.getSupplierContact())
                .supplierAddress(productDTO.getSupplierAddress())
                .batchNumber(productDTO.getBatchNumber())
                .expiryDate(productDTO.getExpiryDate())
                .photoFileName(productDTO.getPhotoFileName())
                .remarks(productDTO.getRemarks())
                .discountPercentage(productDTO.getDiscountPercentage())
                .taxPercentage(productDTO.getTaxPercentage())
                .status(FPOProduct.ProductStatus.AVAILABLE)
                .build();

        if (productDTO.getCategoryId() != null) {
            FPOProductCategory category = fpoProductCategoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product category not found with ID: " + productDTO.getCategoryId()));
            product.setCategory(category);
        }

        FPOProduct savedProduct = fpoProductRepository.save(product);
        return convertToFPOProductDTO(savedProduct);
    }

    @Override
    public FPOProductDTO updateProduct(Long fpoId, Long productId, FPOProductCreationDTO productDTO) {
        FPOProduct product = fpoProductRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
        if (product.getFpo() == null || !product.getFpo().getId().equals(fpoId)) {
            throw new ResourceNotFoundException("Product not found with ID: " + productId + " in FPO: " + fpoId);
        }

        product.setProductName(productDTO.getProductName());
        product.setDescription(productDTO.getDescription());
        product.setBrand(productDTO.getBrand());
        product.setUnit(productDTO.getUnit());
        product.setPrice(productDTO.getPrice());
        product.setStockQuantity(productDTO.getStockQuantity());
        product.setMinimumStock(productDTO.getMinimumStock());
        product.setSupplier(productDTO.getSupplier());
        product.setSupplierContact(productDTO.getSupplierContact());
        product.setSupplierAddress(productDTO.getSupplierAddress());
        product.setBatchNumber(productDTO.getBatchNumber());
        product.setExpiryDate(productDTO.getExpiryDate());
        product.setPhotoFileName(productDTO.getPhotoFileName());
        product.setRemarks(productDTO.getRemarks());
        product.setDiscountPercentage(productDTO.getDiscountPercentage());
        product.setTaxPercentage(productDTO.getTaxPercentage());

        if (productDTO.getCategoryId() != null) {
            FPOProductCategory category = fpoProductCategoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product category not found with ID: " + productDTO.getCategoryId()));
            product.setCategory(category);
        }

        FPOProduct updatedProduct = fpoProductRepository.save(product);
        return convertToFPOProductDTO(updatedProduct);
    }

    @Override
    public void updateProductStock(Long fpoId, Long productId, Integer newStock) {
        FPOProduct product = fpoProductRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
        if (product.getFpo() == null || !product.getFpo().getId().equals(fpoId)) {
            throw new ResourceNotFoundException("Product not found with ID: " + productId + " in FPO: " + fpoId);
        }
        
        product.setStockQuantity(newStock);
        fpoProductRepository.save(product);
    }

    // FPO Product Categories Management
    @Override
    @Transactional(readOnly = true)
    public List<FPOProductCategoryDTO> getFPOProductCategories(Long fpoId) {
        List<FPOProductCategory> categories = fpoProductCategoryRepository.findByFpoId(fpoId);
        return categories.stream().map(this::convertToFPOProductCategoryDTO).collect(Collectors.toList());
    }

    @Override
    public FPOProductCategoryDTO createProductCategory(Long fpoId, FPOProductCategoryCreationDTO categoryDTO) {
        FPO fpo = fpoRepository.findById(fpoId)
                .orElseThrow(() -> new ResourceNotFoundException("FPO not found with ID: " + fpoId));

        FPOProductCategory category = FPOProductCategory.builder()
                .fpo(fpo)
                .categoryName(categoryDTO.getCategoryName())
                .description(categoryDTO.getDescription())
                .build();

        FPOProductCategory savedCategory = fpoProductCategoryRepository.save(category);
        return convertToFPOProductCategoryDTO(savedCategory);
    }

    @Override
    public FPOProductCategoryDTO updateProductCategory(Long fpoId, Long categoryId, FPOProductCategoryCreationDTO categoryDTO) {
        FPOProductCategory category = fpoProductCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found with ID: " + categoryId));
        if (category.getFpo() == null || !category.getFpo().getId().equals(fpoId)) {
            throw new ResourceNotFoundException("Product category not found with ID: " + categoryId + " in FPO: " + fpoId);
        }

        category.setCategoryName(categoryDTO.getCategoryName());
        category.setDescription(categoryDTO.getDescription());

        FPOProductCategory updatedCategory = fpoProductCategoryRepository.save(category);
        return convertToFPOProductCategoryDTO(updatedCategory);
    }

    @Override
    public void deleteProductCategory(Long fpoId, Long categoryId) {
        FPOProductCategory category = fpoProductCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found with ID: " + categoryId));
        if (category.getFpo() == null || !category.getFpo().getId().equals(fpoId)) {
            throw new ResourceNotFoundException("Product category not found with ID: " + categoryId + " in FPO: " + fpoId);
        }
        
        fpoProductCategoryRepository.delete(category);
    }

    // FPO Notifications Management
    @Override
    @Transactional(readOnly = true)
    public List<FPONotificationDTO> getFPONotifications(Long fpoId) {
        List<FPONotification> notifications = fpoNotificationRepository.findByFpoId(fpoId);
        return notifications.stream().map(this::convertToFPONotificationDTO).collect(Collectors.toList());
    }

    @Override
    public FPONotificationDTO createNotification(Long fpoId, FPONotificationCreationDTO notificationDTO) {
        FPO fpo = fpoRepository.findById(fpoId)
                .orElseThrow(() -> new ResourceNotFoundException("FPO not found with ID: " + fpoId));

        FPONotification notification = FPONotification.builder()
                .fpo(fpo)
                .title(notificationDTO.getTitle())
                .message(notificationDTO.getMessage())
                .type(notificationDTO.getType())
                .priority(notificationDTO.getPriority())
                .status(FPONotification.NotificationStatus.UNREAD)
                .build();

        FPONotification savedNotification = fpoNotificationRepository.save(notification);
        return convertToFPONotificationDTO(savedNotification);
    }

    @Override
    public void markNotificationAsRead(Long fpoId, Long notificationId) {
        FPONotification notification = fpoNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + notificationId));
        if (notification.getFpo() == null || !notification.getFpo().getId().equals(fpoId)) {
            throw new ResourceNotFoundException("Notification not found with ID: " + notificationId + " in FPO: " + fpoId);
        }
        
        notification.setStatus(FPONotification.NotificationStatus.READ);
        fpoNotificationRepository.save(notification);
    }

    @Override
    public void deleteNotification(Long fpoId, Long notificationId) {
        FPONotification notification = fpoNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + notificationId));
        if (notification.getFpo() == null || !notification.getFpo().getId().equals(fpoId)) {
            throw new ResourceNotFoundException("Notification not found with ID: " + notificationId + " in FPO: " + fpoId);
        }
        
        fpoNotificationRepository.delete(notification);
    }

    // Helper methods for DTO conversion
    private FPOMemberDTO convertToFPOMemberDTO(FPOMember member) {
        return FPOMemberDTO.builder()
                .id(member.getId())
                .fpoId(member.getFpo() != null ? member.getFpo().getId() : null)
                .fpoName(member.getFpo() != null ? member.getFpo().getFpoName() : null)
                .farmerId(member.getFarmer() != null ? member.getFarmer().getId() : null)
                .employeeId(member.getEmployee() != null ? member.getEmployee().getId() : null)
                .userId(member.getUser() != null ? member.getUser().getId() : null)
                .memberType(member.getMemberType())
                .status(member.getStatus())
                .joinedAt(member.getJoinedAt())
                .updatedAt(member.getUpdatedAt())
                .memberId(member.getMemberId())
                .shareAmount(member.getShareAmount())
                .shareCertificateNumber(member.getShareCertificateNumber())
                .remarks(member.getRemarks())
                .build();
    }

    private FPOBoardMemberDTO convertToFPOBoardMemberDTO(FPOBoardMember boardMember) {
        return FPOBoardMemberDTO.builder()
                .id(boardMember.getId())
                .fpoId(boardMember.getFpo() != null ? boardMember.getFpo().getId() : null)
                .fpoName(boardMember.getFpo() != null ? boardMember.getFpo().getFpoName() : null)
                .name(boardMember.getName())
                .role(boardMember.getRole())
                .phoneNumber(boardMember.getPhoneNumber())
                .email(boardMember.getEmail())
                .address(boardMember.getAddress())
                .qualification(boardMember.getQualification())
                .experience(boardMember.getExperience())
                .photoFileName(boardMember.getPhotoFileName())
                .documentFileName(boardMember.getDocumentFileName())
                .remarks(boardMember.getRemarks())
                .appointedAt(boardMember.getAppointedAt())
                .updatedAt(boardMember.getUpdatedAt())
                .status(boardMember.getStatus())
                .build();
    }

    private FPOServiceDTO convertToFPOServiceDTO(com.farmer.Form.Entity.FPOService service) {
        return FPOServiceDTO.builder()
                .id(service.getId())
                .fpoId(service.getFpo() != null ? service.getFpo().getId() : null)
                .fpoName(service.getFpo() != null ? service.getFpo().getFpoName() : null)
                .farmerId(service.getFarmer() != null ? service.getFarmer().getId() : null)
                .serviceType(service.getServiceType())
                .description(service.getDescription())
                .status(service.getStatus())
                .requestedAt(service.getRequestedAt())
                .updatedAt(service.getUpdatedAt())
                .scheduledAt(service.getScheduledAt())
                .completedAt(service.getCompletedAt())
                .serviceProvider(service.getServiceProvider())
                .serviceProviderContact(service.getServiceProviderContact())
                .serviceCost(service.getServiceCost())
                .paymentStatus(service.getPaymentStatus())
                .remarks(service.getRemarks())
                .result(service.getResult())
                .reportFileName(service.getReportFileName())
                .build();
    }

    private FPOCropDTO convertToFPOCropDTO(FPOCrop crop) {
        return FPOCropDTO.builder()
                .id(crop.getId())
                .cropName(crop.getCropName())
                .variety(crop.getVariety())
                .area(crop.getArea())
                .season(crop.getSeason())
                .sowingDate(crop.getSowingDate())
                .expectedHarvestDate(crop.getExpectedHarvestDate())
                .expectedYield(crop.getExpectedYield())
                .status(crop.getStatus())
                .createdAt(crop.getCreatedAt())
                .updatedAt(crop.getUpdatedAt())
                .build();
    }


    private FPOTurnoverDTO convertToFPOTurnoverDTO(FPOTurnover turnover) {
        return FPOTurnoverDTO.builder()
                .id(turnover.getId())
                .financialYear(turnover.getFinancialYear())
                .quarter(turnover.getQuarter())
                .month(turnover.getMonth())
                .revenue(turnover.getRevenue())
                .expenses(turnover.getExpenses())
                .profit(turnover.getProfit())
                .turnoverType(turnover.getTurnoverType())
                .description(turnover.getDescription())
                .remarks(turnover.getRemarks())
                .createdAt(turnover.getCreatedAt())
                .updatedAt(turnover.getUpdatedAt())
                .build();
    }

    private FPOProductDTO convertToFPOProductDTO(FPOProduct product) {
        return FPOProductDTO.builder()
                .id(product.getId())
                .fpoId(product.getFpo() != null ? product.getFpo().getId() : null)
                .fpoName(product.getFpo() != null ? product.getFpo().getFpoName() : null)
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getCategoryName() : null)
                .productName(product.getProductName())
                .description(product.getDescription())
                .brand(product.getBrand())
                .unit(product.getUnit())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .minimumStock(product.getMinimumStock())
                .supplier(product.getSupplier())
                .supplierContact(product.getSupplierContact())
                .supplierAddress(product.getSupplierAddress())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .batchNumber(product.getBatchNumber())
                .expiryDate(product.getExpiryDate())
                .photoFileName(product.getPhotoFileName())
                .remarks(product.getRemarks())
                .discountPercentage(product.getDiscountPercentage())
                .taxPercentage(product.getTaxPercentage())
                .build();
    }

    private FPOProductCategoryDTO convertToFPOProductCategoryDTO(FPOProductCategory category) {
        return FPOProductCategoryDTO.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .description(category.getDescription())
                .build();
    }

    private FPONotificationDTO convertToFPONotificationDTO(FPONotification notification) {
        return FPONotificationDTO.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .priority(notification.getPriority())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }
}
