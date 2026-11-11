@echo off
chcp 65001 >nul
echo ===== Verificacion del entorno =====
echo.
echo [1] Java (JRE/JDK) instalado:
where java >nul 2>&1 || (echo     NO se encontro java en el PATH. Instala un JDK 17 o superior. & goto fin)
java -version
echo.
echo [2] Compilador (JDK) - necesario para compilar y para jpackage:
where javac >nul 2>&1 && javac -version || echo     No hay javac: solo hay JRE (sirve para ejecutar el JAR, no para compilar).
echo.
echo [3] JAVA_HOME = %JAVA_HOME%
echo.
echo [4] Maven (o usa el que trae NetBeans):
where mvn >nul 2>&1 && mvn -v || echo     mvn no esta en el PATH (compila con NetBeans: Clean and Build).
echo.
echo [5] MariaDB/MySQL escuchando en el puerto 3306 (XAMPP encendido):
powershell -NoProfile -Command "if ((Test-NetConnection localhost -Port 3306 -WarningAction SilentlyContinue).TcpTestSucceeded) { 'Si, el puerto 3306 responde.' } else { 'NO responde: enciende MySQL en el panel de XAMPP.' }"
echo.
echo Nota: el proyecto se compila con --release 11 y JavaFX 13 (viene como dependencia de Maven,
echo no hace falta instalar JavaFX aparte). Funciona con JDK 11 o superior.
:fin
echo.
pause
