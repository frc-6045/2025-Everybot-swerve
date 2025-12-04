# 🎯 Complete Vision System Overview

## 📊 System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         YOUR ROBOT                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────────────────────────────────────────┐      │
│  │         RoboRIO (Main Robot Controller)              │      │
│  │                                                       │      │
│  │  ┌────────────────────────────────────────────────┐  │      │
│  │  │      HybridVisionSubsystem (Java)              │  │      │
│  │  │                                                │  │      │
│  │  │  ┌──────────────┐    ┌──────────────────┐    │  │      │
│  │  │  │  Limelight   │    │  Coral           │    │  │      │
│  │  │  │  Vision      │    │  Vision          │    │  │      │
│  │  │  │  Subsystem   │    │  Subsystem       │    │  │      │
│  │  │  └──────┬───────┘    └────────┬─────────┘    │  │      │
│  │  │         │                     │               │  │      │
│  │  │         │                     │               │  │      │
│  │  │    NetworkTables         NetworkTables       │  │      │
│  │  │         │                     │               │  │      │
│  │  └─────────┼─────────────────────┼───────────────┘  │      │
│  │            │                     │                  │      │
│  └────────────┼─────────────────────┼──────────────────┘      │
│               │                     │                         │
└───────────────┼─────────────────────┼─────────────────────────┘
                │                     │
                │                     │
    ┌───────────▼──────────┐   ┌──────▼────────────────────┐
    │   Limelight 3        │   │   Raspberry Pi            │
    │   (Smart Camera)     │   │   (Coprocessor)           │
    │                      │   │                           │
    │  • Real-time vision  │   │  ┌──────────────────────┐ │
    │  • AprilTags         │   │  │  Coral USB           │ │
    │  • Neural Detector   │   │  │  Accelerator         │ │
    │  • Color tracking    │   │  │  (AI Chip)           │ │
    │                      │   │  └──────────────────────┘ │
    │  Publishes to:       │   │                           │
    │  limelight/*         │   │  Runs: coral_vision.py    │
    │                      │   │                           │
    └──────────────────────┘   │  Publishes to:            │
                               │  Coral/*                  │
                               └───────────────────────────┘
```

---

## 🔄 How Data Flows

### **1. Limelight Path (Fast & Simple)**
```
Camera → Limelight Processing → NetworkTables → HybridVisionSubsystem
                                                         ↓
                                              TrackTargetCommand
                                                         ↓
                                                  Swerve Drive
```

### **2. Coral Path (Accurate & Smart)**
```
Camera → Raspberry Pi → Coral AI Chip → Python Script → NetworkTables
                                                              ↓
                                                  HybridVisionSubsystem
                                                              ↓
                                                   TrackTargetCommand
                                                              ↓
                                                        Swerve Drive
```

### **3. FUSION Mode (Best of Both)**
```
┌─ Limelight ─┐
│   TX: -5.2° │─┐
└─────────────┘ │
                ├─→ Average → -4.9° → TrackTargetCommand
┌─ Coral ─────┐ │
│   TX: -4.6° │─┘
└─────────────┘
```

---

## 📡 NetworkTables Values Published

### **Limelight → NetworkTables:**
```
limelight/
├── tv (0 or 1)          - Has target?
├── tx (-27 to 27)       - Horizontal offset (degrees)
├── ty (-20 to 20)       - Vertical offset (degrees)
├── ta (0 to 100)        - Target area (%)
├── tclass (string)      - Class name (if using neural detector)
└── pipeline (0-9)       - Active pipeline
```

### **Coral → NetworkTables:**
```
Coral/
├── connected (boolean)           - Is coprocessor connected?
├── num_detections (number)       - How many objects detected
├── labels (string array)         - ["algae", "algae", ...]
├── confidences (number array)    - [0.95, 0.87, ...]
├── x_positions (number array)    - [0.5, 0.3, ...] (normalized 0-1)
├── y_positions (number array)    - [0.4, 0.6, ...]
├── widths (number array)         - [0.2, 0.15, ...]
├── heights (number array)        - [0.3, 0.25, ...]
└── timestamp (number)            - Last update time
```

### **Hybrid Vision → SmartDashboard:**
```
HybridVision/
├── Mode (string)                 - "FUSION", "LIMELIGHT_ONLY", etc.
├── HasTarget (boolean)           - Combined detection status
├── TX (number)                   - Combined horizontal offset
├── TY (number)                   - Combined vertical offset
├── Area (number)                 - Combined target area
├── LimelightActive (boolean)     - Is Limelight seeing something?
├── CoralActive (boolean)         - Is Coral detecting?
└── TotalDetections (number)      - Total objects from both systems
```

---

## 🎮 Control Flow

```
Driver presses B button
         ↓
TrackTargetCommand starts
         ↓
Command asks: m_vision.hasTarget()
         ↓
HybridVisionSubsystem checks FUSION mode:
  - Limelight has target? YES
  - Coral has detection? YES
  - Are they looking at same thing? YES
         ↓
Returns: TRUE
         ↓
Command asks: m_vision.getHorizontalOffset()
         ↓
HybridVisionSubsystem:
  - Gets Limelight TX: -5.2°
  - Gets Coral TX: -4.6°
  - Averages: -4.9°
         ↓
Returns: -4.9°
         ↓
TrackTargetCommand calculates:
  rotation = -4.9 * 0.04 = -0.196
         ↓
Sends to drive: m_drive.drive(0, 0, -0.196)
         ↓
Robot rotates left to center algae!
```

---

## 🎯 Vision Modes Comparison

| Mode | Speed | Accuracy | Latency | Use Case |
|------|-------|----------|---------|----------|
| **FUSION** | Medium | ⭐⭐⭐⭐⭐ | ~50ms | Default - Best overall |
| **LIMELIGHT_ONLY** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ~20ms | Fast teleop tracking |
| **CORAL_ONLY** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ~60ms | Accurate classification |
| **LIMELIGHT_PRIMARY** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ~25ms | Fast with backup |
| **CORAL_PRIMARY** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ~60ms | Accurate with backup |

---

## 🔧 Hardware Requirements

### **Minimum Setup (Limelight Only):**
- ✅ Limelight camera
- ✅ roboRIO
- ✅ Ethernet connection

### **Full Setup (Hybrid):**
- ✅ Limelight camera
- ✅ Raspberry Pi 3B+ or 4
- ✅ Google Coral USB Accelerator
- ✅ USB Camera (or Pi Camera)
- ✅ roboRIO
- ✅ Network switch (to connect all devices)
- ✅ PoE injector or power supply for Limelight
- ✅ Power supply for Raspberry Pi

---

## 📁 Complete File Structure

```
2025-Everybot-swerve/
├── src/main/java/frc/robot/
│   ├── subsystems/
│   │   ├── LimelightVisionSubsystem.java    ← Limelight interface
│   │   ├── CoralVisionSubsystem.java        ← Coral interface
│   │   ├── HybridVisionSubsystem.java       ← Combines both ⭐
│   │   └── Detection.java                    ← Detection data class
│   ├── commands/
│   │   └── TrackTargetCommand.java          ← Auto-tracking command
│   ├── RobotContainer.java                   ← Button bindings
│   └── Constants.java                        ← Vision tuning values
│
├── coral_coprocessor/
│   ├── coral_vision.py                       ← Python script for Pi
│   ├── requirements.txt                      ← Python dependencies
│   └── README.md                             ← Setup instructions
│
├── CORAL_SETUP_GUIDE.md                      ← Full Coral setup guide
├── QUICK_CORAL_SETUP.md                      ← Quick start guide
└── VISION_SYSTEM_OVERVIEW.md                 ← This file
```

---

## 🚀 Quick Start Checklist

### **For Limelight Only (10 minutes):**
- [ ] Connect Limelight to robot network
- [ ] Configure pipeline at limelight.local:5801
- [ ] Deploy robot code
- [ ] Test with D-Pad LEFT → Hold B button

### **For Full Hybrid System (45 minutes):**
- [ ] Train model using Teachable Machine
- [ ] Compile model for Edge TPU
- [ ] Setup Raspberry Pi with Coral runtime
- [ ] Upload model and script to Pi
- [ ] Configure robot IP in script
- [ ] Test Coral connection
- [ ] Deploy robot code
- [ ] Test with D-Pad UP (FUSION) → Hold B button

---

## 📊 Performance Expectations

### **Limelight:**
- FPS: 60-90 (depending on pipeline)
- Latency: 11-22ms
- Range: Up to 20+ feet (depends on lighting/target)

### **Coral:**
- FPS: 20-30 (with USB camera)
- Latency: 50-80ms (includes network transfer)
- Accuracy: 85-95% (with good training data)

### **FUSION Mode:**
- FPS: 20-30 (limited by Coral)
- Latency: ~60ms
- Accuracy: 90-98% (best of both systems)

---

## 🎉 What You Get

✅ **Real-time algae tracking** - Hold B button to auto-aim
✅ **Redundancy** - If one system fails, use the other
✅ **Flexibility** - Switch modes with D-Pad
✅ **Accuracy** - FUSION mode combines both for best results
✅ **Easy tuning** - All constants in Constants.java
✅ **Full monitoring** - SmartDashboard shows everything

---

**Questions? See the detailed guides:**
- [CORAL_SETUP_GUIDE.md](CORAL_SETUP_GUIDE.md) - Complete setup
- [QUICK_CORAL_SETUP.md](QUICK_CORAL_SETUP.md) - Quick start
- [coral_coprocessor/README.md](coral_coprocessor/README.md) - Python script docs
