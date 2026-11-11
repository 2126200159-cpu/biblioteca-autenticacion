-- =====================================================================
-- Sistema de Biblioteca - Agrega la columna de imagen a libro y editorial
-- Autor: Joshua Odin Montoya Flores
-- Úsalo SOLO si ya habías creado biblioteca_db con el script 01 anterior.
-- (Si vuelves a ejecutar el 01 actualizado, este archivo no hace falta.)
-- =====================================================================
USE biblioteca_db;

ALTER TABLE libro     ADD COLUMN url_imagen VARCHAR(255) NULL;
ALTER TABLE editorial ADD COLUMN url_imagen VARCHAR(255) NULL;

DESCRIBE libro;
DESCRIBE editorial;
