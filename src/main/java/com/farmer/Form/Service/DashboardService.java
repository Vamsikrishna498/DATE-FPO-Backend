package com.farmer.Form.Service;

import com.farmer.Form.DTO.DashboardStatsDTO;
import com.farmer.Form.DTO.EmployeeStatsDTO;
import com.farmer.Form.DTO.TodoItemsDTO;
import com.farmer.Form.DTO.AssignmentRequestDTO;
import com.farmer.Form.Entity.User;
import com.farmer.Form.Entity.Role;
import com.farmer.Form.Entity.UserStatus;
import com.farmer.Form.Entity.KycStatus;
import com.farmer.Form.Entity.Farmer;
import com.farmer.Form.Repository.FarmerRepository;
import com.farmer.Form.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {
    
    private final UserRepository userRepository;
    private final FarmerRepository farmerRepository;
    
    // Super Admin Dashboard Stats
    public DashboardStatsDTO getSuperAdminDashboardStats() {
        long totalFarmers = farmerRepository.count();
        long kycApprovedFarmers = farmerRepository.countByKycApprovedTrue();
        long pendingKycFarmers = Math.max(0, totalFarmers - kycApprovedFarmers);
        DashboardStatsDTO stats = DashboardStatsDTO.builder()
            .totalFarmers(totalFarmers)
            .totalEmployees(userRepository.countByRole(Role.EMPLOYEE))
            .pendingUsers(userRepository.countByStatus(UserStatus.PENDING))
            .approvedUsers(userRepository.countByStatus(UserStatus.APPROVED))
            .totalFPO(userRepository.countByRole(Role.FPO))
            .pendingEmployees(userRepository.countByRoleAndStatus(Role.EMPLOYEE, UserStatus.PENDING))
            .pendingFarmers(pendingKycFarmers)
            .approvedEmployees(userRepository.countByRoleAndStatus(Role.EMPLOYEE, UserStatus.APPROVED))
            .approvedFarmers(userRepository.countByRoleAndStatus(Role.FARMER, UserStatus.APPROVED))
            .kycApprovedFarmers(kycApprovedFarmers)
            .build();
        return stats;
    }
    
    // Admin Dashboard Stats
    public DashboardStatsDTO getAdminDashboardStats() {
        return getSuperAdminDashboardStats(); // Same logic for now
    }
    
    // Employee Performance Stats
    public List<EmployeeStatsDTO> getEmployeeStats() {
        List<User> employees = userRepository.findByRole(Role.EMPLOYEE);
        List<EmployeeStatsDTO> stats = new ArrayList<>();
        
        for (User employee : employees) {
            EmployeeStatsDTO empStats = EmployeeStatsDTO.builder()
                .id(employee.getId())
                .name(employee.getName())
                .build();
            
            List<User> assignedFarmers = userRepository.findByAssignedEmployeeId(employee.getId());
            empStats.setTotalAssigned((long) assignedFarmers.size());
            
            empStats.setApproved(assignedFarmers.stream()
                .filter(f -> KycStatus.APPROVED.equals(f.getKycStatus()))
                .count());
            empStats.setPending(assignedFarmers.stream()
                .filter(f -> KycStatus.PENDING.equals(f.getKycStatus()))
                .count());
            empStats.setReferBack(assignedFarmers.stream()
                .filter(f -> KycStatus.REFER_BACK.equals(f.getKycStatus()))
                .count());
            empStats.setRejected(assignedFarmers.stream()
                .filter(f -> KycStatus.REJECTED.equals(f.getKycStatus()))
                .count());
            
            stats.add(empStats);
        }
        
        return stats;
    }
    
    // Todo Items
    public TodoItemsDTO getTodoItems() {
        TodoItemsDTO todo = TodoItemsDTO.builder()
            .unassignedFarmers(userRepository.countByRoleAndAssignedEmployeeIdIsNull(Role.FARMER))
            .build();
        
        // Overdue KYC (pending for 3+ days)
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        todo.setOverdueKyc(userRepository.countByKycStatusAndCreatedAtBefore(KycStatus.PENDING, threeDaysAgo));
        
        // Employees with high pending (more than 5 pending)
        todo.setEmployeesWithHighPending(userRepository.countEmployeesWithHighPending(Role.EMPLOYEE, KycStatus.PENDING, 5L));
        
        return todo;
    }
    
    // Assign Farmers to Employees
    @Transactional
    public void assignFarmersToEmployee(AssignmentRequestDTO request) {
        List<User> farmers = userRepository.findAllById(request.getFarmerIds());
        User employee = userRepository.findById(request.getEmployeeId())
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        for (User farmer : farmers) {
            farmer.setAssignedEmployeeId(employee.getId());
            userRepository.save(farmer);
        }
    }
} 