package com.farmer.Form.DTO;

import com.farmer.Form.Entity.FPOBoardMember;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOBoardMemberDTO {
    private Long id;
    private Long fpoId;
    private String fpoName;
    private String name;
    private String phoneNumber;
    private String email;
    private FPOBoardMember.BoardRole role;
    private LocalDateTime appointedAt;
    private LocalDateTime updatedAt;
    private String address;
    private String qualification;
    private String experience;
    private String photoFileName;
    private String documentFileName;
    private String remarks;
    private FPOBoardMember.BoardMemberStatus status;
}
