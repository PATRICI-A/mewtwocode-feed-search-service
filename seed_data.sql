-- Insertar Parches de prueba
INSERT INTO patches (id, title, description, category, campus_zone, location, start_time, end_time, capacity, current_count, status, creator_id, is_public, created_at)
VALUES 
(gen_random_uuid(), 'Torneo de Futbol 5', 'Ven a jugar un picadito en las canchas de la selva', 'SPORTS', 'CANCHA', 'Canchas de futbol', NOW() + INTERVAL '1 day', NOW() + INTERVAL '1 day 2 hours', 10, 0, 'OPEN', gen_random_uuid(), true, NOW()),

(gen_random_uuid(), 'Grupo de Estudio Java', 'Repaso para el parcial de Programación Orientada a Objetos', 'STUDY', 'BIBLIOTECA', 'Piso 2 cubículos', NOW() + INTERVAL '2 days', NOW() + INTERVAL '2 days 3 hours', 5, 0, 'OPEN', gen_random_uuid(), true, NOW()),

(gen_random_uuid(), 'Almuerzo en la Cafetería', 'Almorzamos algo rico y charlamos', 'FOOD', 'CAFETERIA', 'Cafetería central', NOW() + INTERVAL '5 hours', NOW() + INTERVAL '6 hours', 4, 0, 'OPEN', gen_random_uuid(), true, NOW()),

(gen_random_uuid(), 'Torneo de Ajedrez', 'Trae tu tablero o usa los de la biblioteca', 'CULTURE', 'BIBLIOTECA', 'Sala de juegos', NOW() + INTERVAL '3 days', NOW() + INTERVAL '3 days 4 hours', 20, 0, 'OPEN', gen_random_uuid(), true, NOW());
