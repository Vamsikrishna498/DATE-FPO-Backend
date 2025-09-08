package com.farmer.Form.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fpo_notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPONotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fpo_id", nullable = false)
    private FPO fpo;

    @NotBlank(message = "Notification title is required")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Notification message is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.UNREAD;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Additional details
    private String priority;
    private String targetAudience; // ALL, FARMERS, EMPLOYEES, BOARD_MEMBERS
    private LocalDateTime scheduledAt;
    private LocalDateTime readAt;
    private String readBy;
    private String actionUrl;
    private String attachmentFileName;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum NotificationType {
        GENERAL,
        SCHEME_UPDATE,
        SERVICE_ALERT,
        PAYMENT_REMINDER,
        MEETING_NOTICE,
        TRAINING_NOTICE,
        MARKET_UPDATE,
        WEATHER_ALERT,
        SYSTEM_MAINTENANCE,
        OTHER
    }

    public enum NotificationStatus {
        UNREAD,
        READ,
        ARCHIVED
    }
}
