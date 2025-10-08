package com.farmer.Form.Controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.farmer.Form.DTO.LoginRequest;
import com.farmer.Form.DTO.ResetPasswordDTO;
import com.farmer.Form.DTO.UserDTO;
// import com.farmer.Form.DTO.UserResponseDTO;
import com.farmer.Form.DTO.UserViewDTO;
import com.farmer.Form.DTO.UserRegistrationDTO;
import com.farmer.Form.Entity.User;
import com.farmer.Form.Entity.FPO;
import com.farmer.Form.Entity.FPOUser;
import com.farmer.Form.Repository.FPORepository;
import com.farmer.Form.Repository.FPOUserRepository;
import com.farmer.Form.Service.CountryStateCityService;
import com.farmer.Form.Service.EmailService;
import com.farmer.Form.Service.OtpService;
import com.farmer.Form.Service.UserService;
import com.farmer.Form.security.JwtUtil;
import com.farmer.Form.exception.UserNotApprovedException;
import com.farmer.Form.exception.UserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final OtpService otpService;
    private final EmailService emailService;
    private final CountryStateCityService countryService;
    private final FPOUserRepository fpoUserRepository;
    private final FPORepository fpoRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    // ✅ LOGIN
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        log.info("🔄 Login attempt for user: {}", request.getUserName());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword())
            );
            String token = jwtUtil.generateToken(authentication);
            // Get user details
            User user = userService.getUserByEmailOrPhone(request.getUserName());
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "Login successful");
            response.put("forcePasswordChange", user.isForcePasswordChange());
            
            // Add user information directly to login response
            response.put("user", new HashMap<String, Object>() {{
                put("id", user.getId());
                put("name", user.getName());
                put("email", user.getEmail());
                put("phoneNumber", user.getPhoneNumber());
                put("role", user.getRole().name());
                put("status", user.getStatus().name());
            }});
            
            return ResponseEntity.ok(response);
        } catch (UserNotApprovedException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Your account is not yet approved by admin.");
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(error);
        } catch (Exception e) {
            log.error("❌ Login failed for user: {} - Error: {}", request.getUserName(), e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(error);
        }
    }

    // ✅ FPO LOGIN
    @PostMapping("/fpo-login")
    @Transactional(readOnly = true)
    public ResponseEntity<?> fpoLogin(@RequestBody Map<String, String> req) {
        try {
            log.info("🔄 FPO Login attempt started");
            log.info("📧 Request data: {}", req);
            
            final String emailOrPhone = (req.get("email") != null && !req.get("email").isBlank())
                    ? req.get("email")
                    : req.get("userName");
            String password = req.get("password");
            
            log.info("📧 Email/Phone: {}", emailOrPhone);
            
            if (emailOrPhone == null || password == null) {
                log.warn("❌ Missing credentials - emailOrPhone: {}, password: {}", emailOrPhone, password != null ? "[PROVIDED]" : "[MISSING]");
                return ResponseEntity.badRequest().body(Map.of("message", "Email and password are required"));
            }
            
            // Try to find user by email first, then by phone
            Optional<FPOUser> userOpt = fpoUserRepository.findByEmail(emailOrPhone);
            if (userOpt.isEmpty()) {
                userOpt = fpoUserRepository.findByPhoneNumber(emailOrPhone);
            }
            
            if (userOpt.isEmpty()) {
                log.warn("❌ No FPO user found with email/phone: {}", emailOrPhone);
                return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
            }
            
            FPOUser user = userOpt.get();
            log.info("✅ FPO user found: ID={}, Email={}, Status={}", user.getId(), user.getEmail(), user.getStatus());
            
            // Check password
            if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                log.warn("❌ Password mismatch for user: {}", user.getEmail());
                return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
            }
            
            // Check if user is approved
            if (user.getStatus() != com.farmer.Form.Entity.UserStatus.APPROVED) {
                log.warn("❌ User not approved: {}", user.getEmail());
                return ResponseEntity.status(401).body(Map.of("message", "Account inactive. Contact FPO admin."));
            }
            
            FPO fpo = user.getFpo();
            log.info("🏢 FPO: ID={}, Name={}", fpo != null ? fpo.getId() : "null", fpo != null ? fpo.getFpoName() : "null");
            
            // Generate JWT token for FPO user
            String token = jwtUtil.generateTokenForFPOUser(user);
            log.info("🎫 Token generated successfully, length: {}", token != null ? token.length() : "null");
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "Login successful");
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("role", user.getRole().name());
            response.put("userType", user.getRole().name());
            response.put("fpoId", fpo != null ? fpo.getId() : null);
            response.put("fpoName", fpo != null ? fpo.getFpoName() : null);
            
            log.info("✅ FPO Login successful for user: {}", user.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ FPO Login error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("message", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    // Test endpoint to check FPO users
    @GetMapping("/test/fpo-users")
    public ResponseEntity<?> testFPOUsers() {
        try {
            java.util.List<FPOUser> users = fpoUserRepository.findAll();
            java.util.List<Map<String, Object>> userData = users.stream().map(user -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", user.getId());
                data.put("email", user.getEmail());
                data.put("phoneNumber", user.getPhoneNumber());
                data.put("firstName", user.getFirstName());
                data.put("lastName", user.getLastName());
                data.put("role", user.getRole());
                data.put("status", user.getStatus());
                data.put("fpoId", user.getFpo() != null ? user.getFpo().getId() : null);
                data.put("fpoName", user.getFpo() != null ? user.getFpo().getFpoName() : null);
                return data;
            }).collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                "totalUsers", users.size(),
                "users", userData
            ));
        } catch (Exception e) {
            log.error("❌ Test FPO users error: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ REGISTER
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            userService.registerUser(userDTO);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Registration successful - waiting for approval");
            return ResponseEntity.ok(response);
        } catch (UserAlreadyExistsException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            Map<String, String> error = new HashMap<>();
            String message = "Registration failed: ";
            if (e.getMessage().contains("phone_number")) {
                message += "Phone number already registered. Please use a different phone number.";
            } else if (e.getMessage().contains("email")) {
                message += "Email already registered. Please use a different email address.";
            } else {
                message += "Duplicate data detected. Please check your information.";
            }
            error.put("message", message);
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ✅ REGISTER WITH ROLE (for Employee and Farmer registrations)
    @PostMapping("/register-with-role")
    public ResponseEntity<Map<String, String>> registerUserWithRole(@Valid @RequestBody UserDTO userDTO) {
        try {
            // Validate that only EMPLOYEE or FARMER roles are allowed for this endpoint
            String role = userDTO.getRole().toUpperCase();
            if (!role.equals("EMPLOYEE") && !role.equals("FARMER")) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Only EMPLOYEE and FARMER roles are allowed for this registration endpoint");
                return ResponseEntity.badRequest().body(error);
            }
            
            userService.registerUserWithRole(userDTO);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Registration successful - waiting for approval");
            return ResponseEntity.ok(response);
        } catch (UserAlreadyExistsException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            Map<String, String> error = new HashMap<>();
            String message = "Registration failed: ";
            if (e.getMessage().contains("phone_number")) {
                message += "Phone number already registered. Please use a different phone number.";
            } else if (e.getMessage().contains("email")) {
                message += "Email already registered. Please use a different email address.";
            } else {
                message += "Duplicate data detected. Please check your information.";
            }
            error.put("message", message);
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ✅ Simplified registration for role-based registration
    @PostMapping("/register-simple")
    public ResponseEntity<Map<String, String>> registerUserSimple(@Valid @RequestBody UserRegistrationDTO userDTO) {
        // Validate that only EMPLOYEE or FARMER roles are allowed for this endpoint
        String role = userDTO.getRole().toUpperCase();
        if (!role.equals("EMPLOYEE") && !role.equals("FARMER")) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Only EMPLOYEE and FARMER roles are allowed for this registration endpoint");
            return ResponseEntity.badRequest().body(error);
        }
        
        // Convert UserRegistrationDTO to UserDTO
        UserDTO userDTOConverted = new UserDTO();
        userDTOConverted.setName(userDTO.getName());
        userDTOConverted.setEmail(userDTO.getEmail());
        userDTOConverted.setPhoneNumber(userDTO.getPhoneNumber());
        userDTOConverted.setDateOfBirth(userDTO.getDateOfBirth());
        userDTOConverted.setGender(userDTO.getGender());
        userDTOConverted.setRole(userDTO.getRole());
        
        userService.registerUserWithRole(userDTOConverted);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Registration successful - waiting for approval");
        return ResponseEntity.ok(response);
    }

    // ✅ Simple Captcha endpoint
    @GetMapping("/captcha")
    public ResponseEntity<Map<String, String>> generateCaptcha() {
        // Generate a simple 6-character captcha
        String captcha = generateSimpleCaptcha();
        Map<String, String> response = new HashMap<>();
        response.put("captcha", captcha);
        return ResponseEntity.ok(response);
    }

    private String generateSimpleCaptcha() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            captcha.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return captcha.toString();
    }

    // ✅ SEND OTP
    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestBody Map<String, String> request) {
        String emailOrPhone = request.get("emailOrPhone");
        if (emailOrPhone == null || emailOrPhone.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email or phone number is required."));
        }
        try {
            otpService.generateAndSendOtp(emailOrPhone.trim());
            return ResponseEntity.ok(Map.of("message", "OTP sent successfully to your registered email or phone."));
        } catch (IllegalStateException ex) {
            // Cooldown hit
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                                 .header("Retry-After", String.valueOf(otpService.getRemainingCooldown(emailOrPhone.trim())))
                                 .body(Map.of("message", ex.getMessage()));
        } catch (Exception ex) {
            // Transient error, but OTP might have been dispatched
            log.warn("Transient error sending OTP to {}: {}", emailOrPhone, ex.getMessage());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("message", "OTP request accepted. Delivery may take a few seconds."));
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody Map<String, String> body) {
        String emailOrPhone = body.get("emailOrPhone");
        if (emailOrPhone == null || emailOrPhone.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email or phone number is required"));
        }
        try {
            otpService.generateAndSendOtp(emailOrPhone);
            return ResponseEntity.ok(Map.of("message", "OTP re-sent successfully to " + emailOrPhone));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error resending OTP"));
        }
    }

    // ✅ VERIFY OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> request) {
        String emailOrPhone = request.get("emailOrPhone");
        String otp = request.get("otp");
        if (emailOrPhone == null || emailOrPhone.trim().isEmpty() ||
                otp == null || otp.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email/Phone and OTP are required.");
        }
        boolean verified = otpService.verifyOtp(emailOrPhone.trim(), otp.trim());
        return verified
                ? ResponseEntity.ok("OTP verified successfully.")
                : ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                                .body("Invalid or expired OTP.");
    }

    // ✅ FORGOT USER ID
    @PostMapping("/forgot-user-id")
    public ResponseEntity<String> forgotUserId(@RequestBody Map<String, String> request) {
        String emailOrPhone = request.get("emailOrPhone");
        if (emailOrPhone == null || emailOrPhone.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email or phone number is required.");
        }
        String result = userService.forgotUserId(emailOrPhone.trim());
        return ResponseEntity.ok(result);
    }

    // ✅ FORGOT PASSWORD
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        String emailOrPhone = request.get("emailOrPhone");
        if (emailOrPhone == null || emailOrPhone.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email or phone number is required."));
        }
        try {
            otpService.generateAndSendOtp(emailOrPhone.trim());
            // The emailService.sendOtpEmail is already called inside otpService.generateAndSendOtp
            return ResponseEntity.ok(Map.of("message", "Password reset OTP sent successfully."));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                                 .header("Retry-After", String.valueOf(otpService.getRemainingCooldown(emailOrPhone.trim())))
                                 .body(Map.of("message", ex.getMessage()));
        } catch (Exception ex) {
            log.warn("Transient error sending Forgot Password OTP to {}: {}", emailOrPhone, ex.getMessage());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("message", "Password reset OTP request accepted. Delivery may take a few seconds."));
        }
    }

    // ✅ RESET PASSWORD WITHOUT OTP
    @PostMapping("/reset-password/confirm")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordDTO request) {
        try {
            boolean success = userService.resetPasswordWithoutOtp(
                    request.getEmailOrPhone().trim(),
                    request.getNewPassword().trim(),
                    request.getConfirmPassword().trim()
            );
            return success
                    ? ResponseEntity.ok("Password changed successfully.")
                    : ResponseEntity.status(500).body("Password change failed.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("An error occurred: " + ex.getMessage());
        }
    }

    // 🌐 Location APIs
    @GetMapping("/countries")
    public ResponseEntity<String> getCountries() {
        return ResponseEntity.ok(countryService.getCountries());
    }

    @GetMapping("/states/{countryCode}")
    public ResponseEntity<String> getStates(@PathVariable String countryCode) {
        return ResponseEntity.ok(countryService.getStates(countryCode));
    }

    @GetMapping("/districts/{countryCode}/{stateCode}")
    public ResponseEntity<String> getDistricts(@PathVariable String countryCode,
                                               @PathVariable String stateCode) {
        return ResponseEntity.ok(countryService.getDistricts(countryCode, stateCode));
    }

    // ✅ Get all users (unfiltered)
    @GetMapping("/users/all")
    public ResponseEntity<List<UserViewDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ✅ Get users by role
    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<UserViewDTO>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    // ✅ Get users by status
    @GetMapping("/users/status/{status}")
    public ResponseEntity<List<UserViewDTO>> getUsersByStatus(@PathVariable String status) {
        return ResponseEntity.ok(userService.getUsersByStatus(status));
    }

    // ✅ Get users by role and status
    @GetMapping("/users")
    public ResponseEntity<List<UserViewDTO>> getUsersByRoleAndStatus(
            @RequestParam String role,
            @RequestParam String status) {
        return ResponseEntity.ok(userService.getUsersByRoleAndStatus(role, status));
    }

    // ✅ Get individual user by ID
    @GetMapping("/users/{id}")
    public ResponseEntity<UserViewDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // ✅ Get current user profile
    @GetMapping("/users/profile")
    public ResponseEntity<Map<String, Object>> getCurrentUserProfile(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            User user = userService.getUserByEmailOrPhone(userEmail);
            
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("name", user.getName());
            profile.put("email", user.getEmail());
            profile.put("phoneNumber", user.getPhoneNumber());
            profile.put("role", user.getRole().name());
            profile.put("status", user.getStatus().name());
            profile.put("forcePasswordChange", user.isForcePasswordChange());
            
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error fetching user profile: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // ✅ Update user status
    @PutMapping("/users/{id}/status")
    public ResponseEntity<String> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String newStatus = request.get("status");
        if (newStatus == null || newStatus.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Status is required");
        }
        userService.updateUserStatus(id, newStatus.trim());
        return ResponseEntity.ok("Status updated successfully");
    }

    // ✅ Approve user and assign role (Super Admin)
    @PutMapping("/users/{id}/approve")
    public ResponseEntity<String> approveAndAssignRole(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String role = request.get("role");
        if (role == null || role.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Role is required");
        }
        try {
            userService.approveAndAssignRole(id, role.trim());
            return ResponseEntity.ok("User approved and role assigned successfully. Credentials sent to user email.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // ✅ Reject user registration (Super Admin)
    @PutMapping("/users/{id}/reject")
    public ResponseEntity<String> rejectUserRegistration(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        if (reason == null || reason.trim().isEmpty()) {
            reason = "Rejected by Super Admin";
        }
        try {
            userService.updateUserStatus(id, "REJECTED");
            return ResponseEntity.ok("User registration rejected successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // 🔧 Test endpoint
    @GetMapping("/test")
    public String test() {
        return "This is a test endpoint.";
    }

    // 🔧 Debug endpoint to check user details
    @GetMapping("/debug-user/{email}")
    public ResponseEntity<Map<String, Object>> debugUser(@PathVariable String email) {
        try {
            User user = userService.getUserByEmailOrPhone(email);
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("id", user.getId());
            debugInfo.put("name", user.getName());
            debugInfo.put("email", user.getEmail());
            debugInfo.put("phoneNumber", user.getPhoneNumber());
            debugInfo.put("role", user.getRole().name());
            debugInfo.put("status", user.getStatus().name());
            debugInfo.put("hasPassword", user.getPassword() != null);
            debugInfo.put("forcePasswordChange", user.isForcePasswordChange());
            debugInfo.put("kycStatus", user.getKycStatus().name());
            return ResponseEntity.ok(debugInfo);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(404).body(error);
        }
    }

    // 🔧 Debug endpoint to check password matching
    @PostMapping("/debug-password-match")
    public ResponseEntity<Map<String, Object>> debugPasswordMatch(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            
            if (email == null || password == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Email and password are required");
                return ResponseEntity.badRequest().body(error);
            }
            
            User user = userService.getUserByEmailOrPhone(email);
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("email", user.getEmail());
            debugInfo.put("status", user.getStatus().name());
            debugInfo.put("hasPassword", user.getPassword() != null);
            debugInfo.put("forcePasswordChange", user.isForcePasswordChange());
            
            // Test password matching
            boolean passwordMatches = false;
            if (user.getPassword() != null) {
                passwordMatches = passwordEncoder.matches(password, user.getPassword());
            }
            
            debugInfo.put("passwordMatches", passwordMatches);
            debugInfo.put("providedPassword", password);
            debugInfo.put("storedPasswordHash", user.getPassword());
            
            return ResponseEntity.ok(debugInfo);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(404).body(error);
        }
    }

    // 🔧 Test login endpoint for debugging
    @PostMapping("/test-login")
    public ResponseEntity<Map<String, Object>> testLogin(@RequestBody Map<String, String> request) {
        try {
            String userName = request.get("userName");
            String password = request.get("password");
            
            if (userName == null || password == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "userName and password are required");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Try to authenticate
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userName, password)
            );
            
            // Get user details
            User user = userService.getUserByEmailOrPhone(userName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Test login successful");
            response.put("userName", userName);
            response.put("userRole", user.getRole().name());
            response.put("userName_display", user.getName());
            response.put("userEmail", user.getEmail());
            response.put("userStatus", user.getStatus().name());
            response.put("forcePasswordChange", user.isForcePasswordChange());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Test login failed: " + e.getMessage());
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(error);
        }
    }

    // 🔧 Test registration endpoint
    @GetMapping("/test-registration")
    public String testRegistration() {
        return "Registration endpoint is accessible.";
    }

    // Test OTP endpoint for debugging
    @PostMapping("/test-otp")
    public ResponseEntity<Map<String, String>> testOtp(@RequestBody Map<String, String> request) {
        String emailOrPhone = request.get("emailOrPhone");
        if (emailOrPhone == null || emailOrPhone.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email or phone number is required."));
        }
        
        try {
            String otp = otpService.generateAndSendOtp(emailOrPhone.trim());
            return ResponseEntity.ok(Map.of(
                "message", "OTP sent successfully",
                "otp", otp, // Only for testing - remove in production
                "emailOrPhone", emailOrPhone
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ Check email availability
    @PostMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmailAvailability(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        log.info("Checking email availability for: {}", email);
        
        if (email == null || email.trim().isEmpty()) {
            log.warn("Email is null or empty");
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }
        
        try {
            boolean exists = userService.emailExists(email.trim());
            log.info("Email {} exists: {}", email.trim(), exists);
            
            Map<String, Object> response = new HashMap<>();
            response.put("available", !exists);
            response.put("email", email.trim());
            if (exists) {
                response.put("message", "Email is already registered");
                log.info("Email {} is already registered", email.trim());
            } else {
                response.put("message", "Email is available");
                log.info("Email {} is available", email.trim());
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error checking email availability for {}: {}", email, e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error checking email availability: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}