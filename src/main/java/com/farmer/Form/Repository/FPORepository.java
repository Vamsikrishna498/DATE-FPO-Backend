package com.farmer.Form.Repository;

import com.farmer.Form.Entity.FPO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FPORepository extends JpaRepository<FPO, Long> {

    Optional<FPO> findByFpoId(String fpoId);

    List<FPO> findByStatus(FPO.FPOStatus status);

    @Query("SELECT f FROM FPO f WHERE " +
           "(:searchTerm IS NULL OR :searchTerm = '' OR " +
           "LOWER(f.fpoName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(f.ceoName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "f.phoneNumber LIKE CONCAT('%', :searchTerm, '%') OR " +
           "LOWER(f.village) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(f.district) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(f.state) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:state IS NULL OR :state = '' OR LOWER(f.state) = LOWER(:state)) " +
           "AND (:district IS NULL OR :district = '' OR LOWER(f.district) = LOWER(:district)) " +
           "AND (:status IS NULL OR f.status = :status) " +
           "AND (:startDate IS NULL OR f.joinDate >= :startDate) " +
           "AND (:endDate IS NULL OR f.joinDate <= :endDate)")
    Page<FPO> findFPOsWithFilters(
            @Param("searchTerm") String searchTerm,
            @Param("state") String state,
            @Param("district") String district,
            @Param("status") FPO.FPOStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query("SELECT COUNT(f) FROM FPO f WHERE f.status = :status")
    Long countByStatus(@Param("status") FPO.FPOStatus status);

    @Query("SELECT f FROM FPO f WHERE f.state = :state")
    List<FPO> findByState(@Param("state") String state);

    @Query("SELECT f FROM FPO f WHERE f.district = :district")
    List<FPO> findByDistrict(@Param("district") String district);

    @Query("SELECT DISTINCT f.state FROM FPO f ORDER BY f.state")
    List<String> findDistinctStates();

    @Query("SELECT DISTINCT f.district FROM FPO f WHERE f.state = :state ORDER BY f.district")
    List<String> findDistinctDistrictsByState(@Param("state") String state);
}
