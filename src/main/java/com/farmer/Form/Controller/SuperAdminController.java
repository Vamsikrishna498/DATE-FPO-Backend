package com.farmer.Form.Controller;

import com.farmer.Form.Entity.User;
import com.farmer.Form.Entity.Farmer;
import com.farmer.Form.Entity.Employee;
import com.farmer.Form.DTO.DashboardStatsDTO;
import com.farmer.Form.DTO.EmployeeStatsDTO;
import com.farmer.Form.DTO.TodoItemsDTO;
import com.farmer.Form.Service.UserService;
import com.farmer.Form.Service.FarmerService;
import com.farmer.Form.Service.EmployeeService;
import com.farmer.Form.Service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import com.farmer.Form.Entity.Role;

@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
public class SuperAdminController {

    private final UserService userService;
    private final FarmerService farmerService;
    private final EmployeeService employeeService;
    private final DashboardService dashboardService;

    // --- USER CRUD ---
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsersRaw());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserRawById(id));
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUserBySuperAdmin(user));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUserBySuperAdmin(id, user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUserBySuperAdmin(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting user: " + e.getMessage());
        }
    }

    @DeleteMapping("/users/bulk")
    public ResponseEntity<String> deleteUsersBulk(@RequestBody List<Long> userIds) {
        try {
            for (Long id : userIds) {
                userService.deleteUserBySuperAdmin(id);
            }
            return ResponseEntity.ok(userIds.size() + " users deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting users: " + e.getMessage());
        }
    }

    // --- REGISTRATION LIST ENDPOINTS ---
    @GetMapping("/registration-list")
    public ResponseEntity<List<User>> getRegistrationList() {
        return ResponseEntity.ok(userService.getAllUsersRaw());
    }

    @GetMapping("/registration-list/filter")
    public ResponseEntity<List<User>> getRegistrationListByStatus(@RequestParam(required = false) String status) {
        if (status == null || status.isEmpty() || status.equalsIgnoreCase("ALL")) {
            return ResponseEntity.ok(userService.getAllUsersRaw());
        } else if (status.equalsIgnoreCase("PENDING")) {
            return ResponseEntity.ok(userService.getPendingUsersRaw());
        } else if (status.equalsIgnoreCase("APPROVED")) {
            return ResponseEntity.ok(userService.getApprovedUsersRaw());
        } else if (status.equalsIgnoreCase("REJECTED")) {
            return ResponseEntity.ok(userService.getRejectedUsersRaw());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/registration-list/search")
    public ResponseEntity<List<User>> searchRegistrations(@RequestParam String query) {
        // This would need to be implemented in UserService
        // For now, return all users and let frontend filter
        return ResponseEntity.ok(userService.getAllUsersRaw());
    }

    // --- FARMER CRUD ---
    @GetMapping("/farmers")
    public ResponseEntity<List<Map<String, Object>>> getAllFarmers() {
        List<Farmer> farmers = farmerService.getAllFarmersRaw();
        List<Map<String, Object>> farmersData = farmers.stream().map(farmer -> {
            Map<String, Object> farmerData = new HashMap<>();
            farmerData.put("id", farmer.getId());
            farmerData.put("name", farmer.getFirstName() + " " + farmer.getLastName());
            farmerData.put("firstName", farmer.getFirstName());
            farmerData.put("lastName", farmer.getLastName());
            farmerData.put("contactNumber", farmer.getContactNumber());
            farmerData.put("email", ""); // Farmer entity doesn't have email
            farmerData.put("state", farmer.getState());
            farmerData.put("district", farmer.getDistrict());
            farmerData.put("village", farmer.getVillage());
            farmerData.put("kycStatus", farmer.getKycStatus() != null ? farmer.getKycStatus().name() : "PENDING");
            farmerData.put("kycApproved", farmer.getKycApproved());
            farmerData.put("accessStatus", "ACTIVE"); // Default status
            
            // Handle assignedEmployee - convert to string if present
            if (farmer.getAssignedEmployee() != null) {
                Employee emp = farmer.getAssignedEmployee();
                farmerData.put("assignedEmployee", emp.getFirstName() + " " + emp.getLastName());
                farmerData.put("assignedEmployeeId", emp.getId());
                farmerData.put("assignedEmployeeEmail", emp.getEmail());
            } else {
                farmerData.put("assignedEmployee", "Not Assigned");
                farmerData.put("assignedEmployeeId", null);
                farmerData.put("assignedEmployeeEmail", null);
            }
            
            return farmerData;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(farmersData);
    }

    @GetMapping("/farmers/{id}")
    public ResponseEntity<Farmer> getFarmerById(@PathVariable Long id) {
        return ResponseEntity.ok(farmerService.getFarmerRawById(id));
    }

    @PostMapping(value = "/farmers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Farmer> createFarmer(
            @RequestPart("farmerDto") String farmerDtoJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestPart(value = "passbookPhoto", required = false) MultipartFile passbookPhoto,
            @RequestPart(value = "aadhaar", required = false) MultipartFile aadhaar,
            @RequestPart(value = "soilTestCertificate", required = false) MultipartFile soilTestCertificate
    ) throws JsonProcessingException {
        
        // Parse the JSON to Farmer entity
        ObjectMapper objectMapper = new ObjectMapper();
        Farmer farmer = objectMapper.readValue(farmerDtoJson, Farmer.class);
        
        // For now, we'll create the farmer without file handling in SuperAdmin
        // The ID card generation will still work
        return ResponseEntity.ok(farmerService.createFarmerBySuperAdmin(farmer));
    }
    
    @PostMapping(value = "/farmers/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Farmer> createFarmerJson(@RequestBody Farmer farmer) {
        return ResponseEntity.ok(farmerService.createFarmerBySuperAdmin(farmer));
    }

    @PutMapping("/farmers/{id}")
    public ResponseEntity<Farmer> updateFarmer(@PathVariable Long id, @RequestBody Farmer farmer) {
        return ResponseEntity.ok(farmerService.updateFarmerBySuperAdmin(id, farmer));
    }

    @DeleteMapping("/farmers/{id}")
    public ResponseEntity<String> deleteFarmer(@PathVariable Long id) {
        farmerService.deleteFarmerBySuperAdmin(id);
        return ResponseEntity.ok("Farmer deleted successfully");
    }

    // --- BULK ASSIGN FARMERS TO EMPLOYEE ---
    @PostMapping("/bulk-assign-farmers")
    public ResponseEntity<Map<String, Object>> bulkAssignFarmers(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> farmerIds = (List<Long>) request.get("farmerIds");
            Long employeeId = Long.valueOf(request.get("employeeId").toString());
            
            if (farmerIds == null || farmerIds.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No farmer IDs provided"));
            }
            
            int assignedCount = 0;
            for (Long farmerId : farmerIds) {
                try {
                    farmerService.assignFarmerToEmployee(farmerId, employeeId);
                    assignedCount++;
                } catch (Exception e) {
                    System.err.println("Error assigning farmer " + farmerId + ": " + e.getMessage());
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Farmers assigned successfully");
            response.put("assignedCount", assignedCount);
            response.put("totalRequested", farmerIds.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error in bulk assignment: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to assign farmers: " + e.getMessage()));
        }
    }

    // --- SINGLE ASSIGN FARMER TO EMPLOYEE ---
    @PostMapping("/assign-farmer")
    public ResponseEntity<String> assignFarmerToEmployee(@RequestParam Long farmerId, @RequestParam Long employeeId) {
        try {
            farmerService.assignFarmerToEmployee(farmerId, employeeId);
            return ResponseEntity.ok("Farmer assigned to employee successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to assign farmer: " + e.getMessage());
        }
    }

    // --- EMPLOYEE CRUD ---
    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployeesRaw());
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeRawById(id));
    }

    @PostMapping("/employees")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.createEmployeeBySuperAdmin(employee));
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.updateEmployeeBySuperAdmin(id, employee));
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployeeBySuperAdmin(id);
        return ResponseEntity.ok("Employee deleted successfully");
    }

    // --- DASHBOARD ENDPOINTS ---
    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(dashboardService.getSuperAdminDashboardStats());
    }
    
    @GetMapping("/dashboard/employee-stats")
    public ResponseEntity<List<EmployeeStatsDTO>> getEmployeeStats() {
        return ResponseEntity.ok(dashboardService.getEmployeeStats());
    }
    
    @GetMapping("/dashboard/todo")
    public ResponseEntity<TodoItemsDTO> getTodoItems() {
        return ResponseEntity.ok(dashboardService.getTodoItems());
    }

    @GetMapping("/pending-registrations")
    public ResponseEntity<List<User>> getPendingRegistrations() {
        return ResponseEntity.ok(userService.getPendingUsersRaw());
    }

    @GetMapping("/approved-users")
    public ResponseEntity<List<User>> getApprovedUsers() {
        return ResponseEntity.ok(userService.getApprovedUsersRaw());
    }

    // ✅ Get users by role for dashboard
    @GetMapping("/users/by-role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        try {
            Role userRole = Role.valueOf(role.toUpperCase());
            List<User> users = userService.getUsersByRoleRaw(userRole);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ Get pending users by role
    @GetMapping("/pending-users/by-role/{role}")
    public ResponseEntity<List<User>> getPendingUsersByRole(@PathVariable String role) {
        try {
            Role userRole = Role.valueOf(role.toUpperCase());
            List<User> users = userService.getPendingUsersByRoleRaw(userRole);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 