package com.farmer.Form.Controller;

import com.farmer.Form.DTO.*;
// Avoid name collision with entity FPOService by not importing and using FQN
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fpo/{fpoId}/board-members")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FPOBoardMemberController {

    private final com.farmer.Form.Service.FPOService fpoService;

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<List<FPOBoardMemberDTO>> getFPOBoardMembers(@PathVariable Long fpoId) {
        List<FPOBoardMemberDTO> boardMembers = fpoService.getFPOBoardMembers(fpoId);
        return ResponseEntity.ok(boardMembers);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<FPOBoardMemberDTO> addBoardMember(
            @PathVariable Long fpoId,
            @Valid @RequestBody FPOBoardMemberCreationDTO boardMemberDTO) {
        log.info("Adding board member to FPO with ID: {}", fpoId);
        FPOBoardMemberDTO addedBoardMember = fpoService.addBoardMember(fpoId, boardMemberDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedBoardMember);
    }

    @PutMapping("/{boardMemberId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<FPOBoardMemberDTO> updateBoardMember(
            @PathVariable Long fpoId,
            @PathVariable Long boardMemberId,
            @Valid @RequestBody FPOBoardMemberCreationDTO boardMemberDTO) {
        log.info("Updating board member {} in FPO with ID: {}", boardMemberId, fpoId);
        FPOBoardMemberDTO updatedBoardMember = fpoService.updateBoardMember(fpoId, boardMemberId, boardMemberDTO);
        return ResponseEntity.ok(updatedBoardMember);
    }

    @DeleteMapping("/{boardMemberId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('FPO')")
    public ResponseEntity<Void> removeBoardMember(
            @PathVariable Long fpoId,
            @PathVariable Long boardMemberId) {
        log.info("Removing board member {} from FPO with ID: {}", boardMemberId, fpoId);
        fpoService.removeBoardMember(fpoId, boardMemberId);
        return ResponseEntity.noContent().build();
    }
}
