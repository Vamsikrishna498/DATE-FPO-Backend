package com.farmer.Form.Repository;

import com.farmer.Form.Entity.CropName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CropNameRepository extends JpaRepository<CropName, Long> {
    
    List<CropName> findByIsActiveTrue();
    
    Optional<CropName> findByNameAndIsActiveTrue(String name);
    
    Optional<CropName> findByCodeAndIsActiveTrue(String code);
    
    boolean existsByNameAndIsActiveTrue(String name);
    
    boolean existsByCodeAndIsActiveTrue(String code);
    
    boolean existsByNameAndIsActiveTrueAndIdNot(String name, Long id);
    
    boolean existsByCodeAndIsActiveTrueAndIdNot(String code, Long id);
}
