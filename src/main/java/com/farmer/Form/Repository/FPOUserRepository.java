package com.farmer.Form.Repository;

import com.farmer.Form.Entity.FPO;
import com.farmer.Form.Entity.FPOUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FPOUserRepository extends JpaRepository<FPOUser, Long> {
    List<FPOUser> findByFpo(FPO fpo);
    List<FPOUser> findByFpoId(Long fpoId);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<FPOUser> findByEmail(String email);
    Optional<FPOUser> findByPhoneNumber(String phoneNumber);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM FPOUser u WHERE u.fpo.id = :fpoId")
    void deleteByFpoId(@Param("fpoId") Long fpoId);
}


