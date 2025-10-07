-- Fix user role assignment to ensure proper access to User & Roles management
-- This script ensures that the current user has the proper role assigned

-- First, let's check the current user and their role
SELECT 'Current Users and Roles:' as info;
SELECT id, name, email, role, status, created_at 
FROM users 
ORDER BY created_at DESC;

-- Check if the user with email 'projecthinfintiy@12.in' exists and has the right role
SELECT 'Checking specific user:' as info;
SELECT id, name, email, role, status 
FROM users 
WHERE email = 'projecthinfintiy@12.in';

-- Update the user to have SUPER_ADMIN role if they don't already have it
UPDATE users 
SET role = 'SUPER_ADMIN' 
WHERE email = 'projecthinfintiy@12.in' AND role != 'SUPER_ADMIN';

-- If the user doesn't exist, create them with SUPER_ADMIN role
INSERT INTO users (
    name, 
    email, 
    phone_number, 
    password, 
    date_of_birth, 
    gender, 
    role, 
    status, 
    kyc_status,
    created_at,
    updated_at
)
SELECT 
    'Super Admin',
    'projecthinfintiy@12.in',
    '9999999999',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFOSlwqJYvYqJYvYqJYvYq', -- This is a bcrypt hash for 'password'
    '1990-01-01',
    'MALE',
    'SUPER_ADMIN',
    'APPROVED',
    'APPROVED',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'projecthinfintiy@12.in'
);

-- Verify the user now has the correct role
SELECT 'Updated user role:' as info;
SELECT id, name, email, role, status, created_at 
FROM users 
WHERE email = 'projecthinfintiy@12.in';

-- Check all users with SUPER_ADMIN role
SELECT 'All SUPER_ADMIN users:' as info;
SELECT id, name, email, role, status, created_at 
FROM users 
WHERE role = 'SUPER_ADMIN';

-- Check if user_roles table has the required roles
SELECT 'Available roles in user_roles table:' as info;
SELECT id, role_name, description, is_active 
FROM user_roles 
ORDER BY id;

-- Ensure all default roles are active
UPDATE user_roles SET is_active = true WHERE role_name IN ('SUPER_ADMIN', 'ADMIN', 'EMPLOYEE', 'FARMER', 'FPO');

-- Final verification
SELECT 'Final verification - User with proper role:' as info;
SELECT u.id, u.name, u.email, u.role, u.status, u.created_at
FROM users u
WHERE u.email = 'projecthinfintiy@12.in' AND u.role = 'SUPER_ADMIN';
