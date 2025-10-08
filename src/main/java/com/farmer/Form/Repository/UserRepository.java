package com.farmer.Form.Repository;

import com.farmer.Form.Entity.User;
import com.farmer.Form.Entity.Role;
import com.farmer.Form.Entity.UserStatus;
import com.farmer.Form.Entity.KycStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
 
public interface UserRepository extends JpaRepository<User, Long> {
 
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByEmailOrPhoneNumber(String email, String phone);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    // ✅ ADD THESE METHODS:
    List<User> findByRole(Role role);
    List<User> findByStatus(UserStatus status);
    List<User> findByRoleAndStatus(Role role, UserStatus status);
    
    // Dashboard stats
    Long countByRole(Role role);
    Long countByStatus(UserStatus status);
    Long countByRoleAndStatus(Role role, UserStatus status);
    Long countByRoleAndAssignedEmployeeIdIsNull(Role role);
    Long countByKycStatusAndCreatedAtBefore(KycStatus kycStatus, LocalDateTime date);
    
    // Employee assignments
    List<User> findByAssignedEmployeeId(Long employeeId);
    
    // Company relationships
    List<User> findByCompany(com.farmer.Form.Entity.Company company);
    
    // Search and filter methods
    @Query("SELECT u FROM User u WHERE u.role = :role AND " +
           "(:name IS NULL OR u.name LIKE %:name%) AND " +
           "(:state IS NULL OR u.state = :state) AND " +
           "(:district IS NULL OR u.district = :district) AND " +
           "(:region IS NULL OR u.region = :region) AND " +
           "(:status IS NULL OR u.status = :status) AND " +
           "(:kycStatus IS NULL OR u.kycStatus = :kycStatus)")
    Page<User> findUsersWithFilters(
        @Param("role") Role role,
        @Param("name") String name,
        @Param("state") String state,
        @Param("district") String district,
        @Param("region") String region,
        @Param("status") UserStatus status,
        @Param("kycStatus") KycStatus kycStatus,
        Pageable pageable
    );
    
    @Query("SELECT COUNT(DISTINCT u.assignedEmployeeId) FROM User u " +
           "WHERE u.role = :employeeRole AND " +
           "(SELECT COUNT(f) FROM User f WHERE f.assignedEmployeeId = u.id AND f.kycStatus = :pendingStatus) > :threshold")
    Long countEmployeesWithHighPending(
        @Param("employeeRole") Role employeeRole,
        @Param("pendingStatus") KycStatus pendingStatus,
        @Param("threshold") Long threshold
    );
}
