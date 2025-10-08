-- Test script to check FPO Users data
-- Check if FPO users table exists and has data

-- Check table structure
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'fpo_users' 
ORDER BY ordinal_position;

-- Check if there are any FPO users
SELECT COUNT(*) as total_fpo_users FROM fpo_users;

-- Check FPO users with their associated FPO
SELECT 
    fu.id as user_id,
    fu.email,
    fu.first_name,
    fu.last_name,
    fu.role,
    fu.status,
    f.id as fpo_id,
    f.fpo_name,
    f.fpo_id as fpo_code
FROM fpo_users fu
LEFT JOIN fpos f ON fu.fpo_id = f.id
ORDER BY fu.id;

-- Check if there are any FPOs
SELECT COUNT(*) as total_fpos FROM fpos;

-- List all FPOs
SELECT id, fpo_name, fpo_id, status FROM fpos ORDER BY id;
