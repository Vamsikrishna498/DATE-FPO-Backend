package com.farmer.Form.Repository;

import com.farmer.Form.Entity.FPO;
import com.farmer.Form.Entity.FPOInputShop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FPOInputShopRepository extends JpaRepository<FPOInputShop, Long> {
    List<FPOInputShop> findByFpo(FPO fpo);
    List<FPOInputShop> findByFpoId(Long fpoId);
}


