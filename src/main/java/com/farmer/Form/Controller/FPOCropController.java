package com.farmer.Form.Controller;

import com.farmer.Form.DTO.*;
import com.farmer.Form.Entity.FPOCrop;
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
@RequestMapping("/api/fpo/{fpoId}/crops")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FPOCropController {

    private final FPOService fpoService;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO') or hasRole('FARMER')")
    public ResponseEntity<List<FPOCropDTO>> getFPOCrops(@PathVariable Long fpoId) {
        List<FPOCropDTO> crops = fpoService.getFPOCrops(fpoId);
        return ResponseEntity.ok(crops);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO') or hasRole('FARMER')")
    public ResponseEntity<FPOCropDTO> createCrop(
            @PathVariable Long fpoId,
            @Valid @RequestBody FPOCropCreationDTO cropDTO) {
        log.info("Creating crop for FPO with ID: {}", fpoId);
        FPOCropDTO createdCrop = fpoService.createCrop(fpoId, cropDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCrop);
    }

    @PutMapping("/{cropId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO') or hasRole('FARMER')")
    public ResponseEntity<FPOCropDTO> updateCrop(
            @PathVariable Long fpoId,
            @PathVariable Long cropId,
            @Valid @RequestBody FPOCropCreationDTO cropDTO) {
        log.info("Updating crop {} in FPO with ID: {}", cropId, fpoId);
        FPOCropDTO updatedCrop = fpoService.updateCrop(fpoId, cropId, cropDTO);
        return ResponseEntity.ok(updatedCrop);
    }

    @PutMapping("/{cropId}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<Void> updateCropStatus(
            @PathVariable Long fpoId,
            @PathVariable Long cropId,
            @RequestParam String status) {
        log.info("Updating crop {} status to {} in FPO with ID: {}", cropId, status, fpoId);
        fpoService.updateCropStatus(fpoId, cropId, FPOCrop.CropStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cropId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<Void> deleteCrop(
            @PathVariable Long fpoId,
            @PathVariable Long cropId) {
        log.info("Deleting crop {} from FPO with ID: {}", cropId, fpoId);
        fpoService.deleteCrop(fpoId, cropId);
        return ResponseEntity.noContent().build();
    }
}
