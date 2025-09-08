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
@RequestMapping("/api/fpo/{fpoId}/turnovers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FPOTurnoverController {

    private final FPOService fpoService;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<List<FPOTurnoverDTO>> getFPOTurnovers(@PathVariable Long fpoId) {
        List<FPOTurnoverDTO> turnovers = fpoService.getFPOTurnovers(fpoId);
        return ResponseEntity.ok(turnovers);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO')")
    public ResponseEntity<FPOTurnoverDTO> createTurnover(
            @PathVariable Long fpoId,
            @Valid @RequestBody FPOTurnoverCreationDTO turnoverDTO) {
        log.info("Creating turnover for FPO with ID: {}", fpoId);
        FPOTurnoverDTO createdTurnover = fpoService.createTurnover(fpoId, turnoverDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTurnover);
    }

    @PutMapping("/{turnoverId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<FPOTurnoverDTO> updateTurnover(
            @PathVariable Long fpoId,
            @PathVariable Long turnoverId,
            @Valid @RequestBody FPOTurnoverCreationDTO turnoverDTO) {
        log.info("Updating turnover {} in FPO with ID: {}", turnoverId, fpoId);
        FPOTurnoverDTO updatedTurnover = fpoService.updateTurnover(fpoId, turnoverId, turnoverDTO);
        return ResponseEntity.ok(updatedTurnover);
    }

    @DeleteMapping("/{turnoverId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO')")
    public ResponseEntity<Void> deleteTurnover(
            @PathVariable Long fpoId,
            @PathVariable Long turnoverId) {
        log.info("Deleting turnover {} from FPO with ID: {}", turnoverId, fpoId);
        fpoService.deleteTurnover(fpoId, turnoverId);
        return ResponseEntity.noContent().build();
    }
}
