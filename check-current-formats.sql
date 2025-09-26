-- Check current code formats in database
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

-- Check what the next ID should be
SELECT 
    code_type,
    prefix,
    current_number,
    CONCAT(prefix, '-', LPAD(current_number + 1, 5, '0')) as next_id
FROM code_formats
WHERE is_active = true;
