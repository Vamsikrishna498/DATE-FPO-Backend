package com.farmer.Form.Service;
 
import com.farmer.Form.Entity.Employee;
import java.util.List;
 
public interface EmployeeService {
 
    // Save a new or updated employee
    Employee saveEmployee(Employee employee);
 
    // Get all employees
    List<Employee> getAllEmployees();
 
    // Get single employee by ID
    Employee getEmployeeById(Long id);
 
    // Delete employee by ID
    void deleteEmployee(Long id);

    // --- SUPER ADMIN RAW CRUD ---
    List<Employee> getAllEmployeesRaw();
    Employee getEmployeeRawById(Long id);
    Employee createEmployeeBySuperAdmin(Employee employee);
    Employee updateEmployeeBySuperAdmin(Long id, Employee employee);
    void deleteEmployeeBySuperAdmin(Long id);
    
    // Get employee by email
    Employee getEmployeeByEmail(String email);

    // Generate temporary password for employee login access
    String generateTempPassword();

    // Create or update user account for employee
    boolean createOrUpdateUserAccount(Employee employee, String tempPassword);

    // Assign farmers to employee
    int assignFarmersToEmployee(Long employeeId, List<Long> farmerIds);

    // Get available farmers for assignment (not assigned to any employee)
    List<com.farmer.Form.Entity.Farmer> getAvailableFarmersForAssignment(Long employeeId);

    // Get assigned farmers for employee
    List<com.farmer.Form.Entity.Farmer> getAssignedFarmersForEmployee(Long employeeId);
}
 