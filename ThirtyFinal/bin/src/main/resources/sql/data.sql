-- Insertar usuarios
INSERT INTO USERS (ID, PASSWORD, USERNAME, ENABLED)
VALUES (0, '', 'anonymous', true), -- usuario anónimo
(1, '$2a$06$OAPObzhRdRXBCbk7Hj/ot.jY3zPwR8n7/mfLtKIgTzdJa4.6TwsIm', 'admin', true), -- usuario administrador
(2, '$2a$06$OAPObzhRdRXBCbk7Hj/ot.jY3zPwR8n7/mfLtKIgTzdJa4.6TwsIm', 'user', true); -- usuario normal
-- Insertar autoridades
INSERT INTO AUTHORITIES (ID, AUTHORITY)
VALUES (0, 'ROLE_ANONYMOUS'), -- rol para el usuario anónimo
(1, 'ROLE_ADMIN'), -- rol para el usuario administrador
(2, 'ROLE_USER'); -- rol para el usuario normal
-- Asignar autoridades a usuarios
INSERT INTO USERS_AUTHORITIES (USER_ID, AUTHORITY_ID)
VALUES (1, 1), -- asignar rol de administrador al usuario administrador
(1, 2), -- asignar rol de usuario normal al usuario administrador
(2, 2); -- asignar rol de usuario normal al usuario normal
-- Insertar publicaciones
INSERT INTO POSTS (ID, BODY, CREATION_DATE, TITLE, USER_ID)
VALUES (1, 'Lorem ipsum', current_timestamp(), 'title1', 2), -- publicación 1
(2, 'PRUEBA', current_timestamp(), 'title2', 1), -- publicación 2
(3, 'Lorem ipsum', current_timestamp(), 'title3', 2); -- publicación 3
-- Insertar comentarios
INSERT INTO COMMENTS (ID, BODY, CREATION_DATE, POST_ID, USER_ID)
VALUES (1, 'comentras a', current_timestamp(), 1, 0), -- comentario en la publicación 1 del usuario anónimo
(2, 'comentras a', current_timestamp(), 1, 2), -- comentario en la publicación 1 del usuario normal
(3, 'comentras a', current_timestamp(), 1, 0), -- otro comentario en la publicación 1 del usuario anónimo
(4, 'comentras a', current_timestamp(), 2, 0), -- comentario en la publicación 2 del usuario anónimo
(5, 'comentras a', current_timestamp(), 2, 1), -- comentario en la publicación 2 del usuario administrador
(6, 'comentras a', current_timestamp(), 1, 0); -- otro comentario en la publicación 1 del usuario anónimo

