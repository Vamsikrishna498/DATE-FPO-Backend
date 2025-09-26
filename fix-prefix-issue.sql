-- Check current state of code_formats table
SELECT 
    id,
    code_type,
    prefix,
    starting_number,
    current_number,
    description,
    is_active,
    created_at
FROM code_formats
ORDER BY code_type;

-- Update farmer prefix to match what you configured (DATE_FA)
UPDATE code_formats 
SET prefix = 'DATE_FA'
WHERE code_type = 'FARMER' AND is_active = true;

-- Update employee prefix to match what you configured (DATE_EMP)  
UPDATE code_formats 
SET prefix = 'DATE_EMP'
WHERE code_type = 'EMPLOYEE' AND is_active = true;

-- Set current numbers to start from 500/400
UPDATE code_formats 
SET current_number = 500 
WHERE code_type = 'FARMER' AND is_active = true;

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
