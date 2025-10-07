package com.farmer.Form.Controller;

import com.farmer.Form.DTO.*;
import com.farmer.Form.Entity.FPOService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fpo/{fpoId}/services")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FPOServiceController {

    private static final Logger log = LoggerFactory.getLogger(FPOServiceController.class);
    private final com.farmer.Form.Service.FPOService fpoService;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO') or hasRole('FARMER')")
    public ResponseEntity<List<FPOServiceDTO>> getFPOServices(@PathVariable Long fpoId) {
        List<FPOServiceDTO> services = fpoService.getFPOServices(fpoId);
        return ResponseEntity.ok(services);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO') or hasRole('FARMER')")
    public ResponseEntity<FPOServiceDTO> createService(
            @PathVariable Long fpoId,
            @Valid @RequestBody FPOServiceCreationDTO serviceDTO) {
        log.info("Creating service for FPO with ID: {}", fpoId);
        FPOServiceDTO createdService = fpoService.createService(fpoId, serviceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdService);
    }

    @PutMapping("/{serviceId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<FPOServiceDTO> updateService(
            @PathVariable Long fpoId,
            @PathVariable Long serviceId,
            @Valid @RequestBody FPOServiceCreationDTO serviceDTO) {
        log.info("Updating service {} in FPO with ID: {}", serviceId, fpoId);
        FPOServiceDTO updatedService = fpoService.updateService(fpoId, serviceId, serviceDTO);
        return ResponseEntity.ok(updatedService);
    }

    @PutMapping("/{serviceId}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<Void> updateServiceStatus(
            @PathVariable Long fpoId,
            @PathVariable Long serviceId,
            @RequestParam String status) {
        log.info("Updating service {} status to {} in FPO with ID: {}", serviceId, status, fpoId);
        fpoService.updateServiceStatus(fpoId, serviceId, FPOService.ServiceStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{serviceId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<Void> removeService(
            @PathVariable Long fpoId,
            @PathVariable Long serviceId) {
        log.info("Deleting service {} from FPO with ID: {}", serviceId, fpoId);
        fpoService.removeService(fpoId, serviceId);
        return ResponseEntity.noContent().build();
    }
}
