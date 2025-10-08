package com.farmer.Form.Service;

import com.farmer.Form.Entity.Company;
import com.farmer.Form.Repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final com.farmer.Form.Repository.UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    private static final String UPLOAD_DIR = "uploads/company-logos";

    public Company createOrUpdateCompany(Company company) {
        // Check for duplicates before saving
        validateCompanyUniqueness(company);
        return companyRepository.save(company);
    }

    public Company createCompanyWithAdmin(Company company, String adminEmail, String adminPassword) {
        // Check for duplicates before saving
        validateCompanyUniqueness(company);
        
        Company saved = companyRepository.save(company);
        if (adminEmail != null && !adminEmail.isBlank() && adminPassword != null && !adminPassword.isBlank()) {
            // Validate admin user details for duplicates
            validateAdminUserUniqueness(adminEmail, company.getPhone());
            
            com.farmer.Form.Entity.User admin = new com.farmer.Form.Entity.User();
            admin.setName(company.getName() + " Admin");
            admin.setEmail(adminEmail);
            admin.setPhoneNumber(company.getPhone() != null ? company.getPhone() : ("0000000000"));
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setDateOfBirth(java.time.LocalDate.of(1990,1,1));
            admin.setGender("OTHER");
            admin.setRole(com.farmer.Form.Entity.Role.ADMIN);
            admin.setStatus(com.farmer.Form.Entity.UserStatus.APPROVED);
            admin.setCompany(saved);
            userRepository.save(admin);
        }
        return saved;
    }

    private void validateCompanyUniqueness(Company company) {
        // Check if company name already exists (for new companies or when name is being changed)
        if (company.getId() == null || companyRepository.findById(company.getId()).map(c -> !c.getName().equals(company.getName())).orElse(true)) {
            if (companyRepository.existsByName(company.getName())) {
                throw new IllegalArgumentException("Company name '" + company.getName() + "' already exists. Please choose a different name.");
            }
        }

        // Check if short name already exists (for new companies or when short name is being changed)
        if (company.getId() == null || companyRepository.findById(company.getId()).map(c -> !c.getShortName().equals(company.getShortName())).orElse(true)) {
            if (companyRepository.existsByShortName(company.getShortName())) {
                throw new IllegalArgumentException("Company short name '" + company.getShortName() + "' already exists. Please choose a different short name.");
            }
        }

        // Check if email already exists (for new companies or when email is being changed)
        if (company.getId() == null || companyRepository.findById(company.getId()).map(c -> !c.getEmail().equals(company.getEmail())).orElse(true)) {
            if (companyRepository.existsByEmail(company.getEmail())) {
                throw new IllegalArgumentException("Company email '" + company.getEmail() + "' already exists. Please choose a different email.");
            }
        }
    }

    private void validateAdminUserUniqueness(String adminEmail, String adminPhone) {
        // Check if admin email already exists
        if (adminEmail != null && !adminEmail.isBlank()) {
            if (userRepository.existsByEmail(adminEmail)) {
                throw new IllegalArgumentException("Admin email '" + adminEmail + "' already exists. Please choose a different email.");
            }
        }

        // Check if admin phone number already exists (if provided)
        if (adminPhone != null && !adminPhone.isBlank() && !adminPhone.equals("0000000000")) {
            if (userRepository.existsByPhoneNumber(adminPhone)) {
                throw new IllegalArgumentException("Admin phone number '" + adminPhone + "' already exists. Please choose a different phone number.");
            }
        }
    }

    public Optional<Company> getCompany(Long id) {
        return companyRepository.findById(id);
    }

    public java.util.List<Company> getAll() {
        return companyRepository.findAll();
    }

    public Optional<Company> getByShortName(String shortName) {
        return companyRepository.findByShortName(shortName);
    }

    public Company saveLogos(Long companyId, Map<String, MultipartFile> files) throws IOException {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        // Ensure absolute path under the app working directory
        Path base = Paths.get(System.getProperty("user.dir")).resolve(UPLOAD_DIR).resolve(String.valueOf(companyId));
        Files.createDirectories(base);

        if (files.get("dark") != null && !files.get("dark").isEmpty()) {
            String filename = "logo-dark-" + System.currentTimeMillis() + getExt(files.get("dark"));
            Path p = base.resolve(filename);
            files.get("dark").transferTo(p.toFile());
            company.setLogoDark(p.getFileName().toString());
        }
        if (files.get("light") != null && !files.get("light").isEmpty()) {
            String filename = "logo-light-" + System.currentTimeMillis() + getExt(files.get("light"));
            Path p = base.resolve(filename);
            files.get("light").transferTo(p.toFile());
            company.setLogoLight(p.getFileName().toString());
        }
        if (files.get("smallDark") != null && !files.get("smallDark").isEmpty()) {
            String filename = "logo-small-dark-" + System.currentTimeMillis() + getExt(files.get("smallDark"));
            Path p = base.resolve(filename);
            files.get("smallDark").transferTo(p.toFile());
            company.setLogoSmallDark(p.getFileName().toString());
        }
        if (files.get("smallLight") != null && !files.get("smallLight").isEmpty()) {
            String filename = "logo-small-light-" + System.currentTimeMillis() + getExt(files.get("smallLight"));
            Path p = base.resolve(filename);
            files.get("smallLight").transferTo(p.toFile());
            company.setLogoSmallLight(p.getFileName().toString());
        }

        return companyRepository.save(company);
    }

    public Map<String, Object> getBranding(String tenant) {
        System.out.println("🏢 Getting branding for tenant: " + tenant);
        Optional<Company> optional = companyRepository.findByShortName(tenant);
        if (optional.isEmpty()) {
            System.out.println("❌ No company found for tenant: " + tenant + ", returning default branding");
            return Map.of(
                    "name", "Default",
                    "shortName", "default",
                    "logoDark", "/public/default/logo-dark.png",
                    "logoLight", "/public/default/logo-light.png",
                    "logoSmallDark", "/public/default/logo-small-dark.png",
                    "logoSmallLight", "/public/default/logo-small-light.png"
            );
        }
        Company c = optional.get();
        System.out.println("✅ Found company: " + c.getName() + " (ID: " + c.getId() + ")");
        System.out.println("📋 Company logo fields: dark=" + c.getLogoDark() + ", light=" + c.getLogoLight() + 
                          ", smallDark=" + c.getLogoSmallDark() + ", smallLight=" + c.getLogoSmallLight());
        
        Map<String, Object> out = new HashMap<>();
        out.put("id", c.getId());
        out.put("name", c.getName());
        out.put("shortName", c.getShortName());
        out.put("status", c.getStatus());
        out.put("logoDark", fileUrl(c.getId(), c.getLogoDark()));
        out.put("logoLight", fileUrl(c.getId(), c.getLogoLight()));
        out.put("logoSmallDark", fileUrl(c.getId(), c.getLogoSmallDark()));
        out.put("logoSmallLight", fileUrl(c.getId(), c.getLogoSmallLight()));
        
        System.out.println("🎨 Final branding data: " + out);
        return out;
    }

    public void deleteCompany(Long id) {
        // First, check if company exists
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with id: " + id));
        
        // Check if there are users associated with this company
        java.util.List<com.farmer.Form.Entity.User> users = userRepository.findByCompany(company);
        if (!users.isEmpty()) {
            // Option 1: Delete all users associated with the company
            // This is the most thorough approach
            userRepository.deleteAll(users);
        }
        
        // Now delete the company
        companyRepository.deleteById(id);
    }

    private String getExt(MultipartFile file) {
        String original = file.getOriginalFilename();
        if (original == null || !original.contains(".")) return ".bin";
        return original.substring(original.lastIndexOf('.'));
    }


    private String fileUrl(Long companyId, String rel) {
        if (rel == null) return null;
        String url = "/uploads/company-logos/" + companyId + "/" + Paths.get(rel).getFileName();
        System.out.println("🔗 Generated logo URL: " + url + " (companyId=" + companyId + ", rel=" + rel + ")");
        return url;
    }

    public Map<String, Object> getBrandingByEmail(String email) {
        if (email == null || email.isBlank()) {
            return getBranding("default");
        }
        return userRepository.findByEmail(email)
                .map(u -> {
                    Company c = u.getCompany();
                    if (c == null) return getBranding("default");
                    return getBranding(c.getShortName());
                })
                .orElseGet(() -> getBranding("default"));
    }
}


