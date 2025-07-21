package com.farmer.Form.Controller;


import org.springframework.web.bind.annotation.*;

import com.farmer.Form.DTO.FarmerDTO;
import com.farmer.Form.Service.FarmerService;

import java.util.List;
 
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
 
    private final FarmerService farmerService;
 
    public DashboardController(FarmerService farmerService) {
        this.farmerService = farmerService;
    }
 
    // ✅ Total farmer count for dashboard card
    @GetMapping("/count")
    public long getFarmerCount() {
        return farmerService.getFarmerCount();    
    }
 
    // ✅ List of all farmers
    @GetMapping("/farmers")
    public List<FarmerDTO> getAllFarmers() {
        return farmerService.getAllFarmers();
    }
 
    // ✅ Single farmer details
    @GetMapping("/farmers/{id}")
    public FarmerDTO getFarmerById(@PathVariable Long id) {
        return farmerService.getFarmerById(id);
    }
}
 