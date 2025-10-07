-- Create user_roles table and related tables for RBAC system
-- This script creates the necessary tables for the User & Roles management functionality

-- Create user_roles table
CREATE TABLE IF NOT EXISTS user_roles (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create role_modules table for allowed modules
CREATE TABLE IF NOT EXISTS role_modules (
    role_id BIGINT NOT NULL,
    module_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (role_id, module_name),
    FOREIGN KEY (role_id) REFERENCES user_roles(id) ON DELETE CASCADE
);

-- Create role_permissions table for permissions
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission VARCHAR(255) NOT NULL,
    PRIMARY KEY (role_id, permission),
    FOREIGN KEY (role_id) REFERENCES user_roles(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_user_roles_role_name ON user_roles(role_name);
CREATE INDEX IF NOT EXISTS idx_user_roles_is_active ON user_roles(is_active);
CREATE INDEX IF NOT EXISTS idx_role_modules_role_id ON role_modules(role_id);
CREATE INDEX IF NOT EXISTS idx_role_permissions_role_id ON role_permissions(role_id);

-- Insert default roles if they don't exist
INSERT INTO user_roles (role_name, description, is_active, created_by) 
VALUES 
    ('SUPER_ADMIN', 'Super Administrator with full system access', true, 'system'),
    ('ADMIN', 'Administrator with management access', true, 'system'),
    ('EMPLOYEE', 'Employee with limited access', true, 'system'),
    ('FARMER', 'Farmer with basic access', true, 'system'),
    ('FPO', 'FPO user with FPO-specific access', true, 'system')
ON CONFLICT (role_name) DO NOTHING;

-- Insert default modules for SUPER_ADMIN
INSERT INTO role_modules (role_id, module_name)
SELECT ur.id, module_name
FROM user_roles ur
CROSS JOIN (VALUES 
    ('Dashboard'),
    ('Registration'),
    ('Farmers'),
    ('Employees'),
    ('FPO'),
    ('Bulk Operations'),
    ('Personalization'),
    ('Configurations'),
    ('User & Roles')
) AS modules(module_name)
WHERE ur.role_name = 'SUPER_ADMIN'
ON CONFLICT (role_id, module_name) DO NOTHING;

-- Insert default modules for ADMIN
INSERT INTO role_modules (role_id, module_name)
SELECT ur.id, module_name
FROM user_roles ur
CROSS JOIN (VALUES 
    ('Dashboard'),
    ('Registration'),
    ('Farmers'),
    ('Employees'),
    ('Bulk Operations')
) AS modules(module_name)
WHERE ur.role_name = 'ADMIN'
ON CONFLICT (role_id, module_name) DO NOTHING;

-- Insert default modules for EMPLOYEE
INSERT INTO role_modules (role_id, module_name)
SELECT ur.id, module_name
FROM user_roles ur
CROSS JOIN (VALUES 
    ('Dashboard'),
    ('Farmers'),
    ('Employees')
) AS modules(module_name)
WHERE ur.role_name = 'EMPLOYEE'
ON CONFLICT (role_id, module_name) DO NOTHING;

-- Insert default modules for FARMER
INSERT INTO role_modules (role_id, module_name)
SELECT ur.id, module_name
FROM user_roles ur
CROSS JOIN (VALUES 
    ('Dashboard'),
    ('Farmers')
) AS modules(module_name)
WHERE ur.role_name = 'FARMER'
ON CONFLICT (role_id, module_name) DO NOTHING;

-- Insert default modules for FPO
INSERT INTO role_modules (role_id, module_name)
SELECT ur.id, module_name
FROM user_roles ur
CROSS JOIN (VALUES 
    ('Dashboard'),
    ('FPO'),
    ('Farmers')
) AS modules(module_name)
WHERE ur.role_name = 'FPO'
ON CONFLICT (role_id, module_name) DO NOTHING;

-- Insert default permissions for SUPER_ADMIN
INSERT INTO role_permissions (role_id, permission)
SELECT ur.id, permission
FROM user_roles ur
CROSS JOIN (VALUES 
    ('CREATE'),
    ('READ'),
    ('UPDATE'),
    ('DELETE'),
    ('APPROVE'),
    ('REJECT'),
    ('ASSIGN'),
    ('MANAGE_ROLES'),
    ('SYSTEM_CONFIG')
) AS permissions(permission)
WHERE ur.role_name = 'SUPER_ADMIN'
ON CONFLICT (role_id, permission) DO NOTHING;

-- Insert default permissions for ADMIN
INSERT INTO role_permissions (role_id, permission)
SELECT ur.id, permission
FROM user_roles ur
CROSS JOIN (VALUES 
    ('CREATE'),
    ('READ'),
    ('UPDATE'),
    ('DELETE'),
    ('APPROVE'),
    ('REJECT'),
    ('ASSIGN')
) AS permissions(permission)
WHERE ur.role_name = 'ADMIN'
ON CONFLICT (role_id, permission) DO NOTHING;

-- Insert default permissions for EMPLOYEE
INSERT INTO role_permissions (role_id, permission)
SELECT ur.id, permission
FROM user_roles ur
CROSS JOIN (VALUES 
    ('CREATE'),
    ('READ'),
    ('UPDATE')
) AS permissions(permission)
WHERE ur.role_name = 'EMPLOYEE'
ON CONFLICT (role_id, permission) DO NOTHING;

-- Insert default permissions for FARMER
INSERT INTO role_permissions (role_id, permission)
SELECT ur.id, permission
FROM user_roles ur
CROSS JOIN (VALUES 
    ('READ'),
    ('UPDATE')
) AS permissions(permission)
WHERE ur.role_name = 'FARMER'
ON CONFLICT (role_id, permission) DO NOTHING;

-- Insert default permissions for FPO
INSERT INTO role_permissions (role_id, permission)
SELECT ur.id, permission
FROM user_roles ur
CROSS JOIN (VALUES 
    ('CREATE'),
    ('READ'),
    ('UPDATE'),
    ('DELETE')
) AS permissions(permission)
WHERE ur.role_name = 'FPO'
ON CONFLICT (role_id, permission) DO NOTHING;

-- Display created tables and data
SELECT 'User Roles Created:' as info;
SELECT id, role_name, description, is_active FROM user_roles ORDER BY id;

SELECT 'Role Modules Created:' as info;
SELECT ur.role_name, rm.module_name 
FROM user_roles ur 
JOIN role_modules rm ON ur.id = rm.role_id 
ORDER BY ur.role_name, rm.module_name;

SELECT 'Role Permissions Created:' as info;
SELECT ur.role_name, rp.permission 
FROM user_roles ur 
JOIN role_permissions rp ON ur.id = rp.role_id 
ORDER BY ur.role_name, rp.permission;
