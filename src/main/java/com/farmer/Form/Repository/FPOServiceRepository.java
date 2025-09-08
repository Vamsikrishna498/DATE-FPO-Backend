package com.farmer.Form.Repository;

import com.farmer.Form.Entity.FPOService;
import com.farmer.Form.Entity.FPO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FPOServiceRepository extends JpaRepository<FPOService, Long> {

    List<FPOService> findByFpo(FPO fpo);

    List<FPOService> findByFpoAndStatus(FPO fpo, FPOService.ServiceStatus status);

    @Query("SELECT fs FROM FPOService fs WHERE fs.fpo.id = :fpoId")
    List<FPOService> findByFpoId(@Param("fpoId") Long fpoId);

    @Query("SELECT fs FROM FPOService fs WHERE fs.farmer.id = :farmerId")
    List<FPOService> findByFarmerId(@Param("farmerId") Long farmerId);

    @Query("SELECT fs FROM FPOService fs WHERE fs.fpo.id = :fpoId AND fs.serviceType = :serviceType")
    List<FPOService> findByFpoIdAndServiceType(@Param("fpoId") Long fpoId, @Param("serviceType") FPOService.ServiceType serviceType);

    @Query("SELECT fs FROM FPOService fs WHERE fs.fpo.id = :fpoId AND fs.status = :status")
    List<FPOService> findByFpoIdAndStatus(@Param("fpoId") Long fpoId, @Param("status") FPOService.ServiceStatus status);

    @Query("SELECT fs FROM FPOService fs WHERE fs.fpo.id = :fpoId AND " +
           "(:serviceType IS NULL OR fs.serviceType = :serviceType) AND " +
           "(:status IS NULL OR fs.status = :status) AND " +
           "(:startDate IS NULL OR fs.requestedAt >= :startDate) AND " +
           "(:endDate IS NULL OR fs.requestedAt <= :endDate)")
    Page<FPOService> findServicesWithFilters(
            @Param("fpoId") Long fpoId,
            @Param("serviceType") FPOService.ServiceType serviceType,
            @Param("status") FPOService.ServiceStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT COUNT(fs) FROM FPOService fs WHERE fs.fpo.id = :fpoId AND fs.status = :status")
    Long countByFpoIdAndStatus(@Param("fpoId") Long fpoId, @Param("status") FPOService.ServiceStatus status);

    @Query("SELECT COUNT(fs) FROM FPOService fs WHERE fs.fpo.id = :fpoId AND fs.serviceType = :serviceType")
    Long countByFpoIdAndServiceType(@Param("fpoId") Long fpoId, @Param("serviceType") FPOService.ServiceType serviceType);
}
