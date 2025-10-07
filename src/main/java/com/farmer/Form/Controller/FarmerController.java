package com.farmer.Form.Controller;

import com.farmer.Form.DTO.FarmerDTO;
import com.farmer.Form.DTO.FarmerDashboardDTO;
import com.farmer.Form.DTO.PincodeApiResponse.PostOffice;
import com.farmer.Form.Service.AddressService;
import com.farmer.Form.Service.FarmerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/farmers")
public class FarmerController {

    private final FarmerService service;
    private final AddressService addressService;
    private final ObjectMapper objectMapper;

    public FarmerController(FarmerService service, AddressService addressService, ObjectMapper objectMapper) {
        this.service = service;
        this.addressService = addressService;
        this.objectMapper = objectMapper;
    }

    // ✅ Create farmer with multipart/form-data
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createFarmer(
            @RequestPart("farmerDto") String farmerDtoJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestPart(value = "passbookPhoto", required = false) MultipartFile passbookPhoto,
            @RequestPart(value = "aadhaar", required = false) MultipartFile aadhaar,
            @RequestPart(value = "soilTestCertificate", required = false) MultipartFile soilTestCertificate
    ) throws JsonProcessingException {

        try {
            FarmerDTO farmerDTO = objectMapper.readValue(farmerDtoJson, FarmerDTO.class);
            
            // Debug logging
            System.out.println("🔍 Backend received farmer data:");
            System.out.println("  - Contact Number: " + farmerDTO.getContactNumber());
            System.out.println("  - Contact Number Type: " + (farmerDTO.getContactNumber() != null ? farmerDTO.getContactNumber().getClass().getSimpleName() : "null"));
            System.out.println("  - First Name: " + farmerDTO.getFirstName());
            System.out.println("  - Last Name: " + farmerDTO.getLastName());

            FarmerDTO createdFarmer = service.createFarmer(
                    farmerDTO,
                    photo,
                    passbookPhoto,
                    aadhaar,
                    soilTestCertificate
            );
            
            // Debug logging for created farmer
            System.out.println("🔍 Created farmer data:");
            System.out.println("  - Contact Number: " + createdFarmer.getContactNumber());
            System.out.println("  - ID: " + createdFarmer.getId());

            return ResponseEntity.ok(createdFarmer);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Email already registered")) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
            throw e; // Re-throw other runtime exceptions
        }
    }

    // ✅ Get farmer by ID
    @GetMapping("/{id}")
    public ResponseEntity<FarmerDTO> getFarmerById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getFarmerById(id));
    }

    // ✅ Get all farmers
    @GetMapping
    public ResponseEntity<List<FarmerDTO>> getAllFarmers() {
        return ResponseEntity.ok(service.getAllFarmers());
    }

    // ✅ Update farmer with optional files
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FarmerDTO> updateFarmer(
            @PathVariable Long id,
            @RequestPart("farmerDto") String farmerDtoJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestPart(value = "passbookPhoto", required = false) MultipartFile passbookPhoto,
            @RequestPart(value = "aadhaar", required = false) MultipartFile aadhaar,
            @RequestPart(value = "soilTestCertificate", required = false) MultipartFile soilTestCertificate
    ) throws JsonProcessingException {

        FarmerDTO farmerDTO = objectMapper.readValue(farmerDtoJson, FarmerDTO.class);

        FarmerDTO updatedFarmer = service.updateFarmer(
                id,
                farmerDTO,
                photo,
                passbookPhoto,
                aadhaar,
                soilTestCertificate
        );

        return ResponseEntity.ok(updatedFarmer);
    }

    // ✅ Get address info by pincode (optional utility)
    @GetMapping("/address-by-pincode/{pincode}")
    public ResponseEntity<PostOffice> getAddressByPincode(@PathVariable String pincode) {
        PostOffice postOffice = addressService.fetchAddressByPincode(pincode);
        return postOffice != null ? ResponseEntity.ok(postOffice) : ResponseEntity.notFound().build();
    }

    // ✅ Get farmer dashboard data
    @GetMapping("/dashboard/{farmerId}")
    public ResponseEntity<FarmerDashboardDTO> getFarmerDashboard(@PathVariable Long farmerId) {
        return ResponseEntity.ok(service.getFarmerDashboardData(farmerId));
    }

    // ✅ Get farmer dashboard data by email
    @GetMapping("/dashboard/by-email")
    public ResponseEntity<FarmerDashboardDTO> getFarmerDashboardByEmail(@RequestParam String email) {
        return ResponseEntity.ok(service.getFarmerDashboardDataByEmail(email));
    }
    
    // ✅ Get farmer by email
    @GetMapping("/by-email")
    public ResponseEntity<FarmerDTO> getFarmerByEmail(@RequestParam String email) {
        return ResponseEntity.ok(service.getFarmerByEmail(email));
    }

    @PatchMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FarmerDTO> updateFarmerPhoto(
            @PathVariable Long id,
            @RequestPart(value = "photo", required = true) MultipartFile photo) {
        FarmerDTO updated = service.updateFarmerPhoto(id, photo);
        return ResponseEntity.ok(updated);
    }
}
