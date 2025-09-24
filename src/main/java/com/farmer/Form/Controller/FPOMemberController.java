package com.farmer.Form.Controller;

import com.farmer.Form.DTO.*;
import com.farmer.Form.Entity.FPOMember;
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
@RequestMapping("/api/fpo/{fpoId}/members")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FPOMemberController {

    private final FPOService fpoService;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<FPOMemberDTO>> getFPOMembers(@PathVariable Long fpoId) {
        List<FPOMemberDTO> members = fpoService.getFPOMembers(fpoId);
        return ResponseEntity.ok(members);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO')")
    public ResponseEntity<FPOMemberDTO> addMemberToFPO(
            @PathVariable Long fpoId,
            @Valid @RequestBody FPOMemberCreationDTO memberDTO) {
        log.info("Adding member to FPO with ID: {}", fpoId);
        FPOMemberDTO addedMember = fpoService.addMemberToFPO(fpoId, memberDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedMember);
    }

    @DeleteMapping("/{memberId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO')")
    public ResponseEntity<Void> removeMemberFromFPO(
            @PathVariable Long fpoId,
            @PathVariable Long memberId) {
        log.info("Removing member {} from FPO with ID: {}", memberId, fpoId);
        fpoService.removeMemberFromFPO(fpoId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{memberId}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('FPO')")
    public ResponseEntity<Void> updateMemberStatus(
            @PathVariable Long fpoId,
            @PathVariable Long memberId,
            @RequestParam String status) {
        log.info("Updating member {} status to {} in FPO with ID: {}", memberId, status, fpoId);
        fpoService.updateMemberStatus(fpoId, memberId, FPOMember.MemberStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }
}
