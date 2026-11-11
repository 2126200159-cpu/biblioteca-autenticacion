package edu.utj.evnd.poo.bibliotecaproyec.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Operaciones básicas de acceso a datos (CRUD) sobre la base de datos.
 *
 * @param <T> tipo de entidad que gestiona
 * @author Joshua Odin Montoya Flores
 */
public interface Dao<T> {

    /**
     * @return todos los registros
     * @throws SQLException si falla la consulta
     */
    List<T> listar() throws SQLException;

    /**
     * Busca registros que coincidan con un texto.
     *
     * @param texto texto a buscar
     * @return registros que coinciden (lista vacía si no hay)
     * @throws SQLException si falla la consulta
     */
    List<T> buscar(String texto) throws SQLException;

    /**
     * @param entidad registro a insertar
     * @throws SQLException si falla la inserción (p. ej. valor único repetido)
     */
    void insertar(T entidad) throws SQLException;

    /**
     * @param entidad registro con los datos nuevos (se identifica por su id)
     * @throws SQLException si falla la actualización
     */
    void actualizar(T entidad) throws SQLException;

    /**
     * @param entidad registro a eliminar (se identifica por su id)
     * @throws SQLException si falla la eliminación (p. ej. tiene datos relacionados)
     */
    void eliminar(T entidad) throws SQLException;
}
