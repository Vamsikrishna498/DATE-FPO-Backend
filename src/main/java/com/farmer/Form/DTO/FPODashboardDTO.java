package com.farmer.Form.DTO;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPODashboardDTO {
    
    // FPO Basic Info
    private Long id;
    private String fpoId;
    private String fpoName;
    private String ceoName;
    private String phoneNumber;
    private String email;
    private String address;
    private LocalDate joinDate;
    private String status;
    
    // Statistics
    private Long totalMembers;
    private Long activeMembers;
    private Long totalServices;
    private Long completedServices;
    private Long pendingServices;
    private Long totalCrops;
    private Double totalArea;
    private Long totalProducts;
    private Long lowStockProductsCount;
    
    // Financial Summary
    private Double totalRevenue;
    private Double totalExpenses;
    private Double netProfit;
    private Double currentYearRevenue;
    private Double currentYearExpenses;
    private Double currentYearProfit;
    
    // Recent Activities
    private List<RecentServiceDTO> recentServices;
    private List<RecentCropDTO> recentCrops;
    private List<RecentNotificationDTO> recentNotifications;
    
    // Charts Data
    private Map<String, Long> servicesByType;
    private Map<String, Long> cropsBySeason;
    private Map<String, Double> monthlyRevenue;
    private Map<String, Double> quarterlyRevenue;
    
    // Board Members
    private List<BoardMemberSummaryDTO> boardMembers;
    
    // Low Stock Products
    private List<ProductSummaryDTO> lowStockProducts;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentServiceDTO {
        private Long id;
        private String serviceType;
        private String farmerName;
        private String status;
        private String requestedDate;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentCropDTO {
        private Long id;
        private String cropName;
        private String variety;
        private Double area;
        private String season;
        private String status;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentNotificationDTO {
        private Long id;
        private String title;
        private String type;
        private String createdAt;
        private String status;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardMemberSummaryDTO {
        private Long id;
        private String name;
        private String role;
        private String phoneNumber;
        private String status;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSummaryDTO {
        private Long id;
        private String productName;
        private String category;
        private Integer stockQuantity;
        private Integer minimumStock;
        private String status;
    }
}
