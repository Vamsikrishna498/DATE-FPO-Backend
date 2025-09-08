package com.farmer.Form.DTO;

import com.farmer.Form.Entity.FPONotification;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPONotificationDTO {
    private Long id;
    private Long fpoId;
    private String fpoName;
    private String title;
    private String message;
    private FPONotification.NotificationType type;
    private FPONotification.NotificationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String priority;
    private String targetAudience;
    private LocalDateTime scheduledAt;
    private LocalDateTime readAt;
    private String readBy;
    private String actionUrl;
    private String attachmentFileName;
}
