package com.farmer.Form.Repository;

import com.farmer.Form.Entity.EducationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EducationTypeRepository extends JpaRepository<EducationType, Long> {
    
    List<EducationType> findByIsActiveTrue();
    
    Optional<EducationType> findByNameAndIsActiveTrue(String name);
    
    boolean existsByNameAndIsActiveTrue(String name);
    
    boolean existsByNameAndIsActiveTrueAndIdNot(String name, Long id);
}
