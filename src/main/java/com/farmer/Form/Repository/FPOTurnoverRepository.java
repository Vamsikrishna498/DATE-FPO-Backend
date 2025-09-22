package com.farmer.Form.Repository;

import com.farmer.Form.Entity.FPOTurnover;
import com.farmer.Form.Entity.FPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FPOTurnoverRepository extends JpaRepository<FPOTurnover, Long> {

    List<FPOTurnover> findByFpo(FPO fpo);

    @Query("SELECT ft FROM FPOTurnover ft WHERE ft.fpo.id = :fpoId")
    List<FPOTurnover> findByFpoId(@Param("fpoId") Long fpoId);

    @Query("SELECT ft FROM FPOTurnover ft WHERE ft.fpo.id = :fpoId AND ft.financialYear = :financialYear")
    List<FPOTurnover> findByFpoIdAndFinancialYear(@Param("fpoId") Long fpoId, @Param("financialYear") Integer financialYear);

    @Query("SELECT ft FROM FPOTurnover ft WHERE ft.fpo.id = :fpoId AND ft.financialYear = :financialYear AND ft.quarter = :quarter")
    List<FPOTurnover> findByFpoIdAndFinancialYearAndQuarter(@Param("fpoId") Long fpoId, 
                                                           @Param("financialYear") Integer financialYear, 
                                                           @Param("quarter") Integer quarter);

    @Query("SELECT ft FROM FPOTurnover ft WHERE ft.fpo.id = :fpoId AND ft.financialYear = :financialYear AND ft.month = :month")
    List<FPOTurnover> findByFpoIdAndFinancialYearAndMonth(@Param("fpoId") Long fpoId, 
                                                         @Param("financialYear") Integer financialYear, 
                                                         @Param("month") Integer month);

    @Query("SELECT ft FROM FPOTurnover ft WHERE ft.fpo.id = :fpoId AND ft.turnoverType = :turnoverType")
    List<FPOTurnover> findByFpoIdAndTurnoverType(@Param("fpoId") Long fpoId, @Param("turnoverType") FPOTurnover.TurnoverType turnoverType);

    @Query("SELECT SUM(ft.revenue) FROM FPOTurnover ft WHERE ft.fpo.id = :fpoId AND ft.financialYear = :financialYear")
    Double sumRevenueByFpoIdAndFinancialYear(@Param("fpoId") Long fpoId, @Param("financialYear") Integer financialYear);

    @Query("SELECT SUM(ft.expenses) FROM FPOTurnover ft WHERE ft.fpo.id = :fpoId AND ft.financialYear = :financialYear")
    Double sumExpensesByFpoIdAndFinancialYear(@Param("fpoId") Long fpoId, @Param("financialYear") Integer financialYear);

    @Query("SELECT SUM(ft.profit) FROM FPOTurnover ft WHERE ft.fpo.id = :fpoId AND ft.financialYear = :financialYear")
    Double sumProfitByFpoIdAndFinancialYear(@Param("fpoId") Long fpoId, @Param("financialYear") Integer financialYear);

    @Query("SELECT SUM(ft.loss) FROM FPOTurnover ft WHERE ft.fpo.id = :fpoId AND ft.financialYear = :financialYear")
    Double sumLossByFpoIdAndFinancialYear(@Param("fpoId") Long fpoId, @Param("financialYear") Integer financialYear);

    @Query("SELECT DISTINCT ft.financialYear FROM FPOTurnover ft WHERE ft.fpo.id = :fpoId ORDER BY ft.financialYear DESC")
    List<Integer> findDistinctFinancialYearsByFpoId(@Param("fpoId") Long fpoId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM FPOTurnover ft WHERE ft.fpo.id = :fpoId")
    void deleteByFpoId(@Param("fpoId") Long fpoId);
}
