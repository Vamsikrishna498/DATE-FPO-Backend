package com.farmer.Form.Repository;

import com.farmer.Form.Entity.AgeSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgeSettingRepository extends JpaRepository<AgeSetting, Long> {
    
    List<AgeSetting> findByIsActiveTrue();
    
    List<AgeSetting> findByUserTypeAndIsActiveTrue(String userType);
    
    boolean existsByIsActiveTrue();
    
    boolean existsByNameAndIsActiveTrueAndIdNot(String name, Long id);
}
