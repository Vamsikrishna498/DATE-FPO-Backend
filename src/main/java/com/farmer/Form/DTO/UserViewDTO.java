package com.farmer.Form.DTO;

import lombok.Data;

@Data
public class UserViewDTO {
    private String name;
    private String phoneNumber;
    private String gender;
    private String role;
    private String dateOfBirth;
    private String email;
    private String status;

    public String getFullName() {
        return name;
    }
}
