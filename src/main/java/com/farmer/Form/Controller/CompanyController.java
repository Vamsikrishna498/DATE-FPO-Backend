package com.farmer.Form.Controller;

import com.farmer.Form.DTO.CompanyCreationRequest;
import com.farmer.Form.Entity.Company;
import com.farmer.Form.Service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(companyService.getAll());
    }

    @PostMapping
    public ResponseEntity<Company> create(@RequestBody CompanyCreationRequest request) {
        Company company = request.toCompany();
        if (request.getAdminEmail() != null && !request.getAdminEmail().isBlank()) {
            return ResponseEntity.ok(companyService.createCompanyWithAdmin(company, request.getAdminEmail(), request.getAdminPassword()));
        }
        return ResponseEntity.ok(companyService.createOrUpdateCompany(company));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Company> update(@PathVariable Long id, @RequestBody Company company) {
        company.setId(id);
        return ResponseEntity.ok(companyService.createOrUpdateCompany(company));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Company> get(@PathVariable Long id) {
        Optional<Company> c = companyService.getCompany(id);
        return c.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok(Map.of("message", "Company deleted"));
    }

    @GetMapping("/branding/{tenant}")
    public ResponseEntity<Map<String, Object>> branding(@PathVariable String tenant) {
        return ResponseEntity.ok(companyService.getBranding(tenant));
    }

    // Convenience: derive branding by user email (to auto-apply after login)
    @GetMapping("/branding/by-email/{email}")
    public ResponseEntity<Map<String, Object>> brandingByEmail(@PathVariable String email) {
        return ResponseEntity.ok(companyService.getBrandingByEmail(email));
    }

    @PostMapping(value = "/{id}/logos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadLogos(
            @PathVariable Long id,
            @RequestPart(value = "dark", required = false) MultipartFile dark,
            @RequestPart(value = "light", required = false) MultipartFile light,
            @RequestPart(value = "smallDark", required = false) MultipartFile smallDark,
            @RequestPart(value = "smallLight", required = false) MultipartFile smallLight
    ) {
        try {
            Map<String, MultipartFile> files = new HashMap<>();
            files.put("dark", dark);
            files.put("light", light);
            files.put("smallDark", smallDark);
            files.put("smallLight", smallLight);
            Company updated = companyService.saveLogos(id, files);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}


