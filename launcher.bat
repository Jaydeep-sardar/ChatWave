@echo off
title ChatWave Launcher
color 0A

:menu
cls
echo.
echo  ╔══════════════════════════════════════╗
echo  ║            ChatWave Launcher         ║
echo  ║         Real-Time Chat App           ║
echo  ╚══════════════════════════════════════╝
echo.
echo  1. Compile Project
echo  2. Start Server
echo  3. Start Client (GUI)
echo  4. Start Client (Console)
echo  5. View README
echo  6. Exit
echo.
set /p choice="Enter your choice (1-6): "

if "%choice%"=="1" goto compile
if "%choice%"=="2" goto server
if "%choice%"=="3" goto client_gui
if "%choice%"=="4" goto client_console
if "%choice%"=="5" goto readme
if "%choice%"=="6" goto exit

echo Invalid choice. Please try again.
pause
goto menu

:compile
echo.
echo Compiling ChatWave...
call compile.bat
echo.
echo Press any key to return to menu...
pause >nul
goto menu

:server
echo.
set /p port="Enter port (default 12345): "
if "%port%"=="" set port=12345
echo Starting server on port %port%...
call run-server.bat %port%
goto menu

:client_gui
echo.
echo Starting GUI client...
call run-client.bat
goto menu

:client_console
echo.
echo Starting console client...
call run-client.bat -console
goto menu

:readme
echo.
type README.md | more
echo.
echo Press any key to return to menu...
pause >nul
goto menu

:exit
echo.
echo Thanks for using ChatWave!
timeout /t 2 >nul
exit
