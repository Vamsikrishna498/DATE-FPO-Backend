package com.farmer.Form.Service.Impl;
 
import com.farmer.Form.Entity.Employee;
import com.farmer.Form.Entity.Farmer;
import com.farmer.Form.Entity.User;
import com.farmer.Form.Entity.Role;
import com.farmer.Form.Entity.UserStatus;
import com.farmer.Form.Repository.EmployeeRepository;
import com.farmer.Form.Repository.FarmerRepository;
import com.farmer.Form.Repository.UserRepository;
import com.farmer.Form.Service.EmployeeService;
import com.farmer.Form.Service.EmailService;
import com.farmer.Form.Service.IdCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import com.farmer.Form.Service.FileStorageService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
 
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;
    private final FarmerRepository farmerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final IdCardService idCardService;
    private final FileStorageService fileStorageService;
 
    @Override
    public Employee saveEmployee(Employee updated) {
        // If ID is null, treat as a new employee
        if (updated.getId() == null) {
            Employee savedNew = repository.save(updated);
            // Generate Employee ID card immediately for newly created employees
            try {
                idCardService.generateEmployeeIdCard(savedNew);
            } catch (Exception e) {
                // Do not fail employee creation if ID card generation fails
                System.err.println("Failed to generate employee ID card: " + e.getMessage());
            }
            return savedNew;
        }
 
        // Fetch existing employee for update
        Employee existing = repository.findById(updated.getId()).orElse(null);
        if (existing == null) return null;
 
        // ✅ Only update fields that are not null
        if (updated.getSalutation() != null) existing.setSalutation(updated.getSalutation());
        if (updated.getFirstName() != null) existing.setFirstName(updated.getFirstName());
        if (updated.getMiddleName() != null) existing.setMiddleName(updated.getMiddleName());
        if (updated.getLastName() != null) existing.setLastName(updated.getLastName());
        if (updated.getGender() != null) existing.setGender(updated.getGender());
        if (updated.getNationality() != null) existing.setNationality(updated.getNationality());
        if (updated.getDob() != null) existing.setDob(updated.getDob());
        if (updated.getPhotoFileName() != null) existing.setPhotoFileName(updated.getPhotoFileName());
        if (updated.getContactNumber() != null) existing.setContactNumber(updated.getContactNumber());
        if (updated.getEmail() != null) existing.setEmail(updated.getEmail());
        if (updated.getRelationType() != null) existing.setRelationType(updated.getRelationType());
        if (updated.getRelationName() != null) existing.setRelationName(updated.getRelationName());
        if (updated.getAltNumber() != null) existing.setAltNumber(updated.getAltNumber());
        if (updated.getAltNumberType() != null) existing.setAltNumberType(updated.getAltNumberType());
        if (updated.getCountry() != null) existing.setCountry(updated.getCountry());
        if (updated.getState() != null) existing.setState(updated.getState());
        if (updated.getDistrict() != null) existing.setDistrict(updated.getDistrict());
        if (updated.getBlock() != null) existing.setBlock(updated.getBlock());
        if (updated.getVillage() != null) existing.setVillage(updated.getVillage());
        if (updated.getZipcode() != null) existing.setZipcode(updated.getZipcode());
        if (updated.getSector() != null) existing.setSector(updated.getSector());
        if (updated.getEducation() != null) existing.setEducation(updated.getEducation());
        if (updated.getExperience() != null) existing.setExperience(updated.getExperience());
        if (updated.getBankName() != null) existing.setBankName(updated.getBankName());
        if (updated.getAccountNumber() != null) existing.setAccountNumber(updated.getAccountNumber());
        if (updated.getBranchName() != null) existing.setBranchName(updated.getBranchName());
        if (updated.getIfscCode() != null) existing.setIfscCode(updated.getIfscCode());
        if (updated.getPassbookFileName() != null) existing.setPassbookFileName(updated.getPassbookFileName());
        if (updated.getDocumentType() != null) existing.setDocumentType(updated.getDocumentType());
        if (updated.getDocumentNumber() != null) existing.setDocumentNumber(updated.getDocumentNumber());
        if (updated.getDocumentFileName() != null) existing.setDocumentFileName(updated.getDocumentFileName());
        if (updated.getRole() != null) existing.setRole(updated.getRole());
        if (updated.getAccessStatus() != null) existing.setAccessStatus(updated.getAccessStatus());
 
        return repository.save(existing);
    }

    @Override
    public Employee updateEmployeePhoto(Long id, MultipartFile photo) {
        Employee existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        try {
            String photoFile = (photo != null && !photo.isEmpty())
                    ? fileStorageService.storeFile(photo, "photos")
                    : existing.getPhotoFileName();
            existing.setPhotoFileName(photoFile);
            Employee saved = repository.save(existing);
            try {
                List<com.farmer.Form.Entity.IdCard> cards = idCardService.getByHolderId(String.valueOf(saved.getId()));
                com.farmer.Form.Entity.IdCard latestActive = cards.stream()
                        .filter(c -> c.getStatus() == com.farmer.Form.Entity.IdCard.CardStatus.ACTIVE)
                        .findFirst().orElse(cards.isEmpty() ? null : cards.get(0));
                if (latestActive != null) {
                    idCardService.regenerateIdCard(latestActive.getCardId());
                }
            } catch (Exception ignore) {}
            return saved;
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to update employee photo", e);
        }
    }
 
    @Override
    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }
 
    @Override
    public Employee getEmployeeById(Long id) {
        return repository.findById(id).orElse(null);
    }
 
    @Override
    public void deleteEmployee(Long id) {
        repository.deleteById(id);
    }

    // --- SUPER ADMIN RAW CRUD ---
    @Override
    public List<Employee> getAllEmployeesRaw() {
        return repository.findAll();
    }

    @Override
    public Employee getEmployeeRawById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));
    }

    @Override
    public Employee createEmployeeBySuperAdmin(Employee employee) {
        Employee saved = repository.save(employee);
        // Generate Employee ID card using configured EMPLOYEE code format
        try {
            idCardService.generateEmployeeIdCard(saved);
        } catch (Exception e) {
            // Log and continue; do not break the flow
            System.err.println("Failed to generate employee ID card (super admin): " + e.getMessage());
        }
        return saved;
    }

    @Override
    public Employee updateEmployeeBySuperAdmin(Long id, Employee updated) {
        Employee employee = getEmployeeRawById(id);
        // Update fields as needed
        employee.setFirstName(updated.getFirstName());
        employee.setLastName(updated.getLastName());
        // ... update other fields as needed
        return repository.save(employee);
    }

    @Override
    public void deleteEmployeeBySuperAdmin(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Employee getEmployeeByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    @Override
    public String generateTempPassword() {
        // Generate a temporary password with format: Emp@123456
        Random random = new Random();
        int number = random.nextInt(900000) + 100000; // 6-digit number
        return "Emp@" + number;
    }

    @Override
    public boolean createOrUpdateUserAccount(Employee employee, String tempPassword) {
        try {
            // Check if user already exists
            User existingUser = userRepository.findByEmail(employee.getEmail()).orElse(null);
            
            if (existingUser != null) {
                // Update existing user
                existingUser.setPassword(passwordEncoder.encode(tempPassword));
                existingUser.setForcePasswordChange(true);
                existingUser.setStatus(UserStatus.APPROVED);
                userRepository.save(existingUser);
            } else {
                // Create new user account
                User newUser = User.builder()
                    .name(employee.getFirstName() + " " + employee.getLastName())
                    .email(employee.getEmail())
                    .phoneNumber(employee.getContactNumber())
                    .password(passwordEncoder.encode(tempPassword))
                    .role(Role.EMPLOYEE)
                    .status(UserStatus.APPROVED)
                    .forcePasswordChange(true)
                    .build();
                userRepository.save(newUser);
            }
            
            // Send email with login credentials
            try {
                emailService.sendAccountApprovedEmail(
                    employee.getEmail(), 
                    employee.getFirstName() + " " + employee.getLastName(), 
                    tempPassword
                );
            } catch (Exception e) {
                // Log error but don't fail the operation
                System.err.println("Failed to send email: " + e.getMessage());
            }
            
            // Note: ID card generation is now handled in UserService during user approval
            // No need to generate ID card here as it's done automatically when user is approved
            
            return true;
        } catch (Exception e) {
            System.err.println("Error creating user account: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int assignFarmersToEmployee(Long employeeId, List<Long> farmerIds) {
        int assignedCount = 0;
        
        Employee employee = repository.findById(employeeId).orElse(null);
        if (employee == null) {
            return 0;
        }
        
        for (Long farmerId : farmerIds) {
            try {
                Farmer farmer = farmerRepository.findById(farmerId).orElse(null);
                if (farmer != null) {
                    farmer.setAssignedEmployee(employee);
                    farmerRepository.save(farmer);
                    assignedCount++;
                }
            } catch (Exception e) {
                System.err.println("Error assigning farmer " + farmerId + ": " + e.getMessage());
            }
        }
        
        return assignedCount;
    }

    @Override
    public List<Farmer> getAvailableFarmersForAssignment(Long employeeId) {
        // Get farmers that are not assigned to any employee
        return farmerRepository.findByAssignedEmployeeIsNull();
    }

    @Override
    public List<Farmer> getAssignedFarmersForEmployee(Long employeeId) {
        // Get farmers assigned to this specific employee
        return farmerRepository.findByAssignedEmployee_Id(employeeId);
    }
}
 