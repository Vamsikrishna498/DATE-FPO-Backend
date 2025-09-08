package com.farmer.Form.Controller;

import com.farmer.Form.DTO.*;
import com.farmer.Form.Service.FPOService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fpo/{fpoId}/notifications")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FPONotificationController {

    private final FPOService fpoService;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO') or hasRole('FARMER')")
    public ResponseEntity<List<FPONotificationDTO>> getFPONotifications(@PathVariable Long fpoId) {
        List<FPONotificationDTO> notifications = fpoService.getFPONotifications(fpoId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO')")
    public ResponseEntity<FPONotificationDTO> createNotification(
            @PathVariable Long fpoId,
            @Valid @RequestBody FPONotificationCreationDTO notificationDTO) {
        log.info("Creating notification for FPO with ID: {}", fpoId);
        FPONotificationDTO createdNotification = fpoService.createNotification(fpoId, notificationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotification);
    }

    @PutMapping("/{notificationId}/read")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO') or hasRole('FARMER')")
    public ResponseEntity<Void> markNotificationAsRead(
            @PathVariable Long fpoId,
            @PathVariable Long notificationId) {
        log.info("Marking notification {} as read in FPO with ID: {}", notificationId, fpoId);
        fpoService.markNotificationAsRead(fpoId, notificationId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO')")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long fpoId,
            @PathVariable Long notificationId) {
        log.info("Deleting notification {} from FPO with ID: {}", notificationId, fpoId);
        fpoService.deleteNotification(fpoId, notificationId);
        return ResponseEntity.noContent().build();
    }
}
