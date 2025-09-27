package com.farmer.Form.Controller;

import com.farmer.Form.DTO.*;
import com.farmer.Form.DTO.UserRoleCreationDTO;
import com.farmer.Form.DTO.UserRoleUpdateDTO;
import com.farmer.Form.DTO.CodeFormatCreationDTO;
import com.farmer.Form.DTO.CodeFormatUpdateDTO;
import com.farmer.Form.DTO.TemplateCreationDTO;
import com.farmer.Form.DTO.TemplateUpdateDTO;
import com.farmer.Form.DTO.SystemSettingCreationDTO;
import com.farmer.Form.DTO.SystemSettingUpdateDTO;
import com.farmer.Form.DTO.SystemPreferenceCreationDTO;
import com.farmer.Form.DTO.SystemPreferenceUpdateDTO;
import com.farmer.Form.Entity.*;
import com.farmer.Form.Service.ConfigurationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ConfigurationController {
    
    private final ConfigurationService configurationService;
    
    // UserRole Endpoints
    @PostMapping("/user-roles")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<UserRoleDTO> createUserRole(
            @Valid @RequestBody UserRoleCreationDTO creationDTO,
            Authentication authentication) {
        creationDTO.setCreatedBy(authentication.getName());
        UserRoleDTO createdRole = configurationService.createUserRole(creationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }
    
    @GetMapping("/user-roles")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<UserRoleDTO>> getAllUserRoles() {
        List<UserRoleDTO> roles = configurationService.getAllUserRoles();
        return ResponseEntity.ok(roles);
    }
    
    @GetMapping("/user-roles/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<UserRoleDTO> getUserRoleById(@PathVariable Long id) {
        UserRoleDTO role = configurationService.getUserRoleById(id);
        return ResponseEntity.ok(role);
    }
    
    @PutMapping("/user-roles/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<UserRoleDTO> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UserRoleUpdateDTO updateDTO,
            Authentication authentication) {
        updateDTO.setUpdatedBy(authentication.getName());
        UserRoleDTO updatedRole = configurationService.updateUserRole(id, updateDTO);
        return ResponseEntity.ok(updatedRole);
    }
    
    @DeleteMapping("/user-roles/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> deleteUserRole(@PathVariable Long id) {
        configurationService.deleteUserRole(id);
        return ResponseEntity.ok(Map.of("message", "User role deleted successfully"));
    }
    
    // CodeFormat Endpoints
    @PostMapping("/code-formats")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<CodeFormatDTO> createCodeFormat(
            @RequestBody CodeFormatCreationDTO creationDTO,
            Authentication authentication) {
        try {
            log.info("🔄 Received create code format request: {}", creationDTO);
            log.info("🔄 Authentication: {}", authentication);
            log.info("🔄 Authentication name: {}", authentication != null ? authentication.getName() : "null");
            
            if (authentication != null && authentication.getName() != null) {
                creationDTO.setCreatedBy(authentication.getName());
            } else {
                creationDTO.setCreatedBy("system"); // fallback
            }
            
            log.info("🔄 Set createdBy to: {}", creationDTO.getCreatedBy());
            CodeFormatDTO createdFormat = configurationService.createCodeFormat(creationDTO);
            log.info("✅ Successfully created code format: {}", createdFormat);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFormat);
        } catch (Exception e) {
            log.error("❌ Error in createCodeFormat controller: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    
    @GetMapping("/code-formats")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<CodeFormatDTO>> getAllCodeFormats() {
        List<CodeFormatDTO> formats = configurationService.getAllCodeFormats();
        return ResponseEntity.ok(formats);
    }
    
    @GetMapping("/code-formats/{codeType}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<CodeFormatDTO> getCodeFormatByType(@PathVariable CodeFormat.CodeType codeType) {
        CodeFormatDTO format = configurationService.getCodeFormatByType(codeType);
        return ResponseEntity.ok(format);
    }
    
    @PutMapping("/code-formats/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<CodeFormatDTO> updateCodeFormat(
            @PathVariable Long id,
            @RequestBody CodeFormatUpdateDTO updateDTO,
            Authentication authentication) {
        updateDTO.setUpdatedBy(authentication.getName());
        CodeFormatDTO updatedFormat = configurationService.updateCodeFormat(id, updateDTO);
        return ResponseEntity.ok(updatedFormat);
    }
    
    @PostMapping("/code-formats/generate/{codeType}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> generateNextCode(@PathVariable CodeFormat.CodeType codeType) {
        String nextCode = configurationService.generateNextCode(codeType);
        return ResponseEntity.ok(Map.of("nextCode", nextCode));
    }
    
    // Template Endpoints
    @PostMapping("/templates")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<TemplateDTO> createTemplate(
            @Valid @RequestBody TemplateCreationDTO creationDTO,
            Authentication authentication) {
        creationDTO.setCreatedBy(authentication.getName());
        TemplateDTO createdTemplate = configurationService.createTemplate(creationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTemplate);
    }
    
    @GetMapping("/templates")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<TemplateDTO>> getAllTemplates() {
        List<TemplateDTO> templates = configurationService.getAllTemplates();
        return ResponseEntity.ok(templates);
    }
    
    @GetMapping("/templates/{templateType}/{moduleType}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<TemplateDTO>> getTemplatesByTypeAndModule(
            @PathVariable Template.TemplateType templateType,
            @PathVariable Template.ModuleType moduleType) {
        List<TemplateDTO> templates = configurationService.getTemplatesByTypeAndModule(templateType, moduleType);
        return ResponseEntity.ok(templates);
    }
    
    @PutMapping("/templates/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<TemplateDTO> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody TemplateUpdateDTO updateDTO,
            Authentication authentication) {
        updateDTO.setUpdatedBy(authentication.getName());
        TemplateDTO updatedTemplate = configurationService.updateTemplate(id, updateDTO);
        return ResponseEntity.ok(updatedTemplate);
    }
    
    @DeleteMapping("/templates/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> deleteTemplate(@PathVariable Long id) {
        configurationService.deleteTemplate(id);
        return ResponseEntity.ok(Map.of("message", "Template deleted successfully"));
    }
    
    // SystemSetting Endpoints
    @PostMapping("/settings")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<SystemSettingDTO> createSystemSetting(
            @Valid @RequestBody SystemSettingCreationDTO creationDTO,
            Authentication authentication) {
        creationDTO.setCreatedBy(authentication.getName());
        SystemSettingDTO createdSetting = configurationService.createSystemSetting(creationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSetting);
    }
    
    @GetMapping("/settings")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<SystemSettingDTO>> getAllSystemSettings() {
        List<SystemSettingDTO> settings = configurationService.getAllSystemSettings();
        return ResponseEntity.ok(settings);
    }
    
    @GetMapping("/settings/{category}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<SystemSettingDTO>> getSystemSettingsByCategory(
            @PathVariable SystemSetting.SettingCategory category) {
        List<SystemSettingDTO> settings = configurationService.getSystemSettingsByCategory(category);
        return ResponseEntity.ok(settings);
    }
    
    @GetMapping("/settings/key/{settingKey}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<SystemSettingDTO> getSystemSettingByKey(@PathVariable String settingKey) {
        SystemSettingDTO setting = configurationService.getSystemSettingByKey(settingKey);
        return ResponseEntity.ok(setting);
    }
    
    @PutMapping("/settings/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<SystemSettingDTO> updateSystemSetting(
            @PathVariable Long id,
            @Valid @RequestBody SystemSettingUpdateDTO updateDTO,
            Authentication authentication) {
        updateDTO.setUpdatedBy(authentication.getName());
        SystemSettingDTO updatedSetting = configurationService.updateSystemSetting(id, updateDTO);
        return ResponseEntity.ok(updatedSetting);
    }
    
    @DeleteMapping("/settings/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> deleteSystemSetting(@PathVariable Long id) {
        configurationService.deleteSystemSetting(id);
        return ResponseEntity.ok(Map.of("message", "System setting deleted successfully"));
    }
    
    // SystemPreference Endpoints
    @PostMapping("/preferences")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<SystemPreferenceDTO> createSystemPreference(
            @Valid @RequestBody SystemPreferenceCreationDTO creationDTO,
            Authentication authentication) {
        creationDTO.setCreatedBy(authentication.getName());
        SystemPreferenceDTO createdPreference = configurationService.createSystemPreference(creationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPreference);
    }
    
    @GetMapping("/preferences")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<SystemPreferenceDTO>> getAllSystemPreferences() {
        List<SystemPreferenceDTO> preferences = configurationService.getAllSystemPreferences();
        return ResponseEntity.ok(preferences);
    }
    
    @GetMapping("/preferences/{preferenceType}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<SystemPreferenceDTO>> getSystemPreferencesByType(
            @PathVariable SystemPreference.PreferenceType preferenceType) {
        List<SystemPreferenceDTO> preferences = configurationService.getSystemPreferencesByType(preferenceType);
        return ResponseEntity.ok(preferences);
    }
    
    @GetMapping("/preferences/key/{preferenceKey}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<SystemPreferenceDTO> getSystemPreferenceByKey(@PathVariable String preferenceKey) {
        SystemPreferenceDTO preference = configurationService.getSystemPreferenceByKey(preferenceKey);
        return ResponseEntity.ok(preference);
    }
    
    @PutMapping("/preferences/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<SystemPreferenceDTO> updateSystemPreference(
            @PathVariable Long id,
            @Valid @RequestBody SystemPreferenceUpdateDTO updateDTO,
            Authentication authentication) {
        updateDTO.setUpdatedBy(authentication.getName());
        SystemPreferenceDTO updatedPreference = configurationService.updateSystemPreference(id, updateDTO);
        return ResponseEntity.ok(updatedPreference);
    }
    
    @DeleteMapping("/preferences/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> deleteSystemPreference(@PathVariable Long id) {
        configurationService.deleteSystemPreference(id);
        return ResponseEntity.ok(Map.of("message", "System preference deleted successfully"));
    }
    
    // Global Area Settings Endpoints
    @GetMapping("/global-area/age")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAgeSettings() {
        List<Map<String, Object>> ageSettings = configurationService.getAgeSettings();
        return ResponseEntity.ok(ageSettings);
    }
    
    @GetMapping("/global-area/education")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getEducationTypes() {
        List<Map<String, Object>> educationTypes = configurationService.getEducationTypes();
        return ResponseEntity.ok(educationTypes);
    }
    
    @GetMapping("/global-area/education-categories")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getEducationCategories() {
        List<Map<String, Object>> educationCategories = configurationService.getEducationCategories();
        return ResponseEntity.ok(educationCategories);
    }
    
    @PostMapping("/global-area")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createGlobalAreaSetting(
            @RequestBody Map<String, Object> settingData,
            Authentication authentication) {
        settingData.put("createdBy", authentication.getName());
        Map<String, Object> createdSetting = configurationService.createGlobalAreaSetting(settingData);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSetting);
    }
    
    @PostMapping("/global-area/age")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<AgeSettingDTO> createAgeSetting(
            @Valid @RequestBody AgeSettingCreationDTO creationDTO,
            Authentication authentication) {
        creationDTO.setCreatedBy(authentication.getName());
        AgeSettingDTO createdAgeSetting = configurationService.createAgeSetting(creationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAgeSetting);
    }
    
    @GetMapping("/validate-age")
    public ResponseEntity<Map<String, Object>> validateAge(
            @RequestParam Integer age,
            @RequestParam String userType) {
        boolean isValid = configurationService.validateAge(age, userType);
        String message = configurationService.getAgeValidationMessage(age, userType);
        
        Map<String, Object> response = Map.of(
            "isValid", isValid,
            "message", message != null ? message : "Age is valid"
        );
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/global-area/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateGlobalAreaSetting(
            @PathVariable Long id,
            @RequestBody Map<String, Object> settingData,
            Authentication authentication) {
        settingData.put("updatedBy", authentication.getName());
        Map<String, Object> updatedSetting = configurationService.updateGlobalAreaSetting(id, settingData);
        return ResponseEntity.ok(updatedSetting);
    }
    
    @DeleteMapping("/global-area/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> deleteGlobalAreaSetting(@PathVariable Long id) {
        configurationService.deleteGlobalAreaSetting(id);
        return ResponseEntity.ok(Map.of("message", "Global area setting deleted successfully"));
    }
    
    // Crop Settings Endpoints
    @GetMapping("/crop/names")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getCropNames() {
        List<Map<String, Object>> cropNames = configurationService.getCropNames();
        return ResponseEntity.ok(cropNames);
    }
    
    @GetMapping("/crop/types")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getCropTypes() {
        List<Map<String, Object>> cropTypes = configurationService.getCropTypes();
        return ResponseEntity.ok(cropTypes);
    }
    
    @PostMapping("/crop")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createCropSetting(
            @RequestBody Map<String, Object> cropData,
            Authentication authentication) {
        cropData.put("createdBy", authentication.getName());
        Map<String, Object> createdCrop = configurationService.createCropSetting(cropData);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCrop);
    }
    
    @PutMapping("/crop/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateCropSetting(
            @PathVariable Long id,
            @RequestBody Map<String, Object> cropData,
            Authentication authentication) {
        cropData.put("updatedBy", authentication.getName());
        Map<String, Object> updatedCrop = configurationService.updateCropSetting(id, cropData);
        return ResponseEntity.ok(updatedCrop);
    }
    
    @DeleteMapping("/crop/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> deleteCropSetting(@PathVariable Long id) {
        configurationService.deleteCropSetting(id);
        return ResponseEntity.ok(Map.of("message", "Crop setting deleted successfully"));
    }
}
