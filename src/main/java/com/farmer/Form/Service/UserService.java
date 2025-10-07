package com.farmer.Form.Service;



import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.farmer.Form.DTO.EmailServiceDTO;
import com.farmer.Form.DTO.UserDTO;
import com.farmer.Form.DTO.UserViewDTO;
import com.farmer.Form.Entity.User;
import com.farmer.Form.Mapper.UserMapper;
import com.farmer.Form.Repository.UserRepository;
import com.farmer.Form.exception.UserAlreadyExistsException;
import com.farmer.Form.exception.UserNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

    import com.farmer.Form.Entity.Role;
import com.farmer.Form.Service.IdCardService;
import com.farmer.Form.Repository.FarmerRepository;
import com.farmer.Form.Repository.EmployeeRepository;
 
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
 
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final OtpService otpService;
    private final IdCardService idCardService;
    private final FarmerRepository farmerRepository;
    private final EmployeeRepository employeeRepository;
 
    // ✅ Register a new user with OTP verification
    public User registerUser(UserDTO userDTO) {
        log.info("Registering user with email: {}", userDTO.getEmail());
 
        // Check for existing email
        userRepository.findByEmail(userDTO.getEmail()).ifPresent(user -> {
            log.warn("Email already registered: {}", userDTO.getEmail());
            throw new UserAlreadyExistsException("Email already registered: " + userDTO.getEmail());
        });
        
        // Check for existing phone number
        if (userDTO.getPhoneNumber() != null && !userDTO.getPhoneNumber().trim().isEmpty()) {
            userRepository.findByPhoneNumber(userDTO.getPhoneNumber()).ifPresent(user -> {
                log.warn("Phone number already registered: {}", userDTO.getPhoneNumber());
                throw new UserAlreadyExistsException("Phone number already registered: " + userDTO.getPhoneNumber());
            });
        }
 
        // Replace isVerified with isEmailOtpVerified
        if (!otpService.isEmailOtpVerified(userDTO.getEmail())) {
            throw new IllegalStateException("OTP not verified. Please verify before registering.");
        }
 
        User user = userMapper.toEntity(userDTO);
        
        // Handle password - set to null since registration doesn't require password
        // Super admin will assign temporary password upon approval
        user.setPassword(null);
        
        // Set role from DTO (convert to enum)
        user.setRole(com.farmer.Form.Entity.Role.valueOf(userDTO.getRole().toUpperCase()));
        user.setStatus(com.farmer.Form.Entity.UserStatus.PENDING);
        user.setForcePasswordChange(false);
        
        // Set KYC status to PENDING by default
        user.setKycStatus(com.farmer.Form.Entity.KycStatus.PENDING);
 
        User savedUser = userRepository.save(user);
        // Replace clearVerification with clearEmailVerification
        otpService.clearEmailVerification(userDTO.getEmail());
 
        try {
            emailService.sendRegistrationEmail(savedUser.getEmail(), savedUser.getName());
        } catch (Exception e) {
            log.error("Failed to send welcome email: {}", e.getMessage());
        }
 
        return savedUser;
    }

    // ✅ Register a new user with role (for Employee and Farmer registrations)
    public User registerUserWithRole(UserDTO userDTO) {
        log.info("Registering user with role: {} and email: {}", userDTO.getRole(), userDTO.getEmail());
 
        // Check for existing email
        userRepository.findByEmail(userDTO.getEmail()).ifPresent(user -> {
            log.warn("Email already registered: {}", userDTO.getEmail());
            throw new UserAlreadyExistsException("Email already registered: " + userDTO.getEmail());
        });
        
        // Check for existing phone number
        if (userDTO.getPhoneNumber() != null && !userDTO.getPhoneNumber().trim().isEmpty()) {
            userRepository.findByPhoneNumber(userDTO.getPhoneNumber()).ifPresent(user -> {
                log.warn("Phone number already registered: {}", userDTO.getPhoneNumber());
                throw new UserAlreadyExistsException("Phone number already registered: " + userDTO.getPhoneNumber());
            });
        }
 
        User user = userMapper.toEntity(userDTO);
        
        // Handle password - set to null since registration doesn't require password
        // Super admin will assign temporary password upon approval
        user.setPassword(null);
        
        // Set role from DTO (convert to enum)
        Role role = com.farmer.Form.Entity.Role.valueOf(userDTO.getRole().toUpperCase());
        user.setRole(role);
        user.setStatus(com.farmer.Form.Entity.UserStatus.PENDING);
        user.setForcePasswordChange(false);
        
        // Set KYC status to PENDING by default
        user.setKycStatus(com.farmer.Form.Entity.KycStatus.PENDING);
        
        // Handle optional fields with default values
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            user.setName("Not Provided");
        }
        if (user.getPhoneNumber() == null || user.getPhoneNumber().trim().isEmpty()) {
            user.setPhoneNumber("Not Provided");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            user.setEmail("Not Provided");
        }
 
        User savedUser = userRepository.save(user);
 
        try {
            emailService.sendRegistrationEmail(savedUser.getEmail(), savedUser.getName());
        } catch (Exception e) {
            log.error("Failed to send welcome email: {}", e.getMessage());
        }
 
        return savedUser;
    }
 
    // ✅ Forgot User ID
    public String forgotUserId(String emailOrPhone) {
        User user = userRepository.findByEmail(emailOrPhone)
                .or(() -> userRepository.findByPhoneNumber(emailOrPhone))
                .orElseThrow(() -> new UserNotFoundException("User not found with email or phone: " + emailOrPhone));
 
        String otp = otpService.generateAndSendOtp(emailOrPhone);
        try {
            emailService.sendOtpEmail(emailOrPhone,
                    "Your OTP to recover your User ID is: " + otp +
                    ". Use this OTP to verify your identity. Valid for 10 minutes.");
        } catch (Exception e) {
            log.error("Failed to send OTP email: {}", e.getMessage());
            throw new RuntimeException("Failed to send OTP.");
        }
 
        return "OTP sent to " + (emailOrPhone.contains("@") ? "email" : "phone") + ".";
    }
 
    // ✅ Reset password without OTP
    public boolean resetPasswordWithoutOtp(String emailOrPhone, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Password and Confirm Password do not match.");
        }
 
        User user = userRepository.findByEmail(emailOrPhone)
                .or(() -> userRepository.findByPhoneNumber(emailOrPhone))
                .orElseThrow(() -> new UserNotFoundException("User not found with email or phone: " + emailOrPhone));
 
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setForcePasswordChange(false); // Ensure flag is reset after password change
        userRepository.save(user);
        log.info("After password reset: user={}, forcePasswordChange={}", user.getEmail(), user.isForcePasswordChange());
 
        try {
            String subject = "Password Reset Confirmation";
            String body = "Dear " + user.getName() + ",\n\n"
                    + "Your password has been successfully reset.\n"
                    + "If this was not you, please contact support immediately.\n\n"
                    + "Regards,\nFarmer Management Team";
 
            emailService.sendEmail(EmailServiceDTO.builder()
                    .to(user.getEmail())
                    .subject(subject)
                    .body(body)
                    .build());
 
        } catch (Exception e) {
            log.error("Failed to send password reset email: {}", e.getMessage());
        }
 
        return true;
    }
 
    // ✅ Get all users
    public List<UserViewDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toViewDto)
                .collect(Collectors.toList());
    }
 
    // ✅ Get all users by role
    public List<UserViewDTO> getUsersByRole(String role) {
        return userRepository.findByRole(com.farmer.Form.Entity.Role.valueOf(role.toUpperCase())).stream()
                .map(userMapper::toViewDto)
                .collect(Collectors.toList());
    }
 
    // ✅ Get all users by status
    public List<UserViewDTO> getUsersByStatus(String status) {
        return userRepository.findByStatus(com.farmer.Form.Entity.UserStatus.valueOf(status.toUpperCase())).stream()
                .map(userMapper::toViewDto)
                .collect(Collectors.toList());
    }
 
    // ✅ Get users by role and status
    public List<UserViewDTO> getUsersByRoleAndStatus(String role, String status) {
        return userRepository.findByRoleAndStatus(
            com.farmer.Form.Entity.Role.valueOf(role.toUpperCase()),
            com.farmer.Form.Entity.UserStatus.valueOf(status.toUpperCase())
        ).stream()
                .map(userMapper::toViewDto)
                .collect(Collectors.toList());
    }
 
    // ✅ Get individual user by ID
    public UserViewDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        return userMapper.toViewDto(user);
    }
 
    // ✅ Update status (approve/reject user)
    public void updateUserStatus(Long id, String newStatus) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        user.setStatus(com.farmer.Form.Entity.UserStatus.valueOf(newStatus.toUpperCase()));
        userRepository.save(user);
        // Send rejection email if status is REJECTED
        if ("REJECTED".equalsIgnoreCase(newStatus)) {
            emailService.sendAccountRejectedEmail(user.getEmail(), user.getName());
        }
    }

    // ✅ Approve user and assign role (Super Admin)
    public void approveAndAssignRole(Long userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        if (user.getStatus() != com.farmer.Form.Entity.UserStatus.PENDING && 
            user.getStatus() != com.farmer.Form.Entity.UserStatus.REJECTED) {
            throw new IllegalStateException("User is not in a state that allows approval. Current status: " + user.getStatus());
        }
        String tempPassword = generateTempPassword();
        user.setRole(com.farmer.Form.Entity.Role.valueOf(role.toUpperCase()));
        user.setStatus(com.farmer.Form.Entity.UserStatus.APPROVED);
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setForcePasswordChange(true);
        userRepository.save(user);
        
        // Generate ID card immediately after approval
        try {
            if ("FARMER".equalsIgnoreCase(role)) {
                // Find the farmer record and generate ID card
                generateFarmerIdCard(userId);
            } else if ("EMPLOYEE".equalsIgnoreCase(role)) {
                // Find the employee record and generate ID card
                generateEmployeeIdCard(userId);
            }
            log.info("ID card generated successfully for user {} with role {}", userId, role);
        } catch (Exception e) {
            log.error("Failed to generate ID card for user {}: {}", userId, e.getMessage());
        }
        
        log.info("Attempting to send approval email to {}", user.getEmail());
        try {
            emailService.sendAccountApprovedEmail(user.getEmail(), user.getName(), tempPassword);
            log.info("Approval email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send approval email: {}", e.getMessage());
        }
    }

    // ✅ Approve user and assign role (Admin)
    public void approveAndAssignRoleByAdmin(Long userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        if (user.getStatus() != com.farmer.Form.Entity.UserStatus.PENDING && 
            user.getStatus() != com.farmer.Form.Entity.UserStatus.REJECTED) {
            throw new IllegalStateException("User is not in a state that allows approval. Current status: " + user.getStatus());
        }
        String tempPassword = generateTempPassword();
        user.setRole(com.farmer.Form.Entity.Role.valueOf(role.toUpperCase()));
        user.setStatus(com.farmer.Form.Entity.UserStatus.APPROVED);
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setForcePasswordChange(true);
        userRepository.save(user);
        
        // Generate ID card immediately after approval
        try {
            if ("FARMER".equalsIgnoreCase(role)) {
                // Find the farmer record and generate ID card
                generateFarmerIdCard(userId);
            } else if ("EMPLOYEE".equalsIgnoreCase(role)) {
                // Find the employee record and generate ID card
                generateEmployeeIdCard(userId);
            }
            log.info("Admin approval: ID card generated successfully for user {} with role {}", userId, role);
        } catch (Exception e) {
            log.error("Admin approval: Failed to generate ID card for user {}: {}", userId, e.getMessage());
        }
        
        log.info("Admin approval: Attempting to send approval email to {}", user.getEmail());
        try {
            emailService.sendAccountApprovedEmail(user.getEmail(), user.getName(), tempPassword);
            log.info("Admin approval: Approval email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Admin approval: Failed to send approval email: {}", e.getMessage());
        }
    }

    private String generateTempPassword() {
        // Simple temp password generator (customize as needed)
        return "Temp@" + (int)(Math.random() * 100000);
    }
    
    private void generateFarmerIdCard(Long userId) {
        try {
            // Find farmer by user ID (assuming farmer table has user_id or email reference)
            // You may need to adjust this based on your database relationship
            com.farmer.Form.Entity.Farmer farmer = findFarmerByUserId(userId);
            if (farmer != null) {
                idCardService.generateFarmerIdCard(farmer);
            } else {
                log.warn("Farmer not found for user ID: {}", userId);
            }
        } catch (Exception e) {
            log.error("Error generating farmer ID card for user {}: {}", userId, e.getMessage());
        }
    }
    
    private void generateEmployeeIdCard(Long userId) {
        try {
            // Find employee by user ID (assuming employee table has user_id or email reference)
            // You may need to adjust this based on your database relationship
            com.farmer.Form.Entity.Employee employee = findEmployeeByUserId(userId);
            if (employee != null) {
                idCardService.generateEmployeeIdCard(employee);
            } else {
                log.warn("Employee not found for user ID: {}", userId);
            }
        } catch (Exception e) {
            log.error("Error generating employee ID card for user {}: {}", userId, e.getMessage());
        }
    }
    
    private com.farmer.Form.Entity.Farmer findFarmerByUserId(Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null && user.getEmail() != null) {
                return farmerRepository.findByEmail(user.getEmail()).orElse(null);
            }
        } catch (Exception e) {
            log.error("Error finding farmer by user ID {}: {}", userId, e.getMessage());
        }
        return null;
    }
    
    private com.farmer.Form.Entity.Employee findEmployeeByUserId(Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null && user.getEmail() != null) {
                return employeeRepository.findByEmail(user.getEmail()).orElse(null);
            }
        } catch (Exception e) {
            log.error("Error finding employee by user ID {}: {}", userId, e.getMessage());
        }
        return null;
    }

    public User getUserByEmailOrPhone(String emailOrPhone) {
        return userRepository.findByEmail(emailOrPhone)
                .or(() -> userRepository.findByPhoneNumber(emailOrPhone))
                .orElseThrow(() -> new UserNotFoundException("User not found with email or phone: " + emailOrPhone));
    }

    public boolean resetPasswordAndClearForceFlag(String emailOrPhone, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Password and Confirm Password do not match.");
        }
        User user = getUserByEmailOrPhone(emailOrPhone);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setForcePasswordChange(false);
        userRepository.save(user);
        try {
            String subject = "Password Reset Confirmation";
            String body = "Dear " + user.getName() + ",\n\n"
                    + "Your password has been successfully reset.\n"
                    + "If this was not you, please contact support immediately.\n\n"
                    + "Regards,\nFarmer Management Team";
            emailService.sendEmail(EmailServiceDTO.builder()
                    .to(user.getEmail())
                    .subject(subject)
                    .body(body)
                    .build());
        } catch (Exception e) {
            log.error("Failed to send password reset email: {}", e.getMessage());
        }
        return true;
    }

    public List<User> getAllUsersRaw() {
        return userRepository.findAll();
    }

    public User getUserRawById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    public User createUserBySuperAdmin(User user) {
        // Only encode password if it's not null
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        return userRepository.save(user);
    }

    public User updateUserBySuperAdmin(Long id, User updatedUser) {
        User user = getUserRawById(id);
        user.setName(updatedUser.getName());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setEmail(updatedUser.getEmail());
        user.setRole(updatedUser.getRole());
        user.setStatus(updatedUser.getStatus());
        user.setForcePasswordChange(updatedUser.isForcePasswordChange());
        // ... update other fields as needed
        return userRepository.save(user);
    }

    public void deleteUserBySuperAdmin(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> getPendingUsersRaw() {
        return userRepository.findByStatus(com.farmer.Form.Entity.UserStatus.PENDING);
    }

    public List<User> getApprovedUsersRaw() {
        return userRepository.findByStatus(com.farmer.Form.Entity.UserStatus.APPROVED);
    }

    public List<User> getRejectedUsersRaw() {
        return userRepository.findByStatus(com.farmer.Form.Entity.UserStatus.REJECTED);
    }

    // ✅ Get users by role (raw format)
    public List<User> getUsersByRoleRaw(Role role) {
        return userRepository.findByRole(role);
    }

    // ✅ Get pending users by role (raw format)
    public List<User> getPendingUsersByRoleRaw(Role role) {
        return userRepository.findByRoleAndStatus(role, com.farmer.Form.Entity.UserStatus.PENDING);
    }

    public void resendOtp(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        otpService.generateAndSendOtp(email);
        emailService.sendEmail(EmailServiceDTO.builder()
            .to(email)
            .subject("Your OTP Code")
            .body("Your new OTP has been sent to this email.")
            .build());
    }

    // ✅ Check if email exists
    public boolean emailExists(String email) {
        log.info("Checking if email exists: {}", email);
        if (email == null || email.trim().isEmpty()) {
            log.info("Email is null or empty, returning false");
            return false;
        }
        boolean exists = userRepository.findByEmail(email.trim()).isPresent();
        log.info("Email {} exists: {}", email.trim(), exists);
        return exists;
    }
}