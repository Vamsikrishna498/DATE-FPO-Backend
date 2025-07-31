-- Database Migration for Dashboard Features
-- Add new fields to users table

-- Add new columns to users table
ALTER TABLE users ADD COLUMN kyc_status VARCHAR(20) DEFAULT 'PENDING';
ALTER TABLE users ADD COLUMN state VARCHAR(100);
ALTER TABLE users ADD COLUMN district VARCHAR(100);
ALTER TABLE users ADD COLUMN region VARCHAR(100);
ALTER TABLE users ADD COLUMN assigned_employee_id BIGINT;
ALTER TABLE users ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE users ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Create index for better performance
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_kyc_status ON users(kyc_status);
CREATE INDEX idx_users_assigned_employee_id ON users(assigned_employee_id);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Update existing records to have default values
UPDATE users SET 
    kyc_status = 'PENDING',
    created_at = CURRENT_TIMESTAMP,
    updated_at = CURRENT_TIMESTAMP
WHERE kyc_status IS NULL;

-- Add foreign key constraint for assigned_employee_id
ALTER TABLE users ADD CONSTRAINT fk_users_assigned_employee 
    FOREIGN KEY (assigned_employee_id) REFERENCES users(id); 