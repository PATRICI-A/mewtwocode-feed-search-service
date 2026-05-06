INSERT INTO patches (id, title, description, category, location, campus_zone, start_time, end_time, capacity, current_count, status, creator_id, is_public, created_at)
VALUES
  ('b0000000-0000-0000-0000-000000000001','Grupo de estudio Calculo III','Sesion de repaso para parcial de calculo, traer apuntes y calculadora.','STUDY','Sala B-204','SALON','2026-05-05 14:00:00','2026-05-05 16:00:00',10,3,'OPEN','a0000000-0000-0000-0000-000000000001',true,NOW()),
  ('b0000000-0000-0000-0000-000000000002','Torneo relampago de futbol','Partido rapido 5v5 en la cancha principal, todos bienvenidos.','SPORTS','Cancha principal','CANCHA','2026-05-06 17:00:00','2026-05-06 19:00:00',20,8,'OPEN','a0000000-0000-0000-0000-000000000002',true,NOW());

INSERT INTO user_interests (id, user_id, interest_tag, created_at)
VALUES
  (gen_random_uuid(),'a0000000-0000-0000-0000-000000000001','STUDY',NOW()),
  (gen_random_uuid(),'a0000000-0000-0000-0000-000000000001','GAMING',NOW()),
  (gen_random_uuid(),'a0000000-0000-0000-0000-000000000002','SPORTS',NOW()),
  (gen_random_uuid(),'a0000000-0000-0000-0000-000000000002','CULTURE',NOW());

INSERT INTO feed_interactions (id, user_id, patch_id, action, interacted_at)
VALUES
  (gen_random_uuid(),'a0000000-0000-0000-0000-000000000001','b0000000-0000-0000-0000-000000000001','JOIN',NOW() - INTERVAL '2 days'),
  (gen_random_uuid(),'a0000000-0000-0000-0000-000000000001','b0000000-0000-0000-0000-000000000002','VIEW',NOW() - INTERVAL '1 day'),
  (gen_random_uuid(),'a0000000-0000-0000-0000-000000000002','b0000000-0000-0000-0000-000000000001','SKIP',NOW() - INTERVAL '3 days'),
  (gen_random_uuid(),'a0000000-0000-0000-0000-000000000002','b0000000-0000-0000-0000-000000000002','JOIN',NOW() - INTERVAL '1 day');

INSERT INTO patch_memberships (id, patch_id, user_id, joined_at, status)
VALUES
  (gen_random_uuid(),'b0000000-0000-0000-0000-000000000001','a0000000-0000-0000-0000-000000000001',NOW() - INTERVAL '2 days','ACTIVE'),
  (gen_random_uuid(),'b0000000-0000-0000-0000-000000000002','a0000000-0000-0000-0000-000000000002',NOW() - INTERVAL '1 day','ACTIVE');

INSERT INTO search_index (patch_id, search_vector, last_indexed)
VALUES
  ('b0000000-0000-0000-0000-000000000001','grupo estudio calculo III repaso parcial salon',NOW()),
  ('b0000000-0000-0000-0000-000000000002','torneo futbol 5v5 cancha deporte relampago',NOW());

INSERT INTO recommendation_cache (user_id, recommended_patch_ids, score_breakdown, generated_at, expires_at)
VALUES
  ('a0000000-0000-0000-0000-000000000001','["b0000000-0000-0000-0000-000000000001","b0000000-0000-0000-0000-000000000002"]','{"b0000000-0000-0000-0000-000000000001":0.85,"b0000000-0000-0000-0000-000000000002":0.40}',NOW(),NOW() + INTERVAL '5 minutes'),
  ('a0000000-0000-0000-0000-000000000002','["b0000000-0000-0000-0000-000000000002","b0000000-0000-0000-0000-000000000001"]','{"b0000000-0000-0000-0000-000000000002":0.90,"b0000000-0000-0000-0000-000000000001":0.30}',NOW(),NOW() + INTERVAL '5 minutes');
