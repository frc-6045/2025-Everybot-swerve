# 🪸 Coral USB Accelerator Setup Guide - Algae Detection

This guide shows you how to train a neural network model for algae detection and deploy it to a Google Coral USB Accelerator on a Raspberry Pi coprocessor.

---

## 📋 Overview

**What You Need:**
- Raspberry Pi (3B+ or 4 recommended)
- Google Coral USB Accelerator
- Camera (USB webcam or Pi Camera)
- MicroSD card (16GB+ recommended)
- Training images of algae (100+ recommended)

**What This Does:**
- Runs real-time object detection on coprocessor
- Publishes detections to NetworkTables
- Works with your HybridVisionSubsystem for accurate algae tracking

---

## 🎯 Step 1: Collect Training Data

### Option A: Take Photos During Testing
1. Drive your robot around practice field
2. Take 100+ photos of algae from different angles
3. Include various lighting conditions and distances
4. Save to a folder: `training_data/algae/`

### Option B: Use Existing FRC Datasets
- Check Chief Delphi forums for shared datasets
- Ask other teams for training images
- Use game manual images as a starting point (limited)

### Image Requirements:
- **Format:** JPG or PNG
- **Size:** 640x480 or higher
- **Quantity:** Minimum 100, recommended 500+ for best results
- **Variety:** Different angles, distances, lighting

---

## 🏋️ Step 2: Train Your Model

You have **3 options** for training:

### **Option A: Use Google Teachable Machine (EASIEST)**

1. **Go to:** https://teachablemachine.withgoogle.com/
2. **Select:** "Image Project" → "Standard image model"
3. **Create classes:**
   - Class 1: "algae" (upload your algae photos)
   - Class 2: "background" (upload non-algae photos)
4. **Train:** Click "Train Model" (takes 5-30 minutes)
5. **Export:**
   - Under "Export Model" → Select "TensorFlow Lite"
   - Check "Quantized" for Coral compatibility
   - Download the model files

You'll get:
- `model.tflite` - The trained model
- `labels.txt` - Class names

### **Option B: Use Roboflow (RECOMMENDED FOR OBJECT DETECTION)**

Roboflow is better for bounding box detection (finding exact position of algae):

1. **Sign up:** https://roboflow.com (free for small projects)
2. **Create project:** "Algae Detection" → Object Detection
3. **Upload images:** Drag and drop your algae photos
4. **Label images:** Draw bounding boxes around each algae piece
5. **Generate dataset:**
   - Train/Valid/Test split: 70/20/10
   - Augmentation: Enable rotation, brightness, blur
6. **Train model:**
   - Select "TensorFlow Lite (Edge TPU)"
   - Train in cloud (free tier available)
7. **Download:** Get `model.tflite` and `labels.txt`

### **Option C: Train Locally with TensorFlow**

For advanced users - see TensorFlow documentation:
https://www.tensorflow.org/lite/models/modify/model_maker/object_detection

---

## 🔧 Step 3: Prepare Your Model for Coral

Coral requires **Edge TPU compiled models**. Convert your model:

### Using Google's Edge TPU Compiler:

1. **Install compiler:**
```bash
curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
echo "deb https://packages.cloud.google.com/apt coral-edgetpu-stable main" | sudo tee /etc/apt/sources.list.d/coral-edgetpu.list
sudo apt-get update
sudo apt-get install edgetpu-compiler
```

2. **Compile your model:**
```bash
edgetpu_compiler model.tflite
```

This creates: `model_edgetpu.tflite` - **This is what you upload to the Pi!**

---

## 🥧 Step 4: Set Up Raspberry Pi Coprocessor

### A. Install Raspberry Pi OS

1. Download Raspberry Pi Imager
2. Install **Raspberry Pi OS Lite (64-bit)** - no desktop needed
3. Enable SSH during setup
4. Boot Pi and connect to robot network

### B. Install Coral USB Runtime

SSH into the Pi and run:

```bash
# Update system
sudo apt-get update
sudo apt-get upgrade -y

# Install Coral Edge TPU runtime
echo "deb https://packages.cloud.google.com/apt coral-edgetpu-stable main" | sudo tee /etc/apt/sources.list.d/coral-edgetpu.list
curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
sudo apt-get update

# Install with maximum performance (gets hot!)
sudo apt-get install libedgetpu1-max

# OR install with reduced performance (cooler)
# sudo apt-get install libedgetpu1-std
```

### C. Install Python Dependencies

```bash
# Install Python packages
sudo apt-get install python3-pip python3-opencv -y
pip3 install tflite-runtime pynetworktables numpy pillow
```

### D. Connect Coral USB Accelerator

1. Plug Coral USB Accelerator into Pi USB 3.0 port (blue)
2. Verify detection:
```bash
lsusb | grep "Global Unichip"
```
You should see: `Bus 001 Device 002: ID 1a6e:089a Global Unichip Corp.`

---

## 📁 Step 5: Upload Model Files to Pi

### Create directories on Pi:
```bash
ssh pi@raspberrypi.local
mkdir -p ~/frc-vision/models
```

### Copy your model files from your computer:
```bash
# From your computer (not on Pi):
scp model_edgetpu.tflite pi@raspberrypi.local:~/frc-vision/models/
scp labels.txt pi@raspberrypi.local:~/frc-vision/models/
```

Your Pi should now have:
```
~/frc-vision/
  └── models/
      ├── model_edgetpu.tflite
      └── labels.txt
```

---

## 🐍 Step 6: Install Vision Script on Pi

The Python script is in your project at `coral_coprocessor/vision_coral.py`

### Copy script to Pi:
```bash
# From your computer:
scp -r coral_coprocessor/ pi@raspberrypi.local:~/frc-vision/
```

### Configure the script:

Edit `~/frc-vision/coral_coprocessor/vision_coral.py` on the Pi:
```bash
nano ~/frc-vision/coral_coprocessor/vision_coral.py
```

Update these settings if needed:
```python
MODEL_PATH = '/home/pi/frc-vision/models/model_edgetpu.tflite'
LABELS_PATH = '/home/pi/frc-vision/models/labels.txt'
ROBORIO_IP = '10.TE.AM.2'  # Replace with your team number
CAMERA_INDEX = 0  # Try 0, 1, or 2 if camera not found
```

---

## 🚀 Step 7: Run Vision Script

### Test manually:
```bash
cd ~/frc-vision/coral_coprocessor
python3 vision_coral.py
```

You should see:
```
Coral Vision System Starting...
Connected to roboRIO at 10.TE.AM.2
Camera opened successfully
Coral TPU loaded successfully
Publishing detections to NetworkTables...
```

### Make it auto-start on boot:

Create systemd service:
```bash
sudo nano /etc/systemd/system/frc-vision.service
```

Add this content:
```ini
[Unit]
Description=FRC Coral Vision
After=network.target

[Service]
Type=simple
User=pi
WorkingDirectory=/home/pi/frc-vision/coral_coprocessor
ExecStart=/usr/bin/python3 vision_coral.py
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl enable frc-vision.service
sudo systemctl start frc-vision.service
```

Check status:
```bash
sudo systemctl status frc-vision.service
```

---

## 📊 Step 8: Verify It's Working

### On Driver Station / SmartDashboard:

You should see these NetworkTables values:
- `Coral/connected` = true
- `Coral/num_detections` = number of algae detected
- `Coral/labels` = ["algae", "algae", ...]
- `Coral/confidences` = [0.95, 0.87, ...]
- `Coral/x_positions` = [0.5, 0.3, ...]

### On Your Robot Code:

Check SmartDashboard:
- `Coral/Connected` = true
- `Coral/Detections` = count
- `Coral/Status` = "Connected - X detections"
- `HybridVision/CoralActive` = true

### Debug on Pi:

View logs:
```bash
sudo journalctl -u frc-vision.service -f
```

---

## 🎯 Model Performance Tips

### If detection is poor:

1. **Collect more training data** (500+ images recommended)
2. **Vary training conditions:**
   - Different lighting (bright/dim)
   - Different angles (top/side/front)
   - Different distances (near/far)
   - Include partial algae (cut off by frame)

3. **Balance your dataset:**
   - 50% images with algae
   - 50% images without algae (background)
   - Include other game elements in background

4. **Tune confidence threshold** in Constants.java:
```java
public static final float CONFIDENCE_THRESHOLD = 0.5f;  // Lower = more detections (less accurate)
```

5. **Retrain with more epochs** in Teachable Machine/Roboflow

---

## 🔄 Updating Your Model

To update the model after retraining:

```bash
# 1. Compile new model
edgetpu_compiler new_model.tflite

# 2. Upload to Pi
scp new_model_edgetpu.tflite pi@raspberrypi.local:~/frc-vision/models/model_edgetpu.tflite

# 3. Restart vision service
ssh pi@raspberrypi.local
sudo systemctl restart frc-vision.service
```

---

## 🐛 Troubleshooting

### Coral Not Detected:
```bash
lsusb  # Should show "Global Unichip Corp"
# If not, try different USB port or cable
```

### Camera Not Working:
```bash
ls /dev/video*  # Should show /dev/video0 or similar
v4l2-ctl --list-devices  # List all cameras
# Try changing CAMERA_INDEX in script
```

### NetworkTables Not Connecting:
- Check roboRIO IP in script matches your team
- Verify robot is powered on and connected
- Check firewall isn't blocking port 1735

### Low FPS:
- Use smaller model (MobileNet v2 SSD recommended)
- Reduce camera resolution
- Use libedgetpu1-max (faster but hotter)

---

## 📚 Additional Resources

- **Coral Documentation:** https://coral.ai/docs/
- **TensorFlow Lite Models:** https://www.tensorflow.org/lite/models
- **Roboflow Tutorials:** https://blog.roboflow.com/
- **FRC Chief Delphi:** https://www.chiefdelphi.com/

---

## ✅ Quick Checklist

- [ ] Collected 100+ training images of algae
- [ ] Trained model using Teachable Machine or Roboflow
- [ ] Compiled model with Edge TPU compiler
- [ ] Installed Raspberry Pi OS on Pi
- [ ] Installed Coral USB runtime
- [ ] Connected Coral USB Accelerator
- [ ] Uploaded model files to Pi
- [ ] Installed and configured vision script
- [ ] Set up auto-start service
- [ ] Verified detections in SmartDashboard
- [ ] Tested with robot in FUSION mode

---

**You're now ready to use AI-powered algae detection on your robot!** 🤖🪸
