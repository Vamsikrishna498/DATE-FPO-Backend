-- Fix the current number to start from 500 so next generated will be 501
-- This matches your configuration where starting number is 500

-- Check current state
SELECT 
    code_type,
    prefix,
    starting_number,
    current_number,
    CONCAT(prefix, '-', LPAD(current_number + 1, 5, '0')) as next_generated_id
FROM code_formats
WHERE is_active = true
ORDER BY code_type;

-- Update farmer current number to 500 (so next will be 501)
UPDATE code_formats 
SET current_number = 500 
WHERE code_type = 'FARMER' AND is_active = true;

-- Update employee current number to 400 (so next will be 401)
UPDATE code_formats 
SET current_number = 400 
WHERE code_type = 'EMPLOYEE' AND is_active = true;

-- Verify the updates
SELECT 
    code_type,
    prefix,
    starting_number,
    current_number,
    CONCAT(prefix, '-', LPAD(current_number + 1, 5, '0')) as next_generated_id
FROM code_formats
WHERE is_active = true
ORDER BY code_type;
