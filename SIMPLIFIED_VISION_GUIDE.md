# 🎯 Your Simplified Vision System Guide

## Your Actual Hardware Setup

```
┌─────────────────────────────┐
│     Limelight 3             │
│                             │
│  • Built-in Camera          │
│  • Coral USB Accelerator ✓  │
│  • Neural Network Processing│
│                             │
│  Runs AI on device!         │
└─────────────┬───────────────┘
              │
              │ Ethernet
              │
┌─────────────▼───────────────┐
│         RoboRIO             │
│                             │
│  • Your Java code           │
│  • Reads NetworkTables      │
│  • Controls robot           │
└─────────────────────────────┘
```

**You DON'T need:**
- ❌ Raspberry Pi
- ❌ Separate coprocessor
- ❌ External Python scripts
- ❌ coral_coprocessor folder

**Everything runs on the Limelight!**

---

## 🚀 Complete Setup (30 minutes total)

### **Step 1: Train Your AI Model (15 min)**

Go to: https://teachablemachine.withgoogle.com/

1. Click "Get Started" → "Image Project"
2. Create two classes:
   - **Class "algae"**: Upload 100+ photos of algae
   - **Class "background"**: Upload 100+ photos without algae
3. Click "Train Model" (wait 10-15 min)
4. Click "Export Model" → TensorFlow Lite → Quantized → Download

**You get:** `model.tflite` + `labels.txt`

---

### **Step 2: Upload to Limelight (5 min)**

1. **Connect to robot WiFi**
2. **Open browser**: http://limelight.local:5801
3. **Go to "Neural Networks" tab** (on left sidebar)
4. **Click "+" to add new model**
5. **Upload your files:**
   - Select `model.tflite`
   - Give it a name: "algae_detector_2025"
6. **Click "Upload"** (Limelight will auto-convert for Coral)

---

### **Step 3: Configure Pipeline (5 min)**

Still in Limelight web interface:

1. **Go to "Pipeline" tab**
2. **Select Pipeline 0** (or any unused pipeline)
3. **Change "Type" dropdown** to "Neural Detector"
4. **Select Model**: Choose "algae_detector_2025"
5. **Settings to configure:**
   ```
   Confidence Threshold: 0.5
   Multi-target: Enabled
   LED Mode: On
   ```
6. **Click "Save"**

---

### **Step 4: Test Detection (2 min)**

1. **Put algae in front of camera**
2. **Check "Output" tab** in Limelight interface
3. **You should see:**
   - Green bounding boxes around algae
   - Class name: "algae"
   - Confidence score: >0.5

4. **Check NetworkTables:**
   - `tv`: 1
   - `tx`: offset angle
   - `tclass`: "algae"

---

### **Step 5: Test with Robot (3 min)**

1. **Deploy your code**: `./gradlew deploy`
2. **Enable robot in Driver Station**
3. **Press and hold B button** while algae is visible
4. **Robot should auto-rotate to center algae!**

---

## 📊 What's Happening Behind the Scenes

### **When you press B button:**

```
1. TrackTargetCommand starts
         ↓
2. Asks: m_vision.hasTarget()
         ↓
3. LimelightVisionSubsystem checks limelight/tv
         ↓
4. Limelight responds: tv=1 (yes, algae detected)
         ↓
5. Command asks: m_vision.getHorizontalOffset()
         ↓
6. Gets limelight/tx value (e.g., -5.2°)
         ↓
7. Calculates rotation: -5.2 * 0.04 = -0.208
         ↓
8. Sends to drive: rotate at -0.208 speed
         ↓
9. Robot turns left to center algae!
```

---

## 🎮 Your Control Scheme

### **Driver Controller:**

| Button | Action |
|--------|--------|
| **B** (hold) | Auto-track algae with AI |
| **A** | Zero gyro |
| **X** | Coral out |
| **Y** | Coral stack |
| **Right Bumper** | Algae in |
| **Right Trigger** | Algae out |
| **D-Pad UP** | Set to FUSION mode |
| **D-Pad LEFT** | Set to Limelight only mode |
| **D-Pad RIGHT** | Set to Coral only mode |
| **Start** | Zero gyro with alliance |

---

## 🔧 Tuning the Tracking

If tracking is too slow/fast/jittery:

Edit [TrackTargetCommand.java](src/main/java/frc/robot/commands/TrackTargetCommand.java):

```java
// Line 24-26
private static final double ROTATION_KP = 0.04;    // ← Make bigger = faster rotation
private static final double MIN_COMMAND = 0.05;    // ← Minimum speed to move
private static final double DEADBAND = 1.0;        // ← How close = "centered" (degrees)
```

**Examples:**
- **Too slow?** Change `ROTATION_KP` to `0.06` or `0.08`
- **Too fast/oscillates?** Change `ROTATION_KP` to `0.03` or `0.02`
- **Doesn't finish centering?** Lower `DEADBAND` to `0.5`

---

## 📈 Improving Detection Accuracy

### **If algae not detected reliably:**

1. **Collect more training data:**
   - Take 200-500 photos
   - Vary distance (near/far)
   - Vary angle (front/side/top)
   - Vary lighting (bright/dim)
   - Include partial algae (cut off by frame)

2. **Balance your dataset:**
   - 50% images WITH algae
   - 50% images WITHOUT algae (background only)

3. **Retrain model** with new data

4. **Re-upload to Limelight**

5. **Adjust confidence threshold**:
   - In Limelight: Lower to 0.3-0.4 for more detections
   - Or raise to 0.6-0.7 for fewer false positives

---

## 📊 Monitoring Your System

### **SmartDashboard Values to Watch:**

```
Vision/
├── HasTarget (boolean)          - Is algae detected?
├── TX (number)                  - Horizontal angle to algae
├── TY (number)                  - Vertical angle to algae
├── TA (number)                  - Size of algae (area %)
├── Status (string)              - "Target Acquired" or "No Target"
└── Algae (string)               - Detection details

HybridVision/
├── Mode (string)                - "FUSION", "LIMELIGHT_ONLY", etc.
├── HasTarget (boolean)          - Combined detection status
├── LimelightActive (boolean)    - Is Limelight working?
└── TX (number)                  - Combined horizontal offset
```

---

## 🎯 Multiple Pipeline Strategy

Create different pipelines for different situations:

### **Pipeline 0: AI Detection (Coral)**
- **When**: Need accurate classification
- **Use**: Auto-tracking in teleop
- **Speed**: ~25 FPS
- **Accuracy**: ⭐⭐⭐⭐⭐

```java
m_vision.getLimelight().setPipeline(0);
```

### **Pipeline 1: Color Tracking**
- **When**: Need speed over accuracy
- **Use**: Quick reactions, testing
- **Speed**: ~90 FPS
- **Accuracy**: ⭐⭐⭐

```java
m_vision.getLimelight().setPipeline(1);
```

### **Pipeline 2: AprilTag**
- **When**: Autonomous navigation
- **Use**: Localization, auto-align
- **Speed**: ~60 FPS
- **Accuracy**: ⭐⭐⭐⭐⭐

```java
m_vision.getLimelight().setPipeline(2);
```

---

## 🔄 Advanced: Auto-Switching Pipelines

Want to automatically switch based on what you're doing?

```java
// In a command's initialize():
@Override
public void initialize() {
    // Switch to AI detection for accuracy
    m_vision.getLimelight().setPipeline(0);
    m_vision.getLimelight().setLEDs(true);
}

@Override
public void end(boolean interrupted) {
    // Switch back to color tracking for speed
    m_vision.getLimelight().setPipeline(1);
}
```

---

## 📷 Camera Positioning Tips

For best detection:

1. **Mount camera high** (12-18 inches from ground)
2. **Angle down slightly** (10-20 degrees)
3. **Keep lens clean!**
4. **Avoid direct sunlight** in lens
5. **Test in competition lighting** if possible

---

## 🏆 Competition Day Checklist

Before each match:

- [ ] Limelight powered on and connected
- [ ] Check `limelight/tv` in NetworkTables
- [ ] Pipeline 0 set to neural detector
- [ ] LEDs working
- [ ] Test B button tracking in practice area
- [ ] Wipe camera lens clean
- [ ] Check SmartDashboard values updating
- [ ] Verify tracking in both bright and dim areas

---

## 🐛 Quick Troubleshooting

### **Problem: Robot not tracking**
**Check:**
1. Is robot enabled?
2. Is B button bound correctly?
3. Does `Vision/HasTarget` show true?
4. Is Limelight on correct pipeline?

**Fix:**
```java
// Test manually in code:
if (m_vision.hasTarget()) {
    SmartDashboard.putBoolean("Test/SeeingTarget", true);
}
```

### **Problem: Detection not working**
**Check:**
1. Is Coral plugged into Limelight USB?
2. Is pipeline set to "Neural Detector"?
3. Is correct model selected?
4. Is confidence threshold too high?

**Fix:**
1. Open `limelight.local:5801`
2. Go to Pipeline tab
3. Verify Type = "Neural Detector"
4. Lower confidence to 0.3, test again

### **Problem: Tracking too slow/fast**
**Fix:**
Edit `TrackTargetCommand.java` line 24:
```java
// Too slow:
private static final double ROTATION_KP = 0.08;  // Was 0.04

// Too fast:
private static final double ROTATION_KP = 0.02;  // Was 0.04
```

### **Problem: Wrong object detected**
**Fix:**
1. Retrain model with more background images
2. Raise confidence threshold in Limelight
3. Add negative examples to training data

---

## 📚 Files You Actually Need

```
Your Project/
├── src/main/java/frc/robot/
│   ├── subsystems/
│   │   ├── LimelightVisionSubsystem.java  ✓ USING THIS
│   │   ├── HybridVisionSubsystem.java     ✓ USING THIS (manages Limelight)
│   │   └── CoralVisionSubsystem.java      ⚠️ NOT NEEDED (Limelight has Coral)
│   ├── commands/
│   │   └── TrackTargetCommand.java        ✓ USING THIS
│   ├── RobotContainer.java                ✓ Button bindings
│   └── Constants.java                     ✓ Tuning values
│
├── LIMELIGHT_CORAL_SETUP.md               ✓ Read this first!
├── SIMPLIFIED_VISION_GUIDE.md             ✓ This file
└── coral_coprocessor/                     ⚠️ Ignore this (for external Pi only)
```

---

## 🎉 What You Achieved

✅ **AI-powered algae detection** using Google Coral
✅ **One-button auto-tracking** (just hold B!)
✅ **No external coprocessor needed** (Limelight does it all)
✅ **Easy to update model** (just upload new .tflite file)
✅ **Production-ready code** (compiles and runs!)
✅ **Full SmartDashboard monitoring**
✅ **Multiple detection modes** (AI, color, AprilTag)

---

## 🚀 Next Steps

1. **Train your model** with YOUR algae photos
2. **Upload to Limelight** via web interface
3. **Test tracking** with B button
4. **Tune constants** in TrackTargetCommand.java
5. **Practice driving** with auto-aim assistance
6. **Win matches!** 🏆

---

**Questions? Check:**
- [LIMELIGHT_CORAL_SETUP.md](LIMELIGHT_CORAL_SETUP.md) - Detailed Limelight setup
- [VISION_SYSTEM_OVERVIEW.md](VISION_SYSTEM_OVERVIEW.md) - System architecture
- Limelight Docs: https://docs.limelightvision.io/

**Your vision system is ready to go!** 🤖🪸
