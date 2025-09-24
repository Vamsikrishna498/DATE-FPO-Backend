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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fpo/{fpoId}/users")
@RequiredArgsConstructor
public class FPOUserController {

    private final FPORepository fpoRepository;
    private final FPOUserRepository fpoUserRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<List<com.farmer.Form.Entity.FPOUser>> list(@PathVariable Long fpoId) {
        FPO fpo = fpoRepository.findById(fpoId).orElseThrow(() -> new ResourceNotFoundException("FPO not found with id: " + fpoId));
        return ResponseEntity.ok(fpoUserRepository.findByFpo(fpo));
    }

    public record CreateUserRequest(String email, String phoneNumber, String firstName, String lastName, String role, String password) {}

    @PostMapping
    @PreAuthorize("hasRole('FPO') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> create(@PathVariable Long fpoId, @Valid @RequestBody CreateUserRequest req) {
        try {
            FPO fpo = fpoRepository.findById(fpoId)
                    .orElseThrow(() -> new ResourceNotFoundException("FPO not found with id: " + fpoId));

            // Allow only FPO-scoped roles for creation from FPO Admin dashboard
            String roleUpper = req.role() == null ? "" : req.role().toUpperCase();
            if (!("FPO".equals(roleUpper) || "EMPLOYEE".equals(roleUpper) || "FARMER".equals(roleUpper))) {
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
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<Void> toggle(@PathVariable Long fpoId, @PathVariable Long userId, @RequestBody StatusRequest req) {
        var user = fpoUserRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus(req.active() ? UserStatus.APPROVED : UserStatus.REJECTED);
        fpoUserRepository.save(user);
        return ResponseEntity.noContent().build();
    }

    public record PasswordRequest(String password) {}

    @PutMapping("/{userId}/password")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<Void> updatePassword(@PathVariable Long fpoId, @PathVariable Long userId, @RequestBody PasswordRequest req) {
        var user = fpoUserRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        fpoUserRepository.save(user);
        return ResponseEntity.noContent().build();
    }
}


