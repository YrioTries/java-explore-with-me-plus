TRUNCATE TABLE apps CASCADE;
TRUNCATE TABLE stats CASCADE;

INSERT INTO apps (name) VALUES ('ewm-main-service');

INSERT INTO stats (app_id, uri, ip, created) VALUES
(1, '/events', '121.0.0.1', NOW() - INTERVAL '1 hour'),
(1, '/events', '121.0.0.1', NOW() - INTERVAL '50 minutes'),
(1, '/events', '121.0.0.1', NOW() - INTERVAL '40 minutes');

INSERT INTO stats (app_id, uri, ip, created) VALUES
(1, '/events/1', '192.168.1.4', NOW() - INTERVAL '30 minutes'),
(1, '/events/2', '192.168.1.5', NOW() - INTERVAL '15 minutes');