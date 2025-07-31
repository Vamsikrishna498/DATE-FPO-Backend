@echo off
echo Starting Farmer Management System with optimized memory settings...

REM Set JVM memory options to prevent OOM errors
set JAVA_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heapdump.hprof

REM Set additional JVM options for better performance
set JAVA_OPTS=%JAVA_OPTS% -XX:+UseStringDeduplication -XX:+UseCompressedOops -XX:+UseCompressedClassPointers

REM Set garbage collection options
set JAVA_OPTS=%JAVA_OPTS% -XX:G1HeapRegionSize=16m -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40

REM Set memory management options
set JAVA_OPTS=%JAVA_OPTS% -XX:+DisableExplicitGC -XX:+UseG1GC -XX:+UseG1GC -XX:MaxGCPauseMillis=200

echo JVM Options: %JAVA_OPTS%
echo.

REM Check if Maven is available
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo Error: Maven is not installed or not in PATH
    echo Please install Maven and try again
    pause
    exit /b 1
)

REM Clean and compile the project
echo Cleaning and compiling the project...
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo Error: Compilation failed
    pause
    exit /b 1
)

REM Start the application with optimized settings
echo Starting Spring Boot application...
call mvn spring-boot:run -Dspring-boot.run.jvmArguments="%JAVA_OPTS%"

pause 