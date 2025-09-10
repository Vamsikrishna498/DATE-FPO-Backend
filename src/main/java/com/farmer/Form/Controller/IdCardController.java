package com.farmer.Form.Controller;

import com.farmer.Form.Entity.IdCard;
import com.farmer.Form.Entity.Farmer;
import com.farmer.Form.Entity.Employee;
import com.farmer.Form.Service.IdCardService;
import com.farmer.Form.Service.IdCardService.IdCardStatistics;
import com.farmer.Form.Service.FarmerService;
import com.farmer.Form.Service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/id-cards")
@CrossOrigin(origins = "*")
public class IdCardController {
    
    @Autowired
    private IdCardService idCardService;
    
    @Autowired
    private FarmerService farmerService;
    
    @Autowired
    private EmployeeService employeeService;
    
    /**
     * Generate ID card for farmer
     */
    @PostMapping("/generate/farmer/{farmerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<IdCard> generateFarmerIdCard(@PathVariable Long farmerId) {
        try {
            // Get farmer entity (raw) details first
            Farmer farmer = farmerService.getFarmerRawById(farmerId);
            if (farmer == null) {
                return ResponseEntity.notFound().build();
            }
            IdCard idCard = idCardService.generateFarmerIdCard(farmer);
            return ResponseEntity.ok(idCard);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Generate ID card for employee
     */
    @PostMapping("/generate/employee/{employeeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<IdCard> generateEmployeeIdCard(@PathVariable Long employeeId) {
        try {
            // Get employee details first
            Employee employee = employeeService.getEmployeeById(employeeId);
            if (employee == null) {
                return ResponseEntity.notFound().build();
            }
            IdCard idCard = idCardService.generateEmployeeIdCard(employee);
            return ResponseEntity.ok(idCard);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get ID card by card ID
     */
    @GetMapping("/{cardId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('FARMER') or hasRole('EMPLOYEE')")
    public ResponseEntity<IdCard> getIdCard(@PathVariable String cardId) {
        return idCardService.getById(cardId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get ID cards by holder ID
     */
    @GetMapping("/holder/{holderId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('FARMER') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<IdCard>> getIdCardsByHolder(@PathVariable String holderId) {
        List<IdCard> idCards = idCardService.getByHolderId(holderId);
        return ResponseEntity.ok(idCards);
    }
    
    /**
     * Get all ID cards with pagination
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Page<IdCard>> getAllIdCards(Pageable pageable) {
        Page<IdCard> idCards = idCardService.getAllIdCards(pageable);
        return ResponseEntity.ok(idCards);
    }
    
    /**
     * Get ID cards by type
     */
    @GetMapping("/type/{cardType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Page<IdCard>> getIdCardsByType(@PathVariable IdCard.CardType cardType, Pageable pageable) {
        Page<IdCard> idCards = idCardService.getIdCardsByType(cardType, pageable);
        return ResponseEntity.ok(idCards);
    }
    
    /**
     * Search ID cards by name
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Page<IdCard>> searchIdCards(
            @RequestParam String name,
            @RequestParam(required = false) IdCard.CardType cardType,
            Pageable pageable) {
        Page<IdCard> idCards = idCardService.searchIdCardsByName(name, cardType, pageable);
        return ResponseEntity.ok(idCards);
    }
    
    /**
     * Get ID cards by state
     */
    @GetMapping("/state/{state}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Page<IdCard>> getIdCardsByState(
            @PathVariable String state,
            @RequestParam(required = false) IdCard.CardType cardType,
            Pageable pageable) {
        Page<IdCard> idCards = idCardService.getIdCardsByState(state, cardType, pageable);
        return ResponseEntity.ok(idCards);
    }
    
    /**
     * Get ID cards by district
     */
    @GetMapping("/district/{district}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Page<IdCard>> getIdCardsByDistrict(
            @PathVariable String district,
            @RequestParam(required = false) IdCard.CardType cardType,
            Pageable pageable) {
        Page<IdCard> idCards = idCardService.getIdCardsByDistrict(district, cardType, pageable);
        return ResponseEntity.ok(idCards);
    }
    
    /**
     * Download ID card PDF
     */
    @GetMapping("/{cardId}/download/pdf")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('FARMER') or hasRole('EMPLOYEE')")
    public ResponseEntity<byte[]> downloadIdCardPdf(@PathVariable String cardId) {
        try {
            byte[] pdfBytes = idCardService.downloadIdCardPdf(cardId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "idcard_" + cardId + ".pdf");
            headers.setContentLength(pdfBytes.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Download ID card PNG
     */
    @GetMapping("/{cardId}/download/png")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('FARMER') or hasRole('EMPLOYEE')")
    public ResponseEntity<byte[]> downloadIdCardPng(@PathVariable String cardId) {
        try {
            byte[] pngBytes = idCardService.downloadIdCardPng(cardId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("attachment", "idcard_" + cardId + ".png");
            headers.setContentLength(pngBytes.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pngBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Regenerate ID card
     */
    @PostMapping("/{cardId}/regenerate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<IdCard> regenerateIdCard(@PathVariable String cardId) {
        try {
            IdCard idCard = idCardService.regenerateIdCard(cardId);
            return ResponseEntity.ok(idCard);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Revoke ID card
     */
    @PostMapping("/{cardId}/revoke")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<IdCard> revokeIdCard(@PathVariable String cardId) {
        IdCard idCard = idCardService.revokeIdCard(cardId);
        return ResponseEntity.ok(idCard);
    }
    
    /**
     * Get ID card statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<IdCardStatistics> getIdCardStatistics() {
        IdCardStatistics statistics = idCardService.getIdCardStatistics();
        return ResponseEntity.ok(statistics);
    }
}
