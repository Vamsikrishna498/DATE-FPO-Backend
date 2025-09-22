package com.farmer.Form.Repository;

import com.farmer.Form.Entity.FPO;
import com.farmer.Form.Entity.FPOInputShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FPOInputShopRepository extends JpaRepository<FPOInputShop, Long> {
    List<FPOInputShop> findByFpo(FPO fpo);
    List<FPOInputShop> findByFpoId(Long fpoId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM FPOInputShop fis WHERE fis.fpo.id = :fpoId")
    void deleteByFpoId(@Param("fpoId") Long fpoId);
}


