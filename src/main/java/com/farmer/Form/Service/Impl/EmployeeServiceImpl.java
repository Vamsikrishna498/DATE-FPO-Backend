package com.farmer.Form.Service.Impl;
 
import com.farmer.Form.Entity.Employee;
import com.farmer.Form.Repository.EmployeeRepository;
import com.farmer.Form.Service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
 
import java.util.List;
 
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
 
    private final EmployeeRepository repository;
 
    @Override
    public Employee saveEmployee(Employee updated) {
        // If ID is null, treat as a new employee
        if (updated.getId() == null) {
            return repository.save(updated);
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
        return repository.save(employee);
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
}
 