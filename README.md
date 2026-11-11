# Sistema de Biblioteca – CRUD con base de datos (JavaFX + MariaDB)

Aplicación de escritorio para gestionar una biblioteca. Los datos se guardan en
una base de datos **MariaDB/MySQL** (`biblioteca_db`) mediante JDBC, y las
imágenes se copian a una carpeta del proyecto guardando su ruta en la base.

**Autor:** Joshua Odin Montoya Flores  
**Institución:** Universidad Tecnológica de Jalisco (UTJ) – Programación Orientada a Objetos

## Tecnologías

- Java 17 o superior
- JavaFX (FXML, `TableView`, `ComboBox`, `DatePicker`, `FileChooser`, `ImageView`, `TextFormatter`)
- JDBC con MariaDB Connector/J
- MariaDB 10.4 / MySQL 8 (XAMPP + phpMyAdmin)
- Maven y NetBeans

## Base de datos

Los scripts están en [`base_de_datos/`](base_de_datos/):

| Archivo | Para qué sirve |
|---|---|
| `01_crear_base_de_datos.sql` | Crea `biblioteca_db` con sus 7 tablas, llaves primarias, foráneas y restricciones |
| `02_datos_de_prueba.sql` | Datos de ejemplo (opcional) |
| `03_agregar_columnas_imagen.sql` | Solo si la base se creó con una versión anterior del script 01 |
| `04_agregar_autenticacion.sql` | Solo si ya tenías la base creada: agrega roles, permisos, bitácora de sesiones y la cuenta inicial sin borrar tus datos |

Tablas: `estado_administrador`, `persona`, `administrador`, `lector`,
`categoria`, `editorial`, `libro` y las de seguridad `rol`, `permiso`,
`rol_permiso` y `sesion_log`. No se incluyen `préstamo` ni
`detalle_préstamo`. `administrador` y `lector` heredan de `persona`
(su llave primaria es también foránea a `persona`).

La conexión se configura en `dao/ConexionBD.java` (por defecto
`localhost:3306`, usuario `root`, sin contraseña).

## Estructura del proyecto

```
src/main/java/edu/utj/evnd/poo/bibliotecaproyec/
├── model/        Persona (abstract), Administrador, Lector, Libro, Categoria, Editorial, Imagenable
├── enums/        EstadoAdministrador, Modulo, Accion, Permiso
├── dao/          Dao<T>, ConexionBD, PersonaDao, LibroDao, CategoriaDao, EditorialDao,
│                 AutenticacionDao, SesionDao, PermisoDao, AutenticacionException
├── service/      ImagenService (imágenes), ContrasenaService (hash PBKDF2), SesionActual
├── controller/   LoginController, MenuController, CrudController<T> (CRUD, validaciones,
│                 imagen y permisos), PersonaController, LibroController,
│                 CategoriaController, EditorialController
├── App.java, Main.java
src/main/resources/
├── inicio_sesion.fxml, menu.fxml, persona.fxml, libro.fxml, categoria.fxml, editorial.fxml
└── styles/app.css
```

**Separación de responsabilidades:** el *modelo* conserva datos y reglas; los
*DAO* ejecutan las consultas SQL; el *servicio* gestiona las imágenes; los
*controladores* manejan eventos, validaciones y botones; los *FXML/CSS* solo
presentan los controles.

## Vistas (todas las tablas, excepto préstamo y detalle de préstamo)

| Vista | Tablas |
|---|---|
| Personas | `persona` + `administrador` + `lector` en una sola pantalla; el combo **Tipo** muestra los campos del administrador o del lector |
| Libros | `libro` (con listas de editorial y categoría cargadas desde la base) |
| Categorías | `categoria` |
| Editoriales | `editorial` |

Cada vista tiene formulario, tabla de solo lectura y los botones Nuevo, Guardar,
Editar, Eliminar, Validar y Limpiar / Cancelar, además de búsqueda
(**Buscar** / **Mostrar todos**) hecha con consultas SQL (`LIKE`).

## Gestión de imágenes

1. **Seleccionar:** `FileChooser` (png, jpg, jpeg, gif; máximo 5 MB).
2. **Vista previa:** la imagen se carga en un `ImageView`.
3. **Guardar:** se **copia** a `Documentos/SistemaBiblioteca/imagenes/` con nombre
   único (`prefijo_marcaDeTiempo.ext`) y el **nombre del archivo** se guarda en la
   columna `url_imagen` al hacer el `INSERT`.
4. **Actualizar:** si se elige otra imagen, se copia la nueva, se guarda su ruta
   con el `UPDATE` y se borra la anterior. Si no se elige otra, se conserva la actual.
5. **Mostrar:** al seleccionar un registro de la tabla, su imagen se carga en el `ImageView`.
6. **Eliminar:** la imagen solo se borra del disco **después** de que el
   `DELETE` se ejecutó correctamente en la base. Si la base rechaza la
   operación (p. ej. el registro está relacionado), la imagen se conserva.
   Del mismo modo, si el `INSERT`/`UPDATE` falla, se borra la copia recién creada.

Los registros antiguos que guardaban `imagenes/archivo.png` siguen funcionando:
se busca ese nombre de archivo en la carpeta nueva (copia ahí las imágenes de ejemplo
de la carpeta `imagenes/` del proyecto si quieres verlas).

## Almacenamiento externo, log y distribución

`AlmacenamientoService` crea al arrancar (si no existen) y registra en el log:

```
Documentos/SistemaBiblioteca/                      carpeta principal
Documentos/SistemaBiblioteca/imagenes/             imágenes
Documentos/SistemaBiblioteca/log_directorios.txt   log: fecha | operación | ruta | resultado
Documentos/SistemaBiblioteca/configuracion.properties   conexión a la base (opcional editar)
```

Si las carpetas ya existen se continúa normal (el log anota «ya existe»). Si se
borra `imagenes/` con la app abierta, se recrea al guardar la siguiente imagen.
En Windows la ruta de Documentos se consulta al sistema (respeta OneDrive).

La conexión usa por defecto `localhost:3306`, `root`, sin contraseña; para otro
servidor edita `configuracion.properties` (`db.url`, `db.usuario`, `db.clave`).

Scripts en [`distribucion/`](distribucion/) (Windows):

| Script | Qué hace |
|---|---|
| `01_verificar_entorno.bat` | Java/JDK, versión, JAVA_HOME, Maven y puerto 3306 de MySQL |
| `02_compilar_jar.bat` | `mvn clean package` → `target/Bibliotecaproyec-1.0-SNAPSHOT.jar` (JAR con JavaFX y driver incluidos) |
| `03_ejecutar_jar.bat` | `java -jar` del JAR, fuera del IDE |
| `04_crear_paquete_con_java.bat` | `jpackage` → `dist/Biblioteca/Biblioteca.exe` con Java incluido (no requiere instalar Java) |

El JAR trae las librerías nativas de JavaFX del sistema donde se compila; compílalo
en el mismo sistema operativo donde se va a usar. Siempre hay que tener MySQL/MariaDB
encendido (XAMPP) con la base `biblioteca_db` importada.

## Validaciones en tiempo real

- **Filtros de teclado** (`TextFormatter`): ID y teléfono solo dígitos; CURP y RFC
  solo letras y números en mayúsculas; nombres solo letras; precio y multa
  decimales; etc. Un carácter no permitido se rechaza y se avisa debajo del campo.
- **Mensajes debajo de cada control** (un `Label` por campo): «Este campo es
  obligatorio.» o el formato esperado. Se actualizan al escribir, borrar, pegar o elegir.
- **Botón Validar:** revisa todos los campos y muestra una alerta si son válidos; no guarda nada.
- **Botones:** al iniciar solo están activos Nuevo y Validar; **Guardar** se
  habilita únicamente con el formulario válido; **Editar** y **Eliminar** solo con
  una fila seleccionada.
- Los valores únicos repetidos (ID, CURP, usuario, ISBN…) y los registros
  relacionados los controla la base de datos y se informan con un mensaje claro.

## Ejecución

1. Iniciar **Apache** y **MySQL/MariaDB** en XAMPP.
2. En phpMyAdmin, importar `01_crear_base_de_datos.sql` (y opcionalmente `02_datos_de_prueba.sql`).
   Si ya tenías la base de la actividad anterior, importa solo `04_agregar_autenticacion.sql`.
3. Abrir el proyecto en NetBeans, hacer **Clean and Build** y ejecutar la clase `Main`.
4. Iniciar sesión con la cuenta inicial: **ID `1`** o **`admin@biblioteca.com`**, contraseña **`Admin123`**.

## Autenticación y autorización

### Login
La primera ventana que se abre es `login.fxml` (`LoginController`). Se escribe el
**ID o el correo** y la **contraseña**; `AutenticacionDao` los valida contra la
base de datos y solo deja entrar si la cuenta está **ACTIVA** (las cuentas
BLOQUEADAS, SUSPENDIDAS o dadas de BAJA reciben un mensaje y no entran).
No existe formulario de registro: las cuentas las crea un administrador desde
la vista Personas, y el script `01` deja una cuenta de administrador inicial.

- Las contraseñas **no se guardan en texto plano**: se almacenan con PBKDF2
  (HMAC-SHA256, sal aleatoria) mediante `ContrasenaService`.
- Un mensaje único («ID/correo o contraseña incorrectos») no revela si la cuenta existe.
- Tras 5 intentos fallidos seguidos, el formulario se bloquea 15 segundos.
- Cada inicio y cierre de sesión queda en la tabla `sesion_log`.

### Los tres perfiles de usuario

| | **Administrador** | **Bibliotecario** | **Consultor** |
|---|---|---|---|
| **Responsabilidad** | Dirige y configura el sistema; administra al personal | Opera el catálogo y atiende a los lectores día a día | Consulta información del catálogo |
| Libros | Consultar, registrar, editar, eliminar | Consultar, registrar, editar | Solo consultar |
| Categorías | Consultar, registrar, editar, eliminar | Consultar, registrar, editar | Solo consultar |
| Editoriales | Consultar, registrar, editar, eliminar | Consultar, registrar, editar | Solo consultar |
| Personas: lectores | Consultar, registrar, editar, eliminar | Consultar, registrar, editar | Sin acceso (el módulo no aparece) |
| Personas: administradores (cuentas, roles, estado) | Consultar, registrar, editar, eliminar | Solo consultar | Sin acceso |
| Botones visibles | Nuevo, Guardar, Editar, Eliminar, Validar | Nuevo, Guardar, Editar, Validar (sin Eliminar) | Solo búsqueda y tabla |

Los permisos no están escritos en el código de las pantallas: viven en las tablas
`permiso` y `rol_permiso`, y `PermisoDao` los consulta. Un cambio de permisos de un
rol se hace en la base de datos sin recompilar.

### Autorización en JavaFX
- **Menú:** solo aparecen los módulos que el rol puede consultar (los demás ni se cargan).
- **Botones:** `CrudController` oculta los botones que el rol nunca puede usar y
  habilita los demás según el estado del formulario y el permiso.
- **Acciones:** antes de guardar, actualizar o eliminar se vuelve a validar el permiso
  contra la base (`PermisoDao.tienePermiso`), por si la interfaz fuera manipulada.
- **Cuentas de administrador:** solo quien tiene `PERSONAS_GESTIONAR_ADMIN` puede
  crearlas, editarlas o eliminarlas; el combo **Rol** del formulario se carga desde la tabla `rol`.
- **Protecciones:** un usuario no puede eliminar su propia cuenta, bloquearla ni
  cambiarse el rol con la sesión iniciada. Al editar un administrador, dejar la
  contraseña vacía conserva la actual.

### Cerrar sesión
El botón **Cerrar sesión** de la barra superior pide confirmación, registra el cierre
(`SesionDao`), borra la sesión (`SesionActual`), cierra la ventana principal y vuelve al login.

### DAOs de seguridad
| DAO | Función |
|---|---|
| `AutenticacionDao` | Inicio de sesión: busca por ID o correo, verifica la contraseña, comprueba que la cuenta esté activa y carga los permisos |
| `SesionDao` | Registra el inicio y el cierre de sesión en `sesion_log` |
| `PermisoDao` | Permisos de un rol, validación de un permiso y lista de roles |

### Cuentas para probar (con `02_datos_de_prueba.sql`)
| ID | Correo | Contraseña | Rol | Estado |
|---|---|---|---|---|
| 1 | admin@biblioteca.com | `Admin123` | Administrador | ACTIVO |
| 1001 | joshua@correo.com | `Biblio123` | Bibliotecario | ACTIVO |
| 1003 | pedro@correo.com | `Consul123` | Consultor | ACTIVO |
| 1002 | carla@correo.com | `Consul123` | Consultor | BLOQUEADO (no puede entrar) |

## Evidencias

Capturas en la carpeta [`capturas/`](capturas/): base de datos creada, cada
vista, operaciones CRUD, imágenes y validaciones.
