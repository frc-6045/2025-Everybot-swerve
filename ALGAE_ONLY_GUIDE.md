# Algae Detection - Simplified Guide

## ✅ What This Does

**Robot automatically detects and drives to ALGAE game pieces using Limelight camera.**

Inspired by FRC teams 2056 and 2910's vision approaches.
Simplified to ONLY detect algae - no coral detection complexity.

---

## 🎮 How to Use

### Controller Button:
**Press and hold B button** → Robot automatically:
1. Rotates to face algae
2. Drives forward toward algae
3. Stops at 0.3m from algae

---

## 📋 Setup Steps

### 1. Deploy Code to Robot
```cmd
gradlew deploy
```

### 2. Configure Limelight for Green Algae

**Access Limelight:**
- Connect to robot WiFi
- Open browser: `http://limelight.local:5801`

**Configure Pipeline 0:**
- Select Pipeline 0
- Set to "Color" mode
- Tune HSV for **GREEN** algae:

```
Hue:        40-80     (green color)
Saturation: 150-255   (vivid green)
Value:      80-255    (bright)
```

**Adjust until ONLY the algae lights up green!**

**Tune Contours:**
- Area Min: 1%
- Area Max: 100%
- Aspect Ratio: Based on algae shape
- Fullness: Based on algae solidity

### 3. Measure Camera (Critical for Accuracy!)

**Camera Height:**
```
Measure from ground to camera lens center
Update Constants.java line 75:
CAMERA_HEIGHT_METERS = 0.XX;
```

**Camera Tilt Angle:**
```
Measure angle camera is tilted (0° = horizontal)
Update Constants.java line 76:
CAMERA_PITCH_DEGREES = X.X;
```

**Algae Height:**
```
Measure algae height from ground
Update Constants.java line 79:
ALGAE_HEIGHT_METERS = 0.XX;
```

---

## 🧪 Testing

### Check SmartDashboard Values:

After deploying, open SmartDashboard and look for:

```
Algae/HasTarget    → true when algae detected ✅
Algae/TX           → -27 to +27 (horizontal angle)
Algae/TY           → -20 to +20 (vertical angle)
Algae/Distance     → Distance in meters
Algae/Angle        → Angle to algae
```

### Test Auto-Drive:

1. Enable robot
2. Place algae 2-3 meters away
3. **Hold B button**
4. Robot should drive to algae and stop at ~0.3m
5. Release B button

---

## 🔧 Tuning (If Needed)

### If Robot is Too Fast/Slow:

Edit `Constants.java` lines 86-87:
```java
AUTO_DRIVE_SPEED = 0.3;  // Lower = slower forward speed
AUTO_TURN_SPEED = 0.2;   // Lower = slower rotation
```

### If Distance is Wrong:

Edit `LimelightSubsystem.java` line 103:
```java
double calibrationConstant = 2.0;  // Increase if too close, decrease if too far
```

### If Robot Doesn't Align Well:

Edit `DriveToGamePieceCommand.java` lines 39-43:
```java
// Turn PID (rotation alignment)
m_turnController = new PIDController(0.02, 0.0, 0.001);

// Drive PID (forward movement)
m_driveController = new PIDController(0.5, 0.0, 0.05);
```

---

## 🎯 Quick HSV Tuning Tips

**Test in match lighting!** Different lights = different colors

**Green Algae Starting Points:**
```
Bright Arena:
Hue: 45-75, Sat: 180-255, Val: 120-255

Dim Arena:
Hue: 40-80, Sat: 150-255, Val: 60-200

Outdoor:
Hue: 50-70, Sat: 200-255, Val: 80-255
```

**Adjust HSV until:**
- ✅ Algae lights up bright green in camera view
- ✅ Everything else is dark
- ✅ "tv" value = 1.0 in Limelight interface

---

## ⚡ What Changed from Original

**Removed:**
- ❌ Coral detection pipeline
- ❌ Pipeline switching buttons
- ❌ Coral constants

**Kept:**
- ✅ Algae detection only (Pipeline 0)
- ✅ Simple one-button operation (B button)
- ✅ Clean, focused code
- ✅ Easy to understand and tune

**Inspired by Teams 2056 & 2910:**
- Simple, reliable vision approach
- Focus on one game piece type
- Clean subsystem architecture
- Real-time dashboard feedback

---

## 🚀 Deploy & Test Checklist

- [ ] Code deployed: `gradlew deploy`
- [ ] Limelight configured for green algae (Hue 40-80)
- [ ] Camera height measured and updated in Constants.java
- [ ] Camera angle measured and updated in Constants.java
- [ ] SmartDashboard shows `Algae/HasTarget = true` when algae visible
- [ ] B button drives robot to algae correctly
- [ ] Robot stops at correct distance (~0.3m)

---

## 📊 Files Modified

- `Constants.java` - Only algae constants, removed coral
- `LimelightSubsystem.java` - Simplified to algae only, removed coral methods
- `RobotContainer.java` - Removed pipeline switching, B button only
- SmartDashboard labels changed from "Limelight" to "Algae"

---

**You're ready to detect algae! 🟢**

Simple. Focused. Effective.
