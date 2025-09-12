TRUNCATE TABLE apps CASCADE;
TRUNCATE TABLE stats CASCADE;

INSERT INTO apps (name) VALUES ('ewm-main-service');
INSERT INTO apps (name) VALUES ('ewm-stats-service');
INSERT INTO apps (name) VALUES ('test-client');
INSERT INTO apps (name) VALUES ('n1');
INSERT INTO apps (name) VALUES ('n2');
INSERT INTO apps (name) VALUES ('n3');

INSERT INTO stats (app_id, uri, ip, created) VALUES
(1, '/events', '192.168.1.1', NOW() - INTERVAL '1 hour'),
(1, '/events', '192.168.1.2', NOW() - INTERVAL '50 minutes'),
(1, '/events', '192.168.1.3', NOW() - INTERVAL '40 minutes'),
(1, '/events/1', '192.168.1.4', NOW() - INTERVAL '30 minutes'),
(2, '/stats', '192.168.1.5', NOW() - INTERVAL '15 minutes'),
(1, '/events', '192.168.1.6', NOW()),
(1, '/events/1', '192.168.1.7', NOW() - INTERVAL '20 minutes'),
(1, '/events/2', '192.168.1.8', NOW() - INTERVAL '10 minutes'),
(1, '/events/2', '192.168.1.9', NOW() - INTERVAL '5 minutes'),
(1, '/events/2', '192.168.1.10', NOW());