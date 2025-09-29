package com.farmer.Form.Repository;

import com.farmer.Form.Entity.CropType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CropTypeRepository extends JpaRepository<CropType, Long> {
    
    List<CropType> findByIsActiveTrue();
    
    List<CropType> findByParentIdAndIsActiveTrue(Long parentId);
    
    Optional<CropType> findByNameAndIsActiveTrue(String name);
    
    Optional<CropType> findByCodeAndIsActiveTrue(String code);
    
    boolean existsByNameAndIsActiveTrue(String name);
    
    boolean existsByCodeAndIsActiveTrue(String code);
    
    boolean existsByNameAndIsActiveTrueAndIdNot(String name, Long id);
    
    boolean existsByCodeAndIsActiveTrueAndIdNot(String code, Long id);
}
