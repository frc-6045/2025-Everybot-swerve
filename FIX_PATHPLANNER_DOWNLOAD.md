# Fix: PathPlanner Download Failure on Other Computer

## Problem
```
Could not GET 'https://3015rangerrobotics.github.io/pathplannerlib/...'
Remote host terminated the handshake
```

This means the other computer **cannot download PathPlanner** from the internet due to:
- Network/firewall blocking GitHub Pages
- School/work network restrictions
- TLS/SSL handshake failure

## Solution: Copy Dependencies from Working Computer

Since **this computer** already downloaded PathPlanner, we can copy it!

---

## Option 1: Use the Dependency Package (EASIEST)

### Step 1: Transfer the file
Copy **`pathplanner-dependencies.tar.gz`** from this computer to the other computer.

### Step 2: Extract on Windows computer

**On the other computer (Windows):**

1. Download and install 7-Zip if you don't have it: https://www.7-zip.org/

2. Right-click `pathplanner-dependencies.tar.gz` → 7-Zip → Extract Here
   (You'll need to extract twice - once for .gz, once for .tar)

3. Copy the extracted `PathplannerLib-java` folder to:
   ```
   C:\Users\[YourUsername]\.gradle\caches\modules-2\files-2.1\com.pathplanner.lib\
   ```

   Full path example:
   ```
   C:\Users\Student\.gradle\caches\modules-2\files-2.1\com.pathplanner.lib\PathplannerLib-java\
   ```

4. Then run:
   ```cmd
   gradlew build --offline
   ```

---

## Option 2: Copy Entire Gradle Cache (More Complete)

### On this computer (Mac):
```bash
cd ~/.gradle/caches/modules-2/files-2.1/
zip -r pathplanner-full.zip com.pathplanner.lib/
```

Transfer `pathplanner-full.zip` to Windows computer.

### On Windows computer:
1. Extract to: `C:\Users\[YourUsername]\.gradle\caches\modules-2\files-2.1\`
2. Run: `gradlew build --offline`

---

## Option 3: Try Different Network

If possible, try:
1. **Mobile hotspot** from phone
2. **Home network** instead of school network
3. **VPN** (connect or disconnect if already connected)

Then run:
```cmd
gradlew clean build --refresh-dependencies
```

---

## Option 4: Update PathPlanner in VSCode

Try installing PathPlanner through VSCode:

1. Press `Ctrl+Shift+P`
2. Type: "WPILib: Manage Vendor Libraries"
3. Select "Check for updates (online)"
4. Let it update PathPlanner

Or manually install:
1. `Ctrl+Shift+P` → "WPILib: Manage Vendor Libraries"
2. "Install new libraries (online)"
3. Enter URL: `https://3015rangerrobotics.github.io/pathplannerlib/PathplannerLib.json`

---

## Option 5: Temporarily Disable Firewall/Antivirus

**⚠️ Only if you trust your network!**

1. Temporarily disable Windows Firewall or antivirus
2. Run: `gradlew clean build --refresh-dependencies`
3. Re-enable firewall after download completes

---

## Verify It Worked

After copying dependencies or fixing network, run:
```cmd
gradlew build
```

Should show:
```
BUILD SUCCESSFUL
```

---

## Still Not Working?

Check if the PathPlanner folder exists:
```cmd
dir C:\Users\%USERNAME%\.gradle\caches\modules-2\files-2.1\com.pathplanner.lib\
```

Should show `PathplannerLib-java` folder with version `2025.2.7` inside.

If not, the extraction didn't work correctly. Try Option 2 (full cache copy).
