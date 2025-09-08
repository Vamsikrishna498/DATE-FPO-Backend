-- FPO Board Members Table Migration
-- Create the fpo_board_members table if it doesn't exist

-- Create FPO table if it doesn't exist
CREATE TABLE IF NOT EXISTS fpo (
    id BIGSERIAL PRIMARY KEY,
    fpo_id VARCHAR(50) UNIQUE NOT NULL,
    fpo_name VARCHAR(255) NOT NULL,
    ceo_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    village VARCHAR(255),
    district VARCHAR(255),
    state VARCHAR(255),
    pincode VARCHAR(10),
    join_date DATE NOT NULL,
    registration_type VARCHAR(50) NOT NULL,
    number_of_members INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create fpo_board_members table if it doesn't exist
CREATE TABLE IF NOT EXISTS fpo_board_members (
    id BIGSERIAL PRIMARY KEY,
    fpo_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    email VARCHAR(255),
    role VARCHAR(50) NOT NULL,
    address TEXT,
    qualification VARCHAR(255),
    experience TEXT,
    photo_file_name VARCHAR(255),
    document_file_name VARCHAR(255),
    remarks TEXT,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    appointed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fpo_id) REFERENCES fpo(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_fpo_board_members_fpo_id ON fpo_board_members(fpo_id);
CREATE INDEX IF NOT EXISTS idx_fpo_board_members_status ON fpo_board_members(status);
CREATE INDEX IF NOT EXISTS idx_fpo_board_members_role ON fpo_board_members(role);

-- Add address column if it doesn't exist (for existing tables)
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'fpo_board_members' AND column_name = 'address') THEN
        ALTER TABLE fpo_board_members ADD COLUMN address TEXT;
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'fpo_board_members' AND column_name = 'qualification') THEN
        ALTER TABLE fpo_board_members ADD COLUMN qualification VARCHAR(255);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'fpo_board_members' AND column_name = 'experience') THEN
        ALTER TABLE fpo_board_members ADD COLUMN experience TEXT;
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'fpo_board_members' AND column_name = 'photo_file_name') THEN
        ALTER TABLE fpo_board_members ADD COLUMN photo_file_name VARCHAR(255);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'fpo_board_members' AND column_name = 'document_file_name') THEN
        ALTER TABLE fpo_board_members ADD COLUMN document_file_name VARCHAR(255);
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'fpo_board_members' AND column_name = 'remarks') THEN
        ALTER TABLE fpo_board_members ADD COLUMN remarks TEXT;
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'fpo_board_members' AND column_name = 'status') THEN
        ALTER TABLE fpo_board_members ADD COLUMN status VARCHAR(20) DEFAULT 'ACTIVE';
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'fpo_board_members' AND column_name = 'appointed_at') THEN
        ALTER TABLE fpo_board_members ADD COLUMN appointed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'fpo_board_members' AND column_name = 'updated_at') THEN
        ALTER TABLE fpo_board_members ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
    END IF;
END $$;
