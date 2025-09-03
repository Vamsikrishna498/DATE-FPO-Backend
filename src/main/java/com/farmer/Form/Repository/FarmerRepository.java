package com.farmer.Form.Repository;


import com.farmer.Form.Entity.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
 
public interface FarmerRepository extends JpaRepository<Farmer, Long> {
    List<Farmer> findByAssignedEmployee_Email(String email);
    List<Farmer> findByAssignedEmployeeIsNull();
    List<Farmer> findByAssignedEmployee_Id(Long employeeId);
    List<Farmer> findByDistrictContainingIgnoreCase(String district);
    Long countByKycApprovedTrue();
    
    // Find farmer by email for login purposes
    java.util.Optional<Farmer> findByEmail(String email);

    // Additional helpers for bulk assignment/import logic
    List<Farmer> findByDistrictIgnoreCase(String district);
    List<Farmer> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);
}