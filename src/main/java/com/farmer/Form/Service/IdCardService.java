package com.farmer.Form.Service;

import com.farmer.Form.Entity.IdCard;
import com.farmer.Form.Entity.Farmer;
import com.farmer.Form.Entity.Employee;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IdCardService {
    
    /**
     * Generate ID card for farmer
     */
    IdCard generateFarmerIdCard(Farmer farmer) throws IOException;
    
    /**
     * Generate ID card for employee
     */
    IdCard generateEmployeeIdCard(Employee employee) throws IOException;
    
    /**
     * Get ID card by card ID
     */
    Optional<IdCard> getById(String cardId);
    
    /**
     * Get ID card by holder ID
     */
    List<IdCard> getByHolderId(String holderId);
    
    /**
     * Get all ID cards with pagination
     */
    Page<IdCard> getAllIdCards(Pageable pageable);
    
    /**
     * Get ID cards by type
     */
    Page<IdCard> getIdCardsByType(IdCard.CardType cardType, Pageable pageable);
    
    /**
     * Search ID cards by name
     */
    Page<IdCard> searchIdCardsByName(String name, IdCard.CardType cardType, Pageable pageable);
    
    /**
     * Get ID cards by state
     */
    Page<IdCard> getIdCardsByState(String state, IdCard.CardType cardType, Pageable pageable);
    
    /**
     * Get ID cards by district
     */
    Page<IdCard> getIdCardsByDistrict(String district, IdCard.CardType cardType, Pageable pageable);
    
    /**
     * Download ID card PDF
     */
    byte[] downloadIdCardPdf(String cardId) throws IOException;
    
    /**
     * Download ID card PNG
     */
    byte[] downloadIdCardPng(String cardId) throws IOException;
    
    /**
     * Regenerate ID card
     */
    IdCard regenerateIdCard(String cardId) throws IOException;
    
    /**
     * Revoke ID card
     */
    IdCard revokeIdCard(String cardId);
    
    /**
     * Get ID card statistics
     */
    IdCardStatistics getIdCardStatistics();
    
    class IdCardStatistics {
        private long totalIdCards;
        private long farmerIdCards;
        private long employeeIdCards;
        private long activeIdCards;
        private long expiredIdCards;
        private long revokedIdCards;
        
        // Constructors, getters, and setters
        public IdCardStatistics() {}
        
        public IdCardStatistics(long totalIdCards, long farmerIdCards, long employeeIdCards, 
                               long activeIdCards, long expiredIdCards, long revokedIdCards) {
            this.totalIdCards = totalIdCards;
            this.farmerIdCards = farmerIdCards;
            this.employeeIdCards = employeeIdCards;
            this.activeIdCards = activeIdCards;
            this.expiredIdCards = expiredIdCards;
            this.revokedIdCards = revokedIdCards;
        }
        
        // Getters and setters
        public long getTotalIdCards() { return totalIdCards; }
        public void setTotalIdCards(long totalIdCards) { this.totalIdCards = totalIdCards; }
        
        public long getFarmerIdCards() { return farmerIdCards; }
        public void setFarmerIdCards(long farmerIdCards) { this.farmerIdCards = farmerIdCards; }
        
        public long getEmployeeIdCards() { return employeeIdCards; }
        public void setEmployeeIdCards(long employeeIdCards) { this.employeeIdCards = employeeIdCards; }
        
        public long getActiveIdCards() { return activeIdCards; }
        public void setActiveIdCards(long activeIdCards) { this.activeIdCards = activeIdCards; }
        
        public long getExpiredIdCards() { return expiredIdCards; }
        public void setExpiredIdCards(long expiredIdCards) { this.expiredIdCards = expiredIdCards; }
        
        public long getRevokedIdCards() { return revokedIdCards; }
        public void setRevokedIdCards(long revokedIdCards) { this.revokedIdCards = revokedIdCards; }
    }
}
