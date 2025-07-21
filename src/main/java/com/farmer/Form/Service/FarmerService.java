package com.farmer.Form.Service;


import org.springframework.web.multipart.MultipartFile;

import com.farmer.Form.DTO.FarmerDTO;
import com.farmer.Form.Entity.Farmer;
import com.farmer.Form.Repository.FarmerRepository;

import java.util.List;
 
public interface FarmerService {
 
    FarmerDTO createFarmer(FarmerDTO dto,
                           MultipartFile photo,
                           MultipartFile passbookPhoto,
                           MultipartFile aadhaar,
                           MultipartFile soilTestCertificate);
 
    FarmerDTO updateFarmer(Long id,
                           FarmerDTO dto,
                           MultipartFile photo,
                           MultipartFile passbookPhoto,
                           MultipartFile aadhaar,
                           MultipartFile soilTestCertificate);
 
    FarmerDTO updateFarmer(Long id, FarmerDTO dto);
 
    FarmerDTO getFarmerById(Long id);
 
    List<FarmerDTO> getAllFarmers();
 
    void deleteFarmer(Long id);
 
    long getFarmerCount(); // ✅ Add this

    List<Farmer> getAllFarmersRaw();

    Farmer getFarmerRawById(Long id);

    Farmer createFarmerBySuperAdmin(Farmer farmer);

    Farmer updateFarmerBySuperAdmin(Long id, Farmer updatedFarmer);

    void deleteFarmerBySuperAdmin(Long id);

    void assignFarmerToEmployee(Long farmerId, Long employeeId);

    List<Farmer> getFarmersByEmployeeEmail(String email);
    void approveKyc(Long farmerId);
}
 