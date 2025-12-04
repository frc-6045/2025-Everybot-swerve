# 🎯 Google Coral Settings Guide - Exact Configuration

## Complete settings for Limelight 3 + Coral USB Accelerator detecting FRC 2025 algae

---

## 🏋️ Part 1: Training Model Settings

### **Google Teachable Machine Settings:**

1. **Go to:** https://teachablemachine.withgoogle.com/train/image

2. **Project Type:**
   - ✅ **Image Project**
   - ✅ **Standard image model** (NOT embedded)

3. **Class Setup:**
   ```
   Class 1 Name: algae
   Class 2 Name: background

   (You can add more classes like "coral" if needed)
   ```

4. **Image Requirements:**
   ```
   Resolution: 640x480 or higher
   Format: JPG or PNG
   Quantity per class:
     - Minimum: 100 images
     - Recommended: 200-500 images
     - Optimal: 500+ images

   Composition:
     - 50% with algae clearly visible
     - 50% background only (no algae)
   ```

5. **Training Settings (Advanced):**
   ```
   Epochs: 50 (default)
   Batch size: 16 (default)
   Learning rate: 0.001 (default)

   ⚠️ Don't change these unless you know what you're doing!
   ```

6. **Export Settings:** ⭐ **CRITICAL - Must be exactly this:**
   ```
   Model format: TensorFlow Lite
   Model type: ☑️ Quantized (int8)

   ❌ NOT "Floating point"
   ❌ NOT "TensorFlow.js"
   ❌ NOT "TensorFlow (SavedModel)"

   ✅ Must be "Quantized" for Coral Edge TPU!
   ```

7. **Download:**
   - You get: `converted_tflite.zip`
   - Extract to get: `model.tflite` and `labels.txt`

---

## 📤 Part 2: Limelight Upload Settings

### **Access Limelight:**
```
URL: http://limelight.local:5801
OR:  http://10.TE.AM.11:5801
     (Replace TE.AM with your team number)

Example for team 1234: http://10.12.34.11:5801
```

### **Upload Model:**

1. **Go to "Neural Networks" tab** (left sidebar)

2. **Click "+" button** (Add New Model)

3. **Fill in form:**
   ```
   Model Name: algae_detector_2025

   Model File: [Browse] → Select your model.tflite

   Labels File: [Browse] → Select labels.txt (optional but recommended)

   Framework: Auto-detect (Limelight will figure it out)
   ```

4. **Upload** - Wait for conversion (30 seconds - 2 minutes)

5. **Verify:**
   - Status should show: ✅ "Ready"
   - You should see your model in the list

---

## ⚙️ Part 3: Limelight Pipeline Settings

### **Create Neural Network Pipeline:**

1. **Go to "Pipelines" tab**

2. **Select Pipeline 0** (or any unused pipeline 0-9)

3. **Configure Pipeline - Exact Settings:**

#### **A. Input Tab:**
```
Pipeline Name: Algae AI Detector

Type: Neural Detector ⭐ IMPORTANT!
  (NOT "Fiducial", "Color", "Python", etc.)

Camera Mode: Camera Processor
  (Use vision processing, not driver camera)
```

#### **B. Neural Detector Tab:**

```
═══════════════════════════════════════════
MODEL SETTINGS:
═══════════════════════════════════════════

Selected Model: algae_detector_2025
  (The model you just uploaded)

Model Type: Object Detector
  (Should auto-detect)

═══════════════════════════════════════════
DETECTION SETTINGS:
═══════════════════════════════════════════

Confidence Threshold: 0.5
  ├─ Range: 0.0 to 1.0
  ├─ Lower (0.3-0.4): More detections, more false positives
  ├─ Medium (0.5): Balanced ⭐ START HERE
  └─ Higher (0.6-0.7): Fewer detections, very accurate

Max Detections: 10
  (Maximum objects to detect per frame)

Multi-Class: Enabled ✅
  (Allow detecting different object types)

═══════════════════════════════════════════
CLASS FILTERING:
═══════════════════════════════════════════

Target Class: algae
  (Only detect this class, ignore others)

OR leave blank to detect all classes

═══════════════════════════════════════════
NMS (Non-Maximum Suppression):
═══════════════════════════════════════════

NMS Threshold: 0.4 ⭐ RECOMMENDED
  ├─ Range: 0.0 to 1.0
  ├─ Lower (0.2-0.3): Fewer overlapping boxes
  └─ Higher (0.5-0.7): Allow more overlapping boxes

Purpose: Removes duplicate detections of same object
```

#### **C. Output Tab:**

```
═══════════════════════════════════════════
TARGETING:
═══════════════════════════════════════════

Targeting Mode: Largest
  ├─ Largest: Track biggest algae ⭐ RECOMMENDED
  ├─ Closest: Track nearest to center
  └─ Smallest: Track smallest algae

Active Zone: Full Frame
  (Detect anywhere in image)

═══════════════════════════════════════════
CROPPING (Optional):
═══════════════════════════════════════════

Crop X: 0% (no crop)
Crop Y: 0% (no crop)

⚠️ Only crop if you want to limit detection area
```

#### **D. Input Tab - Camera Settings:**

```
═══════════════════════════════════════════
CAMERA:
═══════════════════════════════════════════

Resolution: 960x720 ⭐ RECOMMENDED
  ├─ Lower (640x480): Faster but less accurate
  ├─ Medium (960x720): Balanced ⭐
  └─ Higher (1280x720): More accurate but slower

Exposure: Auto
  OR Manual: 10-30ms (adjust for your lighting)

White Balance: Auto
  (Limelight handles this well)

═══════════════════════════════════════════
LED SETTINGS:
═══════════════════════════════════════════

LED Mode: Pipeline
  (LEDs controlled by pipeline settings)

LED Brightness: 100%
  (When LEDs are on)

Force On: ☑️ Enabled
  (Keep LEDs on during this pipeline)

═══════════════════════════════════════════
IMAGE PROCESSING:
═══════════════════════════════════════════

Black Level: 0 (default)
Brightness: 0 (default)
Contrast: 0 (default)
Saturation: 0 (default)

⚠️ Only adjust if neural network is struggling
   (Usually AUTO is best for neural networks)
```

#### **E. Output Tab - Advanced:**

```
═══════════════════════════════════════════
NETWORKTABLES:
═══════════════════════════════════════════

Publish to NetworkTables: ✅ Enabled ⭐ REQUIRED!

Table Name: limelight
  (Default - your code expects this)

═══════════════════════════════════════════
3D POSITIONING (Optional - Advanced):
═══════════════════════════════════════════

Known Target Height: 0 inches (leave default unless you want distance estimation)

Camera Height: 0 inches
Camera Pitch: 0 degrees

⚠️ Only configure if you need 3D distance calculations
```

---

## 🎯 Part 4: Optimal Settings by Scenario

### **Scenario 1: Competition - Balanced Performance**
```
Pipeline Type: Neural Detector
Model: algae_detector_2025
Confidence: 0.5
Resolution: 960x720
Exposure: Auto
LED: Force On (100%)
Targeting: Largest
Max Detections: 10
NMS Threshold: 0.4
```
**Expected FPS:** 20-30
**Accuracy:** ⭐⭐⭐⭐

---

### **Scenario 2: Testing - Maximum Speed**
```
Pipeline Type: Neural Detector
Model: algae_detector_2025
Confidence: 0.4 (lower for more detections)
Resolution: 640x480 (smaller = faster)
Exposure: Manual 15ms
LED: Force On
Targeting: Largest
Max Detections: 5 (fewer = faster)
NMS Threshold: 0.3
```
**Expected FPS:** 30-40
**Accuracy:** ⭐⭐⭐

---

### **Scenario 3: Precision - Maximum Accuracy**
```
Pipeline Type: Neural Detector
Model: algae_detector_2025
Confidence: 0.7 (higher for fewer false positives)
Resolution: 1280x720 (larger = more accurate)
Exposure: Auto
LED: Force On (100%)
Targeting: Largest
Max Detections: 10
NMS Threshold: 0.4
```
**Expected FPS:** 15-25
**Accuracy:** ⭐⭐⭐⭐⭐

---

### **Scenario 4: Bright Lighting / Outdoor**
```
Pipeline Type: Neural Detector
Model: algae_detector_2025
Confidence: 0.5
Resolution: 960x720
Exposure: Manual 5-10ms (short exposure)
LED: Off (sunlight is enough)
Targeting: Largest
Brightness: -10 to -20 (reduce if too bright)
Max Detections: 10
NMS Threshold: 0.4
```

---

### **Scenario 5: Dim Lighting / Indoor**
```
Pipeline Type: Neural Detector
Model: algae_detector_2025
Confidence: 0.4 (lower due to noise)
Resolution: 960x720
Exposure: Manual 30-50ms (longer exposure)
LED: Force On (100%)
Targeting: Largest
Brightness: +10 to +20 (increase if too dark)
Max Detections: 10
NMS Threshold: 0.5 (higher to reduce noise)
```

---

## 📊 Part 5: NetworkTables Output Values

When configured correctly, Limelight publishes:

```
limelight/tv (number)
  ├─ 0 = No target detected
  └─ 1 = Target detected ✓

limelight/tx (number)
  ├─ Range: -27 to +27 degrees
  ├─ Negative = Target is LEFT of center
  ├─ Positive = Target is RIGHT of center
  └─ 0 = Centered

limelight/ty (number)
  ├─ Range: -20.5 to +20.5 degrees
  ├─ Negative = Target is BELOW center
  ├─ Positive = Target is ABOVE center
  └─ 0 = Centered vertically

limelight/ta (number)
  ├─ Range: 0 to 100%
  ├─ Percentage of image covered by target
  └─ Larger = closer, Smaller = farther

limelight/tclass (string)
  └─ "algae" or whatever class was detected

limelight/tid (number)
  └─ Class ID (0, 1, 2, etc.)

limelight/pipeline (number)
  └─ Current pipeline index (0-9)
```

---

## 🔧 Part 6: Tuning Workflow

### **Step-by-Step Tuning:**

1. **Start with recommended settings above**

2. **Test detection:**
   - Put algae at various distances (2ft, 5ft, 10ft, 15ft)
   - Check if green boxes appear on "Output" tab
   - Check `limelight/tv` toggles to 1

3. **Adjust confidence if needed:**
   ```
   Too few detections?
     → Lower confidence to 0.4 or 0.3

   Too many false positives?
     → Raise confidence to 0.6 or 0.7
   ```

4. **Adjust exposure if needed:**
   ```
   Image too bright/washed out?
     → Lower exposure (15ms → 10ms → 5ms)

   Image too dark/noisy?
     → Raise exposure (15ms → 30ms → 50ms)
   ```

5. **Test FPS:**
   ```
   Too slow (<15 FPS)?
     → Lower resolution to 640x480
     → Reduce max detections to 5
     → Lower NMS threshold to 0.3

   Fast enough (>25 FPS)?
     → You're good! Keep current settings
   ```

6. **Test tracking with robot:**
   ```
   Deploy code
   Hold B button
   Robot should smoothly track algae

   If tracking is jerky:
     → Lower confidence (0.4) for more stable detections
     → Increase NMS to 0.5 to reduce box flickering
   ```

---

## ⚡ Part 7: Performance Optimization

### **For Maximum FPS (Speed Priority):**

```
✓ Resolution: 640x480
✓ Max Detections: 5
✓ Confidence: 0.4
✓ NMS: 0.3
✓ Exposure: Manual 15ms
✓ Disable 3D calculations
```

### **For Maximum Accuracy (Precision Priority):**

```
✓ Resolution: 1280x720
✓ Max Detections: 10
✓ Confidence: 0.6-0.7
✓ NMS: 0.4
✓ Exposure: Auto
✓ Train model with 500+ images
```

### **For Balanced (Competition Recommended):**

```
✓ Resolution: 960x720 ⭐
✓ Max Detections: 10
✓ Confidence: 0.5 ⭐
✓ NMS: 0.4 ⭐
✓ Exposure: Auto
✓ LED: Force On
```

---

## 📋 Part 8: Verification Checklist

**After configuring, verify:**

- [ ] Coral USB plugged into Limelight
- [ ] Model uploaded successfully (shows "Ready")
- [ ] Pipeline type set to "Neural Detector"
- [ ] Correct model selected in pipeline
- [ ] Confidence threshold set (start with 0.5)
- [ ] Resolution set (start with 960x720)
- [ ] LED mode set to "Force On"
- [ ] "Publish to NetworkTables" enabled
- [ ] Green boxes appear on Output tab when algae visible
- [ ] `limelight/tv` = 1 in NetworkTables when algae visible
- [ ] `limelight/tclass` = "algae"
- [ ] FPS shown in Output tab (should be 20-30)
- [ ] Save pipeline settings!

---

## 🎯 Part 9: Robot Code Settings

Your code is already configured! These constants in [Constants.java](src/main/java/frc/robot/Constants.java):

```java
public static final class VisionConstants {
    // Limelight name
    public static final String LIMELIGHT_NAME = "limelight";

    // Pipeline indices
    public static final int ALGAE_PIPELINE = 0;  // ← Must match your pipeline!

    // Tracking constants
    public static final double ROTATION_KP = 0.04;  // Rotation speed multiplier
    public static final double TRACKING_DEADBAND = 1.0;  // Centering tolerance (degrees)

    // Detection settings
    public static final float CONFIDENCE_THRESHOLD = 0.5f;  // Matches Limelight
}
```

---

## 🎮 Part 10: Quick Test Procedure

1. **Power on robot**
2. **Open Limelight web interface** (check Output tab)
3. **Put algae in front of camera**
4. **Should see:**
   - ✅ Green box around algae
   - ✅ Label "algae" with confidence score
   - ✅ FPS counter (20-30)
5. **Check NetworkTables:**
   - ✅ `limelight/tv` = 1
   - ✅ `limelight/tx` = angle value
   - ✅ `limelight/tclass` = "algae"
6. **Test with robot:**
   - ✅ Enable robot
   - ✅ Hold B button
   - ✅ Robot rotates to center algae
7. **✅ Success!**

---

## 📊 Settings Summary Table

| Setting | Testing | Competition | High Accuracy |
|---------|---------|-------------|---------------|
| **Confidence** | 0.4 | 0.5 ⭐ | 0.7 |
| **Resolution** | 640x480 | 960x720 ⭐ | 1280x720 |
| **Max Detections** | 5 | 10 ⭐ | 10 |
| **NMS Threshold** | 0.3 | 0.4 ⭐ | 0.4 |
| **Exposure** | Manual 15ms | Auto ⭐ | Auto |
| **LED** | On | Force On ⭐ | Force On |
| **Expected FPS** | 30-40 | 20-30 ⭐ | 15-25 |

**⭐ = Recommended starting point**

---

## 🔍 Common Settings Mistakes

❌ **Wrong:** Pipeline Type = "Color" or "Fiducial"
✅ **Right:** Pipeline Type = "Neural Detector"

❌ **Wrong:** Model format = Floating point
✅ **Right:** Model format = Quantized (int8)

❌ **Wrong:** Confidence = 0.9 (too high, misses detections)
✅ **Right:** Confidence = 0.5 (balanced)

❌ **Wrong:** "Publish to NetworkTables" = Disabled
✅ **Right:** "Publish to NetworkTables" = Enabled

❌ **Wrong:** LED Mode = Off (in dim lighting)
✅ **Right:** LED Mode = Force On

---

**Print this guide and keep it at your programming station!** 📋

All settings are optimized for FRC 2025 Reefscape algae detection using Limelight 3 + Coral USB Accelerator.
