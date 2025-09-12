TRUNCATE TABLE stats;

INSERT INTO stats (app, uri, ip, created) VALUES
('ewm-main-service', '/events', '121.0.0.1', NOW() - INTERVAL '1 hour'),
('ewm-main-service', '/events', '121.0.0.1', NOW() - INTERVAL '50 minutes'),
('ewm-main-service', '/events', '121.0.0.1', NOW() - INTERVAL '40 minutes'),
('ewm-main-service', '/events/1', '192.168.1.4', NOW() - INTERVAL '30 minutes'),
('ewm-main-service', '/events/2', '192.168.1.5', NOW() - INTERVAL '15 minutes');