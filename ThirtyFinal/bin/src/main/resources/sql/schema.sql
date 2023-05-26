-- Tabla que almacena los roles o autoridades de los usuarios
CREATE TABLE AUTHORITIES
(ID BIGINT NOT NULL, -- Identificador único de la autoridad
AUTHORITY VARCHAR(255) NOT NULL, -- Nombre del rol o autoridad
PRIMARY KEY (ID),
CONSTRAINT UK_q0u5f2cdlshec8tlh6818bhbk UNIQUE (AUTHORITY) );-- La autoridad debe ser única

-- Tabla que almacena los usuarios
CREATE TABLE USERS
(ID BIGINT NOT NULL, -- Identificador único del usuario
PASSWORD VARCHAR(255) NOT NULL, -- Contraseña del usuario
USERNAME VARCHAR(255) NOT NULL, -- Nombre de usuario del usuario
EMAIL VARCHAR(255) NOT NULL, -- Campo de email que no puede ser nulo
ENABLED BOOLEAN NOT NULL, -- Indica si el usuario está habilitado o no
PRIMARY KEY (ID),
CONSTRAINT UK_r43af9ap4edm43mmtq01oddj6 UNIQUE (USERNAME) );-- El nombre de usuario debe ser único

-- Tabla que almacena las publicaciones o posts
CREATE TABLE POSTS
(ID BIGINT NOT NULL, -- Identificador único del post
BODY TEXT NOT NULL, -- Cuerpo o contenido del post
CREATION_DATE TIMESTAMP NOT NULL, -- Fecha y hora de creación del post
TITLE VARCHAR(255) NOT NULL, -- Título del post
USER_ID BIGINT NOT NULL, -- Identificador del usuario que creó el post
IMAGE BLOB, -- Nueva columna para almacenar imágenes
PRIMARY KEY (ID),
CONSTRAINT FK5lidm6cqbc7u4xhqpxm898qme
FOREIGN KEY (USER_ID)
REFERENCES USERS );-- La clave foránea referencia a la tabla USERS

-- Tabla que almacena los comentarios de los usuarios en los posts
CREATE TABLE COMMENTS
(ID BIGINT NOT NULL, -- Identificador único del comentario
BODY TEXT NOT NULL, -- Cuerpo o contenido del comentario
CREATION_DATE TIMESTAMP NOT NULL, -- Fecha y hora de creación del comentario
POST_ID BIGINT NOT NULL, -- Identificador del post al que se refiere el comentario
USER_ID BIGINT NOT NULL, -- Identificador del usuario que hizo el comentario
PRIMARY KEY (ID),
CONSTRAINT FKh4c7lvsc298whoyd4w9ta25cr
FOREIGN KEY (POST_ID)
REFERENCES POSTS, -- La clave foránea referencia a la tabla POSTS
CONSTRAINT FK8omq0tc18jd43bu5tjh6jvraq
FOREIGN KEY (USER_ID)
REFERENCES USERS); -- La clave foránea referencia a la tabla USERS

-- Tabla que almacena la relación entre los usuarios y sus autoridades o roles
CREATE TABLE USERS_AUTHORITIES
(USER_ID BIGINT NOT NULL, -- Identificador del usuario
AUTHORITY_ID BIGINT NOT NULL, -- Identificador de la autoridad o rol
PRIMARY KEY (USER_ID, AUTHORITY_ID),
CONSTRAINT FKdsfxx5g8x8mnxne1fe0yxhjhq
FOREIGN KEY (AUTHORITY_ID)
REFERENCES AUTHORITIES, -- La clave foránea referencia a la tabla AUTHORITIES
CONSTRAINT FKq3lq694rr66e6kpo2h84ad92q
FOREIGN KEY (USER_ID)
REFERENCES USERS); -- La clave foránea referencia a la tabla USERS
ALTER TABLE users ALTER COLUMN email varchar(255) NULL;

