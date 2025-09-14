package com.farmer.Form.Test;

import com.farmer.Form.Entity.Farmer;
import com.farmer.Form.Entity.User;
import com.farmer.Form.Repository.FarmerRepository;
import com.farmer.Form.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

// @Component - Disabled to prevent automatic execution during startup
public class CreateMissingFarmer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FarmerRepository farmerRepository;

    @Override
    public void run(String... args) throws Exception {
        String email = "karthikarthik2912@gmail.com";
        
        System.out.println("🔍 Checking for user: " + email);
        
        // Check if user exists
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            System.out.println("❌ User not found: " + email);
            return;
        }
        
        System.out.println("✅ User found: " + user.getEmail() + " (Role: " + user.getRole() + ")");
        
        // Check if farmer exists
        Farmer existingFarmer = farmerRepository.findByEmail(email).orElse(null);
        if (existingFarmer != null) {
            System.out.println("✅ Farmer already exists: " + existingFarmer.getEmail());
            return;
        }
        
        System.out.println("❌ Farmer not found, creating...");
        
        // Create farmer record
        Farmer farmer = new Farmer();
        farmer.setEmail(email);
        farmer.setFirstName("Karthik");
        farmer.setLastName("Farmer");
        farmer.setContactNumber("9999999999");
        farmer.setDateOfBirth(LocalDate.of(1990, 1, 1));
        farmer.setGender("Male");
        farmer.setState("Karnataka");
        farmer.setDistrict("Bangalore");
        farmer.setVillage("Test Village");
        farmer.setPincode("560001");
        farmer.setKycStatus(Farmer.KycStatus.PENDING);
        farmer.setKycApproved(false);
        
        Farmer savedFarmer = farmerRepository.save(farmer);
        System.out.println("✅ Farmer created successfully: " + savedFarmer.getId() + " - " + savedFarmer.getEmail());
    }
}
