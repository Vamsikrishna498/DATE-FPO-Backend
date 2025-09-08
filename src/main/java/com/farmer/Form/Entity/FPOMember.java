package com.farmer.Form.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fpo_members")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fpo_id", nullable = false)
    private FPO fpo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberType memberType;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MemberStatus status = MemberStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Additional member details
    private String memberId; // FPO-specific member ID
    private String shareAmount;
    private String shareCertificateNumber;
    private String remarks;

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (memberId == null) {
            memberId = generateMemberId();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String generateMemberId() {
        // Generate member ID in format: FPO + FPO_ID + 4-digit sequence
        String fpoId = fpo != null ? fpo.getFpoId() : "000";
        String sequence = String.format("%04d", System.currentTimeMillis() % 10000);
        return fpoId + "M" + sequence;
    }

    public enum MemberType {
        FARMER,
        EMPLOYEE,
        BOARD_MEMBER,
        CEO,
        ADMIN
    }

    public enum MemberStatus {
        ACTIVE,
        INACTIVE,
        SUSPENDED,
        TERMINATED
    }
}
