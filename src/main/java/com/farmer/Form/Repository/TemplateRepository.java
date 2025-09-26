package com.farmer.Form.Repository;

import com.farmer.Form.Entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {
    
    List<Template> findByTemplateType(Template.TemplateType templateType);
    
    List<Template> findByModuleType(Template.ModuleType moduleType);
    
    List<Template> findByTemplateTypeAndModuleType(Template.TemplateType templateType, Template.ModuleType moduleType);
    
    List<Template> findByIsActiveTrue();
    
    @Query("SELECT t FROM Template t WHERE t.templateName LIKE %:searchTerm% OR t.subject LIKE %:searchTerm% OR t.content LIKE %:searchTerm%")
    List<Template> findBySearchTerm(@Param("searchTerm") String searchTerm);
    
    Optional<Template> findByTemplateNameAndTemplateTypeAndModuleType(String templateName, Template.TemplateType templateType, Template.ModuleType moduleType);
    
    @Query("SELECT t FROM Template t WHERE t.templateType = :templateType AND t.moduleType = :moduleType AND t.isActive = true")
    List<Template> findActiveByTypeAndModule(@Param("templateType") Template.TemplateType templateType, @Param("moduleType") Template.ModuleType moduleType);
}
