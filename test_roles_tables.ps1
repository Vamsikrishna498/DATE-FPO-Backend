# PowerShell script to test if user roles tables exist and have data
$env:PGPASSWORD = "Meka@123"

Write-Host "Testing user roles tables..." -ForegroundColor Green

$testQueries = @"
-- Check if tables exist
SELECT 'Checking if user_roles table exists...' as status;
SELECT EXISTS (
   SELECT FROM information_schema.tables 
   WHERE table_schema = 'public' 
   AND table_name = 'user_roles'
) as user_roles_exists;

SELECT 'Checking if role_modules table exists...' as status;
SELECT EXISTS (
   SELECT FROM information_schema.tables 
   WHERE table_schema = 'public' 
   AND table_name = 'role_modules'
) as role_modules_exists;

SELECT 'Checking if role_permissions table exists...' as status;
SELECT EXISTS (
   SELECT FROM information_schema.tables 
   WHERE table_schema = 'public' 
   AND table_name = 'role_permissions'
) as role_permissions_exists;

-- Check data in user_roles table
SELECT 'User roles data:' as info;
SELECT id, role_name, description, is_active FROM user_roles ORDER BY id;

-- Check role modules
SELECT 'Role modules data:' as info;
SELECT ur.role_name, rm.module_name 
FROM user_roles ur 
JOIN role_modules rm ON ur.id = rm.role_id 
ORDER BY ur.role_name, rm.module_name;
"@

try {
    echo $testQueries | psql -h localhost -U postgres -d DATE
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Tables test completed successfully!" -ForegroundColor Green
    } else {
        Write-Host "❌ Error testing tables. Check the error messages above." -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Error executing test queries: $($_.Exception.Message)" -ForegroundColor Red
} finally {
    Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue
}
