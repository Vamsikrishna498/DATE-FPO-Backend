package com.farmer.Form.Repository;

import com.farmer.Form.Entity.SystemPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemPreferenceRepository extends JpaRepository<SystemPreference, Long> {
    
    List<SystemPreference> findByPreferenceType(SystemPreference.PreferenceType preferenceType);
    
    List<SystemPreference> findByIsActiveTrue();
    
    @Query("SELECT sp FROM SystemPreference sp WHERE sp.preferenceType = :type AND sp.isActive = true")
    List<SystemPreference> findActiveByType(@Param("type") SystemPreference.PreferenceType type);
    
    Optional<SystemPreference> findByPreferenceKey(String preferenceKey);
    
    @Query("SELECT sp FROM SystemPreference sp WHERE sp.preferenceKey = :preferenceKey AND sp.isActive = true")
    Optional<SystemPreference> findActiveByPreferenceKey(@Param("preferenceKey") String preferenceKey);
    
    @Query("SELECT sp FROM SystemPreference sp WHERE sp.preferenceKey LIKE %:searchTerm% OR sp.preferenceValue LIKE %:searchTerm% OR sp.description LIKE %:searchTerm%")
    List<SystemPreference> findBySearchTerm(@Param("searchTerm") String searchTerm);
    
    boolean existsByPreferenceKey(String preferenceKey);
    
    boolean existsByPreferenceKeyAndIdNot(String preferenceKey, Long id);
}
