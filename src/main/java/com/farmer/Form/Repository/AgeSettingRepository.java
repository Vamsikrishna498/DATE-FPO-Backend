package com.farmer.Form.Repository;

import com.farmer.Form.Entity.AgeSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgeSettingRepository extends JpaRepository<AgeSetting, Long> {
    
    List<AgeSetting> findByIsActiveTrue();
    
    Optional<AgeSetting> findByUserTypeAndIsActiveTrue(String userType);
    
    @Query("SELECT a FROM AgeSetting a WHERE a.userType = :userType AND a.isActive = true")
    Optional<AgeSetting> findActiveAgeSettingByUserType(@Param("userType") String userType);
    
    boolean existsByUserTypeAndIsActiveTrue(String userType);
}
