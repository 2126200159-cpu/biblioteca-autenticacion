@echo off
chcp 65001 >nul
cd /d "%~dp0.."
echo ===== Limpiar, compilar y generar el JAR ejecutable =====
call mvn clean package
if errorlevel 1 (echo. & echo ERROR: la compilacion fallo. & pause & exit /b 1)
echo.
echo Listo: target\Bibliotecaproyec-1.0-SNAPSHOT.jar
echo Ejecutalo con 03_ejecutar_jar.bat
pause
