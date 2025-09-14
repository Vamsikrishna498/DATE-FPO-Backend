package com.farmer.Form.Service.Impl;

import com.farmer.Form.Entity.IdCard;
import com.farmer.Form.Entity.Farmer;
import com.farmer.Form.Entity.Employee;
import com.farmer.Form.Service.IdCardPdfService;
import com.farmer.Form.Service.FileStorageService;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.springframework.core.io.ClassPathResource;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Service
public class IdCardPdfServiceImpl implements IdCardPdfService {
    
    @Autowired
    private FileStorageService fileStorageService;
    
    private static final int CARD_WIDTH = 400;
    private static final int CARD_HEIGHT = 250;
    private static final String FONT_FAMILY = "Helvetica";
    
    @Override
    public byte[] generateFarmerIdCardPdf(Farmer farmer, IdCard idCard) throws IOException {
        // Render the same rich design as PNG, then embed into a single-page PDF
        BufferedImage image = createIdCardImage(farmer, idCard, true);
        byte[] pngBytes = convertImageToByteArray(image);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setDefaultPageSize(new com.itextpdf.kernel.geom.PageSize(CARD_WIDTH, CARD_HEIGHT));
        Document document = new Document(pdf);
        document.setMargins(0, 0, 0, 0);
        Image img = new Image(ImageDataFactory.create(pngBytes));
        img.setAutoScale(true);
        document.add(img);
        document.close();
        return baos.toByteArray();
    }
    
    @Override
    public byte[] generateEmployeeIdCardPdf(Employee employee, IdCard idCard) throws IOException {
        // Render PNG-style card and embed into one-page PDF (ensures identical look)
        BufferedImage image = createIdCardImage(employee, idCard, false);
        byte[] pngBytes = convertImageToByteArray(image);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setDefaultPageSize(new com.itextpdf.kernel.geom.PageSize(CARD_WIDTH, CARD_HEIGHT));
        Document document = new Document(pdf);
        document.setMargins(0, 0, 0, 0);
        Image img = new Image(ImageDataFactory.create(pngBytes));
        img.setAutoScale(true);
        document.add(img);
        document.close();
        return baos.toByteArray();
    }
    
    @Override
    public byte[] generateFarmerIdCardPng(Farmer farmer, IdCard idCard) throws IOException {
        BufferedImage image = createIdCardImage(farmer, idCard, true);
        return convertImageToByteArray(image);
    }
    
    @Override
    public byte[] generateEmployeeIdCardPng(Employee employee, IdCard idCard) throws IOException {
        BufferedImage image = createIdCardImage(employee, idCard, false);
        return convertImageToByteArray(image);
    }
    
    @Override
    public Resource loadPhotoResource(String photoFileName) throws IOException {
        if (photoFileName == null || photoFileName.isEmpty()) {
            return null;
        }
        return fileStorageService.loadFileAsResource(photoFileName, "photos");
    }
    
    @Override
    public byte[] generateQRCode(String cardId) throws IOException {
        // Simple QR code generation - in production, use a proper QR library
        BufferedImage qrImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = qrImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 100, 100);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 8));
        g2d.drawString("QR: " + cardId, 10, 50);
        g2d.dispose();
        return convertImageToByteArray(qrImage);
    }
    
    private Div createIdCardLayout(Object person, IdCard idCard, boolean isFarmer) throws IOException {
        Div cardContainer = new Div();
        cardContainer.setBorder(new SolidBorder(ColorConstants.BLACK, 2));
        cardContainer.setWidth(UnitValue.createPercentValue(100));
        cardContainer.setHeight(UnitValue.createPointValue(CARD_HEIGHT));
        cardContainer.setPadding(10);
        
        // Header
        Paragraph header = new Paragraph("DATE - Digital Agristack")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(16)
                .setBold()
                .setMarginBottom(5);
        cardContainer.add(header);
        
        Paragraph subHeader = new Paragraph("IDENTITY CARD")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(13)
                .setBold()
                .setMarginBottom(10);
        cardContainer.add(subHeader);
        
        // Main content table
        Table contentTable = new Table(2);
        contentTable.setWidth(UnitValue.createPercentValue(100));
        
        // Left column - Photo and basic info
        Div leftColumn = new Div();
        
        // Photo: prefer latest from profile entity, fallback to card record
        String photoFileName = null;
        if (isFarmer) {
            photoFileName = ((Farmer) person).getPhotoFileName();
        } else {
            photoFileName = ((Employee) person).getPhotoFileName();
        }
        if (photoFileName == null || photoFileName.isEmpty()) {
            photoFileName = idCard.getPhotoFileName();
        }
        if (photoFileName != null) {
            try {
                System.out.println("🖼️ IDCard PDF: trying photo " + photoFileName);
                Resource photoResource = loadPhotoResource(photoFileName);
                if (photoResource != null && photoResource.exists()) {
                    System.out.println("🖼️ IDCard PDF: photo exists, embedding.");
                    Image photo = new Image(ImageDataFactory.create(photoResource.getInputStream().readAllBytes()));
                    photo.setWidth(80);
                    photo.setHeight(100);
                    leftColumn.add(photo);
                } else {
                    System.out.println("⚠️ IDCard PDF: photo NOT found, using placeholder.");
                    leftColumn.add(createPhotoPlaceholder());
                }
            } catch (Exception e) {
                System.out.println("❌ IDCard PDF: error loading photo: " + e.getMessage());
                leftColumn.add(createPhotoPlaceholder());
            }
        } else {
            leftColumn.add(createPhotoPlaceholder());
        }
        
        // Basic info
        String name = isFarmer ? 
            ((Farmer) person).getFirstName() + " " + ((Farmer) person).getLastName() :
            ((Employee) person).getFirstName() + " " + ((Employee) person).getLastName();
        
        leftColumn.add(new Paragraph("Name: " + name)
                .setFontSize(10)
                .setMarginTop(5));
        leftColumn.add(new Paragraph("ID: " + idCard.getCardId())
                .setFontSize(10));
        leftColumn.add(new Paragraph("Type: " + (isFarmer ? "FARMER" : "EMPLOYEE"))
                .setFontSize(10));
        
        // Right column - Details
        Div rightColumn = new Div();
        
        if (isFarmer) {
            Farmer farmer = (Farmer) person;
            rightColumn.add(new Paragraph("Age: " + calculateAge(farmer.getDateOfBirth()))
                    .setFontSize(10));
            rightColumn.add(new Paragraph("Gender: " + farmer.getGender())
                    .setFontSize(10));
            rightColumn.add(new Paragraph("Village: " + farmer.getVillage())
                    .setFontSize(10));
            rightColumn.add(new Paragraph("District: " + farmer.getDistrict())
                    .setFontSize(10));
            rightColumn.add(new Paragraph("State: " + farmer.getState())
                    .setFontSize(10));
            rightColumn.add(new Paragraph("Country: " + farmer.getCountry())
                    .setFontSize(10));
        } else {
            Employee employee = (Employee) person;
            rightColumn.add(new Paragraph("Age: " + calculateAge(employee.getDob()))
                    .setFontSize(10));
            rightColumn.add(new Paragraph("Gender: " + employee.getGender())
                    .setFontSize(10));
            rightColumn.add(new Paragraph("Village: " + employee.getVillage())
                    .setFontSize(10));
            rightColumn.add(new Paragraph("District: " + employee.getDistrict())
                    .setFontSize(10));
            rightColumn.add(new Paragraph("State: " + employee.getState())
                    .setFontSize(10));
            rightColumn.add(new Paragraph("Country: " + employee.getCountry())
                    .setFontSize(10));
        }
        
        // Add columns to table
        contentTable.addCell(leftColumn);
        contentTable.addCell(rightColumn);
        
        cardContainer.add(contentTable);
        
        // Footer
        Paragraph footer = new Paragraph("Valid Until: " + idCard.getExpiresAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(8)
                .setMarginTop(10);
        cardContainer.add(footer);
        
        return cardContainer;
    }
    
    private BufferedImage createIdCardImage(Object person, IdCard idCard, boolean isFarmer) throws IOException {
        BufferedImage image = new BufferedImage(CARD_WIDTH, CARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, CARD_WIDTH, CARD_HEIGHT);
        
        // Border
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(5, 5, CARD_WIDTH - 10, CARD_HEIGHT - 10);
        
        // Brand header bar for DATE project with logo if available
        g2d.setColor(new Color(21, 128, 61));
        g2d.fillRoundRect(10, 10, CARD_WIDTH - 20, 36, 10, 10);
        
        try {
            // Try embedded logo in classpath: static/date-logo.png
            ClassPathResource logoRes = new ClassPathResource("static/date-logo.png");
            if (logoRes.exists()) {
                BufferedImage logo = ImageIO.read(logoRes.getInputStream());
                int logoH = 24;
                int logoW = (int) (logo.getWidth() * (logoH / (double) logo.getHeight()));
                g2d.drawImage(logo, 16, 16, logoW, logoH, null);
            }
        } catch (Exception ignored) {}

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        String brand = "Digital Agristack Transaction Enterprises";
        int brandX = (CARD_WIDTH - fm.stringWidth(brand)) / 2;
        g2d.drawString(brand, brandX, 33);
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 13));
        fm = g2d.getFontMetrics();
        String subHeader = "IDENTITY CARD";
        int subHeaderX = (CARD_WIDTH - fm.stringWidth(subHeader)) / 2;
        g2d.drawString(subHeader, subHeaderX, 55);
        
        // Photo (use uploaded one when available)
        boolean drewPhoto = false;
        String inlinePhoto = null;
        if (isFarmer) {
            inlinePhoto = ((Farmer) person).getPhotoFileName();
        } else {
            inlinePhoto = ((Employee) person).getPhotoFileName();
        }
        if (inlinePhoto == null || inlinePhoto.isEmpty()) inlinePhoto = idCard.getPhotoFileName();
        if (inlinePhoto != null && !inlinePhoto.isEmpty()) {
            try {
                System.out.println("🖼️ IDCard PNG: trying photo " + inlinePhoto);
                Resource photoRes = loadPhotoResource(inlinePhoto);
                if (photoRes != null && photoRes.exists()) {
                    System.out.println("🖼️ IDCard PNG: photo exists, drawing.");
                    BufferedImage photo = ImageIO.read(photoRes.getInputStream());
                    if (photo != null) {
                        java.awt.Image scaled = photo.getScaledInstance(80, 100, java.awt.Image.SCALE_SMOOTH);
                        g2d.drawImage(scaled, 20, 75, null);
                        drewPhoto = true;
                    }
                }
            } catch (Exception ex) {
                System.out.println("❌ IDCard PNG: error loading photo: " + ex.getMessage());
            }
        }
        if (!drewPhoto) {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(20, 75, 80, 100);
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawRect(20, 75, 80, 100);
            g2d.drawString("PHOTO", 45, 128);
        }
        
        // Basic info
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        String name = isFarmer ? 
            ((Farmer) person).getFirstName() + " " + ((Farmer) person).getLastName() :
            ((Employee) person).getFirstName() + " " + ((Employee) person).getLastName();
        
        g2d.drawString("Name: " + name, 120, 90);
        g2d.drawString("ID: " + idCard.getCardId(), 120, 110);
        g2d.drawString("Type: " + (isFarmer ? "FARMER" : "EMPLOYEE"), 120, 130);
        
        // Details
        if (isFarmer) {
            Farmer farmer = (Farmer) person;
            g2d.drawString("Age: " + calculateAge(farmer.getDateOfBirth()), 120, 150);
            g2d.drawString("Gender: " + farmer.getGender(), 120, 170);
            g2d.drawString("Village: " + farmer.getVillage(), 120, 190);
            g2d.drawString("District: " + farmer.getDistrict(), 120, 210);
            g2d.drawString("State: " + farmer.getState(), 120, 230);
        } else {
            Employee employee = (Employee) person;
            g2d.drawString("Age: " + calculateAge(employee.getDob()), 120, 150);
            g2d.drawString("Gender: " + employee.getGender(), 120, 170);
            g2d.drawString("Village: " + employee.getVillage(), 120, 190);
            g2d.drawString("District: " + employee.getDistrict(), 120, 210);
            g2d.drawString("State: " + employee.getState(), 120, 230);
        }
        
        // Footer
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        String footer = "Valid Until: " + idCard.getExpiresAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        fm = g2d.getFontMetrics();
        int footerX = (CARD_WIDTH - fm.stringWidth(footer)) / 2;
        g2d.drawString(footer, footerX, 240);
        
        g2d.dispose();
        return image;
    }
    
    private Paragraph createPhotoPlaceholder() {
        return new Paragraph("PHOTO\nPLACEHOLDER")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(8)
                .setWidth(80)
                .setHeight(100)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1));
    }
    
    private int calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) return 0;
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
    
    private byte[] convertImageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }
}
