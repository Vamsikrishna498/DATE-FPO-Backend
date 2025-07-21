package com.farmer.Form.Controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.farmer.Form.Service.OtpService;
 
@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class OtpController {
 
    private final OtpService otpService;
 
    // Generate and send OTP to the phone number
    @PostMapping("/generate")
    public ResponseEntity<String> generateOtp(@RequestParam String phoneNumber) {
        otpService.generateAndSendOtp(phoneNumber);
        return ResponseEntity.ok("OTP sent successfully to " + phoneNumber);
    }
 
    // Verify the OTP entered by the user
    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestParam String phoneNumber,
                                            @RequestParam String otp) {
        boolean isValid = otpService.verifyOtpCode(phoneNumber, otp);
        if (isValid) {
            return ResponseEntity.ok("OTP verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }
    }
}