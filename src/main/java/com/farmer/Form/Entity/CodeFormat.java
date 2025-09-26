package com.farmer.Form.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "code_formats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeFormat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "code_type", nullable = false, unique = true)
    private CodeType codeType;
    
    @Column(name = "prefix", nullable = false)
    private String prefix;
    
    @Column(name = "starting_number", nullable = false)
    private Integer startingNumber;
    
    @Column(name = "current_number", nullable = false)
    @Builder.Default
    private Integer currentNumber = 0;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public enum CodeType {
        FARMER, EMPLOYEE, FPO
    }
}
