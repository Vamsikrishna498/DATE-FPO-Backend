package com.farmer.Form.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "id_cards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdCard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String cardId; // Unique ID like FAM+state+country+4digits or EMP+state+country+4digits
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType cardType; // FARMER or EMPLOYEE
    
    @Column(nullable = false)
    private String holderName;
    
    @Column(nullable = false)
    private String holderId; // Reference to farmer or employee ID
    
    private String photoFileName;
    
    // Address details
    private String village;
    private String district;
    private String state;
    private String country;
    
    // Additional details
    private Integer age;
    private String gender;
    private LocalDate dateOfBirth;
    
    // File paths for generated cards
    private String pdfFileName;
    private String pngFileName;
    
    // Status and metadata
    @Enumerated(EnumType.STRING)
    private CardStatus status = CardStatus.ACTIVE;
    
    private LocalDateTime generatedAt;
    private LocalDateTime expiresAt;
    
    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (generatedAt == null) {
            generatedAt = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum CardType {
        FARMER, EMPLOYEE
    }
    
    public enum CardStatus {
        ACTIVE, EXPIRED, REVOKED
    }
}
