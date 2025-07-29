package com.farmer.Form.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;
 
@Data
public class UserRegistrationDTO {
 
    @NotBlank(message = "Name is required.")
    private String name;
 
    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits.")
    private String phoneNumber;
 
    @NotBlank(message = "Gender is required.")
    private String gender;
 
    @NotBlank(message = "Role is required.")
    private String role;
 
    @NotBlank(message = "Date of birth is required.")
    private String dateOfBirth; // Format: YYYY-MM-DD
 
    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    private String email;
}
 
