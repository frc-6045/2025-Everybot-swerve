@echo off
REM Troubleshooting script for build errors on another computer (Windows version)

echo =========================================
echo   FRC Robot Code Troubleshooting Script
echo =========================================
echo.

REM Check Java version
echo 1. Checking Java version...
java -version 2>&1 | findstr /C:"version"
if %errorlevel% == 0 (
    echo    [OK] Java found
) else (
    echo    [ERROR] Java not found in PATH
)
echo.

REM Check if Gradle wrapper exists
echo 2. Checking Gradle wrapper...
if exist "gradlew.bat" (
    echo    [OK] Gradle wrapper found
    call gradlew.bat --version 2>NUL | findstr /C:"Gradle"
) else (
    echo    [ERROR] Gradle wrapper NOT found
)
echo.

REM Check if LimelightHelpers exists
echo 3. Checking LimelightHelpers.java...
if exist "src\main\java\frc\robot\LimelightHelpers.java" (
    echo    [OK] LimelightHelpers.java found
    for %%A in ("src\main\java\frc\robot\LimelightHelpers.java") do echo    Size: %%~zA bytes
    findstr /N "^" "src\main\java\frc\robot\LimelightHelpers.java" | find /C ":" > temp_count.txt
    set /p linecount=<temp_count.txt
    echo    Lines: %linecount%
    del temp_count.txt
) else (
    echo    [ERROR] LimelightHelpers.java NOT found - This WILL cause compilation errors!
    echo    Fix: Run this command:
    echo    curl -L "https://raw.githubusercontent.com/LimelightVision/limelightlib-wpijava/main/LimelightHelpers.java" -o "src/main/java/frc/robot/LimelightHelpers.java"
)
echo.

REM Check new subsystems exist
echo 4. Checking vision subsystems...
if exist "src\main\java\frc\robot\subsystems\GamePieceData.java" (
    echo    [OK] GamePieceData.java found
) else (
    echo    [ERROR] GamePieceData.java NOT found
)
if exist "src\main\java\frc\robot\subsystems\LimelightSubsystem.java" (
    echo    [OK] LimelightSubsystem.java found
) else (
    echo    [ERROR] LimelightSubsystem.java NOT found
)
echo.

REM Check DriveToGamePieceCommand
echo 5. Checking DriveToGamePieceCommand...
if exist "src\main\java\frc\robot\commands\DriveToGamePieceCommand.java" (
    echo    [OK] DriveToGamePieceCommand.java found
) else (
    echo    [ERROR] DriveToGamePieceCommand.java NOT found
    echo    Try: git pull
)
echo.

REM Check vendordeps
echo 6. Checking vendor dependencies...
dir /b vendordeps\*.json 2>NUL | find /C ".json"
echo.

REM Check git status
echo 7. Checking git status...
git --version >NUL 2>&1
if %errorlevel% == 0 (
    git branch --show-current 2>NUL
    git status --short 2>NUL
) else (
    echo    Git not available
)
echo.

REM Try a clean build
echo 8. Attempting clean build...
echo    Running: gradlew clean build
echo.
call gradlew.bat clean build
if %errorlevel% == 0 (
    echo.
    echo    *** BUILD SUCCESSFUL ***
    echo.
) else (
    echo.
    echo    *** BUILD FAILED ***
    echo.
    echo    For full details run: gradlew build --stacktrace
)
echo.

REM Summary
echo =========================================
echo   Troubleshooting Summary
echo =========================================
echo.
echo If errors persist:
echo   1. Pull latest code:     git pull origin Game-piece-detection!
echo   2. Refresh dependencies: gradlew clean build --refresh-dependencies
echo   3. Restart VSCode and wait for Java indexing to complete
echo   4. Clean Java workspace: Ctrl+Shift+P -^> 'Java: Clean Java Language Server Workspace'
echo.
echo For VSCode warnings (yellow triangles):
echo   - These are usually safe to ignore if build succeeds
echo   - Check Problems panel: View -^> Problems (Ctrl+Shift+M)
echo.
echo =========================================
echo.
pause
