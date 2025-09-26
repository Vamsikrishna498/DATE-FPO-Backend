-- Check what's in the code_formats table
SELECT 
    id,
    code_type,
    prefix,
    starting_number,
    current_number,
    description,
    is_active,
    created_by,
    created_at
FROM code_formats
ORDER BY code_type;

-- Check if the table exists and has data
SELECT COUNT(*) as total_formats FROM code_formats;

-- Check specifically for FARMER format
SELECT * FROM code_formats WHERE code_type = 'FARMER';

-- Check specifically for EMPLOYEE format  
SELECT * FROM code_formats WHERE code_type = 'EMPLOYEE';
