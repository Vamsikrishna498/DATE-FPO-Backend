# PowerShell script to fix the Users & Roles Management 500 error
# This script creates the necessary database tables for the roles management system

Write-Host "🔧 Fixing Users & Roles Management Database Issue..." -ForegroundColor Yellow
Write-Host "=================================================" -ForegroundColor Yellow

# Set PostgreSQL password
$env:PGPASSWORD = "Meka@123"

Write-Host "📊 Testing database connection..." -ForegroundColor Cyan
try {
    $connectionTest = psql -h localhost -U postgres -d DATE -c "SELECT 'Database connection successful!' as status;" 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Database connection successful!" -ForegroundColor Green
    } else {
        Write-Host "❌ Database connection failed: $connectionTest" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ Error connecting to database: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host "🏗️ Creating user_roles table..." -ForegroundColor Cyan
$createUserRolesTable = @"
CREATE TABLE IF NOT EXISTS user_roles (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
"@

try {
    echo $createUserRolesTable | psql -h localhost -U postgres -d DATE
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ user_roles table created successfully!" -ForegroundColor Green
    } else {
        Write-Host "❌ Failed to create user_roles table" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Error creating user_roles table: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "🏗️ Creating role_modules table..." -ForegroundColor Cyan
$createRoleModulesTable = @"
CREATE TABLE IF NOT EXISTS role_modules (
    role_id BIGINT NOT NULL,
    module_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (role_id, module_name),
    FOREIGN KEY (role_id) REFERENCES user_roles(id) ON DELETE CASCADE
);
"@

try {
    echo $createRoleModulesTable | psql -h localhost -U postgres -d DATE
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ role_modules table created successfully!" -ForegroundColor Green
    } else {
        Write-Host "❌ Failed to create role_modules table" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Error creating role_modules table: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "🏗️ Creating role_permissions table..." -ForegroundColor Cyan
$createRolePermissionsTable = @"
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission VARCHAR(255) NOT NULL,
    PRIMARY KEY (role_id, permission),
    FOREIGN KEY (role_id) REFERENCES user_roles(id) ON DELETE CASCADE
);
"@

try {
    echo $createRolePermissionsTable | psql -h localhost -U postgres -d DATE
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ role_permissions table created successfully!" -ForegroundColor Green
    } else {
        Write-Host "❌ Failed to create role_permissions table" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Error creating role_permissions table: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "📝 Inserting default roles..." -ForegroundColor Cyan
$insertRoles = @"
INSERT INTO user_roles (role_name, description, is_active, created_by) 
VALUES 
    ('SUPER_ADMIN', 'Super Administrator with full system access', true, 'system'),
    ('ADMIN', 'Administrator with management access', true, 'system'),
    ('EMPLOYEE', 'Employee with limited access', true, 'system'),
    ('FARMER', 'Farmer with basic access', true, 'system'),
    ('FPO', 'FPO user with FPO-specific access', true, 'system')
ON CONFLICT (role_name) DO NOTHING;
"@

try {
    echo $insertRoles | psql -h localhost -U postgres -d DATE
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Default roles inserted successfully!" -ForegroundColor Green
    } else {
        Write-Host "❌ Failed to insert default roles" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Error inserting roles: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "📝 Adding basic modules for SUPER_ADMIN..." -ForegroundColor Cyan
$insertModules = @"
INSERT INTO role_modules (role_id, module_name)
SELECT ur.id, 'CONFIGURATION'
FROM user_roles ur
WHERE ur.role_name = 'SUPER_ADMIN'
ON CONFLICT (role_id, module_name) DO NOTHING;
"@

try {
    echo $insertModules | psql -h localhost -U postgres -d DATE
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Role modules added successfully!" -ForegroundColor Green
    } else {
        Write-Host "❌ Failed to add role modules" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Error adding modules: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "📝 Adding basic permissions for SUPER_ADMIN..." -ForegroundColor Cyan
$insertPermissions = @"
INSERT INTO role_permissions (role_id, permission)
SELECT ur.id, 'READ'
FROM user_roles ur
WHERE ur.role_name = 'SUPER_ADMIN'
ON CONFLICT (role_id, permission) DO NOTHING;
"@

try {
    echo $insertPermissions | psql -h localhost -U postgres -d DATE
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Role permissions added successfully!" -ForegroundColor Green
    } else {
        Write-Host "❌ Failed to add role permissions" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Error adding permissions: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "🔍 Verifying setup..." -ForegroundColor Cyan
$verifyQuery = @"
SELECT 'Setup verification:' as info;
SELECT COUNT(*) as total_roles FROM user_roles;
SELECT role_name, description FROM user_roles ORDER BY id;
"@

try {
    echo $verifyQuery | psql -h localhost -U postgres -d DATE
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Database setup verification completed!" -ForegroundColor Green
    } else {
        Write-Host "❌ Verification failed" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Error during verification: $($_.Exception.Message)" -ForegroundColor Red
}

# Clean up
Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue

Write-Host "=================================================" -ForegroundColor Yellow
Write-Host "🎉 Database setup completed!" -ForegroundColor Green
Write-Host "📋 Next steps:" -ForegroundColor Yellow
Write-Host "   1. Refresh your frontend application" -ForegroundColor White
Write-Host "   2. Navigate to Users & Roles Management" -ForegroundColor White
Write-Host "   3. The 500 error should now be resolved!" -ForegroundColor White
Write-Host "=================================================" -ForegroundColor Yellow
