
INSERT INTO admins (admin_id, email, first_name, last_name, password, role, username) VALUES (1, 'taiwoh@gmai.com', 'Henry', 'Taiwo', 'test4005$', 'ADMIN' , 'taiwoh@gmail.com');
INSERT INTO admins (admin_id, email, first_name, last_name, password, role, username) VALUES (2, 'path@gmai.com', 'Pat', 'Taiwo', 'test4005$', 'ADMIN' , 'path@gmail.com');

INSERT INTO authToken (id, expired, revoked, authToken, token_type, admin_id, user_id) VALUES (1, FALSE, FALSE, 'LSVMSLMDZCKLMCS', 'BEARER', 1, null );
INSERT INTO authToken (id, expired, revoked, authToken, token_type, admin_id, user_id) VALUES (2, FALSE, FALSE, 'LSVMSLMDZCKLMDS', 'BEARER', 1, null );
INSERT INTO authToken (id, expired, revoked, authToken, token_type, admin_id, user_id) VALUES (3, FALSE, FALSE, 'LSVMSLMDZCKLMES', 'BEARER', 2, null );
