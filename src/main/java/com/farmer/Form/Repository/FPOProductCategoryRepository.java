package com.farmer.Form.Repository;

import com.farmer.Form.Entity.FPOProductCategory;
import com.farmer.Form.Entity.FPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FPOProductCategoryRepository extends JpaRepository<FPOProductCategory, Long> {

    List<FPOProductCategory> findByFpo(FPO fpo);

    @Query("SELECT fpc FROM FPOProductCategory fpc WHERE fpc.fpo.id = :fpoId")
    List<FPOProductCategory> findByFpoId(@Param("fpoId") Long fpoId);

    @Query("SELECT fpc FROM FPOProductCategory fpc WHERE fpc.fpo.id = :fpoId AND LOWER(fpc.categoryName) = LOWER(:categoryName)")
    FPOProductCategory findByFpoIdAndCategoryName(@Param("fpoId") Long fpoId, @Param("categoryName") String categoryName);

    @Query("SELECT COUNT(fpc) FROM FPOProductCategory fpc WHERE fpc.fpo.id = :fpoId")
    Long countByFpoId(@Param("fpoId") Long fpoId);
}
