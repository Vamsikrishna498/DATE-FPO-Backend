package com.farmer.Form.Service;

import com.farmer.Form.Entity.IdCard;
import com.farmer.Form.Entity.Farmer;
import com.farmer.Form.Entity.Employee;
import org.springframework.core.io.Resource;

import java.io.IOException;

public interface IdCardPdfService {
    
    /**
     * Generate PDF for farmer ID card
     */
    byte[] generateFarmerIdCardPdf(Farmer farmer, IdCard idCard) throws IOException;
    
    /**
     * Generate PDF for employee ID card
     */
    byte[] generateEmployeeIdCardPdf(Employee employee, IdCard idCard) throws IOException;
    
    /**
     * Generate PNG image for farmer ID card
     */
    byte[] generateFarmerIdCardPng(Farmer farmer, IdCard idCard) throws IOException;
    
    /**
     * Generate PNG image for employee ID card
     */
    byte[] generateEmployeeIdCardPng(Employee employee, IdCard idCard) throws IOException;
    
    /**
     * Load photo as resource
     */
    Resource loadPhotoResource(String photoFileName) throws IOException;
    
    /**
     * Generate QR code for ID card
     */
    byte[] generateQRCode(String cardId) throws IOException;
}
