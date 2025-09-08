package com.farmer.Form.DTO;

import com.farmer.Form.Entity.FPONotification;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPONotificationCreationDTO {
    
    @NotBlank(message = "Notification title is required")
    private String title;

    @NotBlank(message = "Notification message is required")
    private String message;

    @NotNull(message = "Notification type is required")
    private FPONotification.NotificationType type;

    private String priority;
    private String targetAudience;
    private LocalDateTime scheduledAt;
    private String actionUrl;
    private String attachmentFileName;
}
