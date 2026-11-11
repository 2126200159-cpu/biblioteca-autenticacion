-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 10-11-2026 a las 04:00:03
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `biblioteca_db`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `administrador`
--

CREATE TABLE `administrador` (
  `id_persona` int(11) NOT NULL,
  `usuario` varchar(20) NOT NULL,
  `contrasena` varchar(255) NOT NULL,
  `id_estado` tinyint(3) UNSIGNED NOT NULL DEFAULT 1,
  `rol` varchar(30) NOT NULL,
  `fecha_contratacion` date NOT NULL DEFAULT curdate(),
  `id_jefe` int(11) DEFAULT NULL
) ;

--
-- Volcado de datos para la tabla `administrador`
--

INSERT INTO `administrador` (`id_persona`, `usuario`, `contrasena`, `id_estado`, `rol`, `fecha_contratacion`, `id_jefe`) VALUES
(1002, 'carla_gr', 'pbkdf2$65536$he0uGfPrlk3cLJupe5eLpA==$YVlyV/nTM4nW/4OIdiXiyYef0ToI4YrwSk6yc2QpjMM=', 1, 'Administrador', '2023-06-01', NULL),
(3001, '12ef', 'sde23we', 3, 'Bibliotecario', '2025-11-07', NULL),
(3003, 'elXD', 'pbkdf2$65536$d0OR6ba2HNhm3HfTVAtcEw==$onqR7p1k25JkhAwQwnv25elp+hx93b05cWbR/ww3sMY=', 1, 'Administrador', '2026-11-09', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categoria`
--

CREATE TABLE `categoria` (
  `id_categoria` int(11) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `url_imagen` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `categoria`
--

INSERT INTO `categoria` (`id_categoria`, `nombre`, `descripcion`, `url_imagen`) VALUES
(1, 'Novela', 'Obras de ficción narrativa', NULL),
(3, 'Tecnología', 'Computación e ingeniería', NULL),
(4, 'Terror', 'Monstruos y Ficcion', 'imagenes/categoria_1794179564638.png');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `editorial`
--

CREATE TABLE `editorial` (
  `id_editorial` int(11) NOT NULL,
  `nombre` varchar(80) NOT NULL,
  `nombre_contacto` varchar(60) DEFAULT NULL,
  `cargo_contacto` varchar(40) DEFAULT NULL,
  `direccion` varchar(100) DEFAULT NULL,
  `ciudad` varchar(50) DEFAULT NULL,
  `region` varchar(50) DEFAULT NULL,
  `codigo_postal` varchar(10) DEFAULT NULL,
  `pais` varchar(50) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `sitio_web` varchar(120) DEFAULT NULL,
  `url_imagen` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `editorial`
--

INSERT INTO `editorial` (`id_editorial`, `nombre`, `nombre_contacto`, `cargo_contacto`, `direccion`, `ciudad`, `region`, `codigo_postal`, `pais`, `telefono`, `sitio_web`, `url_imagen`) VALUES
(1, 'Editorial Central', 'Luis Perez', 'Ventas', 'Av. Reforma 100', 'Guadalajara', 'Jalisco', '44100', 'México', '3300001111', 'www.editorialcentral.example', NULL),
(2, 'Ediciones del Sur', 'Marta Gomez', 'Gerente', 'Calle Sur 25', 'Monterrey', 'Nuevo León', '64000', 'México', '8100002222', NULL, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `estado_administrador`
--

CREATE TABLE `estado_administrador` (
  `id_estado` tinyint(3) UNSIGNED NOT NULL,
  `nombre` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `estado_administrador`
--

INSERT INTO `estado_administrador` (`id_estado`, `nombre`) VALUES
(1, 'ACTIVO'),
(4, 'BAJA'),
(2, 'BLOQUEADO'),
(3, 'SUSPENDIDO');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `lector`
--

CREATE TABLE `lector` (
  `id_persona` int(11) NOT NULL,
  `numero_credencial` varchar(15) NOT NULL,
  `multa_pendiente` decimal(8,2) NOT NULL DEFAULT 0.00
) ;

--
-- Volcado de datos para la tabla `lector`
--

INSERT INTO `lector` (`id_persona`, `numero_credencial`, `multa_pendiente`) VALUES
(4001, '34FD', 23.30);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `libro`
--

CREATE TABLE `libro` (
  `id_libro` int(11) NOT NULL,
  `titulo` varchar(120) NOT NULL,
  `autor` varchar(100) NOT NULL,
  `isbn` varchar(17) DEFAULT NULL,
  `id_editorial` int(11) NOT NULL,
  `id_categoria` int(11) NOT NULL,
  `precio` decimal(8,2) NOT NULL DEFAULT 0.00,
  `ejemplares_disponibles` int(11) NOT NULL DEFAULT 0,
  `ejemplares_en_pedido` int(11) NOT NULL DEFAULT 0,
  `nivel_reorden` int(11) NOT NULL DEFAULT 0,
  `descontinuado` tinyint(1) NOT NULL DEFAULT 0,
  `url_imagen` varchar(255) DEFAULT NULL
) ;

--
-- Volcado de datos para la tabla `libro`
--

INSERT INTO `libro` (`id_libro`, `titulo`, `autor`, `isbn`, `id_editorial`, `id_categoria`, `precio`, `ejemplares_disponibles`, `ejemplares_en_pedido`, `nivel_reorden`, `descontinuado`, `url_imagen`) VALUES
(1, 'Introducción a la programación', 'A. Ramirez', '978-000-00-0001-1', 1, 3, 350.00, 0, 0, 2, 1, NULL),
(2, 'Historias del río', 'M. Torres', '978-000-00-0002-2', 2, 1, 220.50, 3, 2, 1, 0, NULL),
(3, 'Fundamentos de física', 'R. Salinas', NULL, 1, 1, 460.00, 0, 4, 2, 0, 'imagenes/libro_1794179373960.png');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `permiso`
--

CREATE TABLE `permiso` (
  `codigo` varchar(40) NOT NULL,
  `descripcion` varchar(120) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `permiso`
--

INSERT INTO `permiso` (`codigo`, `descripcion`) VALUES
('CATEGORIAS_CONSULTAR', 'Ver categorías'),
('CATEGORIAS_CREAR', 'Registrar categorías'),
('CATEGORIAS_EDITAR', 'Modificar categorías'),
('CATEGORIAS_ELIMINAR', 'Eliminar categorías'),
('EDITORIALES_CONSULTAR', 'Ver editoriales'),
('EDITORIALES_CREAR', 'Registrar editoriales'),
('EDITORIALES_EDITAR', 'Modificar editoriales'),
('EDITORIALES_ELIMINAR', 'Eliminar editoriales'),
('LIBROS_CONSULTAR', 'Ver libros'),
('LIBROS_CREAR', 'Registrar libros'),
('LIBROS_EDITAR', 'Modificar libros'),
('LIBROS_ELIMINAR', 'Eliminar libros'),
('PERSONAS_CONSULTAR', 'Ver el módulo de personas'),
('PERSONAS_CREAR', 'Registrar personas'),
('PERSONAS_EDITAR', 'Modificar personas'),
('PERSONAS_ELIMINAR', 'Eliminar personas'),
('PERSONAS_GESTIONAR_ADMIN', 'Crear, editar y eliminar cuentas de administrador');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `persona`
--

CREATE TABLE `persona` (
  `id_persona` int(11) NOT NULL,
  `curp` char(18) NOT NULL,
  `rfc` varchar(13) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `apellido` varchar(50) NOT NULL,
  `nacimiento` date NOT NULL,
  `correo` varchar(80) NOT NULL,
  `telefono` char(10) NOT NULL,
  `url_imagen` varchar(255) DEFAULT NULL
) ;

--
-- Volcado de datos para la tabla `persona`
--

INSERT INTO `persona` (`id_persona`, `curp`, `rfc`, `nombre`, `apellido`, `nacimiento`, `correo`, `telefono`, `url_imagen`) VALUES
(1002, 'GARC900505MJCRMRA5', 'GARC900505XY9', 'Carla', 'Garcia Ramos', '1990-05-05', 'carla@correo.com', '3311112222', NULL),
(3001, 'DAW8U3232IOFJDIEH3', 'PEGKIEOFW3JDE', 'Edson sddw', 'Matinez Gusman', '2007-10-23', 'jcidf@jfid.com', '2134321234', 'imagenes/persona_3001_1794178813537.png'),
(3003, 'WDSDFOI34HDU34UDSI', 'DSAD8734HDF9F', 'Joshua Odin', 'Montoya Flores', '2007-02-12', 'ejemplo@xd.com', '3347509235', 'imagenes/persona_3003_1794278527478.png'),
(4001, 'ASFDS8934EJDI343HC', 'EWR34IPFKSDKR', 'Cesar', 'Flores Martinez', '2005-11-24', 'sdf@isodf.com', '3213482347', 'imagenes/persona_4001_1794178968479.png');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rol`
--

CREATE TABLE `rol` (
  `nombre` varchar(30) NOT NULL,
  `descripcion` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `rol`
--

INSERT INTO `rol` (`nombre`, `descripcion`) VALUES
('Administrador', 'Control total: gestiona todos los módulos y las cuentas de administrador.'),
('Bibliotecario', 'Operación diaria: consulta, registra y edita; no elimina ni gestiona administradores.'),
('Consultor', 'Solo lectura del catálogo (libros, categorías y editoriales).');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rol_permiso`
--

CREATE TABLE `rol_permiso` (
  `rol` varchar(30) NOT NULL,
  `permiso` varchar(40) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `rol_permiso`
--

INSERT INTO `rol_permiso` (`rol`, `permiso`) VALUES
('Administrador', 'CATEGORIAS_CONSULTAR'),
('Administrador', 'CATEGORIAS_CREAR'),
('Administrador', 'CATEGORIAS_EDITAR'),
('Administrador', 'CATEGORIAS_ELIMINAR'),
('Administrador', 'EDITORIALES_CONSULTAR'),
('Administrador', 'EDITORIALES_CREAR'),
('Administrador', 'EDITORIALES_EDITAR'),
('Administrador', 'EDITORIALES_ELIMINAR'),
('Administrador', 'LIBROS_CONSULTAR'),
('Administrador', 'LIBROS_CREAR'),
('Administrador', 'LIBROS_EDITAR'),
('Administrador', 'LIBROS_ELIMINAR'),
('Administrador', 'PERSONAS_CONSULTAR'),
('Administrador', 'PERSONAS_CREAR'),
('Administrador', 'PERSONAS_EDITAR'),
('Administrador', 'PERSONAS_ELIMINAR'),
('Administrador', 'PERSONAS_GESTIONAR_ADMIN'),
('Bibliotecario', 'CATEGORIAS_CONSULTAR'),
('Bibliotecario', 'CATEGORIAS_CREAR'),
('Bibliotecario', 'CATEGORIAS_EDITAR'),
('Bibliotecario', 'EDITORIALES_CONSULTAR'),
('Bibliotecario', 'EDITORIALES_CREAR'),
('Bibliotecario', 'EDITORIALES_EDITAR'),
('Bibliotecario', 'LIBROS_CONSULTAR'),
('Bibliotecario', 'LIBROS_CREAR'),
('Bibliotecario', 'LIBROS_EDITAR'),
('Bibliotecario', 'PERSONAS_CONSULTAR'),
('Bibliotecario', 'PERSONAS_CREAR'),
('Bibliotecario', 'PERSONAS_EDITAR'),
('Consultor', 'CATEGORIAS_CONSULTAR'),
('Consultor', 'EDITORIALES_CONSULTAR'),
('Consultor', 'LIBROS_CONSULTAR');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `sesion_log`
--

CREATE TABLE `sesion_log` (
  `id_sesion` int(11) NOT NULL,
  `id_persona` int(11) NOT NULL,
  `inicio` datetime NOT NULL DEFAULT current_timestamp(),
  `fin` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `sesion_log`
--

INSERT INTO `sesion_log` (`id_sesion`, `id_persona`, `inicio`, `fin`) VALUES
(1, 1002, '2026-11-09 20:37:39', '2026-11-09 20:44:16'),
(2, 3003, '2026-11-09 20:45:55', '2026-11-09 20:47:16'),
(3, 3003, '2026-11-09 20:47:35', '2026-11-09 20:49:11'),
(4, 3003, '2026-11-09 20:49:17', '2026-11-09 20:49:31');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `administrador`
--
ALTER TABLE `administrador`
  ADD PRIMARY KEY (`id_persona`),
  ADD UNIQUE KEY `uq_administrador_usuario` (`usuario`),
  ADD KEY `fk_administrador_estado` (`id_estado`),
  ADD KEY `fk_administrador_jefe` (`id_jefe`),
  ADD KEY `fk_administrador_rol` (`rol`);

--
-- Indices de la tabla `categoria`
--
ALTER TABLE `categoria`
  ADD PRIMARY KEY (`id_categoria`),
  ADD UNIQUE KEY `uq_categoria_nombre` (`nombre`);

--
-- Indices de la tabla `editorial`
--
ALTER TABLE `editorial`
  ADD PRIMARY KEY (`id_editorial`),
  ADD UNIQUE KEY `uq_editorial_nombre` (`nombre`);

--
-- Indices de la tabla `estado_administrador`
--
ALTER TABLE `estado_administrador`
  ADD PRIMARY KEY (`id_estado`),
  ADD UNIQUE KEY `uq_estado_nombre` (`nombre`);

--
-- Indices de la tabla `lector`
--
ALTER TABLE `lector`
  ADD PRIMARY KEY (`id_persona`),
  ADD UNIQUE KEY `uq_lector_credencial` (`numero_credencial`);

--
-- Indices de la tabla `libro`
--
ALTER TABLE `libro`
  ADD PRIMARY KEY (`id_libro`),
  ADD UNIQUE KEY `uq_libro_isbn` (`isbn`),
  ADD KEY `fk_libro_editorial` (`id_editorial`),
  ADD KEY `fk_libro_categoria` (`id_categoria`);

--
-- Indices de la tabla `permiso`
--
ALTER TABLE `permiso`
  ADD PRIMARY KEY (`codigo`);

--
-- Indices de la tabla `persona`
--
ALTER TABLE `persona`
  ADD PRIMARY KEY (`id_persona`),
  ADD UNIQUE KEY `uq_persona_curp` (`curp`),
  ADD UNIQUE KEY `uq_persona_correo` (`correo`);

--
-- Indices de la tabla `rol`
--
ALTER TABLE `rol`
  ADD PRIMARY KEY (`nombre`);

--
-- Indices de la tabla `rol_permiso`
--
ALTER TABLE `rol_permiso`
  ADD PRIMARY KEY (`rol`,`permiso`),
  ADD KEY `fk_rolperm_permiso` (`permiso`);

--
-- Indices de la tabla `sesion_log`
--
ALTER TABLE `sesion_log`
  ADD PRIMARY KEY (`id_sesion`),
  ADD KEY `fk_sesion_persona` (`id_persona`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `categoria`
--
ALTER TABLE `categoria`
  MODIFY `id_categoria` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `editorial`
--
ALTER TABLE `editorial`
  MODIFY `id_editorial` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `estado_administrador`
--
ALTER TABLE `estado_administrador`
  MODIFY `id_estado` tinyint(3) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `libro`
--
ALTER TABLE `libro`
  MODIFY `id_libro` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `sesion_log`
--
ALTER TABLE `sesion_log`
  MODIFY `id_sesion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `administrador`
--
ALTER TABLE `administrador`
  ADD CONSTRAINT `fk_administrador_estado` FOREIGN KEY (`id_estado`) REFERENCES `estado_administrador` (`id_estado`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_administrador_jefe` FOREIGN KEY (`id_jefe`) REFERENCES `administrador` (`id_persona`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_administrador_persona` FOREIGN KEY (`id_persona`) REFERENCES `persona` (`id_persona`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_administrador_rol` FOREIGN KEY (`rol`) REFERENCES `rol` (`nombre`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `lector`
--
ALTER TABLE `lector`
  ADD CONSTRAINT `fk_lector_persona` FOREIGN KEY (`id_persona`) REFERENCES `persona` (`id_persona`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `libro`
--
ALTER TABLE `libro`
  ADD CONSTRAINT `fk_libro_categoria` FOREIGN KEY (`id_categoria`) REFERENCES `categoria` (`id_categoria`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_libro_editorial` FOREIGN KEY (`id_editorial`) REFERENCES `editorial` (`id_editorial`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `rol_permiso`
--
ALTER TABLE `rol_permiso`
  ADD CONSTRAINT `fk_rolperm_permiso` FOREIGN KEY (`permiso`) REFERENCES `permiso` (`codigo`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_rolperm_rol` FOREIGN KEY (`rol`) REFERENCES `rol` (`nombre`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `sesion_log`
--
ALTER TABLE `sesion_log`
  ADD CONSTRAINT `fk_sesion_persona` FOREIGN KEY (`id_persona`) REFERENCES `persona` (`id_persona`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
