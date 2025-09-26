package com.farmer.Form.Service;

import com.farmer.Form.DTO.*;
import com.farmer.Form.DTO.SystemPreferenceCreationDTO;
import com.farmer.Form.DTO.SystemPreferenceUpdateDTO;
import com.farmer.Form.Entity.*;
import com.farmer.Form.exception.ResourceNotFoundException;
import com.farmer.Form.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConfigurationService {
    
    private final UserRoleRepository userRoleRepository;
    private final CodeFormatRepository codeFormatRepository;
    private final TemplateRepository templateRepository;
    private final SystemSettingRepository systemSettingRepository;
    private final SystemPreferenceRepository systemPreferenceRepository;
    
    // UserRole Services
    public UserRoleDTO createUserRole(UserRoleCreationDTO creationDTO) {
        if (userRoleRepository.existsByRoleName(creationDTO.getRoleName())) {
            throw new IllegalArgumentException("Role with name " + creationDTO.getRoleName() + " already exists");
        }
        
        UserRole userRole = UserRole.builder()
                .roleName(creationDTO.getRoleName())
                .description(creationDTO.getDescription())
                .allowedModules(creationDTO.getAllowedModules())
                .permissions(creationDTO.getPermissions())
                .createdBy(creationDTO.getCreatedBy())
                .isActive(true)
                .build();
        
        UserRole savedRole = userRoleRepository.save(userRole);
        log.info("Created user role: {}", savedRole.getRoleName());
        return UserRoleDTO.fromEntity(savedRole);
    }
    
    public UserRoleDTO updateUserRole(Long id, UserRoleUpdateDTO updateDTO) {
        UserRole userRole = userRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User role not found with id: " + id));
        
        if (!userRole.getRoleName().equals(updateDTO.getRoleName()) && 
            userRoleRepository.existsByRoleNameAndIdNot(updateDTO.getRoleName(), id)) {
            throw new IllegalArgumentException("Role with name " + updateDTO.getRoleName() + " already exists");
        }
        
        userRole.setRoleName(updateDTO.getRoleName());
        userRole.setDescription(updateDTO.getDescription());
        userRole.setAllowedModules(updateDTO.getAllowedModules());
        userRole.setPermissions(updateDTO.getPermissions());
        userRole.setIsActive(updateDTO.getIsActive());
        userRole.setUpdatedBy(updateDTO.getUpdatedBy());
        
        UserRole updatedRole = userRoleRepository.save(userRole);
        log.info("Updated user role: {}", updatedRole.getRoleName());
        return UserRoleDTO.fromEntity(updatedRole);
    }
    
    public void deleteUserRole(Long id) {
        UserRole userRole = userRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User role not found with id: " + id));
        
        userRoleRepository.delete(userRole);
        log.info("Deleted user role: {}", userRole.getRoleName());
    }
    
    public List<UserRoleDTO> getAllUserRoles() {
        return userRoleRepository.findAll().stream()
                .map(UserRoleDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public UserRoleDTO getUserRoleById(Long id) {
        UserRole userRole = userRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User role not found with id: " + id));
        return UserRoleDTO.fromEntity(userRole);
    }
    
    // CodeFormat Services
    public CodeFormatDTO createCodeFormat(CodeFormatCreationDTO creationDTO) {
        log.info("🔄 Creating code format with DTO: {}", creationDTO);
        
        // Check if code format already exists
        Optional<CodeFormat> existingFormat = codeFormatRepository.findByCodeType(creationDTO.getCodeType());
        if (existingFormat.isPresent()) {
            log.warn("⚠️ Code format for {} already exists, updating instead of creating", creationDTO.getCodeType());
            // Update existing format instead of creating new one
            CodeFormat existing = existingFormat.get();
            existing.setPrefix(creationDTO.getPrefix());
            existing.setStartingNumber(creationDTO.getStartingNumber());
            existing.setCurrentNumber(creationDTO.getStartingNumber() - 1);
            existing.setDescription(creationDTO.getDescription());
            existing.setUpdatedBy(creationDTO.getCreatedBy());
            existing.setIsActive(true);
            
            CodeFormat updatedFormat = codeFormatRepository.save(existing);
            log.info("✅ Updated existing code format for: {}", updatedFormat.getCodeType());
            return CodeFormatDTO.fromEntity(updatedFormat);
        }
        
        try {
            CodeFormat codeFormat = CodeFormat.builder()
                    .codeType(creationDTO.getCodeType())
                    .prefix(creationDTO.getPrefix())
                    .startingNumber(creationDTO.getStartingNumber())
                    .currentNumber(creationDTO.getStartingNumber() - 1)
                    .description(creationDTO.getDescription())
                    .createdBy(creationDTO.getCreatedBy())
                    .isActive(true)
                    .build();
            
            log.info("🔄 About to save code format: {}", codeFormat);
            CodeFormat savedFormat = codeFormatRepository.save(codeFormat);
            log.info("✅ Created code format for: {}", savedFormat.getCodeType());
            return CodeFormatDTO.fromEntity(savedFormat);
        } catch (Exception e) {
            log.error("❌ Error creating code format: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    public CodeFormatDTO updateCodeFormat(Long id, CodeFormatUpdateDTO updateDTO) {
        log.info("🔄 Updating code format with ID: {}", id);
        log.info("🔄 Update DTO: {}", updateDTO);
        
        CodeFormat codeFormat = codeFormatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Code format not found with id: " + id));
        
        log.info("🔄 Found existing code format: {}", codeFormat);
        
        // Update fields only if they are provided
        // Don't update codeType - it should never change
        if (updateDTO.getPrefix() != null) {
            codeFormat.setPrefix(updateDTO.getPrefix());
        }
        if (updateDTO.getStartingNumber() != null) {
            codeFormat.setStartingNumber(updateDTO.getStartingNumber());
        }
        // Don't update currentNumber - preserve existing value
        // Only update currentNumber if it's explicitly provided and different
        if (updateDTO.getCurrentNumber() != null && !updateDTO.getCurrentNumber().equals(codeFormat.getCurrentNumber())) {
            codeFormat.setCurrentNumber(updateDTO.getCurrentNumber());
        }
        if (updateDTO.getDescription() != null) {
            codeFormat.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getIsActive() != null) {
            codeFormat.setIsActive(updateDTO.getIsActive());
        }
        if (updateDTO.getUpdatedBy() != null) {
            codeFormat.setUpdatedBy(updateDTO.getUpdatedBy());
        }
        // updatedAt is automatically handled by @UpdateTimestamp
        
        log.info("🔄 About to save updated code format: {}", codeFormat);
        
        try {
            CodeFormat updatedFormat = codeFormatRepository.save(codeFormat);
            log.info("✅ Successfully updated code format for: {}", updatedFormat.getCodeType());
            return CodeFormatDTO.fromEntity(updatedFormat);
        } catch (Exception e) {
            log.error("❌ Error saving code format: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    public List<CodeFormatDTO> getAllCodeFormats() {
        return codeFormatRepository.findAll().stream()
                .map(CodeFormatDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public CodeFormatDTO getCodeFormatByType(CodeFormat.CodeType codeType) {
        CodeFormat codeFormat = codeFormatRepository.findByCodeType(codeType)
                .orElseThrow(() -> new ResourceNotFoundException("Code format not found for type: " + codeType));
        return CodeFormatDTO.fromEntity(codeFormat);
    }
    
    public String generateNextCode(CodeFormat.CodeType codeType) {
        log.info("Generating next code for type: {}", codeType);
        Optional<CodeFormat> codeFormatOpt = codeFormatRepository.findByCodeTypeAndIsActiveTrue(codeType);
        if (codeFormatOpt.isEmpty()) {
            throw new ResourceNotFoundException("Active code format not found for type: " + codeType);
        }
        
        CodeFormat codeFormat = codeFormatOpt.get();
        int nextNumber = codeFormat.getCurrentNumber() + 1;
        
        // Generate the next code without actually incrementing the database
        String nextCode = codeFormat.getPrefix() + "-" + String.format("%05d", nextNumber);
        log.info("Generated next code preview: {} (using prefix: '{}')", nextCode, codeFormat.getPrefix());
        
        return nextCode;
    }
    
    // Template Services
    public TemplateDTO createTemplate(TemplateCreationDTO creationDTO) {
        if (templateRepository.findByTemplateNameAndTemplateTypeAndModuleType(
                creationDTO.getTemplateName(), 
                creationDTO.getTemplateType(), 
                creationDTO.getModuleType()).isPresent()) {
            throw new IllegalArgumentException("Template already exists with same name, type, and module");
        }
        
        Template template = Template.builder()
                .templateName(creationDTO.getTemplateName())
                .templateType(creationDTO.getTemplateType())
                .moduleType(creationDTO.getModuleType())
                .subject(creationDTO.getSubject())
                .content(creationDTO.getContent())
                .placeholders(creationDTO.getPlaceholders())
                .createdBy(creationDTO.getCreatedBy())
                .isActive(true)
                .build();
        
        Template savedTemplate = templateRepository.save(template);
        log.info("Created template: {}", savedTemplate.getTemplateName());
        return TemplateDTO.fromEntity(savedTemplate);
    }
    
    public TemplateDTO updateTemplate(Long id, TemplateUpdateDTO updateDTO) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + id));
        
        template.setTemplateName(updateDTO.getTemplateName());
        template.setSubject(updateDTO.getSubject());
        template.setContent(updateDTO.getContent());
        template.setPlaceholders(updateDTO.getPlaceholders());
        template.setIsActive(updateDTO.getIsActive());
        template.setUpdatedBy(updateDTO.getUpdatedBy());
        
        Template updatedTemplate = templateRepository.save(template);
        log.info("Updated template: {}", updatedTemplate.getTemplateName());
        return TemplateDTO.fromEntity(updatedTemplate);
    }
    
    public void deleteTemplate(Long id) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + id));
        
        templateRepository.delete(template);
        log.info("Deleted template: {}", template.getTemplateName());
    }
    
    public List<TemplateDTO> getAllTemplates() {
        return templateRepository.findAll().stream()
                .map(TemplateDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<TemplateDTO> getTemplatesByTypeAndModule(Template.TemplateType templateType, Template.ModuleType moduleType) {
        return templateRepository.findActiveByTypeAndModule(templateType, moduleType).stream()
                .map(TemplateDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    // SystemSetting Services
    public SystemSettingDTO createSystemSetting(SystemSettingCreationDTO creationDTO) {
        if (systemSettingRepository.existsBySettingKey(creationDTO.getSettingKey())) {
            throw new IllegalArgumentException("Setting with key " + creationDTO.getSettingKey() + " already exists");
        }
        
        SystemSetting setting = SystemSetting.builder()
                .settingCategory(creationDTO.getSettingCategory())
                .settingKey(creationDTO.getSettingKey())
                .settingValue(creationDTO.getSettingValue())
                .description(creationDTO.getDescription())
                .dataType(creationDTO.getDataType())
                .createdBy(creationDTO.getCreatedBy())
                .isActive(true)
                .build();
        
        SystemSetting savedSetting = systemSettingRepository.save(setting);
        log.info("Created system setting: {}", savedSetting.getSettingKey());
        return SystemSettingDTO.fromEntity(savedSetting);
    }
    
    public SystemSettingDTO updateSystemSetting(Long id, SystemSettingUpdateDTO updateDTO) {
        SystemSetting setting = systemSettingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("System setting not found with id: " + id));
        
        setting.setSettingValue(updateDTO.getSettingValue());
        setting.setDescription(updateDTO.getDescription());
        setting.setIsActive(updateDTO.getIsActive());
        setting.setUpdatedBy(updateDTO.getUpdatedBy());
        
        SystemSetting updatedSetting = systemSettingRepository.save(setting);
        log.info("Updated system setting: {}", updatedSetting.getSettingKey());
        return SystemSettingDTO.fromEntity(updatedSetting);
    }
    
    public void deleteSystemSetting(Long id) {
        SystemSetting setting = systemSettingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("System setting not found with id: " + id));
        
        systemSettingRepository.delete(setting);
        log.info("Deleted system setting: {}", setting.getSettingKey());
    }
    
    public List<SystemSettingDTO> getAllSystemSettings() {
        return systemSettingRepository.findAll().stream()
                .map(SystemSettingDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<SystemSettingDTO> getSystemSettingsByCategory(SystemSetting.SettingCategory category) {
        return systemSettingRepository.findActiveByCategory(category).stream()
                .map(SystemSettingDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public SystemSettingDTO getSystemSettingByKey(String settingKey) {
        SystemSetting setting = systemSettingRepository.findActiveBySettingKey(settingKey)
                .orElseThrow(() -> new ResourceNotFoundException("System setting not found with key: " + settingKey));
        return SystemSettingDTO.fromEntity(setting);
    }
    
    // SystemPreference Services
    public SystemPreferenceDTO createSystemPreference(SystemPreferenceCreationDTO creationDTO) {
        if (systemPreferenceRepository.existsByPreferenceKey(creationDTO.getPreferenceKey())) {
            throw new IllegalArgumentException("Preference with key " + creationDTO.getPreferenceKey() + " already exists");
        }
        
        SystemPreference preference = SystemPreference.builder()
                .preferenceKey(creationDTO.getPreferenceKey())
                .preferenceValue(creationDTO.getPreferenceValue())
                .description(creationDTO.getDescription())
                .preferenceType(SystemPreference.PreferenceType.valueOf(creationDTO.getPreferenceType()))
                .createdBy(creationDTO.getCreatedBy())
                .isActive(true)
                .build();
        
        SystemPreference savedPreference = systemPreferenceRepository.save(preference);
        log.info("Created system preference: {}", savedPreference.getPreferenceKey());
        return SystemPreferenceDTO.fromEntity(savedPreference);
    }
    
    public SystemPreferenceDTO updateSystemPreference(Long id, SystemPreferenceUpdateDTO updateDTO) {
        SystemPreference preference = systemPreferenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("System preference not found with id: " + id));
        
        preference.setPreferenceValue(updateDTO.getPreferenceValue());
        preference.setDescription(updateDTO.getDescription());
        preference.setIsActive(updateDTO.getIsActive());
        preference.setUpdatedBy(updateDTO.getUpdatedBy());
        
        SystemPreference updatedPreference = systemPreferenceRepository.save(preference);
        log.info("Updated system preference: {}", updatedPreference.getPreferenceKey());
        return SystemPreferenceDTO.fromEntity(updatedPreference);
    }
    
    public void deleteSystemPreference(Long id) {
        SystemPreference preference = systemPreferenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("System preference not found with id: " + id));
        
        systemPreferenceRepository.delete(preference);
        log.info("Deleted system preference: {}", preference.getPreferenceKey());
    }
    
    public List<SystemPreferenceDTO> getAllSystemPreferences() {
        return systemPreferenceRepository.findAll().stream()
                .map(SystemPreferenceDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<SystemPreferenceDTO> getSystemPreferencesByType(SystemPreference.PreferenceType preferenceType) {
        return systemPreferenceRepository.findActiveByType(preferenceType).stream()
                .map(SystemPreferenceDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public SystemPreferenceDTO getSystemPreferenceByKey(String preferenceKey) {
        SystemPreference preference = systemPreferenceRepository.findActiveByPreferenceKey(preferenceKey)
                .orElseThrow(() -> new ResourceNotFoundException("System preference not found with key: " + preferenceKey));
        return SystemPreferenceDTO.fromEntity(preference);
    }
}
