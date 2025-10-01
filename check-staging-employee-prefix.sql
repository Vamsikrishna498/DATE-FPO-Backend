-- PostgreSQL - Quick Fix for Employee Prefix in Staging
-- Run this ONLY if your staging database has NULL or empty prefix for EMPLOYEE

-- Step 1: Check current status
SELECT id, code_type, prefix, is_active, current_number FROM code_formats ORDER BY id;

-- Step 2: Fix EMPLOYEE prefix (replace 'DATE_EMP' with whatever you set in Personalization)
UPDATE code_formats 
SET prefix = 'DATE_EMP', is_active = true, updated_at = CURRENT_TIMESTAMP
WHERE code_type = 'EMPLOYEE';

-- Step 3: Verify
SELECT id, code_type, prefix, is_active FROM code_formats WHERE code_type = 'EMPLOYEE';

-- Then restart your backend 