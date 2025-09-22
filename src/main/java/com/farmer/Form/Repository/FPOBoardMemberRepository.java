package com.farmer.Form.Repository;

import com.farmer.Form.Entity.FPOBoardMember;
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
public interface FPOBoardMemberRepository extends JpaRepository<FPOBoardMember, Long> {

    List<FPOBoardMember> findByFpo(FPO fpo);

    List<FPOBoardMember> findByFpoAndStatus(FPO fpo, FPOBoardMember.BoardMemberStatus status);

    @Query("SELECT fbm FROM FPOBoardMember fbm WHERE fbm.fpo.id = :fpoId")
    List<FPOBoardMember> findByFpoId(@Param("fpoId") Long fpoId);

    @Query("SELECT fbm FROM FPOBoardMember fbm WHERE fbm.fpo.id = :fpoId AND fbm.role = :role")
    List<FPOBoardMember> findByFpoIdAndRole(@Param("fpoId") Long fpoId, @Param("role") FPOBoardMember.BoardRole role);

    @Query("SELECT fbm FROM FPOBoardMember fbm WHERE fbm.fpo.id = :fpoId AND fbm.status = :status")
    List<FPOBoardMember> findByFpoIdAndStatus(@Param("fpoId") Long fpoId, @Param("status") FPOBoardMember.BoardMemberStatus status);

    @Query("SELECT COUNT(fbm) FROM FPOBoardMember fbm WHERE fbm.fpo.id = :fpoId AND fbm.status = :status")
    Long countByFpoIdAndStatus(@Param("fpoId") Long fpoId, @Param("status") FPOBoardMember.BoardMemberStatus status);

    @Query("SELECT fbm FROM FPOBoardMember fbm WHERE fbm.fpo.id = :fpoId AND fbm.role = :role AND fbm.status = :status")
    Optional<FPOBoardMember> findByFpoIdAndRoleAndStatus(@Param("fpoId") Long fpoId, 
                                                        @Param("role") FPOBoardMember.BoardRole role, 
                                                        @Param("status") FPOBoardMember.BoardMemberStatus status);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM FPOBoardMember fbm WHERE fbm.fpo.id = :fpoId")
    void deleteByFpoId(@Param("fpoId") Long fpoId);
}
