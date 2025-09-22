package com.farmer.Form.Controller;
 
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
 
import com.farmer.Form.DTO.EmployeeDTO;
import com.farmer.Form.DTO.PincodeApiResponse.PostOffice;
import com.farmer.Form.Entity.Employee;
import com.farmer.Form.Entity.Farmer;
import com.farmer.Form.Mapper.EmployeeMapper;
import com.farmer.Form.Service.AddressService;
import com.farmer.Form.Service.EmployeeService;
import com.farmer.Form.Service.FileStorageService;
import com.farmer.Form.Service.FarmerService;
 
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.ArrayList;
 
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
 
    private final EmployeeService employeeService;
    private final AddressService addressService;
    private final FileStorageService fileStorageService;
    private final FarmerService farmerService;
    private final com.farmer.Form.Service.IdCardService idCardService;
 
    // ✅ Create employee with file upload
    @PostMapping
    public ResponseEntity<?> createEmployee(@ModelAttribute @Valid EmployeeDTO dto) {
        try {
            String photoFile = fileStorageService.storeFile(dto.getPhoto(), "photos");
            String passbookFile = fileStorageService.storeFile(dto.getPassbook(), "passbooks");
            String docFile = fileStorageService.storeFile(dto.getDocumentFile(), "documents");
 
            Employee employee = EmployeeMapper.toEntity(dto, photoFile, passbookFile, docFile);
            Employee saved = employeeService.saveEmployee(employee);
            return ResponseEntity.ok(saved);
 
        } catch (IOException | MultipartException e) {
            return ResponseEntity.badRequest().body("❌ File upload failed: " + e.getMessage());
        }
    }

    // ✅ Update employee photo only
    @PatchMapping(value = "/{id}/photo", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Employee> updateEmployeePhoto(@PathVariable Long id, @RequestPart("photo") org.springframework.web.multipart.MultipartFile photo) {
        try {
            Employee updated = employeeService.updateEmployeePhoto(id, photo);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
 
    // ✅ Get all employees
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // ✅ Get all employees with formatted data for frontend
    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> getEmployeesList() {
        List<Employee> employees = employeeService.getAllEmployees();
        
        List<Map<String, Object>> employeeList = employees.stream().map(employee -> {
            Map<String, Object> employeeData = new HashMap<>();
            employeeData.put("id", employee.getId());
            employeeData.put("employeeId", "EMP" + String.format("%06d", employee.getId())); // Format: EMP000001
            employeeData.put("name", employee.getFirstName() + " " + employee.getLastName());
            employeeData.put("designation", employee.getRole() != null ? employee.getRole() : "employee");
            employeeData.put("district", employee.getDistrict());
            employeeData.put("contactNumber", employee.getContactNumber());
            employeeData.put("email", employee.getEmail());
            employeeData.put("status", employee.getAccessStatus() != null ? employee.getAccessStatus() : "ACTIVE");
            return employeeData;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(employeeList);
    }
 
    // ✅ Get one employee by ID
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        return employee != null ? ResponseEntity.ok(employee) : ResponseEntity.notFound().build();
    }
 
    // ✅ Update employee with file support
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @ModelAttribute EmployeeDTO dto) {
        try {
            Employee existing = employeeService.getEmployeeById(id);
            if (existing == null) {
                return ResponseEntity.notFound().build();
            }
 
            String photoFile = dto.getPhoto() != null ? fileStorageService.storeFile(dto.getPhoto(), "photos") : existing.getPhotoFileName();
            String passbookFile = dto.getPassbook() != null ? fileStorageService.storeFile(dto.getPassbook(), "passbooks") : existing.getPassbookFileName();
            String docFile = dto.getDocumentFile() != null ? fileStorageService.storeFile(dto.getDocumentFile(), "documents") : existing.getDocumentFileName();
 
            Employee updated = EmployeeMapper.toEntity(dto, photoFile, passbookFile, docFile);
            updated.setId(id);
            Employee saved = employeeService.saveEmployee(updated);
 
            return ResponseEntity.ok(saved);
 
        } catch (IOException | MultipartException e) {
            return ResponseEntity.badRequest().body("❌ File update failed: " + e.getMessage());
        }
    }
 
    // ✅ Delete employee
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("✅ Employee deleted successfully");
    }
 
    // ✅ Get address by pincode (tested endpoint)
    @GetMapping("/address-by-pincode/{pincode}")
    public ResponseEntity<PostOffice> getAddressByPincode(@PathVariable String pincode) {
        System.out.println("📍 Pincode requested: " + pincode); // Debug log
        PostOffice postOffice = addressService.fetchAddressByPincode(pincode);
        return postOffice != null ? ResponseEntity.ok(postOffice) : ResponseEntity.notFound().build();
    }

    // --- EMPLOYEE DASHBOARD ENDPOINTS ---

    // Get assigned farmers with KYC status for logged-in employee
    @GetMapping("/dashboard/assigned-farmers")
    public ResponseEntity<List<Map<String, Object>>> getAssignedFarmersWithKyc(Authentication authentication) {
        String employeeEmail = authentication.getName();
        List<Farmer> assignedFarmers = farmerService.getFarmersByEmployeeEmail(employeeEmail);
        
        List<Map<String, Object>> farmersWithKyc = assignedFarmers.stream().map(farmer -> {
            Map<String, Object> farmerData = new HashMap<>();
            farmerData.put("id", farmer.getId());
            farmerData.put("name", farmer.getFirstName() + " " + farmer.getLastName());
            farmerData.put("contactNumber", farmer.getContactNumber());
            farmerData.put("state", farmer.getState());
            farmerData.put("district", farmer.getDistrict());
            farmerData.put("village", farmer.getVillage());
            farmerData.put("kycStatus", farmer.getKycStatus() != null ? farmer.getKycStatus().name() : "PENDING");
            farmerData.put("kycSubmittedDate", farmer.getKycSubmittedDate());
            farmerData.put("kycReviewedDate", farmer.getKycReviewedDate());
            farmerData.put("kycRejectionReason", farmer.getKycRejectionReason());
            farmerData.put("kycReferBackReason", farmer.getKycReferBackReason());
            return farmerData;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(farmersWithKyc);
    }

    // Get dashboard statistics for logged-in employee
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats(Authentication authentication) {
        String employeeEmail = authentication.getName();
        List<Farmer> assignedFarmers = farmerService.getFarmersByEmployeeEmail(employeeEmail);
        
        long totalAssigned = assignedFarmers.size();
        long approved = assignedFarmers.stream()
            .filter(f -> f.getKycStatus() != null && f.getKycStatus() == Farmer.KycStatus.APPROVED)
            .count();
        long referBack = assignedFarmers.stream()
            .filter(f -> f.getKycStatus() != null && f.getKycStatus() == Farmer.KycStatus.REFER_BACK)
            .count();
        long pending = assignedFarmers.stream()
            .filter(f -> f.getKycStatus() == null || f.getKycStatus() == Farmer.KycStatus.PENDING)
            .count();
        long rejected = assignedFarmers.stream()
            .filter(f -> f.getKycStatus() != null && f.getKycStatus() == Farmer.KycStatus.REJECTED)
            .count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAssigned", totalAssigned);
        stats.put("approved", approved);
        stats.put("referBack", referBack);
        stats.put("pending", pending);
        stats.put("rejected", rejected);
        stats.put("completionRate", totalAssigned > 0 ? (double) approved / totalAssigned * 100 : 0);
        
        return ResponseEntity.ok(stats);
    }

    // Get to-do list for logged-in employee
    @GetMapping("/dashboard/todo-list")
    public ResponseEntity<Map<String, Object>> getTodoList(Authentication authentication) {
        String employeeEmail = authentication.getName();
        List<Farmer> assignedFarmers = farmerService.getFarmersByEmployeeEmail(employeeEmail);
        
        // Pending KYC verifications
        List<Map<String, Object>> pendingKyc = assignedFarmers.stream()
            .filter(f -> f.getKycStatus() == null || f.getKycStatus() == Farmer.KycStatus.PENDING)
            .map(farmer -> {
                Map<String, Object> farmerData = new HashMap<>();
                farmerData.put("id", farmer.getId());
                farmerData.put("name", farmer.getFirstName() + " " + farmer.getLastName());
                farmerData.put("contactNumber", farmer.getContactNumber());
                farmerData.put("state", farmer.getState());
                farmerData.put("district", farmer.getDistrict());
                farmerData.put("kycSubmittedDate", farmer.getKycSubmittedDate());
                return farmerData;
            }).collect(Collectors.toList());
        
        // Refer-back cases to recheck
        List<Map<String, Object>> referBackCases = assignedFarmers.stream()
            .filter(f -> f.getKycStatus() != null && f.getKycStatus() == Farmer.KycStatus.REFER_BACK)
            .map(farmer -> {
                Map<String, Object> farmerData = new HashMap<>();
                farmerData.put("id", farmer.getId());
                farmerData.put("name", farmer.getFirstName() + " " + farmer.getLastName());
                farmerData.put("contactNumber", farmer.getContactNumber());
                farmerData.put("state", farmer.getState());
                farmerData.put("district", farmer.getDistrict());
                farmerData.put("kycReferBackReason", farmer.getKycReferBackReason());
                farmerData.put("kycReviewedDate", farmer.getKycReviewedDate());
                return farmerData;
            }).collect(Collectors.toList());
        
        // New assigned farmers not yet opened
        List<Map<String, Object>> newAssignments = assignedFarmers.stream()
            .filter(f -> f.getKycStatus() == null)
            .map(farmer -> {
                Map<String, Object> farmerData = new HashMap<>();
                farmerData.put("id", farmer.getId());
                farmerData.put("name", farmer.getFirstName() + " " + farmer.getLastName());
                farmerData.put("contactNumber", farmer.getContactNumber());
                farmerData.put("state", farmer.getState());
                farmerData.put("district", farmer.getDistrict());
                return farmerData;
            }).collect(Collectors.toList());
        
        Map<String, Object> todoList = new HashMap<>();
        todoList.put("pendingKyc", pendingKyc);
        todoList.put("referBackCases", referBackCases);
        todoList.put("newAssignments", newAssignments);
        todoList.put("totalPendingKyc", pendingKyc.size());
        todoList.put("totalReferBack", referBackCases.size());
        todoList.put("totalNewAssignments", newAssignments.size());
        
        return ResponseEntity.ok(todoList);
    }

    // KYC Actions - Approve KYC
    @PutMapping("/kyc/approve/{farmerId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<String> approveKyc(@PathVariable Long farmerId, Authentication authentication) {
        try {
            String employeeEmail = authentication.getName();
            farmerService.approveKycByEmployee(farmerId, employeeEmail);
            return ResponseEntity.ok("KYC approved successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error approving KYC: " + e.getMessage());
        }
    }

    // KYC Actions - Refer Back with reason
    @PutMapping("/kyc/refer-back/{farmerId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<String> referBackKyc(@PathVariable Long farmerId, 
                                             @RequestBody Map<String, String> request,
                                             Authentication authentication) {
        try {
            String employeeEmail = authentication.getName();
            String reason = request.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Reason is required for refer back");
            }
            farmerService.referBackKyc(farmerId, employeeEmail, reason);
            return ResponseEntity.ok("KYC referred back successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error referring back KYC: " + e.getMessage());
        }
    }

    // KYC Actions - Reject with reason
    @PutMapping("/kyc/reject/{farmerId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<String> rejectKyc(@PathVariable Long farmerId, 
                                          @RequestBody Map<String, String> request,
                                          Authentication authentication) {
        try {
            String employeeEmail = authentication.getName();
            String reason = request.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Reason is required for rejection");
            }
            farmerService.rejectKyc(farmerId, employeeEmail, reason);
            return ResponseEntity.ok("KYC rejected successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error rejecting KYC: " + e.getMessage());
        }
    }

    // Filter assigned farmers by KYC status
    @GetMapping("/dashboard/farmers/filter")
    public ResponseEntity<List<Map<String, Object>>> filterAssignedFarmers(
            @RequestParam(required = false) String kycStatus,
            Authentication authentication) {
        
        String employeeEmail = authentication.getName();
        List<Farmer> assignedFarmers = farmerService.getFarmersByEmployeeEmail(employeeEmail);
        
        List<Farmer> filteredFarmers = assignedFarmers.stream()
            .filter(farmer -> {
                if (kycStatus == null || kycStatus.isEmpty()) return true;
                if (farmer.getKycStatus() == null) return kycStatus.equals("PENDING");
                return farmer.getKycStatus().name().equals(kycStatus);
            }).collect(Collectors.toList());
        
        List<Map<String, Object>> farmersData = filteredFarmers.stream().map(farmer -> {
            Map<String, Object> farmerData = new HashMap<>();
            farmerData.put("id", farmer.getId());
            farmerData.put("name", farmer.getFirstName() + " " + farmer.getLastName());
            farmerData.put("contactNumber", farmer.getContactNumber());
            farmerData.put("state", farmer.getState());
            farmerData.put("district", farmer.getDistrict());
            farmerData.put("village", farmer.getVillage());
            farmerData.put("kycStatus", farmer.getKycStatus() != null ? farmer.getKycStatus().name() : "PENDING");
            farmerData.put("kycSubmittedDate", farmer.getKycSubmittedDate());
            farmerData.put("kycReviewedDate", farmer.getKycReviewedDate());
            return farmerData;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(farmersData);
    }

    // Get employee profile
    @GetMapping("/dashboard/profile")
    public ResponseEntity<Employee> getEmployeeProfile(Authentication authentication) {
        String employeeEmail = authentication.getName();
        Employee employee = employeeService.getEmployeeByEmail(employeeEmail);
        return employee != null ? ResponseEntity.ok(employee) : ResponseEntity.notFound().build();
    }

    // --- NEW ENHANCED FEATURES ---

    // Get notifications for employee
    @GetMapping("/dashboard/notifications")
    public ResponseEntity<List<Map<String, Object>>> getNotifications(Authentication authentication) {
        String employeeEmail = authentication.getName();
        List<Farmer> assignedFarmers = farmerService.getFarmersByEmployeeEmail(employeeEmail);
        
        List<Map<String, Object>> notifications = new ArrayList<>();
        
        // New assignments (assigned within last 3 days)
        List<Farmer> newAssignments = assignedFarmers.stream()
            .filter(farmer -> farmer.getKycSubmittedDate() != null && 
                    farmer.getKycSubmittedDate().isAfter(LocalDate.now().minusDays(3)))
            .collect(Collectors.toList());
        
        for (Farmer farmer : newAssignments) {
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "NEW_ASSIGNMENT");
            notification.put("message", "New farmer assigned: " + farmer.getFirstName() + " " + farmer.getLastName());
            notification.put("farmerId", farmer.getId());
            notification.put("farmerName", farmer.getFirstName() + " " + farmer.getLastName());
            notification.put("date", farmer.getKycSubmittedDate());
            notification.put("read", false);
            notifications.add(notification);
        }
        
        // Pending KYC reminders
        List<Farmer> pendingFarmers = assignedFarmers.stream()
            .filter(farmer -> farmer.getKycStatus() == null || farmer.getKycStatus() == Farmer.KycStatus.PENDING)
            .collect(Collectors.toList());
        
        for (Farmer farmer : pendingFarmers) {
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "PENDING_KYC");
            notification.put("message", "KYC pending for: " + farmer.getFirstName() + " " + farmer.getLastName());
            notification.put("farmerId", farmer.getId());
            notification.put("farmerName", farmer.getFirstName() + " " + farmer.getLastName());
            notification.put("date", LocalDate.now());
            notification.put("read", false);
            notifications.add(notification);
        }
        
        // Refer-back cases
        List<Farmer> referBackCases = assignedFarmers.stream()
            .filter(farmer -> farmer.getKycStatus() == Farmer.KycStatus.REFER_BACK)
            .collect(Collectors.toList());
        
        for (Farmer farmer : referBackCases) {
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "REFER_BACK");
            notification.put("message", "Document re-submission required for: " + farmer.getFirstName() + " " + farmer.getLastName());
            notification.put("farmerId", farmer.getId());
            notification.put("farmerName", farmer.getFirstName() + " " + farmer.getLastName());
            notification.put("reason", farmer.getKycReferBackReason());
            notification.put("date", farmer.getKycReviewedDate());
            notification.put("read", false);
            notifications.add(notification);
        }
        
        return ResponseEntity.ok(notifications);
    }

    // Mark notification as read
    @PutMapping("/dashboard/notifications/{notificationId}/read")
    public ResponseEntity<String> markNotificationAsRead(@PathVariable String notificationId) {
        // In a real implementation, this would update a notification table
        return ResponseEntity.ok("Notification marked as read");
    }

    // Get enhanced todo list with priority levels
    @GetMapping("/dashboard/enhanced-todo-list")
    public ResponseEntity<Map<String, Object>> getEnhancedTodoList(Authentication authentication) {
        String employeeEmail = authentication.getName();
        List<Farmer> assignedFarmers = farmerService.getFarmersByEmployeeEmail(employeeEmail);
        
        // High priority: New assignments not yet reviewed
        List<Map<String, Object>> newAssignments = assignedFarmers.stream()
            .filter(farmer -> farmer.getKycStatus() == null)
            .map(farmer -> {
                Map<String, Object> farmerData = new HashMap<>();
                farmerData.put("id", farmer.getId());
                farmerData.put("name", farmer.getFirstName() + " " + farmer.getLastName());
                farmerData.put("contactNumber", farmer.getContactNumber());
                farmerData.put("state", farmer.getState());
                farmerData.put("district", farmer.getDistrict());
                farmerData.put("village", farmer.getVillage());
                farmerData.put("priority", "HIGH");
                farmerData.put("assignedDate", farmer.getKycSubmittedDate());
                return farmerData;
            }).collect(Collectors.toList());
        
        // Medium priority: Pending KYC reviews
        List<Map<String, Object>> pendingReviews = assignedFarmers.stream()
            .filter(farmer -> farmer.getKycStatus() == Farmer.KycStatus.PENDING)
            .map(farmer -> {
                Map<String, Object> farmerData = new HashMap<>();
                farmerData.put("id", farmer.getId());
                farmerData.put("name", farmer.getFirstName() + " " + farmer.getLastName());
                farmerData.put("contactNumber", farmer.getContactNumber());
                farmerData.put("state", farmer.getState());
                farmerData.put("district", farmer.getDistrict());
                farmerData.put("village", farmer.getVillage());
                farmerData.put("priority", "MEDIUM");
                farmerData.put("kycSubmittedDate", farmer.getKycSubmittedDate());
                return farmerData;
            }).collect(Collectors.toList());
        
        // High priority: Refer-back cases requiring attention
        List<Map<String, Object>> referBackCases = assignedFarmers.stream()
            .filter(farmer -> farmer.getKycStatus() == Farmer.KycStatus.REFER_BACK)
            .map(farmer -> {
                Map<String, Object> farmerData = new HashMap<>();
                farmerData.put("id", farmer.getId());
                farmerData.put("name", farmer.getFirstName() + " " + farmer.getLastName());
                farmerData.put("contactNumber", farmer.getContactNumber());
                farmerData.put("state", farmer.getState());
                farmerData.put("district", farmer.getDistrict());
                farmerData.put("village", farmer.getVillage());
                farmerData.put("priority", "HIGH");
                farmerData.put("kycReferBackReason", farmer.getKycReferBackReason());
                farmerData.put("kycReviewedDate", farmer.getKycReviewedDate());
                return farmerData;
            }).collect(Collectors.toList());
        
        // Overdue cases (pending for more than 7 days)
        List<Map<String, Object>> overdueCases = assignedFarmers.stream()
            .filter(farmer -> (farmer.getKycStatus() == null || farmer.getKycStatus() == Farmer.KycStatus.PENDING) &&
                    farmer.getKycSubmittedDate() != null &&
                    farmer.getKycSubmittedDate().isBefore(LocalDate.now().minusDays(7)))
            .map(farmer -> {
                Map<String, Object> farmerData = new HashMap<>();
                farmerData.put("id", farmer.getId());
                farmerData.put("name", farmer.getFirstName() + " " + farmer.getLastName());
                farmerData.put("contactNumber", farmer.getContactNumber());
                farmerData.put("state", farmer.getState());
                farmerData.put("district", farmer.getDistrict());
                farmerData.put("village", farmer.getVillage());
                farmerData.put("priority", "URGENT");
                farmerData.put("kycSubmittedDate", farmer.getKycSubmittedDate());
                farmerData.put("daysOverdue", LocalDate.now().getDayOfYear() - farmer.getKycSubmittedDate().getDayOfYear());
                return farmerData;
            }).collect(Collectors.toList());
        
        Map<String, Object> enhancedTodoList = new HashMap<>();
        enhancedTodoList.put("newAssignments", newAssignments);
        enhancedTodoList.put("pendingReviews", pendingReviews);
        enhancedTodoList.put("referBackCases", referBackCases);
        enhancedTodoList.put("overdueCases", overdueCases);
        enhancedTodoList.put("totalNewAssignments", newAssignments.size());
        enhancedTodoList.put("totalPendingReviews", pendingReviews.size());
        enhancedTodoList.put("totalReferBackCases", referBackCases.size());
        enhancedTodoList.put("totalOverdueCases", overdueCases.size());
        enhancedTodoList.put("totalTasks", newAssignments.size() + pendingReviews.size() + referBackCases.size() + overdueCases.size());
        
        return ResponseEntity.ok(enhancedTodoList);
    }

    // Get KYC metrics with trends
    @GetMapping("/dashboard/kyc-metrics")
    public ResponseEntity<Map<String, Object>> getKycMetrics(Authentication authentication) {
        String employeeEmail = authentication.getName();
        List<Farmer> assignedFarmers = farmerService.getFarmersByEmployeeEmail(employeeEmail);
        
        long totalAssigned = assignedFarmers.size();
        long approved = assignedFarmers.stream()
            .filter(f -> f.getKycStatus() == Farmer.KycStatus.APPROVED)
            .count();
        long referBack = assignedFarmers.stream()
            .filter(f -> f.getKycStatus() == Farmer.KycStatus.REFER_BACK)
            .count();
        long pending = assignedFarmers.stream()
            .filter(f -> f.getKycStatus() == null || f.getKycStatus() == Farmer.KycStatus.PENDING)
            .count();
        long rejected = assignedFarmers.stream()
            .filter(f -> f.getKycStatus() == Farmer.KycStatus.REJECTED)
            .count();
        
        // Calculate completion rate
        double completionRate = totalAssigned > 0 ? (double) approved / totalAssigned * 100 : 0;
        
        // Calculate average processing time (simplified)
        double avgProcessingTime = assignedFarmers.stream()
            .filter(f -> f.getKycReviewedDate() != null && f.getKycSubmittedDate() != null)
            .mapToLong(f -> f.getKycReviewedDate().toEpochDay() - f.getKycSubmittedDate().toEpochDay())
            .average()
            .orElse(0);
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalAssigned", totalAssigned);
        metrics.put("approved", approved);
        metrics.put("referBack", referBack);
        metrics.put("pending", pending);
        metrics.put("rejected", rejected);
        metrics.put("completionRate", Math.round(completionRate * 100.0) / 100.0);
        metrics.put("avgProcessingTime", Math.round(avgProcessingTime * 100.0) / 100.0);
        metrics.put("efficiencyScore", calculateEfficiencyScore(approved, pending, referBack, totalAssigned));
        
        return ResponseEntity.ok(metrics);
    }

    private double calculateEfficiencyScore(long approved, long pending, long referBack, long total) {
        if (total == 0) return 0;
        
        // Simple efficiency calculation: (approved - referBack) / total * 100
        double score = ((double) (approved - referBack) / total) * 100;
        return Math.max(0, Math.min(100, score)); // Clamp between 0 and 100
    }

    // Get farmer details for KYC review
    @GetMapping("/dashboard/farmers/{farmerId}/kyc-details")
    public ResponseEntity<Map<String, Object>> getFarmerKycDetails(@PathVariable Long farmerId, Authentication authentication) {
        String employeeEmail = authentication.getName();
        List<Farmer> assignedFarmers = farmerService.getFarmersByEmployeeEmail(employeeEmail);
        
        Farmer farmer = assignedFarmers.stream()
            .filter(f -> f.getId().equals(farmerId))
            .findFirst()
            .orElse(null);
        
        if (farmer == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> farmerDetails = new HashMap<>();
        farmerDetails.put("id", farmer.getId());
        farmerDetails.put("name", farmer.getFirstName() + " " + farmer.getLastName());
        farmerDetails.put("contactNumber", farmer.getContactNumber());
        farmerDetails.put("state", farmer.getState());
        farmerDetails.put("district", farmer.getDistrict());
        farmerDetails.put("village", farmer.getVillage());
        farmerDetails.put("kycStatus", farmer.getKycStatus() != null ? farmer.getKycStatus().name() : "PENDING");
        farmerDetails.put("kycSubmittedDate", farmer.getKycSubmittedDate());
        farmerDetails.put("kycReviewedDate", farmer.getKycReviewedDate());
        farmerDetails.put("kycRejectionReason", farmer.getKycRejectionReason());
        farmerDetails.put("kycReferBackReason", farmer.getKycReferBackReason());
        farmerDetails.put("kycReviewedBy", farmer.getKycReviewedBy());
        
        // Add document information
        farmerDetails.put("photoFileName", farmer.getPhotoFileName());
        farmerDetails.put("passbookFileName", farmer.getPassbookFileName());
        farmerDetails.put("documentFileName", farmer.getDocumentFileName());
        farmerDetails.put("soilTestCertificateFileName", farmer.getSoilTestCertificateFileName());
        
        return ResponseEntity.ok(farmerDetails);
    }

    // ✅ Allow employee to view ID card of their assigned farmer
    @GetMapping("/dashboard/farmers/{farmerId}/id-card")
    public ResponseEntity<Map<String, Object>> getAssignedFarmerIdCard(
            @PathVariable Long farmerId,
            Authentication authentication
    ) {
        String employeeEmail = authentication.getName();
        List<Farmer> assignedFarmers = farmerService.getFarmersByEmployeeEmail(employeeEmail);
        boolean isAssigned = assignedFarmers.stream().anyMatch(f -> f.getId().equals(farmerId));
        if (!isAssigned) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "You do not have access to this farmer's ID card");
            return ResponseEntity.status(403).body(resp);
        }

        // Primary lookup by holderId
        List<com.farmer.Form.Entity.IdCard> cards = idCardService.getByHolderId(String.valueOf(farmerId));
        com.farmer.Form.Entity.IdCard card = cards.stream()
                .filter(c -> c.getCardType() == com.farmer.Form.Entity.IdCard.CardType.FARMER)
                .findFirst()
                .orElse(null);

        // Fallback: search by holder name if legacy cards were generated without numeric holderId
        if (card == null) {
            Farmer farmer = assignedFarmers.stream()
                    .filter(f -> f.getId().equals(farmerId))
                    .findFirst().orElse(null);
            if (farmer != null) {
                String nameQuery = ((farmer.getFirstName() != null ? farmer.getFirstName() : "") + " " +
                        (farmer.getLastName() != null ? farmer.getLastName() : "")).trim();
                try {
                    // Fallback A: search by name for FARMER type
                    org.springframework.data.domain.Page<com.farmer.Form.Entity.IdCard> pageA =
                            idCardService.searchIdCardsByName(nameQuery,
                                    com.farmer.Form.Entity.IdCard.CardType.FARMER,
                                    org.springframework.data.domain.PageRequest.of(0, 20));
                    card = pageA.getContent().stream()
                            .filter(c -> c.getCardType() == com.farmer.Form.Entity.IdCard.CardType.FARMER)
                            .findFirst().orElse(null);

                    // Fallback B: sometimes type was saved wrongly; try EMPLOYEE too
                    if (card == null) {
                        org.springframework.data.domain.Page<com.farmer.Form.Entity.IdCard> pageB =
                                idCardService.searchIdCardsByName(nameQuery,
                                        com.farmer.Form.Entity.IdCard.CardType.EMPLOYEE,
                                        org.springframework.data.domain.PageRequest.of(0, 20));
                        card = pageB.getContent().stream().findFirst().orElse(null);
                    }

                    // Fallback C: scan a bigger page and match holder name contains
                    if (card == null) {
                        org.springframework.data.domain.Page<com.farmer.Form.Entity.IdCard> all =
                                idCardService.getAllIdCards(org.springframework.data.domain.PageRequest.of(0, 1000));
                        String nq = nameQuery.toLowerCase();
                        card = all.getContent().stream()
                                .filter(c -> c.getHolderName() != null && c.getHolderName().toLowerCase().contains(nq))
                                .findFirst().orElse(null);
                    }
                } catch (Exception ignored) { }
            }
        }

        if (card == null) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "No ID card record found for this farmer");
            return ResponseEntity.status(404).body(resp);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("cardId", card.getCardId());
        response.put("status", card.getStatus());
        response.put("holderId", card.getHolderId());
        response.put("holderName", card.getHolderName());
        response.put("pngUrl", "/api/id-cards/" + card.getCardId() + "/download/png");
        response.put("pdfUrl", "/api/id-cards/" + card.getCardId() + "/download/pdf");

        return ResponseEntity.ok(response);
    }

    // ✅ Allow employee to generate ID card for an assigned farmer (if missing)
    @PostMapping("/dashboard/farmers/{farmerId}/id-card")
    public ResponseEntity<Map<String, Object>> generateAssignedFarmerIdCard(
            @PathVariable Long farmerId,
            Authentication authentication
    ) {
        String employeeEmail = authentication.getName();
        List<Farmer> assignedFarmers = farmerService.getFarmersByEmployeeEmail(employeeEmail);
        Farmer farmer = assignedFarmers.stream().filter(f -> f.getId().equals(farmerId)).findFirst().orElse(null);
        if (farmer == null) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "You do not have access to generate this farmer's ID card");
            return ResponseEntity.status(403).body(resp);
        }

        try {
            // If already exists, return existing
            List<com.farmer.Form.Entity.IdCard> cards = idCardService.getByHolderId(String.valueOf(farmerId));
            com.farmer.Form.Entity.IdCard existing = cards.stream()
                    .filter(c -> c.getCardType() == com.farmer.Form.Entity.IdCard.CardType.FARMER)
                    .findFirst().orElse(null);
            com.farmer.Form.Entity.IdCard card = existing != null ? existing : idCardService.generateFarmerIdCard(farmer);

            Map<String, Object> response = new HashMap<>();
            response.put("cardId", card.getCardId());
            response.put("status", card.getStatus());
            response.put("holderId", card.getHolderId());
            response.put("holderName", card.getHolderName());
            response.put("pngUrl", "/api/id-cards/" + card.getCardId() + "/download/png");
            response.put("pdfUrl", "/api/id-cards/" + card.getCardId() + "/download/pdf");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Failed to generate ID card: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    // === EMPLOYEE SELF ID CARD (like farmer) ===
    // View my ID card
    @GetMapping("/dashboard/my/id-card")
    public ResponseEntity<Map<String, Object>> getMyEmployeeIdCard(Authentication authentication) {
        String employeeEmail = authentication.getName();
        Employee employee = employeeService.getEmployeeByEmail(employeeEmail);
        if (employee == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Employee not found"));
        }

        // Primary lookup by holderId
        List<com.farmer.Form.Entity.IdCard> cards = idCardService.getByHolderId(String.valueOf(employee.getId()));
        com.farmer.Form.Entity.IdCard card = cards.stream()
                .filter(c -> c.getCardType() == com.farmer.Form.Entity.IdCard.CardType.EMPLOYEE)
                .findFirst()
                .orElse(null);

        // Fallback by name similar to farmer flow
        if (card == null) {
            try {
                String nameQuery = ((employee.getFirstName() != null ? employee.getFirstName() : "") + " " +
                        (employee.getLastName() != null ? employee.getLastName() : "")).trim();
                org.springframework.data.domain.Page<com.farmer.Form.Entity.IdCard> pageA =
                        idCardService.searchIdCardsByName(nameQuery,
                                com.farmer.Form.Entity.IdCard.CardType.EMPLOYEE,
                                org.springframework.data.domain.PageRequest.of(0, 20));
                card = pageA.getContent().stream().findFirst().orElse(null);

                if (card == null) {
                    org.springframework.data.domain.Page<com.farmer.Form.Entity.IdCard> all =
                            idCardService.getAllIdCards(org.springframework.data.domain.PageRequest.of(0, 1000));
                    String nq = nameQuery.toLowerCase();
                    card = all.getContent().stream()
                            .filter(c -> c.getHolderName() != null && c.getHolderName().toLowerCase().contains(nq))
                            .findFirst().orElse(null);
                }
            } catch (Exception ignored) { }
        }

        if (card == null) {
            return ResponseEntity.status(404).body(Map.of("message", "No ID card record found for this employee"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("cardId", card.getCardId());
        response.put("status", card.getStatus());
        response.put("holderId", card.getHolderId());
        response.put("holderName", card.getHolderName());
        response.put("pngUrl", "/api/id-cards/" + card.getCardId() + "/download/png");
        response.put("pdfUrl", "/api/id-cards/" + card.getCardId() + "/download/pdf");

        return ResponseEntity.ok(response);
    }

    // Generate my ID card (if missing)
    @PostMapping("/dashboard/my/id-card")
    public ResponseEntity<Map<String, Object>> generateMyEmployeeIdCard(Authentication authentication) {
        String employeeEmail = authentication.getName();
        Employee employee = employeeService.getEmployeeByEmail(employeeEmail);
        if (employee == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Employee not found"));
        }

        try {
            List<com.farmer.Form.Entity.IdCard> cards = idCardService.getByHolderId(String.valueOf(employee.getId()));
            com.farmer.Form.Entity.IdCard existing = cards.stream()
                    .filter(c -> c.getCardType() == com.farmer.Form.Entity.IdCard.CardType.EMPLOYEE)
                    .findFirst().orElse(null);
            com.farmer.Form.Entity.IdCard card = existing != null ? existing : idCardService.generateEmployeeIdCard(employee);

            Map<String, Object> response = new HashMap<>();
            response.put("cardId", card.getCardId());
            response.put("status", card.getStatus());
            response.put("holderId", card.getHolderId());
            response.put("holderName", card.getHolderName());
            response.put("pngUrl", "/api/id-cards/" + card.getCardId() + "/download/png");
            response.put("pdfUrl", "/api/id-cards/" + card.getCardId() + "/download/pdf");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to generate ID card: " + e.getMessage()));
        }
    }

    // ✅ View Employee Details
    @GetMapping("/{id}/details")
    public ResponseEntity<Map<String, Object>> getEmployeeDetails(@PathVariable Long id) {
        try {
            Employee employee = employeeService.getEmployeeById(id);
            if (employee == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> details = new HashMap<>();
            details.put("id", employee.getId());
            details.put("employeeId", "EMP" + String.format("%06d", employee.getId()));
            details.put("name", employee.getFirstName() + " " + employee.getLastName());
            details.put("designation", employee.getRole());
            details.put("email", employee.getEmail());
            details.put("contactNumber", employee.getContactNumber());
            details.put("district", employee.getDistrict());
            details.put("state", employee.getState());
            details.put("status", employee.getAccessStatus());
            details.put("education", employee.getEducation());
            details.put("experience", employee.getExperience());
            details.put("bankName", employee.getBankName());
            details.put("accountNumber", employee.getAccountNumber());
            details.put("ifscCode", employee.getIfscCode());
            details.put("photoFileName", employee.getPhotoFileName());
            details.put("passbookFileName", employee.getPassbookFileName());
            details.put("documentFileName", employee.getDocumentFileName());

            return ResponseEntity.ok(details);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error fetching employee details: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // ✅ Give Login Access to Employee
    @PostMapping("/{id}/give-login-access")
    public ResponseEntity<Map<String, Object>> giveLoginAccess(@PathVariable Long id) {
        try {
            Employee employee = employeeService.getEmployeeById(id);
            if (employee == null) {
                return ResponseEntity.notFound().build();
            }

            // Generate temporary password
            String tempPassword = employeeService.generateTempPassword();
            
            // Create or update user account for employee
            boolean success = employeeService.createOrUpdateUserAccount(employee, tempPassword);
            
            if (success) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login access granted successfully");
                response.put("employeeId", employee.getId());
                response.put("employeeName", employee.getFirstName() + " " + employee.getLastName());
                response.put("email", employee.getEmail());
                response.put("tempPassword", tempPassword);
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Failed to create login access");
                return ResponseEntity.status(500).body(error);
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error giving login access: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // ✅ Assign Farmers to Employee
    @PostMapping("/{id}/assign-farmers")
    public ResponseEntity<Map<String, Object>> assignFarmersToEmployee(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> request) {
        try {
            Employee employee = employeeService.getEmployeeById(id);
            if (employee == null) {
                return ResponseEntity.notFound().build();
            }

            @SuppressWarnings("unchecked")
            List<Long> farmerIds = (List<Long>) request.get("farmerIds");
            
            if (farmerIds == null || farmerIds.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Farmer IDs are required");
                return ResponseEntity.badRequest().body(error);
            }

            // Assign farmers to employee
            int assignedCount = employeeService.assignFarmersToEmployee(id, farmerIds);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Farmers assigned successfully");
            response.put("employeeId", id);
            response.put("employeeName", employee.getFirstName() + " " + employee.getLastName());
            response.put("assignedCount", assignedCount);
            response.put("totalRequested", farmerIds.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error assigning farmers: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // ✅ Get Available Farmers for Assignment
    @GetMapping("/{id}/available-farmers")
    public ResponseEntity<List<Map<String, Object>>> getAvailableFarmersForAssignment(@PathVariable Long id) {
        try {
            Employee employee = employeeService.getEmployeeById(id);
            if (employee == null) {
                return ResponseEntity.notFound().build();
            }

            List<Farmer> availableFarmers = employeeService.getAvailableFarmersForAssignment(id);
            
            List<Map<String, Object>> farmersList = availableFarmers.stream().map(farmer -> {
                Map<String, Object> farmerData = new HashMap<>();
                farmerData.put("id", farmer.getId());
                farmerData.put("name", farmer.getFirstName() + " " + farmer.getLastName());
                farmerData.put("contactNumber", farmer.getContactNumber());
                farmerData.put("district", farmer.getDistrict());
                farmerData.put("state", farmer.getState());
                farmerData.put("kycStatus", farmer.getKycStatus() != null ? farmer.getKycStatus().name() : "PENDING");
                return farmerData;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(farmersList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(List.of());
        }
    }

    // ✅ Get Assigned Farmers for Employee
    @GetMapping("/{id}/assigned-farmers")
    public ResponseEntity<List<Map<String, Object>>> getAssignedFarmersForEmployee(@PathVariable Long id) {
        try {
            Employee employee = employeeService.getEmployeeById(id);
            if (employee == null) {
                return ResponseEntity.notFound().build();
            }

            List<Farmer> assignedFarmers = employeeService.getAssignedFarmersForEmployee(id);
            
            List<Map<String, Object>> farmersList = assignedFarmers.stream().map(farmer -> {
                Map<String, Object> farmerData = new HashMap<>();
                farmerData.put("id", farmer.getId());
                farmerData.put("name", farmer.getFirstName() + " " + farmer.getLastName());
                farmerData.put("contactNumber", farmer.getContactNumber());
                farmerData.put("district", farmer.getDistrict());
                farmerData.put("state", farmer.getState());
                farmerData.put("kycStatus", farmer.getKycStatus() != null ? farmer.getKycStatus().name() : "PENDING");
                // Note: assignedDate not available in Farmer entity
                return farmerData;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(farmersList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(List.of());
        }
    }
}
 
 