@echo off
chcp 1251 >nul
echo ========================================
echo    Java Compiler & Runner Script
echo ========================================

REM
if "%~1"=="" (
    echo Usage: compile_and_run.bat MainClass.java
    echo Example: compile_and_run.bat UnitTests.java
    pause
    exit /b 1
)

REM
set JAVA_FILE=%~1
set CLASS_NAME=%~n1

echo Compiling %JAVA_FILE%...
echo.

REM
javac -cp ".;lib/*" "%JAVA_FILE%" 2>nul

if errorlevel 1 (
    echo JUnit compilation failed. Trying simple compilation...
    echo.
    
    REM
    javac "%JAVA_FILE%"
    
    if errorlevel 1 (
        echo.
        echo ERROR: Compilation failed!
        echo Check your code for errors.
    ) else (
        echo.
        echo SUCCESS: Simple compilation completed!
        echo Running %CLASS_NAME%...
        echo ========================================
        java "%CLASS_NAME%"
    )
) else (
    echo SUCCESS: JUnit compilation completed!
    echo.
    echo Running JUnit tests...
    echo ========================================
    java -cp ".;lib/*" org.junit.runner.JUnitCore "%CLASS_NAME%"
)

echo.
echo ========================================
pause