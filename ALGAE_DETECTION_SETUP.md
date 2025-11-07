# Algae Detection Setup Guide

## ✅ Changes Made

The robot is now configured to **detect ALGAE by default**!

### What Changed:
1. **Default Pipeline:** Limelight starts on Pipeline 1 (Algae) when robot boots
2. **Driver Controls Added:** Easy switching between Coral and Algae detection

---

## 🎮 Controller Button Layout

### Driver Controller:

**Vision Controls:**
- **B button (HOLD)** → Auto-drive to detected game piece
- **Left Bumper (PRESS)** → Switch to Coral detection (Pipeline 0)
- **Left Trigger (PRESS)** → Switch to Algae detection (Pipeline 1) ✅ DEFAULT

**Other Controls:**
- **A** → Zero gyro
- **X** → Coral out
- **Y** → Coral stack
- **Right Bumper** → Algae in
- **Right Trigger** → Algae out
- **Start** → Zero gyro with alliance

### Operator Controller:
- **Left Bumper** → Arm up
- **Left Trigger** → Arm down
- **POV Up** → Climber up
- **POV Down** → Climber down

---

## 📋 Calibration Steps for Algae

### 1. Access Limelight Web Interface
- Connect to robot WiFi
- Open browser: `http://limelight.local:5801`

### 2. Configure Pipeline 1 (Algae)

**Select Pipeline 1:**
- Click "Pipeline" dropdown → Select "1"

**Set Mode:**
- Choose "Color" mode (or "Detector" if using neural network)

**Tune HSV for GREEN Algae:**

Place an algae game piece in front of the camera, then adjust:

```
Hue:        40-80    (green color range)
Saturation: 100-255  (vivid green)
Value:      50-255   (medium to bright)
```

**Adjust until ONLY the algae lights up green in the camera view!**

**Tune Contours:**
- **Area Min:** ~1% (filters noise)
- **Area Max:** ~100%
- **Aspect Ratio:** Adjust based on algae shape
- **Fullness:** Adjust based on how "solid" the algae appears

**Save:**
- Name: "Algae"
- Slot: 1

### 3. Optional: Configure Pipeline 0 (Coral)

If you also want coral detection:

**Select Pipeline 0:**
- Click "Pipeline" dropdown → Select "0"

**Tune HSV for ORANGE/RED Coral:**

```
Hue:        0-30     (red/orange range)
Saturation: 100-255  (vivid color)
Value:      50-255   (medium to bright)
```

**Save:**
- Name: "Coral"
- Slot: 0

### 4. Adjust Camera Settings

**Input Tab:**
- **Exposure:** 10-20ms (lower = less blur)
- **Brightness:** Adjust so algae is clearly visible
- **Black Level Offset:** 0
- **Red Balance:** 1.0
- **Blue Balance:** 1.0

**LED Settings:**
- **LED Mode:** Pipeline (recommended)
- Or set to "On" for consistent lighting

---

## 🧪 Testing Algae Detection

### After Deploying Code:

1. **Deploy to robot:**
   ```cmd
   gradlew deploy
   ```

2. **Open SmartDashboard/Shuffleboard**

3. **Place algae in front of robot**

4. **Check these values:**
   - `Limelight/HasTarget` → Should be `true` ✅
   - `Limelight/TX` → -27 to +27 (horizontal angle)
   - `Limelight/TY` → -20 to +20 (vertical angle)
   - `Limelight/Pipeline` → Should show `1` (Algae) ✅
   - `Limelight/Distance` → Distance in meters
   - `Limelight/Angle` → Angle to algae

5. **Move algae around** → Values should update in real-time

### Test Auto-Drive:

1. **Enable robot**
2. **Place algae 2-3 meters away**
3. **Hold B button** on driver controller
4. **Robot should:**
   - ✅ Rotate to face algae
   - ✅ Drive forward toward algae
   - ✅ Stop at ~0.3m from algae
5. **Release B** to stop

---

## 🔧 Switching Between Algae and Coral

During a match, you can easily switch:

**To detect Algae (green):**
- Press **Left Trigger** on driver controller
- Check SmartDashboard: `Limelight/Pipeline` = 1

**To detect Coral (orange):**
- Press **Left Bumper** on driver controller
- Check SmartDashboard: `Limelight/Pipeline` = 0

**Then use B button to auto-drive to whichever you selected!**

---

## 📏 Measure Camera for Accuracy

For accurate distance calculations:

### 1. Camera Height
Measure from ground to camera lens center:
```java
// Update in Constants.java line 75:
CAMERA_HEIGHT_METERS = 0.XX; // YOUR MEASUREMENT
```

### 2. Camera Tilt Angle
Measure angle camera is tilted (0° = horizontal):
```java
// Update in Constants.java line 76:
CAMERA_PITCH_DEGREES = X.X; // YOUR MEASUREMENT
```

### 3. Algae Height
Measure algae height from ground:
```java
// Update in Constants.java line 80:
ALGAE_HEIGHT_METERS = 0.XX; // YOUR MEASUREMENT
```

---

## 🎯 Expected HSV Values for Common Game Pieces

### Algae (Green):
```
Good starting point:
Hue:        50-70
Saturation: 150-255
Value:      80-255
```

### Coral (Orange):
```
Good starting point:
Hue:        5-20
Saturation: 150-255
Value:      100-255
```

**Note:** Adjust based on your specific arena lighting!

---

## ⚠️ Troubleshooting

**"HasTarget" always false:**
- Check HSV values - too restrictive?
- Check exposure - too dark/bright?
- Verify algae is in camera view
- Check pipeline is set to 1

**Wrong distance readings:**
- Measure and update camera height in Constants.java
- Measure and update camera angle in Constants.java
- Calibrate distance constant (see main calibration guide)

**Robot drives wrong direction:**
- Check camera orientation
- Verify swerve drive is working correctly
- Test manual driving first

**Switches to wrong pipeline:**
- Check SmartDashboard `Limelight/Pipeline` value
- Press Left Trigger to ensure Pipeline 1 (Algae)

---

## 🚀 Quick Start Summary

1. ✅ Code is set to detect **Algae by default** (Pipeline 1)
2. ✅ Access Limelight: `http://limelight.local:5801`
3. ✅ Tune Pipeline 1 for **green algae** (Hue 40-80)
4. ✅ Deploy code: `gradlew deploy`
5. ✅ Check SmartDashboard for detection
6. ✅ Hold **B button** to auto-drive to algae
7. ✅ Use **Left Trigger/Bumper** to switch pipelines

You're ready to detect algae! 🟢
