-- Patch 3: sin interacciones previas (candidato limpio para recomendaciones)
INSERT INTO patches (id, title, description, category, location, campus_zone, start_time, end_time, capacity, current_count, status, creator_id, is_public, created_at)
VALUES (
  'b0000000-0000-0000-0000-000000000003',
  'Noche de juegos de mesa',
  'Trae tu juego favorito, se permiten dados y cartas.',
  'GAMING', 'Salon comunal bloque F', 'SALON',
  '2026-05-07 18:00:00', '2026-05-07 21:00:00',
  12, 2, 'OPEN',
  'a0000000-0000-0000-0000-000000000001',
  true, NOW()
);

-- User 3: interesado en STUDY y GAMING, sin interacciones (candidato ideal para recomendaciones)
INSERT INTO user_interests (id, user_id, interest_tag, created_at)
VALUES
  (gen_random_uuid(), 'a0000000-0000-0000-0000-000000000003', 'STUDY',  NOW()),
  (gen_random_uuid(), 'a0000000-0000-0000-0000-000000000003', 'GAMING', NOW());
