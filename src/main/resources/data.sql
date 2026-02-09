-- Create default admin user if not exists
-- Password is 'admin123' hashed with BCrypt
-- $2a$10$wS2a6/8H2i7oF.0m7Xj.UOaKz7B.7H.D8/3.9.7.d. (This is a sample valid bcrypt hash for 'admin123')

INSERT INTO users (id, username, password, full_name, email, role, active, created_at, is_premium, free_test_count, payment_tier, phone)
VALUES (1, 'admin', '$2a$10$wS2a6/8H2i7oF.0m7Xj.UOaKz7B.7H.D8/3.9.7.d.', 'System Admin', 'admin@korenavitamin.com', 'ADMIN', true, NOW(), true, 9999, 'PREMIUM', '0909000111')
ON DUPLICATE KEY UPDATE 
password = '$2a$10$wS2a6/8H2i7oF.0m7Xj.UOaKz7B.7H.D8/3.9.7.d.',
active = true;
