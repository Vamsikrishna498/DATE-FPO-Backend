package com.farmer.Form.DTO;

public class SystemPreferenceUpdateDTO {
    private String preferenceValue;
    private String description;
    private Boolean isActive;
    private String updatedBy;
    
    public SystemPreferenceUpdateDTO() {}
    
    public SystemPreferenceUpdateDTO(String preferenceValue, String description, Boolean isActive, String updatedBy) {
        this.preferenceValue = preferenceValue;
        this.description = description;
        this.isActive = isActive;
        this.updatedBy = updatedBy;
    }
    
    public String getPreferenceValue() { return preferenceValue; }
    public void setPreferenceValue(String preferenceValue) { this.preferenceValue = preferenceValue; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
