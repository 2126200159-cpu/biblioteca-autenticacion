-- =====================================================================
-- Sistema de Biblioteca - Agrega login, roles y permisos a una base YA creada
-- Autor: Joshua Odin Montoya Flores
-- Úsalo SOLO si ya tenías biblioteca_db con los scripts anteriores y no
-- quieres borrarla. (Si ejecutas el 01 actualizado, este archivo no hace falta.)
-- Las contraseñas que ya estaban en texto plano siguen funcionando: el
-- sistema las convierte a hash PBKDF2 en el primer inicio de sesión.
-- =====================================================================
USE biblioteca_db;

CREATE TABLE IF NOT EXISTS rol (
    nombre      VARCHAR(30)  NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    PRIMARY KEY (nombre)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS permiso (
    codigo      VARCHAR(40)  NOT NULL,
    descripcion VARCHAR(120) NOT NULL,
    PRIMARY KEY (codigo)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS rol_permiso (
    rol     VARCHAR(30) NOT NULL,
    permiso VARCHAR(40) NOT NULL,
    PRIMARY KEY (rol, permiso),
    CONSTRAINT fk_rolperm_rol
        FOREIGN KEY (rol) REFERENCES rol (nombre) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_rolperm_permiso
        FOREIGN KEY (permiso) REFERENCES permiso (codigo) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS sesion_log (
    id_sesion  INT      NOT NULL AUTO_INCREMENT,
    id_persona INT      NOT NULL,
    inicio     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fin        DATETIME NULL,
    PRIMARY KEY (id_sesion),
    CONSTRAINT fk_sesion_persona
        FOREIGN KEY (id_persona) REFERENCES persona (id_persona) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB;

INSERT IGNORE INTO rol (nombre, descripcion) VALUES
    ('Administrador', 'Control total: gestiona todos los módulos y las cuentas de administrador.'),
    ('Bibliotecario', 'Operación diaria: consulta, registra y edita; no elimina ni gestiona administradores.'),
    ('Consultor',     'Solo lectura del catálogo (libros, categorías y editoriales).');

INSERT IGNORE INTO permiso (codigo, descripcion) VALUES
    ('PERSONAS_CONSULTAR',      'Ver el módulo de personas'),
    ('PERSONAS_CREAR',          'Registrar personas'),
    ('PERSONAS_EDITAR',         'Modificar personas'),
    ('PERSONAS_ELIMINAR',       'Eliminar personas'),
    ('PERSONAS_GESTIONAR_ADMIN','Crear, editar y eliminar cuentas de administrador'),
    ('LIBROS_CONSULTAR',        'Ver libros'),
    ('LIBROS_CREAR',            'Registrar libros'),
    ('LIBROS_EDITAR',           'Modificar libros'),
    ('LIBROS_ELIMINAR',         'Eliminar libros'),
    ('CATEGORIAS_CONSULTAR',    'Ver categorías'),
    ('CATEGORIAS_CREAR',        'Registrar categorías'),
    ('CATEGORIAS_EDITAR',       'Modificar categorías'),
    ('CATEGORIAS_ELIMINAR',     'Eliminar categorías'),
    ('EDITORIALES_CONSULTAR',   'Ver editoriales'),
    ('EDITORIALES_CREAR',       'Registrar editoriales'),
    ('EDITORIALES_EDITAR',      'Modificar editoriales'),
    ('EDITORIALES_ELIMINAR',    'Eliminar editoriales');

INSERT IGNORE INTO rol_permiso (rol, permiso)
    SELECT 'Administrador', codigo FROM permiso;
INSERT IGNORE INTO rol_permiso (rol, permiso)
    SELECT 'Bibliotecario', codigo FROM permiso
    WHERE codigo NOT LIKE '%\_ELIMINAR' AND codigo <> 'PERSONAS_GESTIONAR_ADMIN';
INSERT IGNORE INTO rol_permiso (rol, permiso) VALUES
    ('Consultor', 'LIBROS_CONSULTAR'),
    ('Consultor', 'CATEGORIAS_CONSULTAR'),
    ('Consultor', 'EDITORIALES_CONSULTAR');

-- Los administradores existentes pasan a un rol válido (el primero queda como Administrador)
UPDATE administrador SET rol = 'Bibliotecario'
    WHERE rol NOT IN (SELECT nombre FROM rol);
UPDATE administrador SET rol = 'Administrador'
    WHERE id_persona = (SELECT m FROM (SELECT MIN(id_persona) AS m FROM administrador) t);

-- El correo se usa para iniciar sesión: debe ser único
ALTER TABLE persona ADD UNIQUE KEY uq_persona_correo (correo);

-- El rol del administrador ahora depende del catálogo de roles
ALTER TABLE administrador
    ADD CONSTRAINT fk_administrador_rol
    FOREIGN KEY (rol) REFERENCES rol (nombre) ON DELETE RESTRICT ON UPDATE CASCADE;

-- Cuenta inicial si la tabla administrador estaba vacía
-- (ID 1, admin@biblioteca.com, contraseña Admin123)
INSERT INTO persona (id_persona, curp, rfc, nombre, apellido, nacimiento, correo, telefono, url_imagen)
    SELECT 1, 'AAAA000101HJCDMN01', 'AAAA000101AB1', 'Administrador', 'del Sistema', '1990-01-01',
           'admin@biblioteca.com', '3300000000', NULL
    WHERE NOT EXISTS (SELECT 1 FROM administrador);
INSERT INTO administrador (id_persona, usuario, contrasena, id_estado, rol, fecha_contratacion, id_jefe)
    SELECT 1, 'admin', 'pbkdf2$65536$fXRyClS+BFdwSBNPgsR9VQ==$JiWddYYR8ra3kNOkT0+Mm9yLVGPrHkrFAByHXNlmTTs=',
           1, 'Administrador', CURRENT_DATE, NULL
    WHERE NOT EXISTS (SELECT 1 FROM administrador);

SELECT p.id_persona, p.correo, a.usuario, a.rol FROM administrador a JOIN persona p ON p.id_persona = a.id_persona;
