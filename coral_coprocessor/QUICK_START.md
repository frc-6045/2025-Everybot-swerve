# Coral Vision Quick Start Guide

## 5-Minute Setup (Competition Day)

### Prerequisites
- Raspberry Pi with Coral already configured (see main README.md for first-time setup)
- Model files already on Pi
- Pi connected to robot network

### Competition Setup

1. **Power on Raspberry Pi**
   - Connect power cable
   - Wait ~30 seconds for boot

2. **SSH into Pi**
   ```bash
   ssh pi@10.TE.AM.10  # Replace TE.AM with your team number
   # Default password: raspberry (change this!)
   ```

3. **Start vision script**
   ```bash
   cd coral_coprocessor
   python3 coral_vision.py --robot-ip 10.TE.AM.2
   ```

4. **Verify on Driver Station**
   - Open SmartDashboard
   - Check `Coral/Connected` = true
   - Check `Coral/Detections` shows count

### Auto-Start on Boot

If configured with systemd (recommended):

```bash
# Check status
sudo systemctl status coral-vision

# Start manually
sudo systemctl start coral-vision

# View logs
sudo journalctl -u coral-vision -f
```

## Quick Commands Reference

### On Raspberry Pi

```bash
# Test camera
fswebcam test.jpg

# Check Coral is detected
lsusb | grep "Google"

# Run with display output (for testing)
python3 coral_vision.py --display

# Change confidence threshold
python3 coral_vision.py --threshold 0.3

# Stop running script
Ctrl+C

# View system resources
htop
```

### On Driver Station

```bash
# Ping Raspberry Pi
ping 10.TE.AM.10

# SSH to Pi
ssh pi@10.TE.AM.10

# Copy files to Pi
scp mymodel.tflite pi@10.TE.AM.10:~/coral_coprocessor/models/
```

## Troubleshooting Checklist

- [ ] Raspberry Pi powered on and connected to network
- [ ] Can ping Pi from driver station: `ping 10.TE.AM.10`
- [ ] Coral USB is connected: `lsusb | grep Google`
- [ ] Camera is connected: `ls /dev/video*`
- [ ] Python script is running: `ps aux | grep coral`
- [ ] Model files exist: `ls ~/coral_coprocessor/models/`
- [ ] Robot code is running
- [ ] NetworkTables shows `Coral/Connected = true`

## Common Issues

### "Camera not found"
```bash
# List cameras
ls -l /dev/video*

# If no video devices, reconnect USB camera and reboot
sudo reboot
```

### "Model not found"
```bash
# Check model file exists
ls -l ~/coral_coprocessor/models/

# Make sure path in script matches file
```

### "NetworkTables not connecting"
```bash
# Verify robot IP
ping 10.TE.AM.2

# Check robot code is running
# Check firewall isn't blocking port 1735
```

### Low FPS
```bash
# Make sure you're using Edge TPU model (ends with _edgetpu.tflite)
# Close other programs on Pi
# Consider using libedgetpu1-max for higher performance
```

## Emergency Procedures

### Pi Not Responding
1. Power cycle the Pi (unplug, wait 5 seconds, plug back in)
2. Wait 30 seconds for boot
3. Try SSH again
4. If still not working, check network cables

### Vision Script Crashed
```bash
# Find and kill old process
ps aux | grep coral
kill <PID>

# Restart script
python3 coral_vision.py --robot-ip 10.TE.AM.2
```

### Robot Can't See Vision Data
1. Check `Coral/Connected` on SmartDashboard
2. Restart vision script on Pi
3. Redeploy robot code
4. Check network cables

## Quick Settings

Edit these in `coral_vision.py`:

```python
# Camera settings
CAMERA_WIDTH = 640
CAMERA_HEIGHT = 480
CAMERA_FPS = 30

# Detection settings
CONFIDENCE_THRESHOLD = 0.5

# Network settings
ROBOT_IP = '10.TE.AM.2'  # Match your team number
```

## Match Checklist

Before each match:
- [ ] Pi is powered on
- [ ] Green LED on Coral USB is lit
- [ ] `coral_vision.py` is running (or systemd service active)
- [ ] SmartDashboard shows `Coral/Connected = true`
- [ ] Test detection by showing camera a known object
- [ ] Check FPS is reasonable (>15 FPS)

## Performance Monitoring

On SmartDashboard, watch:
- `Coral/Connected` - Should be `true`
- `Coral/Detections` - Number of objects detected
- `Coral/Status` - Status message
- Check for FPS in terminal output on Pi

Good FPS: >20
Acceptable FPS: 10-20
Problem FPS: <10

## Contact Info

**In case of emergency:**
1. Check this guide
2. Ask programming team lead
3. Check Chief Delphi forums
4. Use AprilTag vision as fallback

---

**Remember**: Always test vision before matches. Have a backup plan if vision fails!
