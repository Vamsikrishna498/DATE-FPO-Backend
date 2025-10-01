-- Fix EMPLOYEE Prefix in Staging PostgreSQL Database
-- This ensures the EMPLOYEE code format has a valid prefix

-- Step 1: Check what's currently in the database
SELECT id, code_type, prefix, current_number, is_active 
FROM code_formats 
ORDER BY code_type;

-- Step 2: Check specifically EMPLOYEE format and see if prefix is NULL/empty
SELECT 
    id, 
    code_type, 
    COALESCE(prefix, 'NULL') as prefix,
    current_number,
    is_active,
    CASE 
        WHEN prefix IS NULL THEN 'PREFIX_IS_NULL - NEEDS FIX'
        WHEN prefix = '' THEN 'PREFIX_IS_EMPTY - NEEDS FIX'
        ELSE 'PREFIX_OK'
    END as status
FROM code_formats 
WHERE code_type = 'EMPLOYEE';

-- Step 3: If prefix is NULL or empty, you have 2 options:

-- OPTION A: Set a temporary prefix (you can change it later via Personalization UI)
-- Uncomment and run this if prefix is NULL/empty:
/*
UPDATE code_formats 
SET 
    prefix = 'DATE_EMP',
    is_active = true,
    updated_at = CURRENT_TIMESTAMP
WHERE code_type = 'EMPLOYEE' AND (prefix IS NULL OR prefix = '');
*/

-- OPTION B: Use the same prefix as your local database
-- Check your local database first, then update this:
/*
UPDATE code_formats 
SET 
    prefix = 'YOUR_PREFIX_HERE',  -- Replace with your actual prefix from Personalization
    is_active = true,
    updated_at = CURRENT_TIMESTAMP
WHERE code_type = 'EMPLOYEE';
*/

-- Step 4: Verify the fix
SELECT id, code_type, prefix, current_number, is_active 
FROM code_formats 
WHERE code_type = 'EMPLOYEE';

-- After fixing, restart your Spring Boot application on staging 