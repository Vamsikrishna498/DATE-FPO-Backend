# Simple script to create user roles tables
Write-Host "Setting up PostgreSQL environment..." -ForegroundColor Yellow

# Set password environment variable
$env:PGPASSWORD = "Meka@123"

Write-Host "Testing database connection..." -ForegroundColor Yellow
$connectionTest = psql -h localhost -U postgres -d DATE -c "SELECT 'Connection successful' as status;"
Write-Host "Connection result: $connectionTest" -ForegroundColor Green

Write-Host "Creating user roles tables..." -ForegroundColor Yellow
$createResult = psql -h localhost -U postgres -d DATE -f "create_user_roles_tables.sql"
Write-Host "Create result: $createResult" -ForegroundColor Green

Write-Host "Testing if tables exist..." -ForegroundColor Yellow
$testResult = psql -h localhost -U postgres -d DATE -c "SELECT table_name FROM information_schema.tables WHERE table_name IN ('user_roles', 'role_modules', 'role_permissions');"
Write-Host "Tables found: $testResult" -ForegroundColor Green

# Clean up
Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue
Write-Host "Script completed!" -ForegroundColor Green
