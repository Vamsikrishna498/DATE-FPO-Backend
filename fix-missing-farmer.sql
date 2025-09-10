-- Fix Missing Farmer Record
-- This script creates a farmer record for the user karthikarthik2912@gmail.com

-- First, let's check what users exist
SELECT id, email, role, status FROM users WHERE email = 'karthikarthik2912@gmail.com';

-- Check if farmer record exists
SELECT id, email, first_name, last_name FROM farmers WHERE email = 'karthikarthik2912@gmail.com';

-- If user exists but farmer doesn't, create the farmer record
INSERT INTO farmers (
    email, 
    first_name, 
    last_name, 
    contact_number, 
    date_of_birth, 
    gender, 
    state, 
    district, 
    village, 
    pincode, 
    kyc_status, 
    kyc_approved,
    created_at,
    updated_at
) 
SELECT 
    u.email,
    COALESCE(u.name, 'Karthik') as first_name,
    'Farmer' as last_name,
    COALESCE(u.phone_number, '9999999999') as contact_number,
    '1990-01-01' as date_of_birth,
    'Male' as gender,
    'Karnataka' as state,
    'Bangalore' as district,
    'Test Village' as village,
    '560001' as pincode,
    'PENDING' as kyc_status,
    false as kyc_approved,
    NOW() as created_at,
    NOW() as updated_at
FROM users u 
WHERE u.email = 'karthikarthik2912@gmail.com' 
AND NOT EXISTS (
    SELECT 1 FROM farmers f WHERE f.email = u.email
);

-- Verify the farmer was created
SELECT id, email, first_name, last_name, state, district, village FROM farmers WHERE email = 'karthikarthik2912@gmail.com';
