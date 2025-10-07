package com.farmer.Form.Mapper;

import com.farmer.Form.DTO.FarmerDTO;
import com.farmer.Form.Entity.Farmer;

public class FarmerMapper {

    public static FarmerDTO toDto(Farmer farmer) {
        if (farmer == null) return null;

        // Debug logging
        System.out.println("🔍 FarmerMapper.toDto - Input Entity:");
        System.out.println("  - Contact Number: " + farmer.getContactNumber());
        System.out.println("  - First Name: " + farmer.getFirstName());
        System.out.println("  - Last Name: " + farmer.getLastName());

        return FarmerDTO.builder()
                .id(farmer.getId())
                .photoFileName(farmer.getPhotoFileName())
                .passbookFileName(farmer.getPassbookFileName())
                .documentFileName(farmer.getDocumentFileName())
                .soilTestCertificateFileName(farmer.getSoilTestCertificateFileName())
                // map all other fields...
                .salutation(farmer.getSalutation())
                .firstName(farmer.getFirstName())
                .middleName(farmer.getMiddleName())
                .lastName(farmer.getLastName())
                .dateOfBirth(farmer.getDateOfBirth())
                .gender(farmer.getGender())
                .fatherName(farmer.getFatherName())
                .contactNumber(farmer.getContactNumber())
                .alternativeRelationType(farmer.getAlternativeRelationType())
                .alternativeContactNumber(farmer.getAlternativeContactNumber())
                .nationality(farmer.getNationality())
                .email(farmer.getEmail())
                .country(farmer.getCountry())
                .state(farmer.getState())
                .district(farmer.getDistrict())
                .block(farmer.getBlock())
                .village(farmer.getVillage())
                .pincode(farmer.getPincode())
                .education(farmer.getEducation())
                .experience(farmer.getExperience())
                .cropPhoto(farmer.getCropPhoto())
                .currentSurveyNumber(farmer.getCurrentSurveyNumber())
                .currentLandHolding(farmer.getCurrentLandHolding())
                .currentGeoTag(farmer.getCurrentGeoTag())
                .currentCrop(farmer.getCurrentCrop())
                .currentNetIncome(farmer.getCurrentNetIncome())
                .currentSoilTest(farmer.getCurrentSoilTest())
                .proposedSurveyNumber(farmer.getProposedSurveyNumber())
                .proposedLandHolding(farmer.getProposedLandHolding())
                .proposedGeoTag(farmer.getProposedGeoTag())
                .proposedCrop(farmer.getProposedCrop())
                .proposedNetIncome(farmer.getProposedNetIncome())
                .proposedSoilTest(farmer.getProposedSoilTest())
                .proposedSoilTestCertificate(farmer.getProposedSoilTestCertificate())
                .currentWaterSource(farmer.getCurrentWaterSource())
                .currentDischargeLPH(farmer.getCurrentDischargeLPH())
                .currentSummerDischarge(farmer.getCurrentSummerDischarge())
                .currentBorewellLocation(farmer.getCurrentBorewellLocation())
                .proposedWaterSource(farmer.getProposedWaterSource())
                .proposedDischargeLPH(farmer.getProposedDischargeLPH())
                .proposedSummerDischarge(farmer.getProposedSummerDischarge())
                .proposedBorewellLocation(farmer.getProposedBorewellLocation())
                .bankName(farmer.getBankName())
                .accountNumber(farmer.getAccountNumber())
                .branchName(farmer.getBranchName())
                .ifscCode(farmer.getIfscCode())
                .documentType(farmer.getDocumentType())
                .documentNumber(farmer.getDocumentNumber())
                .portalRole(farmer.getPortalRole())
                .portalAccess(farmer.getPortalAccess())
                .build();
    }

    public static Farmer toEntity(FarmerDTO dto, String photoFileName, String passbookFileName, String documentFileName, String soilTestCertificateFileName) {
        if (dto == null) return null;

        // Debug logging
        System.out.println("🔍 FarmerMapper.toEntity - Input DTO:");
        System.out.println("  - Contact Number: " + dto.getContactNumber());
        System.out.println("  - First Name: " + dto.getFirstName());
        System.out.println("  - Last Name: " + dto.getLastName());

        return Farmer.builder()
                .id(dto.getId())
                .photoFileName(photoFileName)
                .passbookFileName(passbookFileName)
                .documentFileName(documentFileName)
                .soilTestCertificateFileName(soilTestCertificateFileName)
                // map all other fields...
                .salutation(dto.getSalutation())
                .firstName(dto.getFirstName())
                .middleName(dto.getMiddleName())
                .lastName(dto.getLastName())
                .dateOfBirth(dto.getDateOfBirth())
                .gender(dto.getGender())
                .fatherName(dto.getFatherName())
                .contactNumber(dto.getContactNumber())
                .alternativeRelationType(dto.getAlternativeRelationType())
                .alternativeContactNumber(dto.getAlternativeContactNumber())
                .nationality(dto.getNationality())
                .email(dto.getEmail())
                .country(dto.getCountry())
                .state(dto.getState())
                .district(dto.getDistrict())
                .block(dto.getBlock())
                .village(dto.getVillage())
                .pincode(dto.getPincode())
                .education(dto.getEducation())
                .experience(dto.getExperience())
                .cropPhoto(dto.getCropPhoto())
                .currentSurveyNumber(dto.getCurrentSurveyNumber())
                .currentLandHolding(dto.getCurrentLandHolding())
                .currentGeoTag(dto.getCurrentGeoTag())
                .currentCrop(dto.getCurrentCrop())
                .currentNetIncome(dto.getCurrentNetIncome())
                .currentSoilTest(dto.getCurrentSoilTest())
                .proposedSurveyNumber(dto.getProposedSurveyNumber())
                .proposedLandHolding(dto.getProposedLandHolding())
                .proposedGeoTag(dto.getProposedGeoTag())
                .proposedCrop(dto.getProposedCrop())
                .proposedNetIncome(dto.getProposedNetIncome())
                .proposedSoilTest(dto.getProposedSoilTest())
                .proposedSoilTestCertificate(dto.getProposedSoilTestCertificate())
                .currentWaterSource(dto.getCurrentWaterSource())
                .currentDischargeLPH(dto.getCurrentDischargeLPH())
                .currentSummerDischarge(dto.getCurrentSummerDischarge())
                .currentBorewellLocation(dto.getCurrentBorewellLocation())
                .proposedWaterSource(dto.getProposedWaterSource())
                .proposedDischargeLPH(dto.getProposedDischargeLPH())
                .proposedSummerDischarge(dto.getProposedSummerDischarge())
                .proposedBorewellLocation(dto.getProposedBorewellLocation())
                .bankName(dto.getBankName())
                .accountNumber(dto.getAccountNumber())
                .branchName(dto.getBranchName())
                .ifscCode(dto.getIfscCode())
                .documentType(dto.getDocumentType())
                .documentNumber(dto.getDocumentNumber())
                .portalRole(dto.getPortalRole())
                .portalAccess(dto.getPortalAccess())
                .build();
    }
}
