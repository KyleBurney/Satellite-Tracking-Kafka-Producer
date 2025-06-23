@echo off
echo Setting up Satellite Tracker environment...
echo.

REM Check if Docker is running
docker version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Docker is not running or not installed.
    echo Please install Docker Desktop for Windows and ensure it's running.
    echo.
    pause
    exit /b 1
)

REM Start Kafka and Schema Registry
echo Starting Kafka and Schema Registry...
docker-compose up -d

REM Wait for services to start
echo Waiting for services to start...
timeout /t 10 /nobreak >nul

REM Check if Maven is installed
mvn --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven is not installed.
    echo Please install Maven and add it to your PATH.
    echo Download from: https://maven.apache.org/download.cgi
    echo.
    pause
    exit /b 1
)

echo.
echo Services started successfully!
echo.
echo Next steps:
echo 1. Set your API keys as environment variables:
echo    set N2YO_API_KEY=your-api-key
echo    set SPACETRACK_USERNAME=your-username
echo    set SPACETRACK_PASSWORD=your-password
echo.
echo 2. Compile the project:
echo    mvn clean compile
echo.
echo 3. Run the application:
echo    mvn spring-boot:run
echo.
pause
