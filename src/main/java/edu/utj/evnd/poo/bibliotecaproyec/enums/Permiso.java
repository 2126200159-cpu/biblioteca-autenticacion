package edu.utj.evnd.poo.bibliotecaproyec.enums;

/**
 * Permisos del sistema. Cada constante corresponde a una fila de la tabla
 * {@code permiso}; qué rol tiene cuáles se define en la tabla
 * {@code rol_permiso} (se consulta con {@code PermisoDao}).
 *
 * @author Joshua Odin Montoya Flores
 */
public enum Permiso {
    PERSONAS_CONSULTAR,
    PERSONAS_CREAR,
    PERSONAS_EDITAR,
    PERSONAS_ELIMINAR,
    /** Crear, modificar o eliminar cuentas de tipo administrador. */
    PERSONAS_GESTIONAR_ADMIN,
    LIBROS_CONSULTAR,
    LIBROS_CREAR,
    LIBROS_EDITAR,
    LIBROS_ELIMINAR,
    CATEGORIAS_CONSULTAR,
    CATEGORIAS_CREAR,
    CATEGORIAS_EDITAR,
    CATEGORIAS_ELIMINAR,
    EDITORIALES_CONSULTAR,
    EDITORIALES_CREAR,
    EDITORIALES_EDITAR,
    EDITORIALES_ELIMINAR;

    /**
     * @param modulo módulo del sistema
     * @param accion acción sobre el módulo
     * @return el permiso {@code MODULO_ACCION}
     */
    public static Permiso de(Modulo modulo, Accion accion) {
        return valueOf(modulo.name() + "_" + accion.name());
    }
}
