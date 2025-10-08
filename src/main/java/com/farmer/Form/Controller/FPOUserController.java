package com.farmer.Form.Controller;

import com.farmer.Form.Entity.FPO;
import com.farmer.Form.Entity.Role;
import com.farmer.Form.Entity.UserStatus;
import com.farmer.Form.Repository.FPORepository;
import com.farmer.Form.Repository.FPOUserRepository;
import com.farmer.Form.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fpo/{fpoId}/users")
@RequiredArgsConstructor
public class FPOUserController {

    private final FPORepository fpoRepository;
    private final FPOUserRepository fpoUserRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        System.out.println("🔄 FPOUserController.test() called");
        return ResponseEntity.ok("FPOUserController is working!");
    }

    @GetMapping
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<List<com.farmer.Form.Entity.FPOUser>> list(@PathVariable Long fpoId) {
        System.out.println("🔄 FPOUserController.list() called with fpoId: " + fpoId);
        
        try {
            // First check if FPO exists
            FPO fpo = fpoRepository.findById(fpoId).orElseThrow(() -> new ResourceNotFoundException("FPO not found with id: " + fpoId));
            System.out.println("✅ Found FPO: " + fpo.getFpoName() + " (ID: " + fpo.getId() + ")");
            
            // Use direct FPO ID query instead of FPO entity
            List<com.farmer.Form.Entity.FPOUser> users = fpoUserRepository.findByFpoId(fpoId);
            System.out.println("📋 Found " + users.size() + " users for FPO " + fpoId);
            
            if (!users.isEmpty()) {
                System.out.println("📋 First user: " + users.get(0).getEmail());
            }
            
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            System.err.println("❌ Error in FPOUserController.list(): " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public record CreateUserRequest(String email, String phoneNumber, String firstName, String lastName, String role, String password) {}

    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('FPO') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> create(@PathVariable Long fpoId, @Valid @RequestBody CreateUserRequest req) {
        System.out.println("🔄 FPOUserController.create() called with fpoId: " + fpoId);
        System.out.println("📋 Create request: " + req.email() + ", " + req.firstName() + " " + req.lastName() + ", " + req.role());
        
        try {
            FPO fpo = fpoRepository.findById(fpoId)
                    .orElseThrow(() -> new ResourceNotFoundException("FPO not found with id: " + fpoId));
            System.out.println("✅ Found FPO for user creation: " + fpo.getFpoName() + " (ID: " + fpo.getId() + ")");

            // Determine requested role
            String roleUpper = req.role() == null ? "" : req.role().toUpperCase();

            // Who is calling?
            Authentication auth = SecurityContextHolder.getContext() != null
                    ? SecurityContextHolder.getContext().getAuthentication()
                    : null;
            boolean callerIsAdminOrSuperAdmin = auth != null && auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(a -> "ROLE_ADMIN".equals(a) || "ROLE_SUPER_ADMIN".equals(a));

            // Allow ADMIN creation only for ADMIN or SUPER_ADMIN callers; otherwise restrict to FPO/EMPLOYEE/FARMER
            boolean roleAllowed =
                    "FPO".equals(roleUpper) || "EMPLOYEE".equals(roleUpper) || "FARMER".equals(roleUpper)
                    || ("ADMIN".equals(roleUpper) && callerIsAdminOrSuperAdmin);

            if (!roleAllowed) {
                return ResponseEntity.status(403).body(java.util.Map.of(
                        "message", "Only FPO, EMPLOYEE or FARMER user types can be created from this screen"
                ));
            }

            com.farmer.Form.Entity.FPOUser user = com.farmer.Form.Entity.FPOUser.builder()
                    .fpo(fpo)
                    .firstName(req.firstName())
                    .lastName(req.lastName())
                    .email(req.email())
                    .phoneNumber(req.phoneNumber())
                    .passwordHash(passwordEncoder.encode(req.password()))
                    .role(Role.valueOf(roleUpper))
                    .status(UserStatus.APPROVED)
                    .build();
            var saved = fpoUserRepository.save(user);
            System.out.println("✅ User created successfully: " + saved.getEmail() + " (ID: " + saved.getId() + ")");
            return ResponseEntity.ok(saved);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                    "message", "Duplicate email or phone number for this FPO user",
                    "error", ex.getMessage()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                    "message", "Invalid role provided",
                    "error", ex.getMessage()
            ));
        }
    }

    public record UpdateUserRequest(String email, String phoneNumber, String firstName, String lastName, String role) {}

    @PutMapping("/{userId}")
    @Transactional
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<?> update(@PathVariable Long fpoId, @PathVariable Long userId, @Valid @RequestBody UpdateUserRequest req) {
        try {
            var user = fpoUserRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            // Update user fields
            if (req.email() != null && !req.email().trim().isEmpty()) {
                user.setEmail(req.email());
            }
            if (req.phoneNumber() != null && !req.phoneNumber().trim().isEmpty()) {
                user.setPhoneNumber(req.phoneNumber());
            }
            if (req.firstName() != null && !req.firstName().trim().isEmpty()) {
                user.setFirstName(req.firstName());
            }
            if (req.lastName() != null && !req.lastName().trim().isEmpty()) {
                user.setLastName(req.lastName());
            }
            if (req.role() != null && !req.role().trim().isEmpty()) {
                String roleUpper = req.role().toUpperCase();
                
                // Who is calling?
                Authentication auth = SecurityContextHolder.getContext() != null
                        ? SecurityContextHolder.getContext().getAuthentication()
                        : null;
                boolean callerIsAdminOrSuperAdmin = auth != null && auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(a -> "ROLE_ADMIN".equals(a) || "ROLE_SUPER_ADMIN".equals(a));

                // Allow ADMIN role only for ADMIN or SUPER_ADMIN callers
                boolean roleAllowed = "FPO".equals(roleUpper) || "EMPLOYEE".equals(roleUpper) || "FARMER".equals(roleUpper)
                        || ("ADMIN".equals(roleUpper) && callerIsAdminOrSuperAdmin);

                if (!roleAllowed) {
                    return ResponseEntity.status(403).body(java.util.Map.of(
                            "message", "Only FPO, EMPLOYEE or FARMER user types can be assigned from this screen"
                    ));
                }
                
                user.setRole(Role.valueOf(roleUpper));
            }
            
            var saved = fpoUserRepository.save(user);
            return ResponseEntity.ok(saved);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                    "message", "Duplicate email or phone number for this FPO user",
                    "error", ex.getMessage()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                    "message", "Invalid role provided",
                    "error", ex.getMessage()
            ));
        }
    }

    public record StatusRequest(boolean active) {}

    @PutMapping("/{userId}/status")
    @Transactional
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<Void> toggle(@PathVariable Long fpoId, @PathVariable Long userId, @RequestBody StatusRequest req) {
        var user = fpoUserRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus(req.active() ? UserStatus.APPROVED : UserStatus.REJECTED);
        fpoUserRepository.save(user);
        return ResponseEntity.noContent().build();
    }

    public record PasswordRequest(String password) {}

    @PutMapping("/{userId}/password")
    @Transactional
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<Void> updatePassword(@PathVariable Long fpoId, @PathVariable Long userId, @RequestBody PasswordRequest req) {
        var user = fpoUserRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        fpoUserRepository.save(user);
        return ResponseEntity.noContent().build();
    }
}


