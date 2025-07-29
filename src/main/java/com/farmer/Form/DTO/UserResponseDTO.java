package com.farmer.Form.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String name;
    private String phoneNumber;
    private String gender;
    private String role;
    private String dateOfBirth;
    private String email;
    private String status;
    private boolean forcePasswordChange;

    public static UserResponseDTO fromEntity(com.farmer.Form.Entity.User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .role(user.getRole().name())
                .dateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null)
                .email(user.getEmail())
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .forcePasswordChange(user.isForcePasswordChange())
                .build();
    }
}
 
