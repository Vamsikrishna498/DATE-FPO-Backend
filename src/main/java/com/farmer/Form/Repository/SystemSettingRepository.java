package com.farmer.Form.Repository;

import com.farmer.Form.Entity.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {
    
    List<SystemSetting> findBySettingCategory(SystemSetting.SettingCategory settingCategory);
    
    List<SystemSetting> findByIsActiveTrue();
    
    @Query("SELECT s FROM SystemSetting s WHERE s.settingCategory = :category AND s.isActive = true")
    List<SystemSetting> findActiveByCategory(@Param("category") SystemSetting.SettingCategory category);
    
    Optional<SystemSetting> findBySettingKey(String settingKey);
    
    @Query("SELECT s FROM SystemSetting s WHERE s.settingKey = :settingKey AND s.isActive = true")
    Optional<SystemSetting> findActiveBySettingKey(@Param("settingKey") String settingKey);
    
    @Query("SELECT s FROM SystemSetting s WHERE s.settingKey LIKE %:searchTerm% OR s.settingValue LIKE %:searchTerm% OR s.description LIKE %:searchTerm%")
    List<SystemSetting> findBySearchTerm(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT s FROM SystemSetting s WHERE s.settingCategory = :category AND (s.settingKey LIKE %:searchTerm% OR s.settingValue LIKE %:searchTerm%)")
    List<SystemSetting> findByCategoryAndSearchTerm(@Param("category") SystemSetting.SettingCategory category, @Param("searchTerm") String searchTerm);
    
    boolean existsBySettingKey(String settingKey);
    
    boolean existsBySettingKeyAndIdNot(String settingKey, Long id);
}
