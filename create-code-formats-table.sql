-- Create code_formats table if it doesn't exist
CREATE TABLE IF NOT EXISTS code_formats (
    id BIGSERIAL PRIMARY KEY,
    code_type VARCHAR(50) NOT NULL UNIQUE,
    prefix VARCHAR(100) NOT NULL,
    starting_number INTEGER NOT NULL,
    current_number INTEGER NOT NULL DEFAULT 0,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Insert default farmer code format if it doesn't exist
INSERT INTO code_formats (code_type, prefix, starting_number, current_number, description, is_active, created_by)
SELECT 'FARMER', 'DATE-FAR', 1, 0, 'Farmer identification code format', true, 'system'
WHERE NOT EXISTS (SELECT 1 FROM code_formats WHERE code_type = 'FARMER');

-- Insert default employee code format if it doesn't exist
INSERT INTO code_formats (code_type, prefix, starting_number, current_number, description, is_active, created_by)
SELECT 'EMPLOYEE', 'DATE-EMP', 1, 0, 'Employee identification code format', true, 'system'
WHERE NOT EXISTS (SELECT 1 FROM code_formats WHERE code_type = 'EMPLOYEE');

-- Verify the data
SELECT * FROM code_formats;
