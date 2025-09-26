package com.farmer.Form.Repository;

import com.farmer.Form.Entity.CodeFormat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodeFormatRepository extends JpaRepository<CodeFormat, Long> {
    
    Optional<CodeFormat> findByCodeType(CodeFormat.CodeType codeType);
    
    Optional<CodeFormat> findByCodeTypeAndIsActiveTrue(CodeFormat.CodeType codeType);
    
    @Modifying
    @Query("UPDATE CodeFormat cf SET cf.currentNumber = cf.currentNumber + 1 WHERE cf.codeType = :codeType")
    int incrementCurrentNumber(@Param("codeType") CodeFormat.CodeType codeType);
    
    @Query("SELECT cf FROM CodeFormat cf WHERE cf.isActive = true")
    java.util.List<CodeFormat> findAllActive();
}
