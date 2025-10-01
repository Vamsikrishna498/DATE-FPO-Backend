-- Diagnostic and Fix Script for Employee ID Generation Issue in Staging
-- PostgreSQL Database Version
-- Run this script on your staging PostgreSQL database

-- ==========================================
-- STEP 1: Diagnose Current State
-- ==========================================

-- Check all code formats with ALL columns
SELECT id, code_type, prefix, starting_number, current_number, description, is_active, created_by, created_at
FROM code_formats
ORDER BY id;

-- Check specifically for EMPLOYEE format
SELECT * FROM code_formats WHERE code_type = 'EMPLOYEE';

-- Check specifically for FARMER format
SELECT * FROM code_formats WHERE code_type = 'FARMER';

-- ==========================================
-- STEP 2: Check if prefix is NULL or empty
-- ==========================================

SELECT 
    id, 
    code_type, 
    prefix,
    CASE 
        WHEN prefix IS NULL THEN 'NULL'
        WHEN prefix = '' THEN 'EMPTY'
        ELSE 'HAS_VALUE'
    END as prefix_status,
    is_active,
    current_number
FROM code_formats
WHERE code_type = 'EMPLOYEE';

-- ==========================================
-- STEP 3: Fix EMPLOYEE Code Format
-- ==========================================

-- Update the EMPLOYEE code format to ensure it has correct prefix
UPDATE code_formats 
SET 
    prefix = 'DATE_EMP',
    is_active = true,
    description = 'Employee ID format with DATE_EMP prefix',
    updated_by = 'system',
    updated_at = CURRENT_TIMESTAMP
WHERE code_type = 'EMPLOYEE';

-- If no rows were updated, insert a new one
INSERT INTO code_formats (code_type, prefix, starting_number, current_number, description, is_active, created_by, updated_by, created_at, updated_at)
SELECT 'EMPLOYEE'::VARCHAR, 'DATE_EMP', 0, 1000, 'Employee ID format with DATE_EMP prefix', true, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM code_formats WHERE code_type = 'EMPLOYEE');

-- ==========================================
-- STEP 4: Verify the fix
-- ==========================================

-- Check all code formats again
SELECT id, code_type, prefix, starting_number, current_number, is_active
FROM code_formats
ORDER BY code_type;

-- Verify EMPLOYEE format specifically (PostgreSQL syntax)
SELECT 
    id,
    code_type,
    prefix,
    starting_number,
    current_number,
    is_active,
    CONCAT(prefix, '-', LPAD((current_number + 1)::TEXT, 5, '0')) as next_id_preview
FROM code_formats
WHERE code_type = 'EMPLOYEE';

-- Alternative preview using PostgreSQL string formatting
SELECT 
    id,
    code_type,
    prefix,
    current_number,
    prefix || '-' || TO_CHAR(current_number + 1, 'FM00000') as next_id_preview
FROM code_formats
WHERE code_type = 'EMPLOYEE';

-- ==========================================
-- STEP 5: Check recent employees
-- ==========================================

-- Check the last 5 created employees and their employee_id
SELECT id, employee_id, first_name, last_name, email, created_at
FROM employees
ORDER BY created_at DESC
LIMIT 5;

-- ==========================================
-- STEP 6: Additional PostgreSQL-specific checks
-- ==========================================

-- Check table structure
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_name = 'code_formats'
ORDER BY ordinal_position;

-- Check for any constraints or triggers
SELECT conname, contype, pg_get_constraintdef(oid)
FROM pg_constraint
WHERE conrelid = 'code_formats'::regclass;

-- ==========================================
-- NOTES FOR POSTGRESQL:
-- ==========================================
-- After running this script:
-- 1. EMPLOYEE code format should have prefix = 'DATE_EMP'
-- 2. is_active should be true
-- 3. Next employee ID will be DATE_EMP-01001 (based on current_number = 1000)
-- 4. Restart your Spring Boot application after running this script
-- 5. Check application logs for messages like:
--    "🔍 Checking EMPLOYEE code format in database..."
--    "✅ EMPLOYEE code format is correct - Prefix: 'DATE_EMP'"
-- ========================================== 