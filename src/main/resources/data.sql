-- Insertar artículos
INSERT INTO article (title, content, category, publish_date, view_count, status) VALUES
-- Artículos publicados
('España gana la Copa Mundial', 'La selección española se corona campeona del mundo...', 'deportes', CURRENT_TIMESTAMP(), 150, 'PUBLISHED'),
('Real Madrid ficha nueva estrella', 'El equipo merengue ha confirmado el fichaje...', 'deportes', CURRENT_TIMESTAMP(), 120, 'PUBLISHED'),
-- Artículos en revisión
('F1: Hamilton gana en Mónaco', 'Lewis Hamilton consigue una victoria espectacular...', 'deportes', CURRENT_TIMESTAMP(), 90, 'IN_REVIEW'),
('Nuevo Ferrari 2024', 'Ferrari presenta su nuevo modelo deportivo...', 'automotor', CURRENT_TIMESTAMP(), 200, 'IN_REVIEW'),
-- Artículos en borrador
('Toyota lanza coche eléctrico', 'La marca japonesa revoluciona el mercado...', 'automotor', CURRENT_TIMESTAMP(), 180, 'DRAFT'),
('BMW presenta moto del futuro', 'La nueva motocicleta eléctrica de BMW...', 'automotor', CURRENT_TIMESTAMP(), 160, 'DRAFT');

-- Insertar usuario admin (contraseña: admin)
DELETE FROM users WHERE username = 'admin';
INSERT INTO users (username, password, role, active) VALUES 
('admin', '$2a$10$7hFLSnApv7CZ66QWpw5bluNiH38N2.qTpCTI7Zuc.A1vbG3EXVBCK', 'ROLE_ADMIN', true);
