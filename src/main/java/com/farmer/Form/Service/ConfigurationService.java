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

import java.util.*;
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
    private final AgeSettingRepository ageSettingRepository;
    private final EducationTypeRepository educationTypeRepository;
    private final EducationCategoryRepository educationCategoryRepository;
    private final CropNameRepository cropNameRepository;
    private final CropTypeRepository cropTypeRepository;
    
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
    
    @Transactional(readOnly = true)
    public List<UserRoleDTO> getAllUserRoles() {
        List<UserRole> userRoles = userRoleRepository.findAll();
        return userRoles.stream()
                .map(UserRoleDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
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
    
    // Global Area Settings Services
    public List<Map<String, Object>> getAgeSettings() {
        try {
            List<AgeSetting> ageSettings = ageSettingRepository.findByIsActiveTrue();
            return ageSettings.stream()
                    .map(this::convertToMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching age settings from database", e);
            return new ArrayList<>();
        }
    }
    
    private Map<String, Object> convertToMap(AgeSetting ageSetting) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", ageSetting.getId());
        map.put("name", ageSetting.getName());
        map.put("minValue", ageSetting.getMinValue());
        map.put("maxValue", ageSetting.getMaxValue());
        map.put("description", ageSetting.getDescription());
        map.put("userType", ageSetting.getUserType());
        map.put("isActive", ageSetting.getIsActive());
        map.put("createdBy", ageSetting.getCreatedBy());
        map.put("createdAt", ageSetting.getCreatedAt());
        map.put("updatedBy", ageSetting.getUpdatedBy());
        map.put("updatedAt", ageSetting.getUpdatedAt());
        return map;
    }
    
    // Age Validation Methods
    public boolean validateAge(Integer age, String userType) {
        try {
            // Since we now have global age settings, get the first active age setting
            List<AgeSetting> ageSettings = ageSettingRepository.findByIsActiveTrue();
            if (!ageSettings.isEmpty()) {
                AgeSetting setting = ageSettings.get(0); // Get the first (and only) active age setting
                return age >= setting.getMinValue() && age <= setting.getMaxValue();
            }
            return true; // If no age setting found, allow any age
        } catch (Exception e) {
            log.error("Error validating age: {}", e.getMessage());
            return true; // Default to allowing if validation fails
        }
    }
    
    public String getAgeValidationMessage(Integer age, String userType) {
        try {
            // Since we now have global age settings, get the first active age setting
            List<AgeSetting> ageSettings = ageSettingRepository.findByIsActiveTrue();
            if (!ageSettings.isEmpty()) {
                AgeSetting setting = ageSettings.get(0); // Get the first (and only) active age setting
                if (age < setting.getMinValue()) {
                    return String.format("Age must be at least %d years for registration", 
                            setting.getMinValue());
                } else if (age > setting.getMaxValue()) {
                    return String.format("Age must not exceed %d years for registration", 
                            setting.getMaxValue());
                }
            }
            return null; // No validation error
        } catch (Exception e) {
            log.error("Error getting age validation message for user type: {}", userType, e);
            return null;
        }
    }
    
    private String determineUserType(String name) {
        if (name == null) return "GLOBAL";
        
        String lowerName = name.toLowerCase();
        if (lowerName.contains("farmer") || lowerName.contains("agriculture")) {
            return "FARMER";
        } else if (lowerName.contains("employee") || lowerName.contains("staff")) {
            return "EMPLOYEE";
        } else if (lowerName.contains("admin") || lowerName.contains("administrator")) {
            return "ADMIN";
        } else if (lowerName.contains("fpo") || lowerName.contains("organization")) {
            return "FPO";
        } else {
            return "GLOBAL";
        }
    }
    
    public AgeSettingDTO createAgeSetting(AgeSettingCreationDTO creationDTO) {
        // Validate min and max values
        if (creationDTO.getMinValue() >= creationDTO.getMaxValue()) {
            throw new IllegalArgumentException("Minimum value must be less than maximum value");
        }
        
        // Check if there's already an active age setting with the same name
        if (ageSettingRepository.existsByNameAndIsActiveTrueAndIdNot(creationDTO.getName(), -1L)) {
            throw new IllegalArgumentException("An active age setting with this name already exists. Please choose a different name.");
        }
        
        // Use userType from DTO, fallback to determineUserType if not provided
        String userType = creationDTO.getUserType() != null && !creationDTO.getUserType().trim().isEmpty() 
            ? creationDTO.getUserType() 
            : determineUserType(creationDTO.getName());
        
        AgeSetting ageSetting = AgeSetting.builder()
                .name(creationDTO.getName())
                .minValue(creationDTO.getMinValue())
                .maxValue(creationDTO.getMaxValue())
                .description(creationDTO.getDescription())
                .userType(userType)
                .isActive(creationDTO.getIsActive())
                .createdBy(creationDTO.getCreatedBy())
                .build();
        
        AgeSetting savedAgeSetting = ageSettingRepository.save(ageSetting);
        
        return AgeSettingDTO.builder()
                .id(savedAgeSetting.getId())
                .name(savedAgeSetting.getName())
                .minValue(savedAgeSetting.getMinValue())
                .maxValue(savedAgeSetting.getMaxValue())
                .description(savedAgeSetting.getDescription())
                .userType(savedAgeSetting.getUserType())
                .isActive(savedAgeSetting.getIsActive())
                .createdBy(savedAgeSetting.getCreatedBy())
                .createdAt(savedAgeSetting.getCreatedAt())
                .updatedBy(savedAgeSetting.getUpdatedBy())
                .updatedAt(savedAgeSetting.getUpdatedAt())
                .build();
    }
    
    public List<Map<String, Object>> getEducationTypes() {
        try {
            List<EducationType> educationTypes = educationTypeRepository.findByIsActiveTrue();
            return educationTypes.stream()
                    .map(this::convertEducationTypeToMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching education types from database", e);
            return new ArrayList<>();
        }
    }
    
    private Map<String, Object> convertEducationTypeToMap(EducationType educationType) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", educationType.getId());
        map.put("name", educationType.getName());
        map.put("description", educationType.getDescription());
        map.put("isActive", educationType.getIsActive());
        map.put("createdBy", educationType.getCreatedBy());
        map.put("createdAt", educationType.getCreatedAt());
        map.put("updatedBy", educationType.getUpdatedBy());
        map.put("updatedAt", educationType.getUpdatedAt());
        return map;
    }
    
    public List<Map<String, Object>> getEducationCategories() {
        try {
            List<EducationCategory> educationCategories = educationCategoryRepository.findByIsActiveTrue();
            return educationCategories.stream()
                    .map(this::convertEducationCategoryToMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching education categories from database", e);
            return new ArrayList<>();
        }
    }
    
    private Map<String, Object> convertEducationCategoryToMap(EducationCategory educationCategory) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", educationCategory.getId());
        map.put("name", educationCategory.getName());
        map.put("description", educationCategory.getDescription());
        map.put("parentId", educationCategory.getParentId());
        map.put("isActive", educationCategory.getIsActive());
        map.put("createdBy", educationCategory.getCreatedBy());
        map.put("createdAt", educationCategory.getCreatedAt());
        map.put("updatedBy", educationCategory.getUpdatedBy());
        map.put("updatedAt", educationCategory.getUpdatedAt());
        return map;
    }
    
    public Map<String, Object> createGlobalAreaSetting(Map<String, Object> settingData) {
        String type = (String) settingData.get("type");
        String name = (String) settingData.get("name");
        String description = (String) settingData.get("description");
        Boolean isActive = (Boolean) settingData.getOrDefault("isActive", true);
        String createdBy = (String) settingData.get("createdBy");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            switch (type) {
                case "EDUCATION_TYPE":
                    if (educationTypeRepository.existsByNameAndIsActiveTrue(name)) {
                        throw new IllegalArgumentException("Education type with name " + name + " already exists");
                    }
                    
                    EducationType educationType = EducationType.builder()
                            .name(name)
                            .description(description)
                            .isActive(isActive)
                            .createdBy(createdBy)
                            .build();
                    
                    EducationType savedEducationType = educationTypeRepository.save(educationType);
                    result = convertEducationTypeToMap(savedEducationType);
                    result.put("message", "Education type created successfully");
                    break;
                    
                case "EDUCATION_CATEGORY":
                    Long parentId = settingData.get("parentId") != null ? 
                            Long.valueOf(settingData.get("parentId").toString()) : null;
                    
                    if (educationCategoryRepository.existsByNameAndIsActiveTrue(name)) {
                        throw new IllegalArgumentException("Education category with name " + name + " already exists");
                    }
                    
                    EducationCategory educationCategory = EducationCategory.builder()
                            .name(name)
                            .description(description)
                            .parentId(parentId)
                            .isActive(isActive)
                            .createdBy(createdBy)
                            .build();
                    
                    EducationCategory savedEducationCategory = educationCategoryRepository.save(educationCategory);
                    result = convertEducationCategoryToMap(savedEducationCategory);
                    result.put("message", "Education category created successfully");
                    break;
                    
                default:
                    throw new IllegalArgumentException("Unknown setting type: " + type);
            }
        } catch (Exception e) {
            log.error("Error creating global area setting", e);
            throw e;
        }
        
        return result;
    }
    
    public Map<String, Object> updateGlobalAreaSetting(Long id, Map<String, Object> settingData) {
        String type = (String) settingData.get("type");
        String name = (String) settingData.get("name");
        String description = (String) settingData.get("description");
        Boolean isActive = (Boolean) settingData.get("isActive");
        String updatedBy = (String) settingData.get("updatedBy");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            switch (type) {
                case "AGE_SETTING":
                    AgeSetting ageSetting = ageSettingRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Age setting not found with id: " + id));
                    
                    if (!ageSetting.getName().equals(name) && 
                        ageSettingRepository.existsByNameAndIsActiveTrueAndIdNot(name, id)) {
                        throw new IllegalArgumentException("Age setting with name " + name + " already exists");
                    }
                    
                    Integer minValue = settingData.get("minValue") != null ? 
                            Integer.valueOf(settingData.get("minValue").toString()) : null;
                    Integer maxValue = settingData.get("maxValue") != null ? 
                            Integer.valueOf(settingData.get("maxValue").toString()) : null;
                    ageSetting.setName(name);
                    ageSetting.setMinValue(minValue);
                    ageSetting.setMaxValue(maxValue);
                    ageSetting.setDescription(description);
                    ageSetting.setUserType("GLOBAL"); // Set as global age setting
                    ageSetting.setIsActive(isActive);
                    ageSetting.setUpdatedBy(updatedBy);
                    
                    AgeSetting updatedAgeSetting = ageSettingRepository.save(ageSetting);
                    result = convertToMap(updatedAgeSetting);
                    result.put("message", "Age setting updated successfully");
                    break;
                    
                case "EDUCATION_TYPE":
                    EducationType educationType = educationTypeRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Education type not found with id: " + id));
                    
                    if (!educationType.getName().equals(name) && 
                        educationTypeRepository.existsByNameAndIsActiveTrueAndIdNot(name, id)) {
                        throw new IllegalArgumentException("Education type with name " + name + " already exists");
                    }
                    
                    educationType.setName(name);
                    educationType.setDescription(description);
                    educationType.setIsActive(isActive);
                    educationType.setUpdatedBy(updatedBy);
                    
                    EducationType updatedEducationType = educationTypeRepository.save(educationType);
                    result = convertEducationTypeToMap(updatedEducationType);
                    result.put("message", "Education type updated successfully");
                    break;
                    
                case "EDUCATION_CATEGORY":
                    EducationCategory educationCategory = educationCategoryRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Education category not found with id: " + id));
                    
                    if (!educationCategory.getName().equals(name) && 
                        educationCategoryRepository.existsByNameAndIsActiveTrueAndIdNot(name, id)) {
                        throw new IllegalArgumentException("Education category with name " + name + " already exists");
                    }
                    
                    Long parentId = settingData.get("parentId") != null ? 
                            Long.valueOf(settingData.get("parentId").toString()) : null;
                    
                    educationCategory.setName(name);
                    educationCategory.setDescription(description);
                    educationCategory.setParentId(parentId);
                    educationCategory.setIsActive(isActive);
                    educationCategory.setUpdatedBy(updatedBy);
                    
                    EducationCategory updatedEducationCategory = educationCategoryRepository.save(educationCategory);
                    result = convertEducationCategoryToMap(updatedEducationCategory);
                    result.put("message", "Education category updated successfully");
                    break;
                    
                default:
                    throw new IllegalArgumentException("Unknown setting type: " + type);
            }
        } catch (Exception e) {
            log.error("Error updating global area setting", e);
            throw e;
        }
        
        return result;
    }
    
    public void deleteGlobalAreaSetting(Long id) {
        log.info("Deleting global area setting with id: {}", id);
        
        try {
            // Try to delete from AgeSetting first
            Optional<AgeSetting> ageSetting = ageSettingRepository.findById(id);
            if (ageSetting.isPresent()) {
                ageSettingRepository.delete(ageSetting.get());
                log.info("Deleted age setting: {}", ageSetting.get().getName());
                return;
            }
            
            // Try to delete from EducationType
            Optional<EducationType> educationType = educationTypeRepository.findById(id);
            if (educationType.isPresent()) {
                educationTypeRepository.delete(educationType.get());
                log.info("Deleted education type: {}", educationType.get().getName());
                return;
            }
            
            // Try to delete from EducationCategory
            Optional<EducationCategory> educationCategory = educationCategoryRepository.findById(id);
            if (educationCategory.isPresent()) {
                educationCategoryRepository.delete(educationCategory.get());
                log.info("Deleted education category: {}", educationCategory.get().getName());
                return;
            }
            
            // If not found in any table, throw exception
            throw new ResourceNotFoundException("Global area setting not found with id: " + id);
            
        } catch (Exception e) {
            log.error("Error deleting global area setting with id: {}", id, e);
            throw e;
        }
    }
    
    public List<Map<String, Object>> getCropNames() {
        try {
            List<CropName> cropNames = cropNameRepository.findByIsActiveTrue();
            return cropNames.stream()
                    .map(this::convertCropNameToMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching crop names from database", e);
            return new ArrayList<>();
        }
    }
    
    private Map<String, Object> convertCropNameToMap(CropName cropName) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", cropName.getId());
        map.put("name", cropName.getName());
        map.put("code", cropName.getCode());
        map.put("description", cropName.getDescription());
        map.put("isActive", cropName.getIsActive());
        map.put("createdBy", cropName.getCreatedBy());
        map.put("createdAt", cropName.getCreatedAt());
        map.put("updatedBy", cropName.getUpdatedBy());
        map.put("updatedAt", cropName.getUpdatedAt());
        return map;
    }
    
    public List<Map<String, Object>> getCropTypes() {
        try {
            List<CropType> cropTypes = cropTypeRepository.findByIsActiveTrue();
            return cropTypes.stream()
                    .map(this::convertCropTypeToMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching crop types from database", e);
            return new ArrayList<>();
        }
    }
    
    private Map<String, Object> convertCropTypeToMap(CropType cropType) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", cropType.getId());
        map.put("name", cropType.getName());
        map.put("code", cropType.getCode());
        map.put("description", cropType.getDescription());
        map.put("parentId", cropType.getParentId());
        map.put("isActive", cropType.getIsActive());
        map.put("createdBy", cropType.getCreatedBy());
        map.put("createdAt", cropType.getCreatedAt());
        map.put("updatedBy", cropType.getUpdatedBy());
        map.put("updatedAt", cropType.getUpdatedAt());
        return map;
    }
    
    public Map<String, Object> createCropSetting(Map<String, Object> settingData) {
        String type = (String) settingData.get("type");
        String name = (String) settingData.get("name");
        String code = (String) settingData.get("code");
        String description = (String) settingData.get("description");
        Boolean isActive = (Boolean) settingData.getOrDefault("isActive", true);
        String createdBy = (String) settingData.get("createdBy");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            switch (type) {
                case "CROP_NAME":
                    if (cropNameRepository.existsByNameAndIsActiveTrue(name)) {
                        throw new IllegalArgumentException("Crop name " + name + " already exists");
                    }
                    
                    CropName cropName = CropName.builder()
                            .name(name)
                            .code(code)
                            .description(description)
                            .isActive(isActive)
                            .createdBy(createdBy)
                            .build();
                    
                    CropName savedCropName = cropNameRepository.save(cropName);
                    result = convertCropNameToMap(savedCropName);
                    result.put("message", "Crop name created successfully");
                    break;
                    
                case "CROP_TYPE":
                    Long parentId = settingData.get("parentId") != null ? 
                            Long.valueOf(settingData.get("parentId").toString()) : null;
                    
                    if (cropTypeRepository.existsByNameAndIsActiveTrue(name)) {
                        throw new IllegalArgumentException("Crop type " + name + " already exists");
                    }
                    
                    CropType cropType = CropType.builder()
                            .name(name)
                            .code(code)
                            .description(description)
                            .parentId(parentId)
                            .isActive(isActive)
                            .createdBy(createdBy)
                            .build();
                    
                    CropType savedCropType = cropTypeRepository.save(cropType);
                    result = convertCropTypeToMap(savedCropType);
                    result.put("message", "Crop type created successfully");
                    break;
                    
                default:
                    throw new IllegalArgumentException("Unknown crop setting type: " + type);
            }
        } catch (Exception e) {
            log.error("Error creating crop setting", e);
            throw e;
        }
        
        return result;
    }
    
    public Map<String, Object> updateCropSetting(Long id, Map<String, Object> settingData) {
        String type = (String) settingData.get("type");
        String name = (String) settingData.get("name");
        String code = (String) settingData.get("code");
        String description = (String) settingData.get("description");
        Boolean isActive = (Boolean) settingData.get("isActive");
        String updatedBy = (String) settingData.get("updatedBy");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            switch (type) {
                case "CROP_NAME":
                    CropName cropName = cropNameRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Crop name not found with id: " + id));
                    
                    if (!cropName.getName().equals(name) && 
                        cropNameRepository.existsByNameAndIsActiveTrueAndIdNot(name, id)) {
                        throw new IllegalArgumentException("Crop name " + name + " already exists");
                    }
                    
                    cropName.setName(name);
                    cropName.setCode(code);
                    cropName.setDescription(description);
                    cropName.setIsActive(isActive);
                    cropName.setUpdatedBy(updatedBy);
                    
                    CropName updatedCropName = cropNameRepository.save(cropName);
                    result = convertCropNameToMap(updatedCropName);
                    result.put("message", "Crop name updated successfully");
                    break;
                    
                case "CROP_TYPE":
                    CropType cropType = cropTypeRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Crop type not found with id: " + id));
                    
                    if (!cropType.getName().equals(name) && 
                        cropTypeRepository.existsByNameAndIsActiveTrueAndIdNot(name, id)) {
                        throw new IllegalArgumentException("Crop type " + name + " already exists");
                    }
                    
                    Long parentId = settingData.get("parentId") != null ? 
                            Long.valueOf(settingData.get("parentId").toString()) : null;
                    
                    cropType.setName(name);
                    cropType.setCode(code);
                    cropType.setDescription(description);
                    cropType.setParentId(parentId);
                    cropType.setIsActive(isActive);
                    cropType.setUpdatedBy(updatedBy);
                    
                    CropType updatedCropType = cropTypeRepository.save(cropType);
                    result = convertCropTypeToMap(updatedCropType);
                    result.put("message", "Crop type updated successfully");
                    break;
                    
                default:
                    throw new IllegalArgumentException("Unknown crop setting type: " + type);
            }
        } catch (Exception e) {
            log.error("Error updating crop setting", e);
            throw e;
        }
        
        return result;
    }
    
    public void deleteCropSetting(Long id) {
        log.info("Deleting crop setting with id: {}", id);
        
        try {
            // Try to delete from CropName first
            Optional<CropName> cropName = cropNameRepository.findById(id);
            if (cropName.isPresent()) {
                cropNameRepository.delete(cropName.get());
                log.info("Deleted crop name: {}", cropName.get().getName());
                return;
            }
            
            // Try to delete from CropType
            Optional<CropType> cropType = cropTypeRepository.findById(id);
            if (cropType.isPresent()) {
                cropTypeRepository.delete(cropType.get());
                log.info("Deleted crop type: {}", cropType.get().getName());
                return;
            }
            
            // If not found in either table, throw exception
            throw new ResourceNotFoundException("Crop setting not found with id: " + id);
            
        } catch (Exception e) {
            log.error("Error deleting crop setting with id: {}", id, e);
            throw e;
        }
    }
}