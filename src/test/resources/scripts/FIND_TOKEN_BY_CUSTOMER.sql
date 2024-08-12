INSERT INTO customers (customer_id, email, first_name, is_suspended, last_name, password, phone, role, username, account_account_id) VALUES (1, 'taiwoh782@gmai.com', 'Henry', FALSE, 'Taiwo', 'test4005$', '08159602684', 'USER' , 'taiwoh782@gmail.com', null);

INSERT INTO customers (customer_id, email, first_name, is_suspended, last_name, password, phone, role, username, account_account_id) VALUES (2, 'pat782@gmai.com', 'Pat', FALSE, 'Taiwo', 'test4005$', '08159602685', 'USER' , 'pat782@gmail.com', null);

INSERT INTO authToken (id, expired, revoked, authToken, token_type, admin_id, user_id) VALUES (1, FALSE, FALSE, 'LSVMSLMDZCKLMCS', 'BEARER', null, 1 );
INSERT INTO authToken (id, expired, revoked, authToken, token_type, admin_id, user_id) VALUES (2, FALSE, FALSE, 'LSVMSLMDZCKLMDS', 'BEARER', null, 1 );
INSERT INTO authToken (id, expired, revoked, authToken, token_type, admin_id, user_id) VALUES (3, FALSE, FALSE, 'LSVMSLMDZCKLMES', 'BEARER', null, 2 );
