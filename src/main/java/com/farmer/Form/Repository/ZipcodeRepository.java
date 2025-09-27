package com.farmer.Form.Repository;

import com.farmer.Form.Entity.Zipcode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ZipcodeRepository extends JpaRepository<Zipcode, Long> {
    
    List<Zipcode> findByVillageId(Long villageId);
    
    Optional<Zipcode> findByCodeAndVillageId(String code, Long villageId);
    
    boolean existsByCodeAndVillageId(String code, Long villageId);
    
    Optional<Zipcode> findByCode(String code);
    
    @Query("SELECT z FROM Zipcode z " +
           "JOIN FETCH z.village v " +
           "JOIN FETCH v.block b " +
           "JOIN FETCH b.district d " +
           "JOIN FETCH d.state s " +
           "JOIN FETCH s.country c " +
           "WHERE z.code = :code")
    Optional<Zipcode> findByCodeWithFullHierarchy(@Param("code") String code);
}
