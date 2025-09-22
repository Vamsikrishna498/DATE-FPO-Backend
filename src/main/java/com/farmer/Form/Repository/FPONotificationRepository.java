package com.farmer.Form.Repository;

import com.farmer.Form.Entity.FPONotification;
import com.farmer.Form.Entity.FPO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FPONotificationRepository extends JpaRepository<FPONotification, Long> {

    List<FPONotification> findByFpo(FPO fpo);

    List<FPONotification> findByFpoAndStatus(FPO fpo, FPONotification.NotificationStatus status);

    @Query("SELECT fn FROM FPONotification fn WHERE fn.fpo.id = :fpoId")
    List<FPONotification> findByFpoId(@Param("fpoId") Long fpoId);

    @Query("SELECT fn FROM FPONotification fn WHERE fn.fpo.id = :fpoId AND fn.status = :status")
    List<FPONotification> findByFpoIdAndStatus(@Param("fpoId") Long fpoId, @Param("status") FPONotification.NotificationStatus status);

    @Query("SELECT fn FROM FPONotification fn WHERE fn.fpo.id = :fpoId AND fn.type = :type")
    List<FPONotification> findByFpoIdAndType(@Param("fpoId") Long fpoId, @Param("type") FPONotification.NotificationType type);

    @Query("SELECT fn FROM FPONotification fn WHERE fn.fpo.id = :fpoId AND " +
           "(:type IS NULL OR fn.type = :type) AND " +
           "(:status IS NULL OR fn.status = :status) AND " +
           "(:startDate IS NULL OR fn.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR fn.createdAt <= :endDate)")
    Page<FPONotification> findNotificationsWithFilters(
            @Param("fpoId") Long fpoId,
            @Param("type") FPONotification.NotificationType type,
            @Param("status") FPONotification.NotificationStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT COUNT(fn) FROM FPONotification fn WHERE fn.fpo.id = :fpoId AND fn.status = :status")
    Long countByFpoIdAndStatus(@Param("fpoId") Long fpoId, @Param("status") FPONotification.NotificationStatus status);

    @Query("SELECT COUNT(fn) FROM FPONotification fn WHERE fn.fpo.id = :fpoId AND fn.type = :type")
    Long countByFpoIdAndType(@Param("fpoId") Long fpoId, @Param("type") FPONotification.NotificationType type);

    @Query("SELECT fn FROM FPONotification fn WHERE fn.fpo.id = :fpoId AND fn.scheduledAt <= :currentTime AND fn.status = 'UNREAD'")
    List<FPONotification> findScheduledNotifications(@Param("fpoId") Long fpoId, @Param("currentTime") LocalDateTime currentTime);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM FPONotification fn WHERE fn.fpo.id = :fpoId")
    void deleteByFpoId(@Param("fpoId") Long fpoId);
}
