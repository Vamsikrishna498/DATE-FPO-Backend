-- Create hierarchical country settings tables for PostgreSQL

-- Countries table
CREATE TABLE IF NOT EXISTS countries (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- States table
CREATE TABLE IF NOT EXISTS states (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    country_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE CASCADE,
    UNIQUE (name, country_id)
);

-- Districts table
CREATE TABLE IF NOT EXISTS districts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    state_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (state_id) REFERENCES states(id) ON DELETE CASCADE,
    UNIQUE (name, state_id)
);

-- Blocks table
CREATE TABLE IF NOT EXISTS blocks (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    district_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (district_id) REFERENCES districts(id) ON DELETE CASCADE,
    UNIQUE (name, district_id)
);

-- Villages table
CREATE TABLE IF NOT EXISTS villages (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    block_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (block_id) REFERENCES blocks(id) ON DELETE CASCADE,
    UNIQUE (name, block_id)
);

-- Zipcodes table
CREATE TABLE IF NOT EXISTS zipcodes (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL,
    village_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (village_id) REFERENCES villages(id) ON DELETE CASCADE,
    UNIQUE (code, village_id)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_states_country_id ON states(country_id);
CREATE INDEX IF NOT EXISTS idx_districts_state_id ON districts(state_id);
CREATE INDEX IF NOT EXISTS idx_blocks_district_id ON blocks(district_id);
CREATE INDEX IF NOT EXISTS idx_villages_block_id ON villages(block_id);
CREATE INDEX IF NOT EXISTS idx_zipcodes_village_id ON zipcodes(village_id);
CREATE INDEX IF NOT EXISTS idx_zipcodes_code ON zipcodes(code);

-- Insert sample data for India
INSERT INTO countries (name) VALUES ('India') ON CONFLICT (name) DO NOTHING;

-- Insert sample states for India
INSERT INTO states (name, country_id) VALUES 
('Andhra Pradesh', 1),
('Telangana', 1),
('Tamil Nadu', 1),
('Karnataka', 1),
('Kerala', 1)
ON CONFLICT (name, country_id) DO NOTHING;

-- Insert sample districts for Andhra Pradesh
INSERT INTO districts (name, state_id) VALUES 
('Anantapur', 1),
('Chittoor', 1),
('East Godavari', 1),
('Guntur', 1),
('Kadapa', 1)
ON CONFLICT (name, state_id) DO NOTHING;

-- Insert sample districts for Telangana
INSERT INTO districts (name, state_id) VALUES 
('Hyderabad', 2),
('Rangareddy', 2),
('Medak', 2),
('Nizamabad', 2),
('Adilabad', 2)
ON CONFLICT (name, state_id) DO NOTHING;

-- Insert sample blocks for Anantapur district
INSERT INTO blocks (name, district_id) VALUES 
('Anantapur', 1),
('Dharmavaram', 1),
('Guntakal', 1),
('Hindupur', 1),
('Kadiri', 1)
ON CONFLICT (name, district_id) DO NOTHING;

-- Insert sample blocks for Chittoor district
INSERT INTO blocks (name, district_id) VALUES 
('Chittoor', 2),
('Tirupati', 2),
('Madanapalle', 2),
('Palamaner', 2),
('Punganur', 2)
ON CONFLICT (name, district_id) DO NOTHING;

-- Insert sample villages for Anantapur block
INSERT INTO villages (name, block_id) VALUES 
('Anantapur', 1),
('Bukkarayasamudram', 1),
('Kambadur', 1),
('Kudair', 1),
('Narpala', 1)
ON CONFLICT (name, block_id) DO NOTHING;

-- Insert sample villages for Dharmavaram block
INSERT INTO villages (name, block_id) VALUES 
('Dharmavaram', 2),
('Bathalapalle', 2),
('Kanekal', 2),
('Kothacheruvu', 2),
('Putlur', 2)
ON CONFLICT (name, block_id) DO NOTHING;

-- Insert sample zipcodes for Anantapur village
INSERT INTO zipcodes (code, village_id) VALUES 
('515001', 1),
('515002', 1),
('515003', 1),
('515004', 1),
('515005', 1)
ON CONFLICT (code, village_id) DO NOTHING;

-- Insert sample zipcodes for Dharmavaram village
INSERT INTO zipcodes (code, village_id) VALUES 
('515671', 6),
('515672', 6),
('515673', 6),
('515674', 6),
('515675', 6)
ON CONFLICT (code, village_id) DO NOTHING;
