-- Insertar usuarios
INSERT INTO USERS (ID, PASSWORD, USERNAME, ENABLED)
VALUES (0, '', 'anonymous', true),
(1, '$2a$06$OAPObzhRdRXBCbk7Hj/ot.jY3zPwR8n7/mfLtKIgTzdJa4.6TwsIm', 'admin', true),
(2, '$2a$06$OAPObzhRdRXBCbk7Hj/ot.jY3zPwR8n7/mfLtKIgTzdJa4.6TwsIm', 'user', true); 
-- Insertar autoridades
INSERT INTO AUTHORITIES (ID, AUTHORITY)
VALUES (0, 'ROLE_ANONYMOUS'), 
(1, 'ROLE_ADMIN'), 
(2, 'ROLE_USER'); 
-- Asignar autoridades a usuarios
INSERT INTO USERS_AUTHORITIES (USER_ID, AUTHORITY_ID)
VALUES (1, 1),
(1, 2), 
(2, 2); 
-- Insertar publicaciones
INSERT INTO POSTS (ID, BODY, CREATION_DATE, TITLE, USER_ID)
VALUES (1, 'Lorem ipsum', current_timestamp(), 'title1', 2),
(2, 'PRUEBA', current_timestamp(), 'title2', 1), 
(3, 'Lorem ipsum', current_timestamp(), 'title3', 2); 
-- Insertar comentarios
INSERT INTO COMMENTS (ID, BODY, CREATION_DATE, POST_ID, USER_ID)
VALUES (1, 'comentras a', current_timestamp(), 1, 0), 
(2, 'comentras a', current_timestamp(), 1, 2), 
(3, 'comentras a', current_timestamp(), 1, 0), 
(4, 'comentras a', current_timestamp(), 2, 0), 
(5, 'comentras a', current_timestamp(), 2, 1), 
(6, 'comentras a', current_timestamp(), 1, 0); 

