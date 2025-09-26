-- Update current numbers to start from 501 for farmer and 401 for employee
-- This ensures the next generated IDs will be 501, 502, etc.

-- Update farmer code format to start from 500 (so next will be 501)
UPDATE code_formats 
SET current_number = 500 
WHERE code_type = 'FARMER' AND is_active = true;

-- Update employee code format to start from 400 (so next will be 401)  
UPDATE code_formats 
SET current_number = 400 
WHERE code_type = 'EMPLOYEE' AND is_active = true;

-- Verify the updates
SELECT 
    code_type,
    prefix,
    starting_number,
    current_number,
    CONCAT(prefix, '-', LPAD(current_number + 1, 5, '0')) as next_id
FROM code_formats
WHERE is_active = true
ORDER BY code_type;
