@echo off
chcp 65001 >nul
rem Crea una carpeta con un .exe que YA INCLUYE el entorno de ejecucion de Java (no requiere Java instalado).
rem Requiere JDK 14+ (jpackage) y haber corrido 02_compilar_jar.bat. No necesita WiX (es tipo app-image).
cd /d "%~dp0.."
if not exist target\Bibliotecaproyec-1.0-SNAPSHOT.jar (echo Primero ejecuta 02_compilar_jar.bat & pause & exit /b 1)
if "%JAVA_HOME%"=="" (echo Define JAVA_HOME apuntando al JDK. & pause & exit /b 1)
rmdir /s /q target\entrada 2>nul
rmdir /s /q dist 2>nul
mkdir target\entrada
copy target\Bibliotecaproyec-1.0-SNAPSHOT.jar target\entrada\ >nul
jpackage --type app-image --name Biblioteca --input target\entrada ^
  --main-jar Bibliotecaproyec-1.0-SNAPSHOT.jar ^
  --main-class edu.utj.evnd.poo.bibliotecaproyec.Main ^
  --runtime-image "%JAVA_HOME%" --dest dist
if errorlevel 1 (echo ERROR al crear el paquete. & pause & exit /b 1)
echo.
echo Listo: dist\Biblioteca\Biblioteca.exe  (copia toda la carpeta dist\Biblioteca a otro equipo)
pause
