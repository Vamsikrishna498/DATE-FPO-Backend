package com.farmer.Form.Controller;

import com.farmer.Form.DTO.DashboardStatsDTO;
import com.farmer.Form.Entity.Employee;
import com.farmer.Form.Entity.Farmer;
import com.farmer.Form.Service.DashboardService;
import com.farmer.Form.Service.EmployeeService;
import com.farmer.Form.Service.FarmerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicAnalyticsController {

    private final DashboardService dashboardService;
    private final FarmerService farmerService;
    private final EmployeeService employeeService;

    // Public counts for analytics (no auth)
    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsDTO> getPublicDashboardStats() {
        return ResponseEntity.ok(dashboardService.getSuperAdminDashboardStats());
    }

    // Public farmers with KYC and assignment info for charts (no auth)
    @GetMapping("/farmers-with-kyc")
    public ResponseEntity<List<Map<String, Object>>> getPublicFarmersWithKyc() {
        List<Farmer> farmers = farmerService.getAllFarmersRaw();
        List<Map<String, Object>> farmersWithKyc = farmers.stream().map(farmer -> {
            Map<String, Object> farmerData = new HashMap<>();
            farmerData.put("id", farmer.getId());
            farmerData.put("firstName", farmer.getFirstName());
            farmerData.put("middleName", farmer.getMiddleName());
            farmerData.put("lastName", farmer.getLastName());
            farmerData.put("name", (farmer.getFirstName() != null ? farmer.getFirstName() : "") +
                    (farmer.getLastName() != null ? (" " + farmer.getLastName()) : ""));
            farmerData.put("dateOfBirth", farmer.getDateOfBirth() != null ? farmer.getDateOfBirth().toString() : null);
            farmerData.put("gender", farmer.getGender());
            farmerData.put("contactNumber", farmer.getContactNumber());
            farmerData.put("fatherName", farmer.getFatherName());
            farmerData.put("nationality", farmer.getNationality());
            farmerData.put("alternativeContactNumber", farmer.getAlternativeContactNumber());
            farmerData.put("alternativeRelationType", farmer.getAlternativeRelationType());
            farmerData.put("state", farmer.getState());
            farmerData.put("district", farmer.getDistrict());
            farmerData.put("country", farmer.getCountry());
            farmerData.put("block", farmer.getBlock());
            farmerData.put("village", farmer.getVillage());
            farmerData.put("pincode", farmer.getPincode());
            farmerData.put("kycStatus", farmer.getKycApproved() != null ?
                    (farmer.getKycApproved() ? "APPROVED" : "PENDING") : "NOT_STARTED");
            farmerData.put("assignedEmployee", farmer.getAssignedEmployee() != null ?
                    farmer.getAssignedEmployee().getFirstName() + " " + farmer.getAssignedEmployee().getLastName() : null);
            farmerData.put("assignedEmployeeId", farmer.getAssignedEmployee() != null ?
                    farmer.getAssignedEmployee().getId() : null);
            return farmerData;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(farmersWithKyc);
    }
}


