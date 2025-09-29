-- Insert initial education types
INSERT INTO education_types (name, description, is_active, created_by, created_at) VALUES
('Primary Education', 'Basic primary education', true, 'system', NOW()),
('Secondary Education', 'Secondary school education', true, 'system', NOW()),
('Higher Secondary Education', 'Higher secondary school education', true, 'system', NOW()),
('Graduation', 'Bachelor degree education', true, 'system', NOW()),
('Post Graduation', 'Master degree education', true, 'system', NOW());

-- Insert initial education categories
INSERT INTO education_categories (name, description, parent_id, is_active, created_by, created_at) VALUES
('Government School', 'Government run educational institution', 1, true, 'system', NOW()),
('Private School', 'Private educational institution', 1, true, 'system', NOW()),
('Government College', 'Government run college', 4, true, 'system', NOW()),
('Private College', 'Private educational college', 4, true, 'system', NOW()),
('University', 'University level education', 5, true, 'system', NOW());

-- Insert initial crop names
INSERT INTO crop_names (name, code, description, is_active, created_by, created_at) VALUES
('Rice', 'RICE', 'Paddy crop', true, 'system', NOW()),
('Wheat', 'WHEAT', 'Wheat crop', true, 'system', NOW()),
('Maize', 'MAIZE', 'Corn crop', true, 'system', NOW()),
('Cotton', 'COTTON', 'Cotton crop', true, 'system', NOW()),
('Sugarcane', 'SUGAR', 'Sugarcane crop', true, 'system', NOW());

-- Insert initial crop types
INSERT INTO crop_types (name, code, description, parent_id, is_active, created_by, created_at) VALUES
('Basmati Rice', 'BASMATI', 'Premium quality rice variety', 1, true, 'system', NOW()),
('Jasmine Rice', 'JASMINE', 'Aromatic rice variety', 1, true, 'system', NOW()),
('Brown Rice', 'BROWN', 'Healthy brown rice variety', 1, true, 'system', NOW()),
('Durum Wheat', 'DURUM', 'Hard wheat variety', 2, true, 'system', NOW()),
('Soft Wheat', 'SOFT', 'Soft wheat variety', 2, true, 'system', NOW());
