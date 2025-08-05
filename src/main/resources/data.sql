-- Delete existing data
DELETE FROM user_permissions;
DELETE FROM article;
DELETE FROM users;

-- Insert users with different roles (password: admin for all)
INSERT INTO users (username, password, email, first_name, last_name, role, active, created_at, updated_at) VALUES 
('Sistema', '$2a$10$7hFLSnApv7CZ66QWpw5bluNiH38N2.qTpCTI7Zuc.A1vbG3EXVBCK', 'sistema@example.com', 'Sistema', 'Automatico', 'ROLE_SYSTEM', true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('admin', '$2a$10$7hFLSnApv7CZ66QWpw5bluNiH38N2.qTpCTI7Zuc.A1vbG3EXVBCK', 'admin@example.com', 'Admin', 'User', 'ROLE_ADMIN', true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('publisher', '$2a$10$7hFLSnApv7CZ66QWpw5bluNiH38N2.qTpCTI7Zuc.A1vbG3EXVBCK', 'publisher@example.com', 'Publisher', 'User', 'ROLE_PUBLISHER', true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('redactor', '$2a$10$7hFLSnApv7CZ66QWpw5bluNiH38N2.qTpCTI7Zuc.A1vbG3EXVBCK', 'redactor@example.com', 'Redactor', 'User', 'ROLE_REDACTOR', true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('subscriber', '$2a$10$7hFLSnApv7CZ66QWpw5bluNiH38N2.qTpCTI7Zuc.A1vbG3EXVBCK', 'subscriber@example.com', 'Subscriber', 'User', 'ROLE_SUBSCRIBER', true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- Add permissions for Sistema
INSERT INTO user_permissions (user_id, permission) 
SELECT u.id, permission
FROM users u,
     (VALUES 
        ('VIEW_ARTICLE'),
        ('CREATE_ARTICLE'),
        ('EDIT_ARTICLE'),
        ('PUBLISH_ARTICLE')
     ) AS p(permission)
WHERE u.username = 'Sistema';

-- Add permissions for admin
INSERT INTO user_permissions (user_id, permission) 
SELECT u.id, permission
FROM users u,
     (VALUES 
        ('VIEW_DASHBOARD'),
        ('MANAGE_USERS'),
        ('VIEW_USERS'),
        ('CREATE_ARTICLE'),
        ('EDIT_ARTICLE'),
        ('DELETE_ARTICLE'),
        ('PUBLISH_ARTICLE'),
        ('REVIEW_ARTICLE'),
        ('VIEW_ARTICLE'),
        ('MANAGE_ROLES'),
        ('VIEW_ROLES')
     ) AS p(permission)
WHERE u.username = 'admin';

-- Add permissions for publisher
INSERT INTO user_permissions (user_id, permission) 
SELECT u.id, permission
FROM users u,
     (VALUES 
        ('VIEW_DASHBOARD'),
        ('PUBLISH_ARTICLE'),
        ('REVIEW_ARTICLE'),
        ('VIEW_ARTICLE'),
        ('VIEW_USERS')
     ) AS p(permission)
WHERE u.username = 'publisher';

-- Add permissions for redactor
INSERT INTO user_permissions (user_id, permission)
SELECT u.id, permission
FROM users u,
     (VALUES 
        ('VIEW_DASHBOARD'),
        ('CREATE_ARTICLE'),
        ('EDIT_ARTICLE'),
        ('VIEW_ARTICLE'),
        ('REVIEW_ARTICLE')
     ) AS p(permission)
WHERE u.username = 'redactor';

-- Add permissions for subscriber
INSERT INTO user_permissions (user_id, permission)
SELECT u.id, permission
FROM users u,
     (VALUES 
        ('VIEW_DASHBOARD'),
        ('VIEW_ARTICLE')
     ) AS p(permission)
WHERE u.username = 'subscriber';

-- Insert sample articles
INSERT INTO article (title, content, author, category, publish_date, submission_date, last_modified, view_count, status, summary) VALUES
-- Published articles
('Breaking News: Major Tech Innovation', 
 'A groundbreaking technological advancement has been announced...', 
 'redactor', 'technology', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 150, 'PUBLISHED',
 'Revolutionary new technology promises to transform the industry'),

('Sports Update: Championship Finals', 
 'The championship match proved to be an exciting battle...', 
 'redactor', 'sports', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 200, 'PUBLISHED',
 'Thrilling championship match ends in dramatic fashion'),

-- Articles under review
('Upcoming Economic Changes', 
 'Analysis of upcoming economic policy changes and their potential impact...', 
 'redactor', 'economics', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0, 'IN_REVIEW',
 'Expert analysis of new economic policies'),

('Environmental Conservation Efforts', 
 'New initiatives are being launched to protect endangered species...', 
 'redactor', 'environment', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0, 'IN_REVIEW',
 'Latest developments in environmental conservation'),

-- Draft articles
('Future of Artificial Intelligence', 
 'Exploring the potential implications of AI advancement...', 
 'redactor', 'technology', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0, 'DRAFT',
 'In-depth look at AI development and future prospects'),

('Healthcare Innovation Report', 
 'Recent breakthroughs in medical research and treatment...', 
 'redactor', 'health', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 0, 'DRAFT',
 'Latest advances in healthcare and medical research');
