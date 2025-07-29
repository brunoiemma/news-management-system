-- Insert articles
INSERT INTO article (title, content, category, publish_date, view_count, status) VALUES
-- Published articles
('España gana la Copa Mundial', 'La selección española se corona campeona del mundo...', 'deportes', CURRENT_TIMESTAMP(), 150, 'PUBLISHED'),
('Real Madrid ficha nueva estrella', 'El equipo merengue ha confirmado el fichaje...', 'deportes', CURRENT_TIMESTAMP(), 120, 'PUBLISHED'),
-- Articles in review
('F1: Hamilton gana en Mónaco', 'Lewis Hamilton consigue una victoria espectacular...', 'deportes', CURRENT_TIMESTAMP(), 90, 'IN_REVIEW'),
('Nuevo Ferrari 2024', 'Ferrari presenta su nuevo modelo deportivo...', 'automotor', CURRENT_TIMESTAMP(), 200, 'IN_REVIEW'),
-- Draft articles
('Toyota lanza coche eléctrico', 'La marca japonesa revoluciona el mercado...', 'automotor', CURRENT_TIMESTAMP(), 180, 'DRAFT'),
('BMW presenta moto del futuro', 'La nueva motocicleta eléctrica de BMW...', 'automotor', CURRENT_TIMESTAMP(), 160, 'DRAFT');

-- Delete existing users and their permissions
DELETE FROM user_permissions;
DELETE FROM users;

-- Insert default users with different roles (password: admin for all)
INSERT INTO users (username, password, email, first_name, last_name, role, active) VALUES 
('admin', '$2a$10$7hFLSnApv7CZ66QWpw5bluNiH38N2.qTpCTI7Zuc.A1vbG3EXVBCK', 'admin@example.com', 'Admin', 'User', 'ROLE_ADMIN', true),
('publisher', '$2a$10$7hFLSnApv7CZ66QWpw5bluNiH38N2.qTpCTI7Zuc.A1vbG3EXVBCK', 'publisher@example.com', 'Publisher', 'User', 'ROLE_PUBLISHER', true),
('redactor', '$2a$10$7hFLSnApv7CZ66QWpw5bluNiH38N2.qTpCTI7Zuc.A1vbG3EXVBCK', 'redactor@example.com', 'Redactor', 'User', 'ROLE_REDACTOR', true),
('subscriber', '$2a$10$7hFLSnApv7CZ66QWpw5bluNiH38N2.qTpCTI7Zuc.A1vbG3EXVBCK', 'subscriber@example.com', 'Subscriber', 'User', 'ROLE_SUBSCRIBER', true);

-- Add permissions for publisher
INSERT INTO user_permissions (user_id, permission) 
SELECT u.id, permission
FROM users u,
     (VALUES ('PUBLISH_ARTICLE'), ('VIEW_ARTICLE'), ('REVIEW_ARTICLE')) AS p(permission)
WHERE u.username = 'publisher';

-- Add permissions for redactor
INSERT INTO user_permissions (user_id, permission)
SELECT u.id, permission
FROM users u,
     (VALUES ('CREATE_ARTICLE'), ('EDIT_ARTICLE'), ('VIEW_ARTICLE'), ('PUBLISH_OWN_ARTICLE')) AS p(permission)
WHERE u.username = 'redactor';

-- Add permissions for subscriber
INSERT INTO user_permissions (user_id, permission)
SELECT u.id, permission
FROM users u,
     (VALUES ('VIEW_ARTICLE')) AS p(permission)
WHERE u.username = 'subscriber';
