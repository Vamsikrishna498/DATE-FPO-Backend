package com.farmer.Form.Repository;

import com.farmer.Form.Entity.FPO;
import com.farmer.Form.Entity.FPOUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FPOUserRepository extends JpaRepository<FPOUser, Long> {
    List<FPOUser> findByFpo(FPO fpo);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<FPOUser> findByEmail(String email);
    Optional<FPOUser> findByPhoneNumber(String phoneNumber);
}


