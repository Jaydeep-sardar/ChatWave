@echo off
echo Starting ChatWave Client...

rem Check if classes directory exists
if not exist classes (
    echo Classes directory not found. Please run compile.bat first.
    pause
    exit /b 1
)

rem Run the client with all arguments passed through
java -cp classes client.ChatClient %*

if not "%1"=="-console" (
    pause
)
