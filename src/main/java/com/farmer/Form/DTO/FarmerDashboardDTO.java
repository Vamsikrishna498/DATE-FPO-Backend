package com.farmer.Form.DTO;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmerDashboardDTO {
    // Personal Information
    private String salutation;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String fatherName;
    private String contactNumber;
    private String alternativeRelationType;
    private String alternativeContactNumber;
    private String nationality;
    
    // Address Information
    private String country;
    private String state;
    private String district;
    private String block;
    private String village;
    private String pincode;
    
    // Professional Information
    private String education;
    private String experience;
    
    // Current Crop Information
    private String currentSurveyNumber;
    private Double currentLandHolding;
    private String currentGeoTag;
    private String currentCrop;
    private Double currentNetIncome;
    private Boolean currentSoilTest;
    private String currentWaterSource;
    private String currentDischargeLPH;
    private String currentSummerDischarge;
    private String currentBorewellLocation;
    
    // Proposed Crop Information
    private String proposedSurveyNumber;
    private Double proposedLandHolding;
    private String proposedGeoTag;
    private String proposedCrop;
    private Double proposedNetIncome;
    private Boolean proposedSoilTest;
    private String proposedWaterSource;
    private String proposedDischargeLPH;
    private String proposedSummerDischarge;
    private String proposedBorewellLocation;
    
    // Bank Details
    private String bankName;
    private String accountNumber;
    private String branchName;
    private String ifscCode;
    
    // Document Information
    private String documentType;
    private String documentNumber;
    
    // Portal Information
    private String portalRole;
    private String portalAccess;
    
    // KYC Information
    private Boolean kycApproved;
    private String kycStatus;
    private String kycRejectionReason;
    private String kycReferBackReason;
    private LocalDate kycSubmittedDate;
    private LocalDate kycReviewedDate;
    private String kycReviewedBy;
    
    // Assigned Employee Information
    private EmployeeAssignmentDTO assignedEmployee;
    
    // File Information
    private String photoFileName;
    private String passbookFileName;
    private String documentFileName;
    private String soilTestCertificateFileName;
    
    // Statistics
    private Integer totalCrops;
    private Integer pendingDocuments;
    private Double totalBenefitsReceived;
    private LocalDate registrationDate;
    private LocalDate lastUpdatedDate;
}
