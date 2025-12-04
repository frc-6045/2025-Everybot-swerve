# 🤖 2025 Everybot Swerve - Vision System Documentation

## 🎯 System Overview

Your robot has a **hybrid vision system** that uses both **Limelight 3** and **Google Coral USB Accelerator** for AI-powered algae detection and automatic tracking.

**Hardware:**
- Limelight 3 camera with integrated Coral USB Accelerator
- Neural network running on-device for real-time object detection
- No external coprocessor needed!

**Capabilities:**
- ✅ Auto-track algae with one button press
- ✅ AI-powered detection using Google Coral
- ✅ Multiple vision modes (AI, color tracking, AprilTags)
- ✅ Real-time SmartDashboard monitoring
- ✅ Easy model updates via web interface

---

## 📚 Documentation Guide

### **🚀 START HERE** → [SIMPLIFIED_VISION_GUIDE.md](SIMPLIFIED_VISION_GUIDE.md)
**Best for:** First-time setup, understanding your system
- Complete setup in 30 minutes
- How your hardware works together
- Button controls and testing
- Troubleshooting common issues

### **📋 QUICK REFERENCE** → [QUICK_START_CHEATSHEET.md](QUICK_START_CHEATSHEET.md)
**Best for:** Drive team, competition day, quick lookups
- One-page cheatsheet for competition
- Button mappings
- Troubleshooting table
- Pre-match checklist
- **Print this and keep at driver station!**

### **🔧 DETAILED SETUP** → [LIMELIGHT_CORAL_SETUP.md](LIMELIGHT_CORAL_SETUP.md)
**Best for:** Detailed Limelight configuration, model training
- Step-by-step Limelight setup
- How to train AI models
- Upload models to Limelight
- Configure neural network pipelines
- Advanced tuning

### **📊 ARCHITECTURE** → [VISION_SYSTEM_OVERVIEW.md](VISION_SYSTEM_OVERVIEW.md)
**Best for:** Understanding how everything works, debugging
- System architecture diagrams
- Data flow explanations
- NetworkTables structure
- File organization
- Performance expectations

### **⚠️ DEPRECATED** → [CORAL_SETUP_GUIDE.md](CORAL_SETUP_GUIDE.md)
**Note:** This guide is for external Raspberry Pi + Coral setups
- **You DON'T need this** - your Coral is integrated into Limelight!
- Keep for reference if you want to add a second Coral later

---

## 🎮 Quick Start (5 Minutes)

### **1. Train Your AI Model**

Go to: https://teachablemachine.withgoogle.com/

1. Upload 100+ photos of algae
2. Upload 100+ photos without algae
3. Train model (15 min)
4. Export as "TensorFlow Lite" → "Quantized"
5. Download `model.tflite`

### **2. Upload to Limelight**

1. Open http://limelight.local:5801
2. Go to "Neural Networks" tab
3. Upload your `model.tflite`
4. Go to "Pipeline" tab → Set Type: "Neural Detector"
5. Select your model, Save

### **3. Test It!**

1. Deploy code: `./gradlew deploy`
2. Enable robot
3. **Hold B button** while looking at algae
4. Robot should auto-rotate to center algae! ✓

---

## 🎮 Driver Controls

| Button | Action |
|:------:|--------|
| **B** (hold) | **Auto-track algae** ← Main tracking button |
| A | Zero gyro |
| X | Coral out |
| Y | Coral stack |
| RB | Algae in |
| RT | Algae out |
| D-Pad UP | FUSION mode (uses both systems) |
| D-Pad LEFT | Limelight-only mode (fastest) |
| D-Pad RIGHT | Coral-only mode (most accurate) |

---

## 📁 Code Structure

```
src/main/java/frc/robot/
├── subsystems/
│   ├── LimelightVisionSubsystem.java     - Interfaces with Limelight camera
│   ├── CoralVisionSubsystem.java         - For external Coral (optional)
│   ├── HybridVisionSubsystem.java        - Combines vision systems
│   ├── Detection.java                     - Detection data structure
│   ├── SwerveSubsystem.java              - Drive control
│   ├── ArmSubsystem.java                 - Arm control
│   ├── RollerSubsystem.java              - Intake control
│   └── ClimberSubsystem.java             - Climber control
│
├── commands/
│   ├── TrackTargetCommand.java           - Auto-track algae (B button)
│   ├── AlgieInCommand.java               - Algae intake
│   ├── CoralOutCommand.java              - Coral outtake
│   └── ...
│
├── RobotContainer.java                    - Button bindings
├── Constants.java                         - Tuning values
└── Robot.java                             - Main robot class
```

---

## 🔧 Key Files to Edit

### **For Tuning Tracking Speed:**
📄 **[TrackTargetCommand.java](src/main/java/frc/robot/commands/TrackTargetCommand.java)** (Line 24-26)

```java
private static final double ROTATION_KP = 0.04;    // Higher = faster
private static final double MIN_COMMAND = 0.05;    // Minimum speed
private static final double DEADBAND = 1.0;        // Centering tolerance
```

### **For Vision Configuration:**
📄 **[Constants.java](src/main/java/frc/robot/Constants.java)** (VisionConstants section)

```java
public static final String LIMELIGHT_NAME = "limelight";
public static final int ALGAE_PIPELINE = 0;
public static final double ROTATION_KP = 0.04;
public static final double TRACKING_DEADBAND = 1.0;
```

### **For Button Mappings:**
📄 **[RobotContainer.java](src/main/java/frc/robot/RobotContainer.java)** (configureBindings method)

```java
// Auto-track algae
m_driverController.b().whileTrue(
    new TrackTargetCommand(m_vision, m_drive, "algae")
);
```

---

## 📊 SmartDashboard Monitoring

### **Main Values to Watch:**

| Key | What It Means | Expected Value |
|-----|---------------|----------------|
| `Vision/HasTarget` | Algae detected? | true when seeing algae |
| `Vision/TX` | Horizontal angle to algae | -27 to 27 degrees |
| `Vision/Status` | Detection status | "Target Acquired" or "No Target" |
| `HybridVision/Mode` | Current vision mode | "FUSION", "LIMELIGHT_ONLY", etc. |
| `HybridVision/LimelightActive` | Limelight working? | true when Limelight sees target |
| `limelight/tv` | Raw Limelight detection | 1 = has target, 0 = no target |

---

## 🐛 Troubleshooting

### **Robot not tracking when B pressed:**

1. Check robot is **enabled** in Driver Station
2. Check `Vision/HasTarget` = true in SmartDashboard
3. Verify algae is visible to camera
4. Check Limelight pipeline is set to 0 (Neural Detector)

### **No detections:**

1. Open http://limelight.local:5801 → Check "Output" tab
2. Verify pipeline Type = "Neural Detector"
3. Lower confidence threshold in Limelight settings
4. Clean camera lens!
5. Check Coral USB is plugged in

### **Tracking too slow/fast:**

1. Edit `TrackTargetCommand.java` line 24
2. Increase `ROTATION_KP` for faster (try 0.06 or 0.08)
3. Decrease `ROTATION_KP` for slower (try 0.02 or 0.03)
4. Redeploy code

### **False detections:**

1. Retrain model with more background images
2. Raise confidence threshold in Limelight (try 0.6-0.7)
3. Add negative examples to training data

---

## 🎯 Vision Modes

Your system supports multiple modes:

### **FUSION Mode (Default)**
- Uses both Limelight and Coral together
- Averages results for smoothest tracking
- Most accurate and robust
- **Recommended for competition**

### **LIMELIGHT_ONLY Mode**
- Uses only Limelight's built-in processing
- Fastest response time
- Good for quick reactions
- Switch with D-Pad LEFT

### **CORAL_ONLY Mode**
- Uses only Coral neural network
- Most accurate classification
- Slightly slower than Limelight
- Switch with D-Pad RIGHT

---

## 🏆 Competition Checklist

**Before Each Match:**

- [ ] Limelight powered on (green LED visible)
- [ ] Wipe camera lens clean
- [ ] Check `limelight/tv` toggles when algae appears
- [ ] Test B button tracking in practice area
- [ ] Verify pipeline 0 selected (Neural Detector)
- [ ] Check battery voltage > 12V
- [ ] Print and bring [QUICK_START_CHEATSHEET.md](QUICK_START_CHEATSHEET.md)

---

## 📈 Improving Detection

### **Model Training Tips:**

1. **Collect diverse data:**
   - 200-500 images recommended
   - Multiple angles (front, side, top)
   - Various distances (near, far)
   - Different lighting (bright, dim)
   - Partial objects (cut off by frame)

2. **Balance dataset:**
   - 50% images WITH algae
   - 50% images WITHOUT algae
   - Remove blurry/duplicate photos

3. **Retrain and test:**
   - Use same export settings (TFLite Quantized)
   - Upload new model to Limelight
   - Test at different distances
   - Adjust confidence threshold as needed

---

## 💻 Build & Deploy Commands

```bash
# Compile only (quick check for errors)
./gradlew compileJava

# Full build
./gradlew build

# Deploy to robot
./gradlew deploy

# Clean build (if having issues)
./gradlew clean build
```

---

## 🔗 Useful Links

### **Documentation:**
- [Limelight Official Docs](https://docs.limelightvision.io/)
- [Teachable Machine](https://teachablemachine.withgoogle.com/)
- [Google Coral Docs](https://coral.ai/docs/)

### **FRC Resources:**
- [Chief Delphi Forums](https://www.chiefdelphi.com/)
- [WPILib Docs](https://docs.wpilib.org/)

### **Your Robot:**
- Limelight Web Interface: http://limelight.local:5801
- Driver Station: Port 1735 (NetworkTables)

---

## 🎉 What You Have

✅ **Hybrid vision system** combining Limelight + Coral
✅ **One-button auto-tracking** for easy driver operation
✅ **AI-powered detection** using neural networks
✅ **No external coprocessor** - everything runs on Limelight
✅ **Production-ready code** - compiles and deploys
✅ **Full documentation** for setup and troubleshooting
✅ **SmartDashboard monitoring** for live debugging
✅ **Multiple vision modes** for different scenarios

---

## 📞 Need Help?

1. **Check docs** in order:
   - [SIMPLIFIED_VISION_GUIDE.md](SIMPLIFIED_VISION_GUIDE.md) - Start here
   - [QUICK_START_CHEATSHEET.md](QUICK_START_CHEATSHEET.md) - Quick reference
   - [LIMELIGHT_CORAL_SETUP.md](LIMELIGHT_CORAL_SETUP.md) - Detailed setup

2. **Check SmartDashboard** for live status

3. **Check Limelight web interface** at http://limelight.local:5801

4. **Search Chief Delphi** for similar issues

---

**Your vision system is ready! Press B and watch it track!** 🤖🪸

Last Updated: December 2, 2025
