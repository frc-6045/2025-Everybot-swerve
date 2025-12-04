# 🎯 Limelight 3 + Coral USB Setup Guide

**Your Setup:** Coral USB Accelerator plugged directly into Limelight 3!

This is actually the **easiest** way to use Coral because Limelight handles everything for you!

---

## 🚀 Super Quick Setup (15 minutes)

### ✅ **What You Have:**
- Limelight 3 (or Limelight 3G)
- Google Coral USB Accelerator (plugged into Limelight)
- Your robot code (already configured!)

### ✅ **What This Does:**
- Limelight runs neural network detection using Coral
- Publishes results to NetworkTables automatically
- Your HybridVisionSubsystem reads from both Limelight AND Coral
- No Raspberry Pi needed!

---

## 📋 Step 1: Train Your Model (15 minutes)

### **Option A: Use Teachable Machine (EASIEST)**

1. **Go to:** https://teachablemachine.withgoogle.com/
2. **Click:** "Image Project" → "Standard image model"
3. **Create classes:**
   - Class 1: "algae" → Upload 100+ photos of algae
   - Class 2: "background" → Upload 100+ photos without algae
4. **Click:** "Train Model" (wait 5-30 minutes)
5. **Export:**
   - Click "Export Model"
   - Select "TensorFlow Lite"
   - Check "Quantized"
   - Download files

**You get:** `model.tflite` and `labels.txt`

### **Option B: Use Limelight's Pre-trained Models**

Limelight may have pre-trained FRC models available:
1. Go to `limelight.local:5801`
2. Check "Neural Networks" tab
3. Look for FRC 2025 Reefscape models
4. Download if available

---

## 🔧 Step 2: Upload Model to Limelight (5 minutes)

### **A. Access Limelight Web Interface**

1. **Connect to robot WiFi**
2. **Open browser:** http://limelight.local:5801
   - Or use your robot IP: http://10.TE.AM.11:5801

### **B. Upload Your Model**

1. **Go to "Neural Networks" tab**
2. **Click "Upload Model"**
3. **Select your files:**
   - Upload `model.tflite`
   - Upload `labels.txt` (if required)
4. **Name it:** "algae_detector"
5. **Click "Upload"**

The Limelight will automatically:
- ✅ Convert model for Edge TPU
- ✅ Load model onto Coral
- ✅ Make it available in pipelines

---

## ⚙️ Step 3: Configure Limelight Pipeline (5 minutes)

### **Create Neural Network Pipeline:**

1. **Go to "Pipeline" tab**
2. **Select a pipeline** (e.g., Pipeline 0)
3. **Change "Type" to "Neural Detector"**
4. **Select your model:** "algae_detector"
5. **Configure settings:**
   - **Confidence Threshold:** 0.5 (adjust as needed)
   - **Enable "Send to NetworkTables"**
   - **Enable LED (if you want LEDs on during detection)**

6. **Save pipeline**

### **Optional: Create Multiple Pipelines**

- **Pipeline 0:** Neural network for algae (Coral)
- **Pipeline 1:** Color tracking (fast Limelight-only mode)
- **Pipeline 2:** AprilTag detection

Switch pipelines in code or SmartDashboard!

---

## 📡 Step 4: Understand NetworkTables Output

When using Limelight's neural detector with Coral, it publishes:

### **Standard Limelight Values:**
```
limelight/
├── tv (0 or 1)          - Has target?
├── tx (-27 to 27)       - Horizontal offset (degrees)
├── ty (-20 to 20)       - Vertical offset (degrees)
├── ta (0 to 100)        - Target area (%)
├── tclass (string)      - Detected class name (e.g., "algae")
├── tid (number)         - Class ID
└── pipeline (0-9)       - Active pipeline
```

### **Your Code Already Handles This!**

Your `LimelightVisionSubsystem` already reads these values:
- `getHorizontalOffset()` - Gets tx
- `getVerticalOffset()` - Gets ty
- `hasTarget()` - Checks tv
- `getPrimaryAlgae()` - Gets the main detection

---

## 🎮 Step 5: Test Your Setup

### **A. Check Limelight is Working**

1. **Power on robot**
2. **Open SmartDashboard**
3. **Look for NetworkTables values:**
   - `limelight/tv` = 1 (when algae visible)
   - `limelight/tclass` = "algae"
   - `limelight/tx` = offset angle

### **B. Test with Robot Code**

1. **Deploy your code:** `./gradlew deploy`
2. **Enable robot**
3. **Press B button** (hold) while looking at algae
4. **Robot should rotate to center the algae!**

### **C. Check SmartDashboard Values**

You should see:
- `Vision/HasTarget` = true
- `Vision/TX` = horizontal offset
- `Vision/Algae` = detection info
- `HybridVision/LimelightActive` = true

---

## 🔄 How Your System Works Now

### **Your Current Setup:**

```
┌─────────────────────────────────────┐
│         Limelight 3                 │
│                                     │
│  ┌──────────────────────────────┐  │
│  │  Built-in Camera             │  │
│  └────────────┬─────────────────┘  │
│               │                     │
│  ┌────────────▼─────────────────┐  │
│  │  Neural Network Processor    │  │
│  │  (Uses Coral USB!)           │  │
│  └────────────┬─────────────────┘  │
│               │                     │
│  ┌────────────▼─────────────────┐  │
│  │  Publish to NetworkTables    │  │
│  │  limelight/*                 │  │
│  └──────────────────────────────┘  │
└─────────────┬───────────────────────┘
              │
              │ NetworkTables
              │
┌─────────────▼────────────────────────┐
│         RoboRIO                      │
│                                      │
│  ┌────────────────────────────────┐ │
│  │  LimelightVisionSubsystem      │ │
│  │  (reads limelight/*)           │ │
│  └────────────┬───────────────────┘ │
│               │                      │
│  ┌────────────▼───────────────────┐ │
│  │  TrackTargetCommand            │ │
│  │  (rotates robot to center)     │ │
│  └────────────────────────────────┘ │
└──────────────────────────────────────┘
```

---

## 🎯 Update Your Robot Code

Since Coral is integrated into Limelight, you can simplify your code:

### **Option 1: Use LimelightVisionSubsystem Only**

Since Limelight handles the Coral automatically:

```java
// In RobotContainer.java:
public final LimelightVisionSubsystem m_vision = new LimelightVisionSubsystem();

// Tracking command:
m_driverController.b().whileTrue(
  new TrackTargetCommand(m_vision, m_drive, "algae")
);
```

### **Option 2: Keep HybridVisionSubsystem (Recommended)**

Keep it for flexibility - you can still add a separate Coral coprocessor later:

```java
// In RobotContainer.java (current setup):
public final HybridVisionSubsystem m_vision = new HybridVisionSubsystem();

// Set mode to LIMELIGHT_ONLY since Limelight has the Coral:
m_vision.setVisionMode(VisionMode.LIMELIGHT_ONLY);

// Or use FUSION if you add a separate Coral later
```

---

## 📊 Pipeline Modes

You can create multiple pipelines on Limelight:

### **Pipeline 0: Neural Network (Coral)**
- Uses Coral USB Accelerator
- Most accurate algae detection
- ~20-30 FPS

### **Pipeline 1: Color Tracking**
- Uses Limelight's built-in processing
- Fastest (60+ FPS)
- Good for quick targeting

### **Pipeline 2: Hybrid**
- Combine neural network with color filtering
- Best accuracy + speed

### **Switch Pipelines in Code:**

```java
// In your command or subsystem:
m_vision.getLimelight().setPipeline(0);  // Neural network
m_vision.getLimelight().setPipeline(1);  // Color tracking
```

---

## 🔧 Tuning Tips

### **If Detection is Poor:**

1. **Adjust confidence threshold** in Limelight web interface
   - Lower = more detections (less accurate)
   - Higher = fewer detections (more accurate)
   - Recommended: 0.4 - 0.6

2. **Train with more data**
   - 500+ images recommended
   - Vary lighting conditions
   - Multiple angles and distances

3. **Adjust camera settings** in Limelight:
   - Exposure
   - Brightness
   - Contrast

4. **Try different pipeline modes:**
   - Neural Detector only
   - Neural Detector + color filtering
   - Compare performance

---

## 🎮 Recommended Button Setup

```java
// In RobotContainer.java configureBindings():

// Auto-track algae using Limelight + Coral
m_driverController.b().whileTrue(
  new TrackTargetCommand(m_vision, m_drive, "algae")
);

// Switch to neural network pipeline (Coral)
m_driverController.pov(0).onTrue(
  Commands.runOnce(() -> m_vision.getLimelight().setPipeline(0))
);

// Switch to color tracking pipeline (fast)
m_driverController.pov(180).onTrue(
  Commands.runOnce(() -> m_vision.getLimelight().setPipeline(1))
);

// Toggle LEDs
m_driverController.leftBumper().onTrue(
  Commands.runOnce(() -> m_vision.getLimelight().setLEDs(true))
);
m_driverController.leftTrigger().onTrue(
  Commands.runOnce(() -> m_vision.getLimelight().setLEDs(false))
);
```

---

## ✅ Quick Checklist

- [ ] Coral USB plugged into Limelight USB port
- [ ] Trained model using Teachable Machine
- [ ] Uploaded model to Limelight web interface
- [ ] Created neural network pipeline (Pipeline 0)
- [ ] Set confidence threshold (0.5 recommended)
- [ ] Deployed robot code
- [ ] Tested tracking with B button
- [ ] Verified detections in SmartDashboard

---

## 🐛 Troubleshooting

### **Limelight not detecting Coral:**
1. Check USB connection
2. Reboot Limelight (power cycle)
3. Check Limelight firmware is up to date

### **No detections:**
1. Check pipeline is set to "Neural Detector"
2. Lower confidence threshold
3. Verify model uploaded correctly
4. Check camera can see algae clearly

### **Poor FPS:**
1. Use smaller model (MobileNet v2 recommended)
2. Reduce camera resolution in Limelight settings
3. Try different pipeline settings

### **Tracking not working:**
1. Check `Vision/HasTarget` in SmartDashboard
2. Verify `Vision/TX` changes when algae moves
3. Check robot is enabled
4. Verify B button binding in code

---

## 🎉 Advantages of This Setup

✅ **No separate coprocessor needed** - Limelight does it all
✅ **Easy to configure** - Everything in web interface
✅ **Low latency** - Direct connection to roboRIO
✅ **Reliable** - One less device to fail
✅ **Easy updates** - Upload new models via web interface
✅ **Multiple pipelines** - Switch between detection modes
✅ **Your code already works!** - No changes needed

---

## 📚 Additional Resources

- **Limelight Docs:** https://docs.limelightvision.io/
- **Neural Network Guide:** https://docs.limelightvision.io/docs/docs-limelight/pipeline-neural
- **Teachable Machine:** https://teachablemachine.withgoogle.com/
- **Limelight Support:** https://www.chiefdelphi.com/

---

**Your setup is simpler and better than using a separate Pi!** Just upload your model to Limelight and you're ready to track algae! 🪸🤖
