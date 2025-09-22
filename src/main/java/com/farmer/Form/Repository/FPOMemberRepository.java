package com.farmer.Form.Repository;

import com.farmer.Form.Entity.FPOMember;
import com.farmer.Form.Entity.FPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FPOMemberRepository extends JpaRepository<FPOMember, Long> {

    List<FPOMember> findByFpo(FPO fpo);

    List<FPOMember> findByFpoAndStatus(FPO fpo, FPOMember.MemberStatus status);

    @Query("SELECT fm FROM FPOMember fm WHERE fm.fpo.id = :fpoId")
    List<FPOMember> findByFpoId(@Param("fpoId") Long fpoId);

    @Query("SELECT fm FROM FPOMember fm WHERE fm.farmer.id = :farmerId")
    List<FPOMember> findByFarmerId(@Param("farmerId") Long farmerId);

    @Query("SELECT fm FROM FPOMember fm WHERE fm.employee.id = :employeeId")
    List<FPOMember> findByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT fm FROM FPOMember fm WHERE fm.user.id = :userId")
    List<FPOMember> findByUserId(@Param("userId") Long userId);

    @Query("SELECT fm FROM FPOMember fm WHERE fm.fpo.id = :fpoId AND fm.memberType = :memberType")
    List<FPOMember> findByFpoIdAndMemberType(@Param("fpoId") Long fpoId, @Param("memberType") FPOMember.MemberType memberType);

    @Query("SELECT COUNT(fm) FROM FPOMember fm WHERE fm.fpo.id = :fpoId AND fm.status = :status")
    Long countByFpoIdAndStatus(@Param("fpoId") Long fpoId, @Param("status") FPOMember.MemberStatus status);

    @Query("SELECT COUNT(fm) FROM FPOMember fm WHERE fm.fpo.id = :fpoId AND fm.memberType = :memberType")
    Long countByFpoIdAndMemberType(@Param("fpoId") Long fpoId, @Param("memberType") FPOMember.MemberType memberType);

    Optional<FPOMember> findByFpoAndFarmer(FPO fpo, com.farmer.Form.Entity.Farmer farmer);

    Optional<FPOMember> findByFpoAndEmployee(FPO fpo, com.farmer.Form.Entity.Employee employee);

    Optional<FPOMember> findByFpoAndUser(FPO fpo, com.farmer.Form.Entity.User user);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM FPOMember fm WHERE fm.fpo.id = :fpoId")
    void deleteByFpoId(@Param("fpoId") Long fpoId);
}
