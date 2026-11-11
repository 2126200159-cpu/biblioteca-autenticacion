@echo off
chcp 65001 >nul
cd /d "%~dp0.."
if not exist target\Bibliotecaproyec-1.0-SNAPSHOT.jar (echo Primero ejecuta 02_compilar_jar.bat & pause & exit /b 1)
java -jar target\Bibliotecaproyec-1.0-SNAPSHOT.jar
if errorlevel 1 pause
