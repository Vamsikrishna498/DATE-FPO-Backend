-- Fix Employee Code Format Issue in Staging Database
-- This script ensures the EMPLOYEE code format is properly configured

-- First, check if EMPLOYEE code format exists
SELECT * FROM code_formats WHERE code_type = 'EMPLOYEE';

-- If it doesn't exist, insert it
-- If it exists but is inactive, activate it
-- If it exists and is active, update it to ensure consistency

-- Option 1: Delete and recreate (safest for fixing issues)
DELETE FROM code_formats WHERE code_type = 'EMPLOYEE';

-- Insert the correct EMPLOYEE code format
INSERT INTO code_formats (
    code_type, 
    prefix, 
    starting_number, 
    current_number, 
    description, 
    is_active, 
    created_by, 
    updated_by, 
    created_at, 
    updated_at
) VALUES (
    'EMPLOYEE',
    'DATE_EMP',
    0,
    0,
    'Employee ID format with DATE_EMP prefix',
    true,
    'system',
    'system',
    NOW(),
    NOW()
);

-- Verify the format was created
SELECT * FROM code_formats WHERE code_type = 'EMPLOYEE';

-- Also verify FARMER format is correct
SELECT * FROM code_formats WHERE code_type = 'FARMER';

-- List all active code formats
SELECT code_type, prefix, current_number, is_active FROM code_formats WHERE is_active = true; 