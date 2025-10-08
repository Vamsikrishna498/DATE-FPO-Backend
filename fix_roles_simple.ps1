# Simple script to fix Users and Roles Management Database Issue
Write-Host "Fixing Users and Roles Management Database Issue..." -ForegroundColor Yellow

# Set PostgreSQL password
$env:PGPASSWORD = "Meka@123"

Write-Host "Testing database connection..." -ForegroundColor Cyan
try {
    psql -h localhost -U postgres -d DATE -c "SELECT 'Database connection successful!' as status;"
    Write-Host "Database connection successful!" -ForegroundColor Green
} catch {
    Write-Host "Database connection failed!" -ForegroundColor Red
    exit 1
}

Write-Host "Creating user_roles table..." -ForegroundColor Cyan
psql -h localhost -U postgres -d DATE -c "CREATE TABLE IF NOT EXISTS user_roles (id BIGSERIAL PRIMARY KEY, role_name VARCHAR(255) NOT NULL UNIQUE, description TEXT, is_active BOOLEAN NOT NULL DEFAULT true, created_by VARCHAR(255), updated_by VARCHAR(255), created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);"

Write-Host "Creating role_modules table..." -ForegroundColor Cyan
psql -h localhost -U postgres -d DATE -c "CREATE TABLE IF NOT EXISTS role_modules (role_id BIGINT NOT NULL, module_name VARCHAR(255) NOT NULL, PRIMARY KEY (role_id, module_name), FOREIGN KEY (role_id) REFERENCES user_roles(id) ON DELETE CASCADE);"

Write-Host "Creating role_permissions table..." -ForegroundColor Cyan
psql -h localhost -U postgres -d DATE -c "CREATE TABLE IF NOT EXISTS role_permissions (role_id BIGINT NOT NULL, permission VARCHAR(255) NOT NULL, PRIMARY KEY (role_id, permission), FOREIGN KEY (role_id) REFERENCES user_roles(id) ON DELETE CASCADE);"

Write-Host "Inserting default roles..." -ForegroundColor Cyan
psql -h localhost -U postgres -d DATE -c "INSERT INTO user_roles (role_name, description, is_active, created_by) VALUES ('SUPER_ADMIN', 'Super Administrator with full system access', true, 'system'), ('ADMIN', 'Administrator with management access', true, 'system'), ('EMPLOYEE', 'Employee with limited access', true, 'system'), ('FARMER', 'Farmer with basic access', true, 'system'), ('FPO', 'FPO user with FPO-specific access', true, 'system') ON CONFLICT (role_name) DO NOTHING;"

Write-Host "Adding basic modules for SUPER_ADMIN..." -ForegroundColor Cyan
psql -h localhost -U postgres -d DATE -c "INSERT INTO role_modules (role_id, module_name) SELECT ur.id, 'CONFIGURATION' FROM user_roles ur WHERE ur.role_name = 'SUPER_ADMIN' ON CONFLICT (role_id, module_name) DO NOTHING;"

Write-Host "Adding basic permissions for SUPER_ADMIN..." -ForegroundColor Cyan
psql -h localhost -U postgres -d DATE -c "INSERT INTO role_permissions (role_id, permission) SELECT ur.id, 'READ' FROM user_roles ur WHERE ur.role_name = 'SUPER_ADMIN' ON CONFLICT (role_id, permission) DO NOTHING;"

Write-Host "Verifying setup..." -ForegroundColor Cyan
psql -h localhost -U postgres -d DATE -c "SELECT COUNT(*) as total_roles FROM user_roles;"

# Clean up
Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue

Write-Host "Database setup completed!" -ForegroundColor Green
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Refresh your frontend application" -ForegroundColor White
Write-Host "2. Navigate to Users and Roles Management" -ForegroundColor White
Write-Host "3. The 500 error should now be resolved!" -ForegroundColor White
