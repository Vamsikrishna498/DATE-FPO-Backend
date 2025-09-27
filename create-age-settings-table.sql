-- Create age_settings table
CREATE TABLE IF NOT EXISTS age_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    min_value INT NOT NULL,
    max_value INT NOT NULL,
    description VARCHAR(500),
    user_type VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_min_max CHECK (min_value < max_value),
    CONSTRAINT chk_age_range CHECK (min_value >= 1 AND max_value <= 100),
    CONSTRAINT chk_user_type CHECK (user_type IN ('FARMER', 'EMPLOYEE', 'ADMIN', 'SUPER_ADMIN'))
);

-- Insert default age settings
INSERT INTO age_settings (name, min_value, max_value, description, user_type, is_active, created_by) VALUES
('Farmer Age Limit', 18, 65, 'Age limit for farmer registration', 'FARMER', TRUE, 'SYSTEM'),
('Employee Age Limit', 21, 60, 'Age limit for employee registration', 'EMPLOYEE', TRUE, 'SYSTEM'),
('Admin Age Limit', 25, 55, 'Age limit for admin registration', 'ADMIN', TRUE, 'SYSTEM'),
('Super Admin Age Limit', 30, 50, 'Age limit for super admin registration', 'SUPER_ADMIN', TRUE, 'SYSTEM');

-- Create index for better performance
CREATE INDEX idx_age_settings_user_type ON age_settings(user_type);
CREATE INDEX idx_age_settings_active ON age_settings(is_active);
