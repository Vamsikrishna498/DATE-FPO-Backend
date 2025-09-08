package com.farmer.Form.DTO;

import com.farmer.Form.Entity.FPOBoardMember;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOBoardMemberCreationDTO {
    
    @NotBlank(message = "Board member name is required")
    private String name;

    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @Email(message = "Please provide a valid email address")
    private String email;

    @NotNull(message = "Board role is required")
    private FPOBoardMember.BoardRole role;

    private String address;
    private String qualification;
    private String experience;
    private String photoFileName;
    private String documentFileName;
    private String remarks;
    private FPOBoardMember.BoardMemberStatus status;
}
