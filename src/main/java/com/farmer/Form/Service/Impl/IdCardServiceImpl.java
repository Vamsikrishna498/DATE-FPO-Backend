package com.farmer.Form.Service.Impl;

import com.farmer.Form.Entity.IdCard;
import com.farmer.Form.Entity.Farmer;
import com.farmer.Form.Entity.Employee;
import com.farmer.Form.Repository.IdCardRepository;
import com.farmer.Form.Repository.FarmerRepository;
import com.farmer.Form.Repository.EmployeeRepository;
import com.farmer.Form.Service.IdCardService;
import com.farmer.Form.Service.IdCardPdfService;
import com.farmer.Form.Service.IdGenerationService;
import com.farmer.Form.Service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class IdCardServiceImpl implements IdCardService {
    
    @Autowired
    private IdCardRepository idCardRepository;
    
    @Autowired
    private FarmerRepository farmerRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private IdCardPdfService idCardPdfService;
    
    @Autowired
    private IdGenerationService idGenerationService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Override
    public IdCard generateFarmerIdCard(Farmer farmer) throws IOException {
        // Check if farmer already has an ID card
        List<IdCard> existingCards = idCardRepository.findByHolderId(farmer.getId().toString());
        if (!existingCards.isEmpty()) {
            IdCard existingCard = existingCards.get(0);
            if (existingCard.getStatus() == IdCard.CardStatus.ACTIVE) {
                return existingCard; // Return existing active card
            }
        }
        
        // Generate unique ID
        String cardId = idGenerationService.generateFarmerId(farmer.getState(), farmer.getDistrict());
        
        // Create ID card entity
        IdCard idCard = IdCard.builder()
                .cardId(cardId)
                .cardType(IdCard.CardType.FARMER)
                .holderName(farmer.getFirstName() + " " + farmer.getLastName())
                .holderId(farmer.getId().toString())
                .photoFileName(farmer.getPhotoFileName())
                .village(farmer.getVillage())
                .district(farmer.getDistrict())
                .state(farmer.getState())
                .country(farmer.getCountry())
                .age(calculateAge(farmer.getDateOfBirth()))
                .gender(farmer.getGender())
                .dateOfBirth(farmer.getDateOfBirth())
                .status(IdCard.CardStatus.ACTIVE)
                .generatedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusYears(5)) // 5 years validity
                .build();
        
        // Generate PDF and PNG files
        byte[] pdfBytes = idCardPdfService.generateFarmerIdCardPdf(farmer, idCard);
        byte[] pngBytes = idCardPdfService.generateFarmerIdCardPng(farmer, idCard);
        
        // Save files
        String pdfFileName = "idcard_" + cardId + ".pdf";
        String pngFileName = "idcard_" + cardId + ".png";
        
        // Store files using file storage service
        // Note: You might need to implement a method to store byte arrays directly
        idCard.setPdfFileName(pdfFileName);
        idCard.setPngFileName(pngFileName);
        
        // Save to database
        return idCardRepository.save(idCard);
    }
    
    @Override
    public IdCard generateEmployeeIdCard(Employee employee) throws IOException {
        // Check if employee already has an ID card
        List<IdCard> existingCards = idCardRepository.findByHolderId(employee.getId().toString());
        if (!existingCards.isEmpty()) {
            IdCard existingCard = existingCards.get(0);
            if (existingCard.getStatus() == IdCard.CardStatus.ACTIVE) {
                return existingCard; // Return existing active card
            }
        }
        
        // Generate unique ID
        String cardId = idGenerationService.generateEmployeeId(employee.getState(), employee.getDistrict());
        
        // Create ID card entity
        IdCard idCard = IdCard.builder()
                .cardId(cardId)
                .cardType(IdCard.CardType.EMPLOYEE)
                .holderName(employee.getFirstName() + " " + employee.getLastName())
                .holderId(employee.getId().toString())
                .photoFileName(employee.getPhotoFileName())
                .village(employee.getVillage())
                .district(employee.getDistrict())
                .state(employee.getState())
                .country(employee.getCountry())
                .age(calculateAge(employee.getDob()))
                .gender(employee.getGender())
                .dateOfBirth(employee.getDob())
                .status(IdCard.CardStatus.ACTIVE)
                .generatedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusYears(5)) // 5 years validity
                .build();
        
        // Generate PDF and PNG files
        byte[] pdfBytes = idCardPdfService.generateEmployeeIdCardPdf(employee, idCard);
        byte[] pngBytes = idCardPdfService.generateEmployeeIdCardPng(employee, idCard);
        
        // Save files
        String pdfFileName = "idcard_" + cardId + ".pdf";
        String pngFileName = "idcard_" + cardId + ".png";
        
        // Store files using file storage service
        idCard.setPdfFileName(pdfFileName);
        idCard.setPngFileName(pngFileName);
        
        // Save to database
        return idCardRepository.save(idCard);
    }
    
    @Override
    public Optional<IdCard> getById(String cardId) {
        return idCardRepository.findByCardId(cardId);
    }
    
    @Override
    public List<IdCard> getByHolderId(String holderId) {
        return idCardRepository.findByHolderId(holderId);
    }
    
    @Override
    public Page<IdCard> getAllIdCards(Pageable pageable) {
        return idCardRepository.findAll(pageable);
    }
    
    @Override
    public Page<IdCard> getIdCardsByType(IdCard.CardType cardType, Pageable pageable) {
        // Repository method returns List; wrap manually for now
        List<IdCard> list = idCardRepository.findByCardType(cardType);
        return new org.springframework.data.domain.PageImpl<>(list, pageable, list.size());
    }
    
    @Override
    public Page<IdCard> searchIdCardsByName(String name, IdCard.CardType cardType, Pageable pageable) {
        List<IdCard> list = idCardRepository.findByHolderNameContainingAndType(name, cardType);
        return new org.springframework.data.domain.PageImpl<>(list, pageable, list.size());
    }
    
    @Override
    public Page<IdCard> getIdCardsByState(String state, IdCard.CardType cardType, Pageable pageable) {
        List<IdCard> list = idCardRepository.findByStateAndType(state, cardType);
        return new org.springframework.data.domain.PageImpl<>(list, pageable, list.size());
    }
    
    @Override
    public Page<IdCard> getIdCardsByDistrict(String district, IdCard.CardType cardType, Pageable pageable) {
        List<IdCard> list = idCardRepository.findByDistrictAndType(district, cardType);
        return new org.springframework.data.domain.PageImpl<>(list, pageable, list.size());
    }
    
    @Override
    public byte[] downloadIdCardPdf(String cardId) throws IOException {
        Optional<IdCard> idCardOpt = idCardRepository.findByCardId(cardId);
        if (idCardOpt.isEmpty()) {
            throw new RuntimeException("ID card not found");
        }
        
        IdCard idCard = idCardOpt.get();
        
        // Get the person details
        if (idCard.getCardType() == IdCard.CardType.FARMER) {
            Optional<Farmer> farmerOpt = farmerRepository.findById(Long.parseLong(idCard.getHolderId()));
            if (farmerOpt.isPresent()) {
                return idCardPdfService.generateFarmerIdCardPdf(farmerOpt.get(), idCard);
            }
        } else {
            Optional<Employee> employeeOpt = employeeRepository.findById(Long.parseLong(idCard.getHolderId()));
            if (employeeOpt.isPresent()) {
                return idCardPdfService.generateEmployeeIdCardPdf(employeeOpt.get(), idCard);
            }
        }
        
        throw new RuntimeException("Person details not found");
    }
    
    @Override
    public byte[] downloadIdCardPng(String cardId) throws IOException {
        Optional<IdCard> idCardOpt = idCardRepository.findByCardId(cardId);
        if (idCardOpt.isEmpty()) {
            throw new RuntimeException("ID card not found");
        }
        
        IdCard idCard = idCardOpt.get();
        
        // Get the person details
        if (idCard.getCardType() == IdCard.CardType.FARMER) {
            Optional<Farmer> farmerOpt = farmerRepository.findById(Long.parseLong(idCard.getHolderId()));
            if (farmerOpt.isPresent()) {
                return idCardPdfService.generateFarmerIdCardPng(farmerOpt.get(), idCard);
            }
        } else {
            Optional<Employee> employeeOpt = employeeRepository.findById(Long.parseLong(idCard.getHolderId()));
            if (employeeOpt.isPresent()) {
                return idCardPdfService.generateEmployeeIdCardPng(employeeOpt.get(), idCard);
            }
        }
        
        throw new RuntimeException("Person details not found");
    }
    
    @Override
    public IdCard regenerateIdCard(String cardId) throws IOException {
        Optional<IdCard> idCardOpt = idCardRepository.findByCardId(cardId);
        if (idCardOpt.isEmpty()) {
            throw new RuntimeException("ID card not found");
        }
        
        IdCard idCard = idCardOpt.get();
        
        // Regenerate based on type
        if (idCard.getCardType() == IdCard.CardType.FARMER) {
            Optional<Farmer> farmerOpt = farmerRepository.findById(Long.parseLong(idCard.getHolderId()));
            if (farmerOpt.isPresent()) {
                return generateFarmerIdCard(farmerOpt.get());
            }
        } else {
            Optional<Employee> employeeOpt = employeeRepository.findById(Long.parseLong(idCard.getHolderId()));
            if (employeeOpt.isPresent()) {
                return generateEmployeeIdCard(employeeOpt.get());
            }
        }
        
        throw new RuntimeException("Person details not found");
    }
    
    @Override
    public IdCard revokeIdCard(String cardId) {
        Optional<IdCard> idCardOpt = idCardRepository.findByCardId(cardId);
        if (idCardOpt.isEmpty()) {
            throw new RuntimeException("ID card not found");
        }
        
        IdCard idCard = idCardOpt.get();
        idCard.setStatus(IdCard.CardStatus.REVOKED);
        return idCardRepository.save(idCard);
    }
    
    @Override
    public IdCardStatistics getIdCardStatistics() {
        long totalIdCards = idCardRepository.count();
        long farmerIdCards = idCardRepository.countByTypeAndState(IdCard.CardType.FARMER, null);
        long employeeIdCards = idCardRepository.countByTypeAndState(IdCard.CardType.EMPLOYEE, null);
        long activeIdCards = idCardRepository.countByStatus(IdCard.CardStatus.ACTIVE);
        long expiredIdCards = idCardRepository.countByStatus(IdCard.CardStatus.EXPIRED);
        long revokedIdCards = idCardRepository.countByStatus(IdCard.CardStatus.REVOKED);
        
        return new IdCardStatistics(totalIdCards, farmerIdCards, employeeIdCards, 
                                   activeIdCards, expiredIdCards, revokedIdCards);
    }
    
    private int calculateAge(java.time.LocalDate dateOfBirth) {
        if (dateOfBirth == null) return 0;
        return java.time.Period.between(dateOfBirth, java.time.LocalDate.now()).getYears();
    }
}
