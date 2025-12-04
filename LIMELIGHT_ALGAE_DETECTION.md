# Limelight Algae Detection Setup Guide

This guide shows you how to set up your Limelight to detect **2025 Reefscape algae** game pieces using neural network object detection.

## What You'll Need

- **Limelight 3** (has built-in Google Coral Edge TPU)
  - OR Limelight 2+ with USB Coral adapter (less common)
- **Training data**: Photos of algae game pieces
- **RoboFlow account** (free) or Google Colab for training

## Overview

```
┌─────────────────────────────────────────────────┐
│            LIMELIGHT 3                           │
│                                                  │
│  Camera → Neural Network (Coral TPU) → Output   │
│                                                  │
│  Detects: "algae" game pieces                   │
└──────────────────┬──────────────────────────────┘
                   │
                   │ NetworkTables (tx, ty, ta, tclass)
                   │
┌──────────────────▼──────────────────────────────┐
│            RoboRIO                               │
│                                                  │
│  LimelightVisionSubsystem                       │
│  - Reads detection data                         │
│  - Provides to commands                         │
│                                                  │
│  TrackTargetCommand                             │
│  - Auto-aims at algae                           │
└──────────────────────────────────────────────────┘
```

## Step 1: Collect Training Data

You need ~100-500 photos of algae to train the detector.

### Quick Method (At Practice/Competition)

1. **Mount Limelight on robot** in final position
2. **Place algae** on field at various:
   - Distances (12" to 120")
   - Angles (straight on, 45°, etc.)
   - Lighting conditions
   - Backgrounds (carpet, field elements)

3. **Capture screenshots** from Limelight:
   - Open Limelight web interface: `http://limelight.local:5801`
   - Go to any pipeline
   - Take snapshots every few seconds as you move algae around
   - Download images from Limelight

4. **Aim for diversity**:
   - Different orientations
   - Partial occlusions
   - Multiple algae in frame
   - Different lighting

### Tips for Good Training Data

✅ **DO:**
- Take photos from robot's perspective
- Include variety of backgrounds
- Capture different distances
- Include "hard" cases (algae at edge of frame, partially hidden)
- Take photos in same lighting as competition

❌ **DON'T:**
- Take only close-up photos
- Use only perfect lighting
- Always center the algae
- Take photos from wrong angle

## Step 2: Label Your Images

Use RoboFlow (easiest) or CVAT.

### Option A: RoboFlow (Recommended)

1. **Sign up** at https://roboflow.com (free for public projects)

2. **Create new project**:
   - Project Type: Object Detection
   - Name: "FRC Reefscape Algae"
   - License: MIT

3. **Upload images**:
   - Drag and drop all your photos
   - Click "Finish Uploading"

4. **Annotate images**:
   - Click "Assign Images" → "Assign to me"
   - Click image to open annotation tool
   - Draw bounding box around each algae
   - Label as "algae"
   - Press `D` to go to next image
   - Repeat for all images

5. **Tips for labeling**:
   - Box should tightly fit the algae
   - Include entire game piece
   - Label ALL algae in each image
   - Be consistent with box size

### Option B: CVAT (Free, Self-Hosted)

See CVAT documentation: https://cvat.org/

## Step 3: Train the Model

### Using RoboFlow (Easiest)

1. **Generate dataset**:
   - Click "Generate" in RoboFlow
   - Version: Select "Generate New Version"
   - Preprocessing:
     - Auto-Orient: ✅
     - Resize: 320x320 (faster) or 640x640 (more accurate)
   - Augmentation (optional, helps with small datasets):
     - Flip: Horizontal
     - Brightness: ±15%
     - Blur: Up to 1px
   - Click "Generate"

2. **Train model**:
   - Click "Train with RoboFlow"
   - Model Type: **YOLO v8** (works great with Limelight)
   - Checkpoint: Start from scratch OR YOLOv8n (nano, fastest)
   - Train from Google Colab (free GPU)

3. **Export for Limelight**:
   - After training completes
   - Click "Deploy"
   - Format: **TensorFlow Lite (TFLite)**
   - Download the `.tflite` file

### Using Google Colab (More Control)

If you want more control, train YOLOv8 or TensorFlow model yourself:

```python
# In Google Colab
!pip install ultralytics

from ultralytics import YOLO

# Load pretrained YOLOv8 nano model
model = YOLO('yolov8n.pt')

# Train on your dataset
results = model.train(
    data='path/to/data.yaml',
    epochs=100,
    imgsz=320,
    batch=16
)

# Export to TFLite
model.export(format='tflite')
```

Then download the `.tflite` file.

## Step 4: Upload Model to Limelight

1. **Access Limelight web interface**:
   - Connect to robot network
   - Navigate to `http://limelight.local:5801`
   - OR use static IP: `http://10.TE.AM.11:5801`

2. **Go to Settings tab**

3. **Upload neural detector**:
   - Scroll to "Neural Detector" section
   - Click "Choose File"
   - Select your `.tflite` model
   - Click "Upload"
   - Wait for upload to complete

4. **Create labels file** (if needed):
   - Some models need a `labels.txt` file
   - Create a text file with one line:
     ```
     algae
     ```
   - Upload this as well

## Step 5: Configure Limelight Pipeline

1. **Create new pipeline**:
   - Go to Pipeline tab
   - Click "+" to add pipeline
   - Name it "Algae Detector"

2. **Set Input**:
   - Stream: Camera
   - Crop: None (or crop to reduce processing)

3. **Set Detector**:
   - Mode: **Neural Detector**
   - Model: Select your uploaded model
   - Target: "algae"

4. **Set Output**:
   - Targeting: Enabled
   - LED Mode: Pipeline (turns on LEDs when using this pipeline)
   - 3D: Disabled (unless you have 3D model of algae)

5. **Tune settings**:
   - Confidence: Start at 50%, adjust based on results
     - Too many false positives? Raise it
     - Missing algae? Lower it
   - NMS Threshold: 0.5 (suppresses duplicate detections)

6. **Test it**:
   - Point Limelight at algae
   - You should see green box around algae
   - Check NetworkTables output (bottom of page):
     - `tv` should be 1 (has target)
     - `tclass` should be "algae"
     - `tx` shows horizontal offset

## Step 6: Test with Robot Code

1. **Deploy robot code** (already done - you have LimelightVisionSubsystem)

2. **Open SmartDashboard**

3. **Check vision data**:
   - `Vision/HasTarget` should be true when algae visible
   - `Vision/TX` shows horizontal offset in degrees
   - `Vision/TY` shows vertical offset
   - `Vision/TA` shows area percentage
   - `Vision/Algae` shows detection info

4. **Test TrackTargetCommand**:
   ```java
   // In RobotContainer.java
   m_driverController.b().whileTrue(
       new TrackTargetCommand(m_vision, m_drive, "algae")
   );
   ```

   - Hold B button
   - Robot should rotate to face algae
   - Release button to stop

## Usage in Robot Code

### Basic Detection Check

```java
if (m_vision.hasTarget()) {
    // Algae detected!
    double tx = m_vision.getHorizontalOffset();  // -29.8 to 29.8 degrees
    double distance = m_vision.getEstimatedDistance();  // Needs calibration
}
```

### Auto-Intake Command

```java
public class AutoIntakeAlgaeCommand extends Command {
    @Override
    public void execute() {
        if (!m_vision.hasTarget()) {
            // Search for algae (rotate slowly)
            m_drive.drive(new Translation2d(0, 0), 0.2, false);
            return;
        }

        // Have algae - drive toward it
        double tx = m_vision.getHorizontalOffset();
        double driveSpeed = 0.3;  // Forward speed
        double rotateSpeed = -tx * 0.04;  // Rotate to center

        m_drive.drive(
            new Translation2d(driveSpeed, 0),
            rotateSpeed,
            false
        );

        // Stop when close enough
        if (m_vision.getTargetArea() > 15.0) {
            m_intake.run();  // Start intake
        }
    }
}
```

### Pipeline Switching

```java
// Switch to algae detection
m_vision.setPipeline(0);

// Switch to AprilTag tracking
m_vision.setPipeline(1);
```

## Tuning & Calibration

### Tune PID Constants

In [TrackTargetCommand.java](src/main/java/frc/robot/commands/TrackTargetCommand.java):

```java
private static final double ROTATION_KP = 0.04;  // Start here
```

- Too slow to react? Increase `ROTATION_KP`
- Oscillates/overshoots? Decrease `ROTATION_KP`
- Doesn't move at all? Increase `MIN_COMMAND`

### Calibrate Distance Estimation

In [LimelightVisionSubsystem.java](src/main/java/frc/robot/subsystems/LimelightVisionSubsystem.java):

```java
public double getEstimatedDistance() {
    double area = getTargetArea();
    double k = 48.0;  // TUNE THIS!
    return k / Math.sqrt(Math.max(area, 0.1));
}
```

**To calibrate**:
1. Place algae at known distance (e.g., 24 inches)
2. Read `ta` value from SmartDashboard
3. Calculate: `k = distance * sqrt(ta)`
4. Update constant in code
5. Test at multiple distances

### Tune Limelight Settings

- **Exposure**: Lower for bright lighting, higher for dark
- **Black Level**: Adjust for contrast
- **Confidence**: Raise if too many false positives

## Troubleshooting

### No detections

- [ ] Check pipeline is set to "Neural Detector"
- [ ] Verify model is uploaded
- [ ] Lower confidence threshold
- [ ] Check lighting (too bright/dark?)
- [ ] Verify NetworkTables connection
- [ ] Check `tv` value on Limelight webpage

### Too many false positives

- [ ] Raise confidence threshold
- [ ] Retrain with more "negative" examples
- [ ] Add data augmentation during training
- [ ] Use larger model (YOLOv8s instead of YOLOv8n)

### Robot doesn't rotate correctly

- [ ] Check `Vision/TX` value changes when you move algae
- [ ] Verify `ROTATION_KP` is reasonable (start at 0.03-0.05)
- [ ] Check motor directions
- [ ] Test with smaller max speed limit

### Model too slow

- [ ] Use smaller input size (320x320 instead of 640x640)
- [ ] Use YOLOv8n (nano) instead of larger models
- [ ] Reduce confidence threshold (fewer post-processing)
- [ ] Ensure Limelight 3 has Coral TPU enabled

## Advanced: Multiple Object Classes

To detect both algae AND coral:

1. **Label both in training**:
   - Draw boxes around algae → label "algae"
   - Draw boxes around coral → label "coral"

2. **Retrain model** with both classes

3. **Use in code**:
   ```java
   Detection algae = m_vision.getPrimaryAlgae();
   if (algae != null && algae.getLabel().equals("algae")) {
       // Found algae
   }

   // Or check specific class
   if (m_vision.hasDetection("coral")) {
       // Found coral
   }
   ```

## Resources

- **Limelight Docs**: https://docs.limelightvision.io/
- **RoboFlow**: https://roboflow.com/
- **YOLOv8**: https://github.com/ultralytics/ultralytics
- **FRC Vision**: https://docs.wpilib.org/en/stable/docs/software/vision-processing/
- **Chief Delphi Vision Forum**: https://www.chiefdelphi.com/c/technical/vision/27

## Competition Checklist

Before each match:
- [ ] Limelight is powered on
- [ ] Correct pipeline selected (algae detector)
- [ ] LEDs working
- [ ] NetworkTables connected (`Vision/HasTarget` appears)
- [ ] Test detection by showing algae to camera
- [ ] Verify auto-aim command works

---

**Need help?** Check the troubleshooting section or ask on Chief Delphi!
