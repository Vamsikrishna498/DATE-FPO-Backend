package com.farmer.Form.Service;

import com.farmer.Form.Entity.IdCard;

public interface IdGenerationService {
    
    /**
     * Generate unique ID for farmer
     * Format: FAM + stateCode + countryCode + 4digits
     * Example: FAM+TN+IN+0001
     */
    String generateFarmerId(String state, String district);
    
    /**
     * Generate unique ID for employee
     * Format: EMP + stateCode + countryCode + 4digits
     * Example: EMP+TN+IN+0001
     */
    String generateEmployeeId(String state, String district);
    
    /**
     * Validate if the generated ID is unique
     */
    boolean isIdUnique(String cardId);
    
    /**
     * Get state code from state name
     */
    String getStateCode(String stateName);
    
    /**
     * Get country code from country name
     */
    String getCountryCode(String countryName);
}
