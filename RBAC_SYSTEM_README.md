# Users & Roles Management (RBAC) System

## Overview

This document describes the comprehensive Role-Based Access Control (RBAC) system implemented for the Farmer Management System. The RBAC system provides fine-grained access control for different modules and operations within the application.

## Features

### 1. Role Management
- **Create, Edit, Delete, and Activate/Deactivate Roles**
- **Role Properties:**
  - Role Name (mandatory, unique)
  - Description (optional)
  - Module Access (multi-select)
  - Permissions per module (Add, View, Edit, Delete)
  - Active/Inactive status

### 2. User Role Assignment
- **Map users to roles** with dropdown selection
- **Support for role updates** (e.g., Employee → Admin)
- **Dynamic dashboard loading** based on assigned role
- **Role inheritance** and permission aggregation

### 3. Module-Based Access Control
- **Available Modules:**
  - Employee Management
  - Farmer Management
  - FPO Management
  - System Configuration
  - Analytics & Reports
  - User Management

### 4. Permission Levels
- **ADD/CREATE:** Create new records
- **VIEW/READ:** View existing records
- **EDIT/UPDATE:** Modify existing records
- **DELETE/REMOVE:** Remove records

## Database Schema

### Tables Created

#### 1. `roles`
```sql
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

#### 2. `role_modules`
```sql
CREATE TABLE role_modules (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    module_name VARCHAR(100) NOT NULL,
    can_add BOOLEAN NOT NULL DEFAULT false,
    can_view BOOLEAN NOT NULL DEFAULT false,
    can_edit BOOLEAN NOT NULL DEFAULT false,
    can_delete BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(role_id, module_name)
);
```

#### 3. `user_role_assignments`
```sql
CREATE TABLE user_role_assignments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    assigned_by VARCHAR(100),
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT true,
    UNIQUE(user_id, role_id)
);
```

## Backend Implementation

### Entities

#### 1. Role Entity
```java
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "role_name", nullable = false, unique = true, length = 100)
    private String roleName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RoleModule> roleModules;
    
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserRoleAssignment> userRoleAssignments;
}
```

#### 2. RoleModule Entity
```java
@Entity
@Table(name = "role_modules")
public class RoleModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    
    @Column(name = "module_name", nullable = false, length = 100)
    private String moduleName;
    
    @Column(name = "can_add", nullable = false)
    private Boolean canAdd = false;
    
    @Column(name = "can_view", nullable = false)
    private Boolean canView = false;
    
    @Column(name = "can_edit", nullable = false)
    private Boolean canEdit = false;
    
    @Column(name = "can_delete", nullable = false)
    private Boolean canDelete = false;
}
```

#### 3. UserRoleAssignment Entity
```java
@Entity
@Table(name = "user_role_assignments")
public class UserRoleAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    
    @Column(name = "assigned_by", length = 100)
    private String assignedBy;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
```

### Services

#### 1. RoleService
- `createRole(RoleCreationDTO)` - Create new role with modules and permissions
- `updateRole(Long id, RoleUpdateDTO)` - Update existing role
- `deleteRole(Long id)` - Delete role (with safety checks)
- `getAllRoles()` - Get all roles
- `getActiveRoles()` - Get only active roles
- `searchRoles(String searchTerm)` - Search roles by name/description

#### 2. UserRoleService
- `assignRoleToUser(UserRoleAssignmentCreationDTO)` - Assign role to user
- `updateUserRole(Long userId, Long newRoleId)` - Change user's role
- `removeRoleFromUser(Long userId, Long roleId)` - Remove role from user
- `getUserPermissions(Long userId)` - Get user's complete permissions
- `hasPermission(Long userId, String module, String permission)` - Check specific permission

#### 3. RBACService
- `hasPermission(String email, String module, String permission)` - Permission check by email
- `hasAnyPermission(String email, String module)` - Check any permission on module
- `getAccessibleModules(String email)` - Get modules user can access
- `isSuperAdmin(String email)` - Check if user is super admin
- `isAdmin(String email)` - Check if user is admin

### Controllers

#### 1. RoleController (`/api/roles`)
- `POST /api/roles` - Create role
- `GET /api/roles` - Get all roles
- `GET /api/roles/active` - Get active roles
- `GET /api/roles/{id}` - Get role by ID
- `PUT /api/roles/{id}` - Update role
- `DELETE /api/roles/{id}` - Delete role
- `POST /api/roles/{id}/activate` - Activate role
- `POST /api/roles/{id}/deactivate` - Deactivate role

#### 2. UserRoleController (`/api/user-roles`)
- `POST /api/user-roles/assign` - Assign role to user
- `PUT /api/user-roles/user/{userId}/role/{newRoleId}` - Update user role
- `DELETE /api/user-roles/assign/{userId}/{roleId}` - Remove role from user
- `GET /api/user-roles/user/{userId}` - Get user's roles
- `GET /api/user-roles/user/{userId}/permissions` - Get user permissions

#### 3. UsersRolesManagementController (`/api/users-roles-management`)
- Comprehensive endpoint for the frontend Users & Roles Management page
- Includes dashboard data, role management, and user assignments

## Frontend Implementation

### Components

#### 1. UsersRolesManagement (Main Page)
- Dashboard with statistics
- Tabbed interface for different functionalities
- Role management, user assignments, permission checking

#### 2. RoleManagement
- Display roles in card format
- Search and filter functionality
- Create, edit, delete, activate/deactivate roles

#### 3. RoleCard
- Visual representation of role with permissions
- Quick actions (edit, delete, activate/deactivate)
- Permission indicators with icons

#### 4. CreateRoleModal / EditRoleModal
- Form for creating/editing roles
- Module and permission selection matrix
- Validation and error handling

#### 5. UserRoleAssignment
- Table view of users and their roles
- Role assignment and updates
- Real-time permission updates

#### 6. PermissionCheck
- Interactive permission checking interface
- Visual permission matrix
- User-specific permission details

### Context & Hooks

#### 1. RBACContext
- Global state management for user permissions
- Permission checking functions
- Role validation utilities

#### 2. ProtectedComponent
- Wrapper component for permission-based rendering
- Support for module, permission, and role-based access
- Fallback rendering for access denied scenarios

### API Integration

#### RBAC API (`rbacAPI`)
```javascript
// Role Management
rbacAPI.getAllRoles()
rbacAPI.createRole(roleData)
rbacAPI.updateRole(id, roleData)
rbacAPI.deleteRole(id)

// User Role Assignments
rbacAPI.assignRoleToUser(assignmentData)
rbacAPI.updateUserRole(userId, newRoleId)
rbacAPI.getUserPermissions(userId)

// Permission Checks
rbacAPI.hasPermission(userId, moduleName, permission)
rbacAPI.getAccessibleModules(userId)
```

## Default Roles & Permissions

### Super Admin
- **Full access** to all modules
- **All permissions** (ADD, VIEW, EDIT, DELETE)
- Can manage other admins and system configuration

### Admin
- **Limited access** to most modules
- **No delete permissions** for critical modules
- Can manage users and basic configuration

### Manager
- **Read/Edit access** to assigned modules
- **No delete permissions**
- Can view analytics and manage farmers

### Employee
- **Limited access** to farmer and FPO modules
- **Basic permissions** (VIEW, some EDIT)
- Cannot access configuration or user management

### Farmer
- **Self-service access** only
- **VIEW and EDIT** own data
- Cannot access other users' data

### FPO Admin / FPO Employee
- **FPO-specific access**
- **Full/limited access** to FPO modules
- Cannot access system-wide configuration

## Usage Examples

### Backend Permission Check
```java
@RequirePermission(module = "EMPLOYEE", permission = "ADD")
@PostMapping("/employees")
public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employee) {
    // Only users with ADD permission on EMPLOYEE module can access this endpoint
    return ResponseEntity.ok(employeeService.create(employee));
}
```

### Frontend Permission Check
```jsx
import { useRBAC } from './contexts/RBACContext';
import ProtectedComponent from './components/rbac/ProtectedComponent';

function MyComponent() {
  const { hasPermission, isAdmin } = useRBAC();
  
  return (
    <div>
      {/* Method 1: Using hook */}
      {hasPermission('EMPLOYEE', 'ADD') && (
        <button>Add Employee</button>
      )}
      
      {/* Method 2: Using ProtectedComponent */}
      <ProtectedComponent module="EMPLOYEE" permission="EDIT">
        <button>Edit Employee</button>
      </ProtectedComponent>
      
      {/* Method 3: Role-based access */}
      <ProtectedComponent role="SUPER_ADMIN">
        <AdminPanel />
      </ProtectedComponent>
    </div>
  );
}
```

## Migration Instructions

### 1. Database Migration
```bash
# Run the database migration script
psql -U your_username -d your_database -f rbac_database_migration.sql
```

### 2. Backend Deployment
1. Deploy the new entities, services, and controllers
2. Update existing controllers to use RBAC annotations
3. Test API endpoints with different user roles

### 3. Frontend Deployment
1. Add the RBAC context provider to App.js
2. Deploy new components and pages
3. Update existing components to use permission checks
4. Add navigation links to Users & Roles Management

## Security Considerations

### 1. Authorization Checks
- All sensitive endpoints protected with `@RequirePermission`
- Frontend components wrapped with `ProtectedComponent`
- Database-level constraints prevent unauthorized access

### 2. Role Hierarchy
- Super Admin has highest privileges
- Role inheritance through permission aggregation
- No privilege escalation possible

### 3. Audit Trail
- All role assignments logged with `assignedBy` and `assignedAt`
- Role modifications tracked with `updatedBy` and `updatedAt`
- Permission changes require appropriate authorization

## Testing

### 1. Backend Testing
```java
@Test
public void testCreateRole() {
    RoleCreationDTO roleData = new RoleCreationDTO();
    roleData.setRoleName("Test Role");
    roleData.setModules(createTestModules());
    
    RoleDTO result = roleService.createRole(roleData);
    assertNotNull(result);
    assertEquals("Test Role", result.getRoleName());
}
```

### 2. Frontend Testing
```javascript
test('should render role management interface', () => {
  render(<UsersRolesManagement />);
  expect(screen.getByText('Users & Roles Management')).toBeInTheDocument();
  expect(screen.getByText('Create Role')).toBeInTheDocument();
});
```

## Troubleshooting

### Common Issues

1. **Permission Denied Errors**
   - Check user role assignments
   - Verify module permissions
   - Ensure role is active

2. **Database Constraint Violations**
   - Check for duplicate role names
   - Verify foreign key relationships
   - Ensure proper cascade settings

3. **Frontend Access Issues**
   - Verify RBAC context is properly initialized
   - Check user authentication state
   - Ensure permission checks are correct

## Future Enhancements

1. **Dynamic Role Creation** - Allow runtime role creation without code changes
2. **Permission Templates** - Predefined permission sets for common roles
3. **Role Inheritance** - Hierarchical role relationships
4. **Temporary Permissions** - Time-limited access grants
5. **Audit Dashboard** - Comprehensive access logging and reporting

## Support

For issues or questions regarding the RBAC system:
1. Check the troubleshooting section above
2. Review the API documentation
3. Examine the database schema and constraints
4. Contact the development team for assistance
