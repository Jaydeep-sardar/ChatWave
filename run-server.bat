@echo off
echo Starting ChatWave Server...

rem Check if classes directory exists
if not exist classes (
    echo Classes directory not found. Please run compile.bat first.
    pause
    exit /b 1
)

rem Create files directory if it doesn't exist
if not exist files mkdir files

rem Run the server
echo Server will start on port %1 (default: 12345)
java -cp classes server.ChatServer %1

pause
