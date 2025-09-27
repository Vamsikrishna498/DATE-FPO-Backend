package com.farmer.Form.Repository;

import com.farmer.Form.Entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    
    List<Block> findByDistrictId(Long districtId);
    
    Optional<Block> findByNameAndDistrictId(String name, Long districtId);
    
    boolean existsByNameAndDistrictId(String name, Long districtId);
    
    @Query("SELECT b FROM Block b LEFT JOIN FETCH b.villages WHERE b.id = :id")
    Optional<Block> findByIdWithVillages(@Param("id") Long id);
    
    @Query("SELECT b FROM Block b LEFT JOIN FETCH b.villages WHERE b.district.id = :districtId")
    List<Block> findByDistrictIdWithVillages(@Param("districtId") Long districtId);
}
