-- Fix Database Schema for Dashboard Features
-- Run this script to update the existing database

-- Add new columns to users table (if they don't exist)
DO $$ 
BEGIN
    -- Add kyc_status column
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'kyc_status') THEN
        ALTER TABLE users ADD COLUMN kyc_status VARCHAR(20) DEFAULT 'PENDING';
    END IF;
    
    -- Add state column
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'state') THEN
        ALTER TABLE users ADD COLUMN state VARCHAR(100);
    END IF;
    
    -- Add district column
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'district') THEN
        ALTER TABLE users ADD COLUMN district VARCHAR(100);
    END IF;
    
    -- Add region column
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'region') THEN
        ALTER TABLE users ADD COLUMN region VARCHAR(100);
    END IF;
    
    -- Add assigned_employee_id column
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'assigned_employee_id') THEN
        ALTER TABLE users ADD COLUMN assigned_employee_id BIGINT;
    END IF;
    
    -- Add created_at column
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'created_at') THEN
        ALTER TABLE users ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
    END IF;
    
    -- Add updated_at column
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'updated_at') THEN
        ALTER TABLE users ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
    END IF;
END $$;

-- Update existing records to have default values
UPDATE users SET 
    kyc_status = 'PENDING',
    created_at = CURRENT_TIMESTAMP,
    updated_at = CURRENT_TIMESTAMP
WHERE kyc_status IS NULL OR created_at IS NULL OR updated_at IS NULL;

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);
CREATE INDEX IF NOT EXISTS idx_users_kyc_status ON users(kyc_status);
CREATE INDEX IF NOT EXISTS idx_users_assigned_employee_id ON users(assigned_employee_id);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);

-- Add foreign key constraint for assigned_employee_id (if not exists)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_users_assigned_employee') THEN
        ALTER TABLE users ADD CONSTRAINT fk_users_assigned_employee 
            FOREIGN KEY (assigned_employee_id) REFERENCES users(id);
    END IF;
END $$; 