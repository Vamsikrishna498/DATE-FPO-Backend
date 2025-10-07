package com.farmer.Form.Mapper;

import org.springframework.stereotype.Component;

import com.farmer.Form.DTO.UserDTO;
import com.farmer.Form.DTO.UserViewDTO;
import com.farmer.Form.Entity.User;

@Component
public class UserMapper {

    // Simple mapping without complex custom methods
    public User toEntity(UserDTO dto) {
        if (dto == null) return null;
        
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .gender(dto.getGender())
                .dateOfBirth(java.time.LocalDate.parse(dto.getDateOfBirth()))
                .role(com.farmer.Form.Entity.Role.valueOf(dto.getRole()))
                .status(com.farmer.Form.Entity.UserStatus.PENDING)
                .kycStatus(com.farmer.Form.Entity.KycStatus.PENDING)
                .build();
    }

    // Simple mapping to view DTO
    public UserViewDTO toViewDto(User user) {
        if (user == null) return null;
        
        UserViewDTO dto = new UserViewDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setGender(user.getGender());
        dto.setDateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null);
        dto.setRole(user.getRole() != null ? user.getRole().toString() : null);
        dto.setStatus(user.getStatus() != null ? user.getStatus().toString() : null);
        dto.setKycStatus(user.getKycStatus() != null ? user.getKycStatus().toString() : null);
        dto.setState(user.getState());
        dto.setDistrict(user.getDistrict());
        dto.setRegion(user.getRegion());
        dto.setAssignedEmployeeId(user.getAssignedEmployeeId());
        return dto;
    }
}
