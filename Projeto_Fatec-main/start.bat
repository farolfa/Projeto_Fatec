@echo off
setlocal
cd /d "%~dp0"

if exist "C:\Program Files\nodejs\node.exe" (
    set "PATH=%PATH%;C:\Program Files\nodejs"
) else if exist "C:\Program Files (x86)\nodejs\node.exe" (
    set "PATH=%PATH%;C:\Program Files (x86)\nodejs"
) else if exist "%LOCALAPPDATA%\Programs\nodejs\node.exe" (
    set "PATH=%PATH%;%LOCALAPPDATA%\Programs\nodejs"
)

where npm >nul 2>&1
if errorlevel 1 (
    echo Node.js/npm nao encontrado no sistema.
    echo Instale o Node.js LTS e abra este script novamente.
    echo Download: https://nodejs.org/
    exit /b 1
)

set "DB_SERVER=LAPTOP-QRV0HV9A\SQLEXPRESS"
set "DB_NAME=Faztudoja"
set "DB_USER=Teste"
set "SSMS_EXE=C:\Program Files\Microsoft SQL Server Management Studio 22\Release\Common7\IDE\SSMS.exe"
set "BACKEND_DIR=%~dp0back_fatec"
set "FRONTEND_DIR=%~dp0trabalho"
set /a ATTEMPTS=0
set /a MAX_ATTEMPTS=20

if exist "%SSMS_EXE%" (
    echo Abrindo SQL Server Management Studio conectado ao banco...
    start "Banco - SSMS" "%SSMS_EXE%" -S "%DB_SERVER%" -d "%DB_NAME%" -U "%DB_USER%"
) else (
    echo SSMS nao encontrado. O site vai abrir normalmente sem o gerenciador do banco.
)

echo Iniciando backend (Spring Boot)...
start "Backend - Spring Boot" cmd /k "cd /d ""%BACKEND_DIR%"" && mvnw.cmd spring-boot:run"

echo Iniciando frontend (Vite)...
start "Frontend - Vite" cmd /k "cd /d ""%FRONTEND_DIR%"" && npm run dev"

echo Aguardando a API ficar pronta em http://localhost:8080...
:wait_backend
powershell -NoProfile -Command "try { Invoke-WebRequest http://localhost:8080/categoria -TimeoutSec 2 -UseBasicParsing | Out-Null; exit 0 } catch { exit 1 }" >nul 2>&1
if not errorlevel 1 goto backend_ready

set /a ATTEMPTS+=1
if %ATTEMPTS% GEQ %MAX_ATTEMPTS% (
    echo A API nao respondeu apos 60 segundos.
    echo Verifique a janela "Backend - Spring Boot" e confirme se o SQL Server esta ativo.
    start http://localhost:5173
    exit /b 0
)

timeout /t 3 /nobreak >nul
goto wait_backend

:backend_ready
echo Tudo pronto! Abrindo o site...
start http://localhost:5173
