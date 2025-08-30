@echo off
echo Testing ChatWave Compilation...

rem Compile the project
call compile.bat

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo.
echo Compilation successful! You can now:
echo 1. Run 'run-server.bat' to start the server
echo 2. Run 'run-client.bat' to start the GUI client
echo 3. Run 'run-client.bat -console' to start the console client
echo.
echo Example usage:
echo   run-server.bat 12345
echo   run-client.bat -host localhost -port 12345 -username Alice
echo.

pause
