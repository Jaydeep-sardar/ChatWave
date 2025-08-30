@echo off
echo Compiling ChatWave...

rem Create classes directory
if not exist classes mkdir classes

rem Compile all Java files
javac -d classes -cp src src\shared\*.java src\server\*.java src\client\*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo Classes are in the 'classes' directory.
) else (
    echo Compilation failed!
    pause
)
