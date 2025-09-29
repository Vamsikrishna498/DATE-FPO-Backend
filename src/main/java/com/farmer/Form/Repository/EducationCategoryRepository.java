package com.farmer.Form.Repository;

import com.farmer.Form.Entity.EducationCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EducationCategoryRepository extends JpaRepository<EducationCategory, Long> {
    
    List<EducationCategory> findByIsActiveTrue();
    
    List<EducationCategory> findByParentIdAndIsActiveTrue(Long parentId);
    
    Optional<EducationCategory> findByNameAndIsActiveTrue(String name);
    
    boolean existsByNameAndIsActiveTrue(String name);
    
    boolean existsByNameAndIsActiveTrueAndIdNot(String name, Long id);
}
