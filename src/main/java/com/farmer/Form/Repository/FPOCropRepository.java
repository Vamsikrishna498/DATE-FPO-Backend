package com.farmer.Form.Repository;

import com.farmer.Form.Entity.FPOCrop;
import com.farmer.Form.Entity.FPO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FPOCropRepository extends JpaRepository<FPOCrop, Long> {

    List<FPOCrop> findByFpo(FPO fpo);

    List<FPOCrop> findByFpoAndStatus(FPO fpo, FPOCrop.CropStatus status);

    @Query("SELECT fc FROM FPOCrop fc WHERE fc.fpo.id = :fpoId")
    List<FPOCrop> findByFpoId(@Param("fpoId") Long fpoId);

    @Query("SELECT fc FROM FPOCrop fc WHERE fc.farmer.id = :farmerId")
    List<FPOCrop> findByFarmerId(@Param("farmerId") Long farmerId);

    @Query("SELECT fc FROM FPOCrop fc WHERE fc.fpo.id = :fpoId AND fc.season = :season")
    List<FPOCrop> findByFpoIdAndSeason(@Param("fpoId") Long fpoId, @Param("season") FPOCrop.Season season);

    @Query("SELECT fc FROM FPOCrop fc WHERE fc.fpo.id = :fpoId AND fc.status = :status")
    List<FPOCrop> findByFpoIdAndStatus(@Param("fpoId") Long fpoId, @Param("status") FPOCrop.CropStatus status);

    @Query("SELECT fc FROM FPOCrop fc WHERE fc.fpo.id = :fpoId AND " +
           "(:cropName IS NULL OR :cropName = '' OR LOWER(fc.cropName) LIKE LOWER(CONCAT('%', :cropName, '%'))) AND " +
           "(:season IS NULL OR fc.season = :season) AND " +
           "(:status IS NULL OR fc.status = :status) AND " +
           "(:startDate IS NULL OR fc.sowingDate >= :startDate) AND " +
           "(:endDate IS NULL OR fc.sowingDate <= :endDate)")
    Page<FPOCrop> findCropsWithFilters(
            @Param("fpoId") Long fpoId,
            @Param("cropName") String cropName,
            @Param("season") FPOCrop.Season season,
            @Param("status") FPOCrop.CropStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query("SELECT COUNT(fc) FROM FPOCrop fc WHERE fc.fpo.id = :fpoId AND fc.status = :status")
    Long countByFpoIdAndStatus(@Param("fpoId") Long fpoId, @Param("status") FPOCrop.CropStatus status);

    @Query("SELECT COUNT(fc) FROM FPOCrop fc WHERE fc.fpo.id = :fpoId AND fc.season = :season")
    Long countByFpoIdAndSeason(@Param("fpoId") Long fpoId, @Param("season") FPOCrop.Season season);

    @Query("SELECT SUM(fc.area) FROM FPOCrop fc WHERE fc.fpo.id = :fpoId AND fc.status = :status")
    Double sumAreaByFpoIdAndStatus(@Param("fpoId") Long fpoId, @Param("status") FPOCrop.CropStatus status);

    @Query("SELECT SUM(fc.actualYield) FROM FPOCrop fc WHERE fc.fpo.id = :fpoId AND fc.status = :status")
    Double sumYieldByFpoIdAndStatus(@Param("fpoId") Long fpoId, @Param("status") FPOCrop.CropStatus status);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM FPOCrop fc WHERE fc.fpo.id = :fpoId")
    void deleteByFpoId(@Param("fpoId") Long fpoId);
}
