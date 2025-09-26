package com.farmer.Form.Repository;

import com.farmer.Form.Entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    
    Optional<UserRole> findByRoleName(String roleName);
    
    List<UserRole> findByIsActiveTrue();
    
    @Query("SELECT ur FROM UserRole ur WHERE ur.roleName LIKE %:searchTerm% OR ur.description LIKE %:searchTerm%")
    List<UserRole> findBySearchTerm(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT ur FROM UserRole ur WHERE ur.allowedModules LIKE %:module%")
    List<UserRole> findByModule(@Param("module") String module);
    
    boolean existsByRoleName(String roleName);
    
    boolean existsByRoleNameAndIdNot(String roleName, Long id);
}
