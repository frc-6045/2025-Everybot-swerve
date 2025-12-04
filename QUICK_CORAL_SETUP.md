# 🚀 Quick Coral Setup - TL;DR Version

**Goal:** Get your Coral detecting algae in 30 minutes or less!

---

## 📝 Super Quick Steps

### 1. Train a Model (15 minutes)

**Use Teachable Machine (EASIEST):**

1. Go to: https://teachablemachine.withgoogle.com/
2. Click "Image Project"
3. Add class "algae" → upload 100+ algae photos
4. Add class "background" → upload 100+ non-algae photos
5. Click "Train Model"
6. Export as "TensorFlow Lite" → "Quantized"
7. Download files

**You get:**
- `model.tflite`
- `labels.txt`

### 2. Convert for Coral (5 minutes)

On your computer with Linux/Mac:

```bash
# Install compiler
curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
echo "deb https://packages.cloud.google.com/apt coral-edgetpu-stable main" | sudo tee /etc/apt/sources.list.d/coral-edgetpu.list
sudo apt-get update
sudo apt-get install edgetpu-compiler

# Compile your model
edgetpu_compiler model.tflite
```

**Result:** `model_edgetpu.tflite` ✅

### 3. Setup Raspberry Pi (5 minutes)

SSH into your Pi and run:

```bash
# Install Coral runtime
echo "deb https://packages.cloud.google.com/apt coral-edgetpu-stable main" | sudo tee /etc/apt/sources.list.d/coral-edgetpu.list
curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
sudo apt-get update
sudo apt-get install libedgetpu1-max python3-pip python3-opencv -y

# Install Python packages
pip3 install tflite-runtime pynetworktables numpy pillow

# Create directories
mkdir -p ~/frc-vision/models
```

### 4. Upload Files (2 minutes)

From your computer:

```bash
# Copy model files
scp model_edgetpu.tflite pi@raspberrypi.local:~/frc-vision/models/detect_edgetpu.tflite
scp labels.txt pi@raspberrypi.local:~/frc-vision/models/labelmap.txt

# Copy vision script
scp -r coral_coprocessor/ pi@raspberrypi.local:~/frc-vision/
```

### 5. Configure Script (2 minutes)

SSH into Pi:

```bash
nano ~/frc-vision/coral_coprocessor/coral_vision.py
```

Change this line (your team number):
```python
ROBOT_IP = '10.TE.AM.2'  # Example: '10.12.34.2' for team 1234
```

Save and exit: `Ctrl+X` → `Y` → `Enter`

### 6. Test It! (1 minute)

```bash
cd ~/frc-vision/coral_coprocessor
python3 coral_vision.py
```

**You should see:**
```
Coral Vision for FRC
Loading model... ✓
Camera opened... ✓
Connected to NetworkTables! ✓
Starting vision processing...
FPS: 28.5 | Detections: 2
```

### 7. Check SmartDashboard

On your driver station, you should see:
- `Coral/connected` = true
- `Coral/num_detections` = (number)
- `HybridVision/CoralActive` = true

---

## 🎉 Done! Your Coral is Working!

Now test on your robot:
1. Hold **B button** on driver controller
2. Robot should auto-track algae using Coral + Limelight!

---

## 🔧 Make It Auto-Start

So it runs on boot:

```bash
sudo nano /etc/systemd/system/frc-vision.service
```

Paste this:
```ini
[Unit]
Description=FRC Coral Vision
After=network.target

[Service]
Type=simple
User=pi
WorkingDirectory=/home/pi/frc-vision/coral_coprocessor
ExecStart=/usr/bin/python3 coral_vision.py --robot-ip 10.TE.AM.2
Restart=always

[Install]
WantedBy=multi-user.target
```

Enable it:
```bash
sudo systemctl enable frc-vision.service
sudo systemctl start frc-vision.service
```

---

## 📊 What Files Go Where?

**Your Computer:**
```
2025-Everybot-swerve/
├── coral_coprocessor/
│   ├── coral_vision.py          ← The Python script
│   ├── requirements.txt
│   └── README.md
└── CORAL_SETUP_GUIDE.md          ← Full guide
```

**Raspberry Pi:**
```
/home/pi/frc-vision/
├── models/
│   ├── detect_edgetpu.tflite    ← Your trained model
│   └── labelmap.txt             ← Class names
└── coral_coprocessor/
    └── coral_vision.py          ← The script
```

---

## ❌ Troubleshooting Quick Fixes

**"Error loading model"**
```bash
# Make sure it's the _edgetpu.tflite file!
ls ~/frc-vision/models/
# Should show: detect_edgetpu.tflite
```

**"Could not open camera"**
```bash
# Check cameras available
ls /dev/video*
# Try different camera index in script (0, 1, or 2)
```

**"No NetworkTables connection"**
```bash
# Check roboRIO IP is correct in script
# Make sure robot is ON and connected
ping 10.TE.AM.2
```

**"Coral not detected"**
```bash
# Plug Coral into USB 3.0 port (blue)
lsusb | grep "Global Unichip"
# Should show Coral device
```

---

## 🎯 Quick Tips

**Improve Detection:**
- Take 500+ training photos (more = better!)
- Vary lighting conditions
- Include different angles and distances
- 50/50 split: algae photos vs background photos

**Speed Up Processing:**
- Use smaller camera resolution
- Lower FPS if needed
- Use `libedgetpu1-max` (faster but hotter)

**Debug:**
```bash
# View live logs
sudo journalctl -u frc-vision.service -f

# Restart service
sudo systemctl restart frc-vision.service

# Check status
sudo systemctl status frc-vision.service
```

---

**Need detailed instructions?** See [CORAL_SETUP_GUIDE.md](CORAL_SETUP_GUIDE.md)

**Questions?** Check [coral_coprocessor/README.md](coral_coprocessor/README.md)

---

🎉 **You're ready to track algae with AI!**
