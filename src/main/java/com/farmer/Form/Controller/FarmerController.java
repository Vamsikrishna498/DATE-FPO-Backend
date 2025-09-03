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
    public ResponseEntity<FarmerDTO> createFarmer(
            @RequestPart("farmerDto") String farmerDtoJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestPart(value = "passbookPhoto", required = false) MultipartFile passbookPhoto,
            @RequestPart(value = "aadhaar", required = false) MultipartFile aadhaar,
            @RequestPart(value = "soilTestCertificate", required = false) MultipartFile soilTestCertificate
    ) throws JsonProcessingException {

        FarmerDTO farmerDTO = objectMapper.readValue(farmerDtoJson, FarmerDTO.class);

        FarmerDTO createdFarmer = service.createFarmer(
                farmerDTO,
                photo,
                passbookPhoto,
                aadhaar,
                soilTestCertificate
        );

        return ResponseEntity.ok(createdFarmer);
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
}
