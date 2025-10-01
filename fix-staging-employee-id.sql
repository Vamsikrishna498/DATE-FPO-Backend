-- Fix Employee ID Generation in Staging (PostgreSQL)
-- Run these commands in your staging database

-- Step 1: Check current EMPLOYEE format
SELECT id, code_type, prefix, current_number, is_active FROM code_formats WHERE code_type = 'EMPLOYEE';

-- Step 2: Update EMPLOYEE format with correct prefix
UPDATE code_formats 
SET 
    prefix = 'DATE_EMP',
    is_active = true,
    updated_at = CURRENT_TIMESTAMP
WHERE code_type = 'EMPLOYEE';

-- Step 3: If no rows updated, insert new EMPLOYEE format
INSERT INTO code_formats (code_type, prefix, starting_number, current_number, description, is_active, created_by, updated_by, created_at, updated_at)
SELECT 'EMPLOYEE'::VARCHAR, 'DATE_EMP', 0, 1000, 'Employee ID format with DATE_EMP prefix', true, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM code_formats WHERE code_type = 'EMPLOYEE');

-- Step 4: Verify the fix
SELECT id, code_type, prefix, current_number, is_active, 
       prefix || '-' || LPAD((current_number + 1)::TEXT, 5, '0') as next_employee_id
FROM code_formats 
WHERE code_type = 'EMPLOYEE';

-- After running this, restart your Spring Boot application 