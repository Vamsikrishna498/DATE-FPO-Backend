-- Minimal setup for user roles tables
-- This creates the essential tables needed for the roles management to work

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

-- Create role_modules table
CREATE TABLE IF NOT EXISTS role_modules (
    role_id BIGINT NOT NULL,
    module_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (role_id, module_name),
    FOREIGN KEY (role_id) REFERENCES user_roles(id) ON DELETE CASCADE
);

-- Create role_permissions table
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission VARCHAR(255) NOT NULL,
    PRIMARY KEY (role_id, permission),
    FOREIGN KEY (role_id) REFERENCES user_roles(id) ON DELETE CASCADE
);

-- Insert basic roles
INSERT INTO user_roles (role_name, description, is_active, created_by) 
VALUES 
    ('SUPER_ADMIN', 'Super Administrator with full system access', true, 'system'),
    ('ADMIN', 'Administrator with management access', true, 'system'),
    ('EMPLOYEE', 'Employee with limited access', true, 'system'),
    ('FARMER', 'Farmer with basic access', true, 'system'),
    ('FPO', 'FPO user with FPO-specific access', true, 'system')
ON CONFLICT (role_name) DO NOTHING;

-- Verify tables were created
SELECT 'Tables created successfully!' as status;
SELECT COUNT(*) as role_count FROM user_roles;
