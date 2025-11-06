# Troubleshooting Guide: "java:warning" with Lots of Errors

## Situation
- **This computer:** ✅ java:ready, no errors, builds successfully
- **Other computer:** ❌ java:warning, lots of errors (mostly com.pathplanner related)

## Most Likely Causes (In Order)

### 1. ⭐ CODE NOT PULLED FROM GITHUB (MOST COMMON)
The other computer doesn't have the latest code!

**Fix:**
```bash
git fetch origin
git pull origin Game-piece-detection!
```

**Verify files exist:**
```bash
ls -lh src/main/java/frc/robot/LimelightHelpers.java
# Should show: 59K file size

ls src/main/java/frc/robot/subsystems/
# Should show: GamePieceData.java and LimelightSubsystem.java
```

---

### 2. ⭐ GRADLE DEPENDENCIES NOT DOWNLOADED
Gradle hasn't downloaded the required libraries yet.

**Fix:**
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

This will download all vendordeps and libraries.

---

### 3. ⭐ JAVA LANGUAGE SERVER CACHE CORRUPT
VSCode's Java indexer has stale/corrupt cache.

**Fix:**
```
1. Press Ctrl+Shift+P (or Cmd+Shift+P on Mac)
2. Type: "Java: Clean Java Language Server Workspace"
3. Click "Reload and delete"
4. Wait 2-3 minutes for re-indexing to complete
```

---

### 4. WRONG JAVA VERSION
Other computer might have Java 11 or Java 21 instead of Java 17.

**Check:**
```bash
java -version
# Should show: Java 17.x.x
```

**Fix:** Install WPILib 2025 which includes correct Java 17.

---

### 5. WPILIB NOT INSTALLED OR WRONG VERSION
Other computer might not have WPILib 2025.3.2 installed.

**Check:**
```bash
./gradlew --version
# Look for GradleRIO version 2025.3.2
```

**Fix:** Install WPILib 2025 from https://docs.wpilib.org/

---

### 6. VENDORDEPS OUT OF SYNC
Vendor dependencies (PathPlanner, Phoenix, etc.) might not match.

**Fix:**
```bash
# In VSCode:
Ctrl+Shift+P → "WPILib: Manage Vendor Libraries" → "Check for updates (online)"
```

---

## Quick Diagnostic Commands

Run these on the **other computer** in order:

```bash
# 1. Check Java version
java -version

# 2. Pull latest code
git pull origin Game-piece-detection!

# 3. Verify LimelightHelpers exists and is correct size
ls -lh src/main/java/frc/robot/LimelightHelpers.java
wc -l src/main/java/frc/robot/LimelightHelpers.java
# Should show: 1699 lines

# 4. Clean and rebuild
./gradlew clean build --refresh-dependencies

# 5. Run troubleshooting script
./troubleshoot.sh
```

---

## Expected Output (Working Computer)

When everything works, you should see:
```
✓ Java 17 detected
✓ Gradle wrapper found
✓ LimelightHelpers.java found (59K, 1699 lines)
✓ GamePieceData.java found
✓ LimelightSubsystem.java found
✓ DriveToGamePieceCommand.java found
✓✓✓ BUILD SUCCESSFUL ✓✓✓
```

And VSCode status bar should show: **java:ready** ✅

---

## Understanding the Errors

If errors mention:
- **"cannot find symbol: class LimelightHelpers"** → File not pulled from GitHub
- **"package com.pathplanner.lib does not exist"** → Run `./gradlew build` to download dependencies
- **"cannot find symbol: class GamePieceData"** → Files not pulled from GitHub
- **Generic compilation errors** → Java Language Server cache issue

---

## Nuclear Option (If Nothing Else Works)

1. Close VSCode completely
2. Delete these folders:
   ```bash
   rm -rf .vscode/
   rm -rf build/
   rm -rf bin/
   ```
3. Pull fresh code:
   ```bash
   git pull origin Game-piece-detection!
   ```
4. Clean build:
   ```bash
   ./gradlew clean build --refresh-dependencies
   ```
5. Reopen VSCode and wait 3-5 minutes for indexing

---

## Still Having Issues?

Run the troubleshooting script and share the output:
```bash
./troubleshoot.sh > diagnostic_output.txt
```

Then check what specific error messages appear in VSCode:
- Open Problems panel: `View → Problems` (Ctrl+Shift+M)
- Copy the first 5-10 error messages

Most likely it's just: **"Did you run `git pull`?"** 😊
