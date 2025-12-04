# 🎯 Vision System Quick Start Cheatsheet

## 30-Second Summary

1. **Train model** → teachablemachine.withgoogle.com (15 min)
2. **Upload to Limelight** → limelight.local:5801 (5 min)
3. **Configure pipeline** → Set type to "Neural Detector" (2 min)
4. **Deploy code** → `./gradlew deploy` (2 min)
5. **Test** → Hold B button while looking at algae! ✓

---

## 🎮 Driver Controls

| Button | Action |
|:------:|--------|
| **B** ← **MAIN BUTTON** | **Hold to auto-track algae** |
| A | Zero gyro |
| X | Coral out |
| Y | Coral stack |
| RB | Algae in |
| RT | Algae out |
| D-Pad ⬆ | FUSION mode |
| D-Pad ⬅ | Limelight mode |
| D-Pad ➡ | Coral mode |

---

## 📊 SmartDashboard - What to Watch

### **These should be TRUE when working:**
```
✓ Vision/HasTarget          - Seeing algae?
✓ HybridVision/LimelightActive  - Limelight working?
✓ limelight/tv             - Raw Limelight detection
```

### **These show tracking data:**
```
Vision/TX                   - Angle to algae (-27 to 27°)
Vision/TA                   - Size of algae (0-100%)
Vision/Status               - "Target Acquired" or "No Target"
```

---

## 🔧 Tuning Tracking Speed

**File:** `src/main/java/frc/robot/commands/TrackTargetCommand.java` (Line 24)

```java
ROTATION_KP = 0.04;    // ← Change this number
```

| Problem | Change To | Effect |
|---------|-----------|--------|
| Too slow | `0.08` | Faster rotation |
| Too fast / oscillates | `0.02` | Slower, smoother |
| Perfect! | `0.04` | Keep it! |

---

## 🌐 Limelight Web Interface

**URL:** http://limelight.local:5801

### **Quick Access Tabs:**

| Tab | What's There |
|-----|--------------|
| **Pipeline** | Configure detection modes |
| **Neural Networks** | Upload AI models |
| **Output** | See live detections |
| **Settings** | Network config, updates |

---

## 🤖 Upload Model to Limelight

1. Open http://limelight.local:5801
2. Click **"Neural Networks"** tab
3. Click **"+"** button
4. Upload `model.tflite` file
5. Name it: **"algae_detector"**
6. Wait for conversion ✓
7. Go to **"Pipeline"** tab
8. Set Type: **"Neural Detector"**
9. Select Model: **"algae_detector"**
10. **Save!**

---

## 📈 Training Model Tips

### **Teachable Machine Settings:**

```
Images needed:
  ✓ algae class: 100-500 photos
  ✓ background class: 100-500 photos

Export settings:
  ✓ TensorFlow Lite
  ✓ Quantized (for Coral)
  ✓ Download model

Training tips:
  ✓ Vary lighting
  ✓ Multiple angles
  ✓ Different distances
  ✓ Partial objects
  ✓ 50/50 algae vs background
```

---

## 🐛 Emergency Troubleshooting

| Problem | Quick Fix |
|---------|-----------|
| **Not tracking** | 1. Check robot enabled<br>2. Check B button pressed<br>3. Check `Vision/HasTarget` |
| **No detection** | 1. Lower confidence (Limelight web)<br>2. Check correct pipeline<br>3. Check lens clean |
| **Wrong pipeline** | `m_vision.getLimelight().setPipeline(0)` |
| **LEDs not on** | `m_vision.getLimelight().setLEDs(true)` |
| **Can't reach Limelight** | 1. Check IP (limelight.local or 10.TE.AM.11)<br>2. Connect to robot WiFi<br>3. Power cycle Limelight |

---

## 💻 Code Snippets

### **Manually set pipeline:**
```java
// In any command or subsystem:
m_vision.getLimelight().setPipeline(0);  // Neural network
m_vision.getLimelight().setPipeline(1);  // Color tracking
```

### **Check if seeing algae:**
```java
if (m_vision.hasTarget()) {
    double angle = m_vision.getHorizontalOffset();
    SmartDashboard.putNumber("Debug/Angle", angle);
}
```

### **Turn on/off LEDs:**
```java
m_vision.getLimelight().setLEDs(true);   // On
m_vision.getLimelight().setLEDs(false);  // Off
```

### **Switch vision mode:**
```java
m_vision.setVisionMode(VisionMode.LIMELIGHT_ONLY);  // Fast
m_vision.setVisionMode(VisionMode.FUSION);          // Accurate
```

---

## 📁 File Quick Reference

| File | What It Does |
|------|--------------|
| `LimelightVisionSubsystem.java` | Talks to Limelight camera |
| `HybridVisionSubsystem.java` | Manages all vision systems |
| `TrackTargetCommand.java` | Auto-tracks algae (B button) |
| `RobotContainer.java` | Button mappings |
| `Constants.java` | Tuning numbers |

---

## 🎯 Pipeline Cheat Sheet

### **Pipeline 0: Neural Network (AI)**
```
Type: Neural Detector
Model: algae_detector
Confidence: 0.5
Speed: ~25 FPS
Accuracy: ⭐⭐⭐⭐⭐
Use for: Auto-tracking in matches
```

### **Pipeline 1: Color Tracking**
```
Type: Color
HSV Range: Set for your algae color
Speed: ~90 FPS
Accuracy: ⭐⭐⭐
Use for: Testing, backup
```

### **Pipeline 2: AprilTag**
```
Type: AprilTag
Speed: ~60 FPS
Accuracy: ⭐⭐⭐⭐⭐
Use for: Localization, auto-align
```

---

## 🏁 Pre-Match Checklist

**60 seconds before match:**

- [ ] Limelight green LED on
- [ ] Check `limelight/tv` in NetworkTables (should toggle when algae appears)
- [ ] Test B button (hold while looking at algae → robot should turn)
- [ ] Wipe camera lens
- [ ] Verify pipeline 0 selected
- [ ] Check battery voltage > 12V

---

## 📊 NetworkTables Quick Reference

```
limelight/
├── tv (0 or 1)         ← Has target? (check this first!)
├── tx (-27 to 27)      ← Horizontal angle (left/right)
├── ty (-20 to 20)      ← Vertical angle (up/down)
├── ta (0 to 100)       ← Target size
├── tclass (string)     ← "algae" or class name
└── pipeline (0-9)      ← Current pipeline

Vision/
├── HasTarget (bool)    ← Combined detection (use in code)
├── TX (number)         ← Angle to target (degrees)
└── Status (string)     ← "Target Acquired" or "No Target"
```

---

## ⚡ Performance Targets

| Metric | Target | Check With |
|--------|--------|-----------|
| FPS | 20-30 | Limelight "Output" tab |
| Latency | <100ms | Visual test (move algae, watch robot) |
| Accuracy | >85% | Count correct vs missed detections |
| Range | 10+ feet | Test at various distances |

---

## 🎓 Training Improvements

### **If model accuracy is poor:**

1. **Collect 200+ more images**
   - Focus on missed cases
   - Add difficult angles
   - Include partial objects

2. **Balance dataset:**
   - Equal algae vs background
   - Remove blurry photos
   - Remove duplicates

3. **Retrain with same settings**

4. **Re-upload to Limelight**

5. **Test again**

### **Confidence Threshold Guide:**

| Value | Effect | Use When |
|-------|--------|----------|
| 0.3 | Many detections, some false | Testing, need to catch all algae |
| 0.5 | Balanced | **← Start here** |
| 0.7 | Few detections, very accurate | Need precision, avoid false positives |

---

## 🚀 Deploy Commands

```bash
# Build code
./gradlew build

# Deploy to robot
./gradlew deploy

# Just compile (faster check)
./gradlew compileJava
```

---

## 💡 Pro Tips

1. **Clean lens before every match** - seriously!
2. **Test in comp lighting** - bring portable lights
3. **Have backup pipeline** - color tracking as fallback
4. **Lower confidence at night** - lighting affects detection
5. **Use FUSION mode in matches** - best overall performance
6. **Practice with B button** - drivers need muscle memory
7. **Monitor FPS in SmartDashboard** - should be 20-30
8. **Keep extra model versions** - in case you need to rollback

---

## 📞 Getting Help

**Documentation:**
- [LIMELIGHT_CORAL_SETUP.md](LIMELIGHT_CORAL_SETUP.md) - Detailed Limelight setup
- [SIMPLIFIED_VISION_GUIDE.md](SIMPLIFIED_VISION_GUIDE.md) - Step-by-step guide
- [VISION_SYSTEM_OVERVIEW.md](VISION_SYSTEM_OVERVIEW.md) - System architecture

**Online Resources:**
- Limelight Docs: https://docs.limelightvision.io/
- Chief Delphi: https://www.chiefdelphi.com/
- Teachable Machine: https://teachablemachine.withgoogle.com/

---

## 🎉 Success Criteria

**Your system is working when:**

✓ B button makes robot turn toward algae
✓ `Vision/HasTarget` = true when algae visible
✓ Robot centers algae in camera automatically
✓ FPS stays above 20 in SmartDashboard
✓ Detection works in both bright and dim areas
✓ False positives are rare (<5%)

---

**Print this page and keep at drive station!** 📋
