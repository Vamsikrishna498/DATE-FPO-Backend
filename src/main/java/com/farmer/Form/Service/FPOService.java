package com.farmer.Form.Service;

import com.farmer.Form.DTO.*;
import com.farmer.Form.Entity.FPO;
import com.farmer.Form.Entity.FPOCrop;
import com.farmer.Form.Entity.FPOMember;
import com.farmer.Form.Entity.FPOBoardMember;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FPOService {
    
    // FPO CRUD Operations
    FPODTO createFPO(FPOCreationDTO fpoCreationDTO);
    FPODTO updateFPO(Long id, FPOCreationDTO fpoCreationDTO);
    FPODTO getFPOById(Long id);
    FPODTO getFPOByFpoId(String fpoId);
    void deleteFPO(Long id);
    void deactivateFPO(Long id);
    void activateFPO(Long id);
    
    // FPO List and Search
    Page<FPODTO> getAllFPOs(FPOListRequestDTO request);
    List<FPODTO> getFPOsByState(String state);
    List<FPODTO> getFPOsByDistrict(String district);
    List<String> getDistinctStates();
    List<String> getDistinctDistrictsByState(String state);
    
    // FPO Dashboard
    FPODashboardDTO getFPODashboard(Long fpoId);
    FPODashboardDTO getFPODashboardByFpoId(String fpoId);
    
    // FPO Statistics
    Long getTotalFPOsCount();
    Long getActiveFPOsCount();
    Long getFPOsCountByStatus(FPO.FPOStatus status);
    
    // FPO Members Management
    List<FPOMemberDTO> getFPOMembers(Long fpoId);
    FPOMemberDTO addMemberToFPO(Long fpoId, FPOMemberCreationDTO memberDTO);
    void removeMemberFromFPO(Long fpoId, Long memberId);
    void updateMemberStatus(Long fpoId, Long memberId, FPOMember.MemberStatus status);
    
    // FPO Board Members Management
    List<FPOBoardMemberDTO> getFPOBoardMembers(Long fpoId);
    FPOBoardMemberDTO addBoardMember(Long fpoId, FPOBoardMemberCreationDTO boardMemberDTO);
    FPOBoardMemberDTO updateBoardMember(Long fpoId, Long boardMemberId, FPOBoardMemberCreationDTO boardMemberDTO);
    void removeBoardMember(Long fpoId, Long boardMemberId);
    
    // FPO Services Management
    List<FPOServiceDTO> getFPOServices(Long fpoId);
    FPOServiceDTO createService(Long fpoId, FPOServiceCreationDTO serviceDTO);
    FPOServiceDTO updateService(Long fpoId, Long serviceId, FPOServiceCreationDTO serviceDTO);
    void updateServiceStatus(Long fpoId, Long serviceId, com.farmer.Form.Entity.FPOService.ServiceStatus status);
    void removeService(Long fpoId, Long serviceId);
    
    // FPO Crops Management
    List<FPOCropDTO> getFPOCrops(Long fpoId);
    FPOCropDTO createCrop(Long fpoId, FPOCropCreationDTO cropDTO);
    FPOCropDTO updateCrop(Long fpoId, Long cropId, FPOCropCreationDTO cropDTO);
    void updateCropStatus(Long fpoId, Long cropId, FPOCrop.CropStatus status);
    void deleteCrop(Long fpoId, Long cropId);
    
    // FPO Turnover Management
    List<FPOTurnoverDTO> getFPOTurnovers(Long fpoId);
    FPOTurnoverDTO createTurnover(Long fpoId, FPOTurnoverCreationDTO turnoverDTO);
    FPOTurnoverDTO updateTurnover(Long fpoId, Long turnoverId, FPOTurnoverCreationDTO turnoverDTO);
    void deleteTurnover(Long fpoId, Long turnoverId);

    // FPO Input Shop
    java.util.List<com.farmer.Form.DTO.FPOInputShopDTO> getFPOInputShops(Long fpoId);
    com.farmer.Form.DTO.FPOInputShopDTO createInputShop(Long fpoId, com.farmer.Form.DTO.FPOInputShopCreationDTO dto);
    com.farmer.Form.DTO.FPOInputShopDTO updateInputShop(Long fpoId, Long shopId, com.farmer.Form.DTO.FPOInputShopCreationDTO dto);
    void deleteInputShop(Long fpoId, Long shopId);
    
    // FPO Products Management
    List<FPOProductDTO> getFPOProducts(Long fpoId);
    FPOProductDTO createProduct(Long fpoId, FPOProductCreationDTO productDTO);
    FPOProductDTO updateProduct(Long fpoId, Long productId, FPOProductCreationDTO productDTO);
    void updateProductStock(Long fpoId, Long productId, Integer newStock);
    
    // FPO Product Categories Management
    List<FPOProductCategoryDTO> getFPOProductCategories(Long fpoId);
    FPOProductCategoryDTO createProductCategory(Long fpoId, FPOProductCategoryCreationDTO categoryDTO);
    FPOProductCategoryDTO updateProductCategory(Long fpoId, Long categoryId, FPOProductCategoryCreationDTO categoryDTO);
    void deleteProductCategory(Long fpoId, Long categoryId);
    
    // FPO Notifications Management
    List<FPONotificationDTO> getFPONotifications(Long fpoId);
    FPONotificationDTO createNotification(Long fpoId, FPONotificationCreationDTO notificationDTO);
    void markNotificationAsRead(Long fpoId, Long notificationId);
    void deleteNotification(Long fpoId, Long notificationId);
}
