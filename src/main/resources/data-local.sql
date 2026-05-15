-- Test data loaded automatically when running with the 'local' profile (H2)
-- For Docker/PostgreSQL, run the equivalent INSERT once via psql or docker exec.

INSERT INTO patches (id, title, description, category, location, campus_zone, start_time, capacity, current_count, status, is_public, creator_id, created_at) VALUES
  ('a1b2c3d4-0001-0001-0001-000000000001', 'Football match 5v5',    'Friendly match at the sports field, all levels welcome',      'SPORTS',  'Sports field main court',   'CANCHA',    DATEADD('HOUR',  2, NOW()), 10, 3, 'OPEN', true, 'f0000000-0000-0000-0000-000000000001', NOW());
INSERT INTO patches (id, title, description, category, location, campus_zone, start_time, capacity, current_count, status, is_public, creator_id, created_at) VALUES
  ('a1b2c3d4-0002-0002-0002-000000000002', 'Java study group',       'Review Spring Boot and microservices for the final exam',      'STUDY',   'Library room 3',             'BIBLIOTECA', DATEADD('HOUR',  5, NOW()),  6, 1, 'OPEN', true, 'f0000000-0000-0000-0000-000000000002', NOW());
INSERT INTO patches (id, title, description, category, location, campus_zone, start_time, capacity, current_count, status, is_public, creator_id, created_at) VALUES
  ('a1b2c3d4-0003-0003-0003-000000000003', 'Indie game night',       'Bring your laptop, we play Stardew Valley and Among Us',      'GAMING',  'Lounge B',                   'SALON',     DATEADD('HOUR', 24, NOW()),  8, 8, 'FULL', true, 'f0000000-0000-0000-0000-000000000003', NOW());
INSERT INTO patches (id, title, description, category, location, campus_zone, start_time, capacity, current_count, status, is_public, creator_id, created_at) VALUES
  ('a1b2c3d4-0004-0004-0004-000000000004', 'Pizza Friday',           'Group order from the cafeteria, split cost',                  'FOOD',    'Main cafeteria',             'CAFETERIA', DATEADD('HOUR',  3, NOW()), 12, 5, 'OPEN', true, 'f0000000-0000-0000-0000-000000000004', NOW());
INSERT INTO patches (id, title, description, category, location, campus_zone, start_time, capacity, current_count, status, is_public, creator_id, created_at) VALUES
  ('a1b2c3d4-0005-0005-0005-000000000005', 'Theatre rehearsal',      'Open rehearsal for the semester show, spectators welcome',    'CULTURE', 'Auditorium',                 'EXTERNO',   DATEADD('HOUR', 48, NOW()), 20, 7, 'OPEN', true, 'f0000000-0000-0000-0000-000000000005', NOW());
INSERT INTO patches (id, title, description, category, location, campus_zone, start_time, capacity, current_count, status, is_public, creator_id, created_at) VALUES
  ('a1b2c3d4-0006-0006-0006-000000000006', 'Photography workshop',   'Learn basic composition and lighting with your phone camera', 'CULTURE', 'Library terrace',            'BIBLIOTECA', DATEADD('HOUR', 72, NOW()), 15, 2, 'OPEN', true, 'f0000000-0000-0000-0000-000000000006', NOW());
INSERT INTO patches (id, title, description, category, location, campus_zone, start_time, capacity, current_count, status, is_public, creator_id, created_at) VALUES
  ('a1b2c3d4-0007-0007-0007-000000000007', 'Basketball 3v3',         'Quick games near the main entrance court',                    'SPORTS',  'Basketball court entrance',  'CANCHA',    DATEADD('HOUR', 24, NOW()),  6, 0, 'OPEN', true, 'f0000000-0000-0000-0000-000000000007', NOW());
