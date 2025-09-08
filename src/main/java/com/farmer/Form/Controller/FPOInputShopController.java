package com.farmer.Form.Controller;

import com.farmer.Form.DTO.FPOInputShopCreationDTO;
import com.farmer.Form.DTO.FPOInputShopDTO;
import com.farmer.Form.Service.FPOService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fpo/{fpoId}/input-shops")
@RequiredArgsConstructor
public class FPOInputShopController {

    private final FPOService fpoService;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<List<FPOInputShopDTO>> list(@PathVariable Long fpoId) {
        return ResponseEntity.ok(fpoService.getFPOInputShops(fpoId));
    }

    @PostMapping
    public ResponseEntity<FPOInputShopDTO> create(@PathVariable Long fpoId, @Valid @RequestBody FPOInputShopCreationDTO dto) {
        return ResponseEntity.ok(fpoService.createInputShop(fpoId, dto));
    }

    @PutMapping("/{shopId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<FPOInputShopDTO> update(@PathVariable Long fpoId, @PathVariable Long shopId, @Valid @RequestBody FPOInputShopCreationDTO dto) {
        return ResponseEntity.ok(fpoService.updateInputShop(fpoId, shopId, dto));
    }

    @DeleteMapping("/{shopId}")
    public ResponseEntity<Void> delete(@PathVariable Long fpoId, @PathVariable Long shopId) {
        fpoService.deleteInputShop(fpoId, shopId);
        return ResponseEntity.noContent().build();
    }
}


