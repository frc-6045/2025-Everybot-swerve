#!/bin/bash
# Troubleshooting script for build errors on another computer

echo "========================================="
echo "  FRC Robot Code Troubleshooting Script"
echo "========================================="
echo ""

# Check Java version
echo "1. Checking Java version..."
if command -v java &> /dev/null; then
    java_version=$(java -version 2>&1 | head -1)
    echo "   $java_version"
    java -version 2>&1 | grep -q "17\." && echo "   ✓ Java 17 detected" || echo "   ⚠ Warning: Java 17 required, other version detected"
else
    echo "   ✗ Java not found in PATH"
fi
echo ""

# Check if Gradle wrapper exists
echo "2. Checking Gradle wrapper..."
if [ -f "./gradlew" ]; then
    echo "   ✓ Gradle wrapper found"
    ./gradlew --version 2>&1 | grep "Gradle" | head -1
else
    echo "   ✗ Gradle wrapper NOT found"
fi
echo ""

# Check if LimelightHelpers exists
echo "3. Checking LimelightHelpers.java..."
if [ -f "src/main/java/frc/robot/LimelightHelpers.java" ]; then
    filesize=$(ls -lh src/main/java/frc/robot/LimelightHelpers.java | awk '{print $5}')
    linecount=$(wc -l < src/main/java/frc/robot/LimelightHelpers.java)
    echo "   ✓ LimelightHelpers.java found"
    echo "   Size: $filesize, Lines: $linecount"
    if [ "$linecount" -lt 1000 ]; then
        echo "   ⚠ Warning: File seems too small, may be corrupted"
    fi
else
    echo "   ✗ LimelightHelpers.java NOT found - This WILL cause compilation errors!"
    echo "   Fix: Run this command:"
    echo '   curl -L "https://raw.githubusercontent.com/LimelightVision/limelightlib-wpijava/main/LimelightHelpers.java" -o "src/main/java/frc/robot/LimelightHelpers.java"'
fi
echo ""

# Check new subsystems exist
echo "4. Checking vision subsystems..."
missing_files=0
for file in "GamePieceData.java" "LimelightSubsystem.java"; do
    if [ -f "src/main/java/frc/robot/subsystems/$file" ]; then
        echo "   ✓ $file found"
    else
        echo "   ✗ $file NOT found"
        missing_files=$((missing_files + 1))
    fi
done
if [ $missing_files -gt 0 ]; then
    echo "   ⚠ $missing_files file(s) missing - Try: git pull"
fi
echo ""

# Check DriveToGamePieceCommand
echo "5. Checking DriveToGamePieceCommand..."
if [ -f "src/main/java/frc/robot/commands/DriveToGamePieceCommand.java" ]; then
    echo "   ✓ DriveToGamePieceCommand.java found"
else
    echo "   ✗ DriveToGamePieceCommand.java NOT found"
    echo "   Try: git pull"
fi
echo ""

# Check vendordeps
echo "6. Checking vendor dependencies..."
vendorcount=$(ls -1 vendordeps/*.json 2>/dev/null | wc -l | xargs)
echo "   Found $vendorcount vendor dependency files"
if [ "$vendorcount" -lt 5 ]; then
    echo "   ⚠ Warning: Expected at least 10 vendordeps"
fi
echo ""

# Check git status
echo "7. Checking git status..."
if command -v git &> /dev/null; then
    current_branch=$(git branch --show-current 2>/dev/null)
    if [ -n "$current_branch" ]; then
        echo "   Current branch: $current_branch"
        git_status=$(git status --short 2>/dev/null | wc -l | xargs)
        echo "   Modified/untracked files: $git_status"

        # Check if behind remote
        git fetch --dry-run 2>&1 | grep -q "up to date" && echo "   ✓ Up to date with remote" || echo "   ⚠ May need to pull latest changes"
    else
        echo "   Not a git repository"
    fi
else
    echo "   Git not available"
fi
echo ""

# Try a clean build
echo "8. Attempting clean build..."
echo "   Running: ./gradlew clean build"
echo ""
if ./gradlew clean build 2>&1 | tee /tmp/gradle_build.log; then
    echo ""
    echo "   ✓✓✓ BUILD SUCCESSFUL ✓✓✓"
    echo ""
    warnings=$(grep -i "warning" /tmp/gradle_build.log | wc -l | xargs)
    if [ "$warnings" -gt 0 ]; then
        echo "   ⚠ $warnings warning(s) found (usually safe to ignore)"
    fi
else
    echo ""
    echo "   ✗✗✗ BUILD FAILED ✗✗✗"
    echo ""
    echo "   Error details:"
    tail -30 /tmp/gradle_build.log
    echo ""
    echo "   For full details run: ./gradlew build --stacktrace"
fi
echo ""

# Summary
echo "========================================="
echo "  Troubleshooting Summary"
echo "========================================="
echo ""
echo "If errors persist:"
echo "  1. Pull latest code:     git pull origin Game-piece-detection!"
echo "  2. Refresh dependencies: ./gradlew clean build --refresh-dependencies"
echo "  3. Restart VSCode and wait for Java indexing to complete"
echo "  4. Clean Java workspace: Ctrl+Shift+P → 'Java: Clean Java Language Server Workspace'"
echo ""
echo "For VSCode warnings (yellow triangles):"
echo "  - These are usually safe to ignore if build succeeds"
echo "  - Check Problems panel: View → Problems (Ctrl+Shift+M)"
echo ""
