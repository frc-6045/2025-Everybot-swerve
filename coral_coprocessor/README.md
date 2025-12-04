# Google Coral Vision for FRC

This directory contains the coprocessor code for running Google Coral USB Accelerator object detection on a Raspberry Pi (or similar device) for FRC robot vision.

## Overview

The Coral USB Accelerator is a USB device that provides hardware acceleration for TensorFlow Lite models using Google's Edge TPU. This setup runs the vision processing on a separate coprocessor (Raspberry Pi) and communicates detection results to the roboRIO via NetworkTables.

### Architecture

```
┌─────────────────────┐
│   Raspberry Pi      │
│                     │
│  USB Camera ────────┼───┐
│                     │   │
│  Coral USB ─────────┼───┤ Vision Processing
│  Accelerator        │   │ (coral_vision.py)
│                     │   │
└──────┬──────────────┘   │
       │                  │
       │ NetworkTables    │
       │ (Ethernet)       │
       │                  │
┌──────▼──────────────┐   │
│     roboRIO         │   │
│                     │   │
│  CoralVisionSubsys ─┼───┘ Consumes detections
│                     │     from NetworkTables
└─────────────────────┘
```

## Hardware Requirements

1. **Raspberry Pi** (4 Model B recommended, 3B+ minimum)
   - 4GB+ RAM recommended
   - MicroSD card (16GB+)
   - Power supply (5V, 3A+)

2. **Google Coral USB Accelerator**
   - Available at: https://coral.ai/products/accelerator

3. **USB Camera**
   - Any USB webcam supported by Linux
   - 640x480 @ 30fps or better

4. **Ethernet Connection**
   - Connect Raspberry Pi to robot network
   - Static IP recommended (or use DHCP reservation)

## Software Setup

### 1. Install Raspberry Pi OS

1. Download Raspberry Pi OS Lite (64-bit recommended)
2. Flash to SD card using Raspberry Pi Imager
3. Enable SSH in config
4. Boot and connect via SSH

### 2. Install Coral Edge TPU Runtime

Follow the official guide: https://coral.ai/docs/accelerator/get-started/

```bash
# Add Coral package repository
echo "deb https://packages.cloud.google.com/apt coral-edgetpu-stable main" | sudo tee /etc/apt/sources.list.d/coral-edgetpu.list
curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
sudo apt-get update

# Install Edge TPU runtime (standard version - lower performance but doesn't overheat)
sudo apt-get install libedgetpu1-std

# For maximum performance (may get hot):
# sudo apt-get install libedgetpu1-max

# Install Python library
sudo apt-get install python3-pycoral
```

### 3. Install Python Dependencies

```bash
# Update system packages
sudo apt-get update
sudo apt-get upgrade -y

# Install system dependencies
sudo apt-get install -y python3-pip python3-opencv

# Clone this repository (or copy files to Pi)
cd ~
# Copy the coral_coprocessor directory to the Pi

# Install Python packages
cd coral_coprocessor
pip3 install -r requirements.txt
```

### 4. Download a TensorFlow Lite Model

You have two options:

#### Option A: Use Pre-trained Model (Quick Start)

Download a pre-trained model compiled for Edge TPU:

```bash
# Create models directory
mkdir -p models

# Download SSD MobileNet v2 (COCO dataset)
wget https://github.com/google-coral/test_data/raw/master/ssd_mobilenet_v2_coco_quant_postprocess_edgetpu.tflite -O models/detect_edgetpu.tflite
wget https://github.com/google-coral/test_data/raw/master/coco_labels.txt -O models/labelmap.txt
```

**Note**: This model detects common objects (person, ball, etc.) but NOT game-specific pieces.

#### Option B: Train Custom Model (Recommended for Competition)

For detecting game pieces (coral, algae, etc.):

1. Collect training images of game pieces
2. Label images using tools like CVAT or LabelImg
3. Train a model using TensorFlow Object Detection API
4. Convert to TensorFlow Lite format
5. Compile for Edge TPU using `edgetpu_compiler`

Resources:
- Training guide: https://coral.ai/docs/edgetpu/retrain-detection/
- FRC-specific guides: Search for "FRC TensorFlow object detection"

### 5. Configure Network

Edit `/etc/dhcpcd.conf` to set a static IP on the robot network:

```bash
sudo nano /etc/dhcpcd.conf
```

Add:
```
interface eth0
static ip_address=10.TE.AM.10/24  # Replace TE.AM with your team number
static routers=10.TE.AM.1
```

Reboot:
```bash
sudo reboot
```

### 6. Test the Setup

```bash
# Make the script executable
chmod +x coral_vision.py

# Test with display output (if you have a monitor connected)
python3 coral_vision.py --display

# Or run headless (normal operation)
python3 coral_vision.py --robot-ip 10.TE.AM.2
```

Replace `TE.AM` with your team number. For example:
- Team 254: `10.2.54.2`
- Team 1234: `10.12.34.2`

## Running on Startup

To run the vision script automatically when the Pi boots:

### Option 1: systemd Service (Recommended)

Create a service file:

```bash
sudo nano /etc/systemd/system/coral-vision.service
```

Add:
```ini
[Unit]
Description=Coral Vision for FRC
After=network.target

[Service]
Type=simple
User=pi
WorkingDirectory=/home/pi/coral_coprocessor
ExecStart=/usr/bin/python3 /home/pi/coral_coprocessor/coral_vision.py --robot-ip 10.TE.AM.2
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl enable coral-vision.service
sudo systemctl start coral-vision.service

# Check status
sudo systemctl status coral-vision.service

# View logs
sudo journalctl -u coral-vision.service -f
```

### Option 2: Cron Job

```bash
crontab -e
```

Add:
```
@reboot cd /home/pi/coral_coprocessor && python3 coral_vision.py --robot-ip 10.TE.AM.2 &
```

## Usage in Robot Code

The vision data is automatically available in your robot code through the `CoralVisionSubsystem`:

```java
// Get the vision subsystem
CoralVisionSubsystem vision = RobotContainer.m_vision;

// Check if coprocessor is connected
if (vision.isCoprocessorConnected()) {
    // Get all detections
    List<Detection> detections = vision.getDetections();

    // Get specific game piece
    Detection coral = vision.getClosestDetection("coral");
    if (coral != null) {
        double x = coral.getX();  // Normalized 0-1
        double y = coral.getY();
        double confidence = coral.getConfidence();
    }

    // Check if object is detected
    boolean hasAlgae = vision.hasDetection("algae");
}
```

See the robot code in `src/main/java/frc/robot/subsystems/CoralVisionSubsystem.java` for more details.

## NetworkTables Structure

The coprocessor publishes data to the `Coral` NetworkTables table:

| Key | Type | Description |
|-----|------|-------------|
| `connected` | boolean | Connection status |
| `timestamp` | number | Timestamp of last update (ms) |
| `num_detections` | number | Number of detections |
| `labels` | string[] | Object labels |
| `confidences` | number[] | Confidence scores (0-1) |
| `x_positions` | number[] | X positions (normalized 0-1) |
| `y_positions` | number[] | Y positions (normalized 0-1) |
| `widths` | number[] | Bounding box widths (normalized 0-1) |
| `heights` | number[] | Bounding box heights (normalized 0-1) |

## Troubleshooting

### Camera not detected
```bash
# List USB devices
lsusb

# Check video devices
ls -l /dev/video*

# Test camera with fswebcam
sudo apt-get install fswebcam
fswebcam test.jpg
```

### Edge TPU not detected
```bash
# Check USB devices
lsusb | grep "Google Inc."

# Reinstall Edge TPU runtime
sudo apt-get install --reinstall libedgetpu1-std
```

### NetworkTables not connecting
- Verify robot IP address matches your team number
- Check network connection: `ping 10.TE.AM.2`
- Ensure robot code is running
- Check firewall settings

### Low FPS
- Use Edge TPU compiled model (ends with `_edgetpu.tflite`)
- Reduce camera resolution
- Use `libedgetpu1-max` for higher performance (may overheat)
- Close other applications on Raspberry Pi

## Performance Tips

1. **Use Edge TPU compiled models** - Regular TFLite models won't use the accelerator
2. **Optimize model input size** - Smaller inputs (e.g., 300x300) are faster
3. **Reduce camera resolution** - 640x480 is usually sufficient
4. **Disable desktop environment** - Use Raspberry Pi OS Lite
5. **Overclock Raspberry Pi** - Can improve performance (may require cooling)

## Additional Resources

- Coral Documentation: https://coral.ai/docs/
- FRC NetworkTables: https://docs.wpilib.org/en/stable/docs/software/networktables/
- TensorFlow Lite Models: https://www.tensorflow.org/lite/models
- FRC Vision Processing: https://docs.wpilib.org/en/stable/docs/software/vision-processing/

## License

This code is released under the WPILib BSD license. See the main repository for details.
