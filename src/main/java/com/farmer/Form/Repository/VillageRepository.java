package com.farmer.Form.Repository;

import com.farmer.Form.Entity.Village;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VillageRepository extends JpaRepository<Village, Long> {
    
    List<Village> findByBlockId(Long blockId);
    
    Optional<Village> findByNameAndBlockId(String name, Long blockId);
    
    boolean existsByNameAndBlockId(String name, Long blockId);
    
    @Query("SELECT v FROM Village v LEFT JOIN FETCH v.zipcodes WHERE v.id = :id")
    Optional<Village> findByIdWithZipcodes(@Param("id") Long id);
    
    @Query("SELECT v FROM Village v LEFT JOIN FETCH v.zipcodes WHERE v.block.id = :blockId")
    List<Village> findByBlockIdWithZipcodes(@Param("blockId") Long blockId);
}
