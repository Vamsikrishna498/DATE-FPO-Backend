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
@RequestMapping("/api/fpo/{fpoId}/products")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FPOProductController {

    private final FPOService fpoService;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO') or hasRole('FARMER')")
    public ResponseEntity<List<FPOProductDTO>> getFPOProducts(@PathVariable Long fpoId) {
        List<FPOProductDTO> products = fpoService.getFPOProducts(fpoId);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO')")
    public ResponseEntity<FPOProductDTO> createProduct(
            @PathVariable Long fpoId,
            @Valid @RequestBody FPOProductCreationDTO productDTO) {
        log.info("Creating product for FPO with ID: {}", fpoId);
        FPOProductDTO createdProduct = fpoService.createProduct(fpoId, productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<FPOProductDTO> updateProduct(
            @PathVariable Long fpoId,
            @PathVariable Long productId,
            @Valid @RequestBody FPOProductCreationDTO productDTO) {
        log.info("Updating product {} in FPO with ID: {}", productId, fpoId);
        FPOProductDTO updatedProduct = fpoService.updateProduct(fpoId, productId, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @PutMapping("/{productId}/stock")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<Void> updateProductStock(
            @PathVariable Long fpoId,
            @PathVariable Long productId,
            @RequestParam Integer newStock) {
        log.info("Updating product {} stock to {} in FPO with ID: {}", productId, newStock, fpoId);
        fpoService.updateProductStock(fpoId, productId, newStock);
        return ResponseEntity.ok().build();
    }

    // Product Categories
    @GetMapping("/categories")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO') or hasRole('FARMER')")
    public ResponseEntity<List<FPOProductCategoryDTO>> getFPOProductCategories(@PathVariable Long fpoId) {
        List<FPOProductCategoryDTO> categories = fpoService.getFPOProductCategories(fpoId);
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/categories")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO')")
    public ResponseEntity<FPOProductCategoryDTO> createProductCategory(
            @PathVariable Long fpoId,
            @Valid @RequestBody FPOProductCategoryCreationDTO categoryDTO) {
        log.info("Creating product category for FPO with ID: {}", fpoId);
        FPOProductCategoryDTO createdCategory = fpoService.createProductCategory(fpoId, categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @PutMapping("/categories/{categoryId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<FPOProductCategoryDTO> updateProductCategory(
            @PathVariable Long fpoId,
            @PathVariable Long categoryId,
            @Valid @RequestBody FPOProductCategoryCreationDTO categoryDTO) {
        log.info("Updating product category {} in FPO with ID: {}", categoryId, fpoId);
        FPOProductCategoryDTO updatedCategory = fpoService.updateProductCategory(fpoId, categoryId, categoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/categories/{categoryId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO')")
    public ResponseEntity<Void> deleteProductCategory(
            @PathVariable Long fpoId,
            @PathVariable Long categoryId) {
        log.info("Deleting product category {} from FPO with ID: {}", categoryId, fpoId);
        fpoService.deleteProductCategory(fpoId, categoryId);
        return ResponseEntity.noContent().build();
    }
}
