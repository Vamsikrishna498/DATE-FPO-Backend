package com.farmer.Form.DTO;

import com.farmer.Form.Entity.FPO;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOCreationDTO {
    
    @NotBlank(message = "FPO Name is required")
    private String fpoName;

    @NotBlank(message = "CEO/Contact Person Name is required")
    private String ceoName;

    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Village is required")
    private String village;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "State is required")
    private String state;

    @Pattern(regexp = "^\\d{6}$", message = "Pincode must be 6 digits")
    @NotBlank(message = "Pincode is required")
    private String pincode;

    @NotNull(message = "Join Date is required")
    private LocalDate joinDate;

    @NotNull(message = "Registration Type is required")
    private FPO.RegistrationType registrationType;

    @Min(value = 1, message = "Number of members must be at least 1")
    @NotNull(message = "Number of members is required")
    private Integer numberOfMembers;

    // Optional fields
    private String registrationNumber;
    private String panNumber;
    private String gstNumber;
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String branchName;
}
