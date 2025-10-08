# PowerShell script to create user roles tables
# Make sure PostgreSQL is running and you have the correct password

$env:PGPASSWORD = "Meka@123"  # Set the password from your application.properties
$sqlFile = "create_user_roles_tables.sql"

Write-Host "Creating user roles tables..." -ForegroundColor Green

try {
    # Execute the SQL file
    psql -h localhost -U postgres -d DATE -f $sqlFile
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ User roles tables created successfully!" -ForegroundColor Green
        Write-Host "You can now access the Roles management in your application." -ForegroundColor Yellow
    } else {
        Write-Host "❌ Error creating tables. Check the error messages above." -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Error executing SQL script: $($_.Exception.Message)" -ForegroundColor Red
} finally {
    # Clear the password from environment
    Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue
}
