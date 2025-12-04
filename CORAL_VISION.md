# Google Coral Vision Integration

This project includes integration with Google Coral USB Accelerator for real-time object detection using machine learning.

## Overview

The vision system consists of two main components:

1. **Coprocessor (Raspberry Pi)** - Runs the Coral USB Accelerator and processes camera frames
2. **RoboRIO (Robot Code)** - Consumes detection data via NetworkTables

## Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                      RASPBERRY PI                             │
│                                                               │
│  ┌──────────┐    ┌──────────────┐    ┌─────────────────┐   │
│  │USB Camera├───▶│coral_vision.py├───▶│  Coral USB      │   │
│  └──────────┘    │               │    │  Accelerator    │   │
│                  │  TensorFlow   │    │  (Edge TPU)     │   │
│                  │  Lite Model   │    └─────────────────┘   │
│                  └───────┬───────┘                           │
│                          │                                   │
│                          ▼                                   │
│                  ┌───────────────┐                           │
│                  │ NetworkTables │                           │
│                  │  Publishing   │                           │
│                  └───────┬───────┘                           │
└──────────────────────────┼───────────────────────────────────┘
                           │ Ethernet
                           │
┌──────────────────────────▼───────────────────────────────────┐
│                       ROBORIO                                 │
│                                                               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │         CoralVisionSubsystem.java                   │    │
│  │                                                      │    │
│  │  - Reads detections from NetworkTables              │    │
│  │  - Provides detection data to commands               │    │
│  │  - Filters by confidence and object type            │    │
│  └──────────────────────┬───────────────────────────────┘    │
│                         │                                    │
│                         ▼                                    │
│  ┌─────────────────────────────────────────────────────┐    │
│  │            Commands (TrackTargetCommand, etc.)      │    │
│  │                                                      │    │
│  │  - Use detection data for autonomous actions        │    │
│  │  - Align to targets, pick up game pieces, etc.      │    │
│  └──────────────────────────────────────────────────────┘    │
└───────────────────────────────────────────────────────────────┘
```

## Robot Code Components

### 1. CoralVisionSubsystem

Location: [`src/main/java/frc/robot/subsystems/CoralVisionSubsystem.java`](src/main/java/frc/robot/subsystems/CoralVisionSubsystem.java)

This subsystem interfaces with the Coral coprocessor via NetworkTables.

**Key Methods:**
- `getDetections()` - Get all current detections
- `getDetectionsByLabel(String label)` - Get detections of a specific type
- `getClosestDetection(String label)` - Get the closest object (by area)
- `getMostConfidentDetection(String label)` - Get highest confidence detection
- `hasDetection(String label)` - Check if an object is detected
- `isCoprocessorConnected()` - Check connection status

**Usage Example:**
```java
// In a command
Detection coral = m_vision.getClosestDetection("coral");
if (coral != null) {
    double x = coral.getX();  // Normalized 0-1 (left to right)
    double y = coral.getY();  // Normalized 0-1 (top to bottom)
    double confidence = coral.getConfidence();  // 0-1
    String label = coral.getLabel();  // "coral"
}
```

### 2. Detection Class

Location: [`src/main/java/frc/robot/subsystems/Detection.java`](src/main/java/frc/robot/subsystems/Detection.java)

Represents a single object detection with:
- Label (object type)
- Confidence score (0.0 to 1.0)
- Position (x, y normalized 0-1)
- Size (width, height normalized 0-1)

### 3. VisionConstants

Location: [`src/main/java/frc/robot/Constants.java`](src/main/java/frc/robot/Constants.java)

Configuration for vision system:
```java
public static final class VisionConstants {
    public static final int CAMERA_WIDTH = 640;
    public static final int CAMERA_HEIGHT = 480;
    public static final float CONFIDENCE_THRESHOLD = 0.5f;
    public static final int MAX_DETECTIONS = 10;
    // ... more constants
}
```

### 4. Example Commands

**TrackTargetCommand** - [`src/main/java/frc/robot/commands/TrackTargetCommand.java`](src/main/java/frc/robot/commands/TrackTargetCommand.java)

Example command that rotates the robot to center a detected object in the camera frame.

```java
// Example usage in RobotContainer
new TrackTargetCommand(m_vision, m_drive, "coral")
```

## Coprocessor Setup

See [`coral_coprocessor/README.md`](coral_coprocessor/README.md) for detailed setup instructions.

### Quick Start

1. **Hardware:**
   - Raspberry Pi 4 (4GB+ RAM recommended)
   - Google Coral USB Accelerator
   - USB Webcam
   - Ethernet connection to robot network

2. **Software:**
   ```bash
   # On Raspberry Pi
   cd coral_coprocessor
   pip3 install -r requirements.txt

   # Download a model (or train your own)
   # See coral_coprocessor/README.md for details

   # Run the vision script
   python3 coral_vision.py --robot-ip 10.TE.AM.2
   ```

3. **Deploy and test** - The robot code will automatically detect and use the vision data

## Using Vision in Your Code

### 1. Basic Detection Check

```java
public class IntakeCommand extends Command {
    private final CoralVisionSubsystem m_vision;

    @Override
    public void execute() {
        if (m_vision.hasDetection("coral")) {
            // Coral detected - proceed with intake
        }
    }
}
```

### 2. Auto-Align to Target

```java
public class AutoAlignCommand extends Command {
    @Override
    public void execute() {
        Detection target = m_vision.getClosestDetection("coral");
        if (target != null) {
            double error = target.getX() - 0.5;  // Error from center
            double turnSpeed = error * kP;
            m_drive.drive(new Translation2d(0, 0), turnSpeed, false);
        }
    }
}
```

### 3. Distance Estimation

```java
// Larger bounding box = closer to camera
Detection coral = m_vision.getClosestDetection("coral");
if (coral != null) {
    double area = coral.getWidth() * coral.getHeight();
    // Use area to estimate distance (requires calibration)
    boolean isClose = area > 0.1;  // Threshold depends on camera
}
```

### 4. Multiple Object Detection

```java
List<Detection> allCoral = m_vision.getDetectionsByLabel("coral");
SmartDashboard.putNumber("Coral Count", allCoral.size());

for (Detection coral : allCoral) {
    // Process each detected coral
}
```

## SmartDashboard Integration

The vision subsystem automatically publishes data to SmartDashboard:

| Key | Type | Description |
|-----|------|-------------|
| `Coral/Connected` | boolean | Coprocessor connection status |
| `Coral/Status` | string | Status message |
| `Coral/Detections` | number | Number of current detections |
| `Coral/Detection0-4` | string | Individual detection info |
| `Coral/HasCoral` | boolean | Coral game piece detected |
| `Coral/HasAlgae` | boolean | Algae game piece detected |
| `Coral/CoralX` | number | X position of closest coral |
| `Coral/CoralY` | number | Y position of closest coral |

## Training Custom Models

To detect game-specific objects (coral, algae, notes, etc.):

1. **Collect training data** - Take 100-500 photos of game pieces
2. **Label images** - Use tools like CVAT or RoboFlow
3. **Train model** - Use TensorFlow Object Detection API or AutoML
4. **Convert to TFLite** - Optimize for Edge TPU
5. **Deploy** - Copy `.tflite` file to Raspberry Pi

**Resources:**
- [Coral Training Guide](https://coral.ai/docs/edgetpu/retrain-detection/)
- [RoboFlow for FRC](https://roboflow.com/)
- [TensorFlow Object Detection](https://tensorflow-object-detection-api-tutorial.readthedocs.io/)

## Troubleshooting

### Robot Code Issues

**Vision subsystem shows "Waiting for coprocessor..."**
- Check that Raspberry Pi is powered on
- Verify network connection (can you ping the Pi?)
- Ensure `coral_vision.py` is running on the Pi
- Check robot IP in Python script matches your team number

**No detections shown**
- Check camera is connected to Pi
- Verify model file exists and is correct format
- Lower confidence threshold in `VisionConstants`
- Check SmartDashboard for error messages

### Coprocessor Issues

See [`coral_coprocessor/README.md`](coral_coprocessor/README.md#troubleshooting) for detailed troubleshooting.

## Performance Tips

1. **Camera Resolution** - 640x480 is a good balance of speed and accuracy
2. **Confidence Threshold** - Start at 0.5, tune as needed
3. **Model Size** - Smaller models (SSD MobileNet) are faster
4. **Processing Rate** - Limit to 20-30 FPS to reduce CPU load
5. **Network** - Use wired Ethernet, not WiFi

## Advanced Usage

### Custom NetworkTables Format

If you want to use a different format, modify:
- `coral_vision.py` - Publishing code
- `CoralVisionSubsystem.java` - Reading code

### Multiple Cameras

Run multiple instances of `coral_vision.py` with different:
- Camera indices (`--camera 0`, `--camera 1`)
- NetworkTables names (edit `TABLE_NAME` in Python)
- Corresponding subsystems in robot code

### Integration with PhotonVision

You can run both Coral vision (for object detection) and PhotonVision (for AprilTags) simultaneously:
- Use different NetworkTables tables
- Create separate subsystems
- Combine data in commands

## Files Modified/Added

### Robot Code (RoboRIO)
- ✅ `src/main/java/frc/robot/Constants.java` - Added VisionConstants
- ✅ `src/main/java/frc/robot/RobotContainer.java` - Added CoralVisionSubsystem
- ✅ `src/main/java/frc/robot/subsystems/CoralVisionSubsystem.java` - NEW
- ✅ `src/main/java/frc/robot/subsystems/Detection.java` - NEW
- ✅ `src/main/java/frc/robot/commands/TrackTargetCommand.java` - NEW (example)
- ✅ `build.gradle` - Added Maven Central repository

### Coprocessor Code (Raspberry Pi)
- ✅ `coral_coprocessor/coral_vision.py` - NEW - Main vision script
- ✅ `coral_coprocessor/requirements.txt` - NEW - Python dependencies
- ✅ `coral_coprocessor/README.md` - NEW - Setup instructions
- ✅ `CORAL_VISION.md` - NEW - This file

## Next Steps

1. **Set up Raspberry Pi** - Follow instructions in `coral_coprocessor/README.md`
2. **Train or download a model** - Get a TFLite model for your game pieces
3. **Test vision system** - Verify detections appear on SmartDashboard
4. **Create commands** - Build autonomous routines using vision data
5. **Tune parameters** - Adjust confidence thresholds, PID gains, etc.

## Additional Resources

- [Google Coral Documentation](https://coral.ai/docs/)
- [WPILib Vision Processing](https://docs.wpilib.org/en/stable/docs/software/vision-processing/)
- [NetworkTables Documentation](https://docs.wpilib.org/en/stable/docs/software/networktables/)
- [Chief Delphi Vision Forum](https://www.chiefdelphi.com/c/technical/vision/27)

## Support

For issues or questions:
1. Check the troubleshooting sections in this file and `coral_coprocessor/README.md`
2. Review SmartDashboard output for error messages
3. Check the Chief Delphi forums
4. Consult your team's mentors or programming lead

---

**Note**: This integration was created for the 2025 Everybot swerve drive robot. Customize the game piece labels and detection logic for your specific competition season.
