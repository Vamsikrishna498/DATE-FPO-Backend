package com.farmer.Form.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fpo_board_members")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOBoardMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fpo_id", nullable = false)
    private FPO fpo;

    @NotBlank(message = "Board member name is required")
    @Column(nullable = false)
    private String name;

    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    @Column(nullable = false)
    private String phoneNumber;

    @Email(message = "Please provide a valid email address")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardRole role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime appointedAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Additional details
    private String address;
    private String qualification;
    private String experience;
    private String photoFileName;
    private String documentFileName;
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BoardMemberStatus status = BoardMemberStatus.ACTIVE;

    @PrePersist
    protected void onCreate() {
        appointedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum BoardRole {
        CHAIRMAN,
        VICE_CHAIRMAN,
        SECRETARY,
        TREASURER,
        MEMBER,
        CEO
    }

    public enum BoardMemberStatus {
        ACTIVE,
        INACTIVE,
        TERMINATED,
        SUSPENDED
    }
}
