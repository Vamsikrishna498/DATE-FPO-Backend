package com.farmer.Form.Controller;

import com.farmer.Form.DTO.*;
import com.farmer.Form.Service.FPOService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fpo")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FPOController {

    private final FPOService fpoService;

    // FPO CRUD Operations
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<FPODTO> createFPO(@Valid @RequestBody FPOCreationDTO fpoCreationDTO) {
        log.info("Creating new FPO: {}", fpoCreationDTO.getFpoName());
        FPODTO createdFPO = fpoService.createFPO(fpoCreationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFPO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO')")
    public ResponseEntity<FPODTO> updateFPO(@PathVariable Long id, @Valid @RequestBody FPOCreationDTO fpoCreationDTO) {
        log.info("Updating FPO with ID: {}", id);
        FPODTO updatedFPO = fpoService.updateFPO(id, fpoCreationDTO);
        return ResponseEntity.ok(updatedFPO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO') or hasRole('FARMER')")
    public ResponseEntity<FPODTO> getFPOById(@PathVariable Long id) {
        FPODTO fpo = fpoService.getFPOById(id);
        return ResponseEntity.ok(fpo);
    }

    @GetMapping("/fpo-id/{fpoId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO') or hasRole('FARMER')")
    public ResponseEntity<FPODTO> getFPOByFpoId(@PathVariable String fpoId) {
        FPODTO fpo = fpoService.getFPOByFpoId(fpoId);
        return ResponseEntity.ok(fpo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteFPO(@PathVariable Long id) {
        log.info("Deleting FPO with ID: {}", id);
        fpoService.deleteFPO(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateFPO(@PathVariable Long id) {
        log.info("Deactivating FPO with ID: {}", id);
        fpoService.deactivateFPO(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Void> activateFPO(@PathVariable Long id) {
        log.info("Activating FPO with ID: {}", id);
        fpoService.activateFPO(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<Void> updateFPOStatus(@PathVariable Long id, @RequestParam String status) {
        log.info("Updating FPO status. id={}, status={}", id, status);
        if ("ACTIVE".equalsIgnoreCase(status)) {
            fpoService.activateFPO(id);
        } else if ("INACTIVE".equalsIgnoreCase(status)) {
            fpoService.deactivateFPO(id);
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    // FPO List and Search
    @PostMapping("/search")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<Page<FPODTO>> searchFPOs(@RequestBody FPOListRequestDTO request) {
        log.info("Searching FPOs with filters: {}", request);
        Page<FPODTO> fpos = fpoService.getAllFPOs(request);
        return ResponseEntity.ok(fpos);
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<Page<FPODTO>> getAllFPOs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String status) {
        
        FPOListRequestDTO request = FPOListRequestDTO.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .searchTerm(searchTerm)
                .state(state)
                .district(district)
                .status(status != null ? com.farmer.Form.Entity.FPO.FPOStatus.valueOf(status) : null)
                .build();
        
        Page<FPODTO> fpos = fpoService.getAllFPOs(request);
        return ResponseEntity.ok(fpos);
    }

    @GetMapping("/state/{state}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<FPODTO>> getFPOsByState(@PathVariable String state) {
        List<FPODTO> fpos = fpoService.getFPOsByState(state);
        return ResponseEntity.ok(fpos);
    }

    @GetMapping("/district/{district}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<FPODTO>> getFPOsByDistrict(@PathVariable String district) {
        List<FPODTO> fpos = fpoService.getFPOsByDistrict(district);
        return ResponseEntity.ok(fpos);
    }

    @GetMapping("/states")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<String>> getDistinctStates() {
        List<String> states = fpoService.getDistinctStates();
        return ResponseEntity.ok(states);
    }

    @GetMapping("/districts/{state}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<String>> getDistinctDistrictsByState(@PathVariable String state) {
        List<String> districts = fpoService.getDistinctDistrictsByState(state);
        return ResponseEntity.ok(districts);
    }

    // FPO Dashboard
    @GetMapping("/{id}/dashboard")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO')")
    public ResponseEntity<FPODashboardDTO> getFPODashboard(@PathVariable Long id) {
        FPODashboardDTO dashboard = fpoService.getFPODashboard(id);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/fpo-id/{fpoId}/dashboard")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO')")
    public ResponseEntity<FPODashboardDTO> getFPODashboardByFpoId(@PathVariable String fpoId) {
        FPODashboardDTO dashboard = fpoService.getFPODashboardByFpoId(fpoId);
        return ResponseEntity.ok(dashboard);
    }

    // FPO Statistics
    @GetMapping("/stats/total")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<Long> getTotalFPOsCount() {
        Long count = fpoService.getTotalFPOsCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/active")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<Long> getActiveFPOsCount() {
        Long count = fpoService.getActiveFPOsCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/status/{status}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Long> getFPOsCountByStatus(@PathVariable String status) {
        Long count = fpoService.getFPOsCountByStatus(com.farmer.Form.Entity.FPO.FPOStatus.valueOf(status));
        return ResponseEntity.ok(count);
    }
}
