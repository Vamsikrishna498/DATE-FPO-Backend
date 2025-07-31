package com.farmer.Form.DTO;

import lombok.Data;

@Data
public class UserViewDTO {
    private Long id;
    private String name;
    private String phoneNumber;
    private String gender;
    private String role;
    private String dateOfBirth;
    private String email;
    private String status;
    private String kycStatus;
    private String state;
    private String district;
    private String region;
    private Long assignedEmployeeId;
    private String createdAt;
    private String updatedAt;

    public String getFullName() {
        return name;
    }
}
