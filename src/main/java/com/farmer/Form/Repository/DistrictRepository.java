package com.farmer.Form.Repository;

import com.farmer.Form.Entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    
    List<District> findByStateId(Long stateId);
    
    Optional<District> findByNameAndStateId(String name, Long stateId);
    
    boolean existsByNameAndStateId(String name, Long stateId);
    
    @Query("SELECT d FROM District d LEFT JOIN FETCH d.blocks WHERE d.id = :id")
    Optional<District> findByIdWithBlocks(@Param("id") Long id);
    
    @Query("SELECT d FROM District d LEFT JOIN FETCH d.blocks WHERE d.state.id = :stateId")
    List<District> findByStateIdWithBlocks(@Param("stateId") Long stateId);
}