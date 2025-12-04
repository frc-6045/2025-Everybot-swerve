# 🎯 Complete Workflow - From Zero to Tracking

## Visual step-by-step guide from training to robot tracking algae

---

## 📊 The Big Picture

```
┌─────────────────────────────────────────────────────────────┐
│                    COMPLETE WORKFLOW                        │
└─────────────────────────────────────────────────────────────┘

Step 1: COLLECT PHOTOS (Day 1 - 30 min)
├─ Take 100-500 photos of algae
├─ Take 100-500 photos of background
└─ Save to folders
         ↓
Step 2: TRAIN MODEL (Day 1 - 15 min active, 30 min wait)
├─ Upload photos to Teachable Machine
├─ Click "Train Model"
├─ Wait for training (15-30 min)
└─ Export as Quantized TFLite
         ↓
Step 3: UPLOAD TO LIMELIGHT (Day 1 - 5 min)
├─ Connect to robot WiFi
├─ Go to limelight.local:5801
├─ Upload model.tflite
└─ Wait for conversion
         ↓
Step 4: CONFIGURE PIPELINE (Day 1 - 3 min)
├─ Set Type: Neural Detector
├─ Select your model
├─ Set confidence: 0.5
└─ Save pipeline
         ↓
Step 5: TEST DETECTION (Day 1 - 2 min)
├─ Check Output tab
├─ See green boxes?
└─ Verify NetworkTables
         ↓
Step 6: DEPLOY CODE (Day 1 - 2 min)
├─ Run: ./gradlew deploy
└─ Wait for completion
         ↓
Step 7: TEST WITH ROBOT (Day 1 - 5 min)
├─ Enable robot
├─ Hold B button
└─ Watch robot track algae!
         ↓
Step 8: TUNE & PRACTICE (Day 2+)
├─ Adjust tracking speed
├─ Improve model with more photos
├─ Practice driving
└─ Ready for competition! 🏆
```

---

## 📅 Day-by-Day Timeline

### **Day 1: Setup (Total: ~2 hours)**

#### **Morning (1 hour):**
```
☐ Take photos of algae (30 min)
  ├─ 100+ with algae
  └─ 100+ without algae

☐ Upload to Teachable Machine (5 min)
  ├─ Create classes
  └─ Upload photos

☐ Train model (15 min active + 30 min wait)
  ├─ Click "Train"
  ├─ Go get lunch while it trains
  └─ Export as Quantized TFLite
```

#### **Afternoon (1 hour):**
```
☐ Upload to Limelight (5 min)
  ├─ Extract model.tflite
  ├─ Go to limelight.local:5801
  └─ Upload in Neural Networks tab

☐ Configure pipeline (5 min)
  ├─ Set to Neural Detector
  ├─ Select model
  └─ Set confidence to 0.5

☐ Test in Limelight (5 min)
  ├─ Check Output tab
  └─ Verify green boxes appear

☐ Deploy code (5 min)
  └─ ./gradlew deploy

☐ Test with robot (15 min)
  ├─ Enable robot
  ├─ Test B button
  ├─ Adjust tracking speed if needed
  └─ Success! ✓

☐ Document settings (5 min)
  └─ Write down what works
```

### **Day 2: Improve (1-2 hours)**

```
☐ Test at different distances (30 min)
  ├─ 2 feet
  ├─ 5 feet
  ├─ 10 feet
  └─ 15 feet

☐ Test in different lighting (30 min)
  ├─ Bright (windows/outdoors)
  ├─ Dim (pit area)
  └─ LED lights only

☐ Collect more photos where it fails (30 min)
  └─ Retrain if needed

☐ Practice driving with tracking (30 min)
  └─ Driver gets comfortable with B button
```

### **Day 3+: Competition Prep**

```
☐ Create pre-match checklist
☐ Print QUICK_START_CHEATSHEET.md
☐ Test in competition lighting
☐ Practice autonomous routines
☐ Ready! 🎉
```

---

## 🔄 Detailed Step-by-Step with Screenshots

### **STEP 1: COLLECT PHOTOS**

```
What you need:
- Phone or camera
- Algae game piece
- Different backgrounds
- Different lighting

How to take photos:
1. Put algae on floor
2. Take photo
3. Move algae to new position
4. Take photo
5. Repeat 100-500 times

6. Take photos WITHOUT algae
7. Just background
8. Repeat 100-500 times

Save to:
~/Desktop/training_photos/
  ├─ algae/
  │   ├─ IMG001.jpg
  │   ├─ IMG002.jpg
  │   └─ ... (100-500 photos)
  └─ background/
      ├─ IMG001.jpg
      ├─ IMG002.jpg
      └─ ... (100-500 photos)
```

---

### **STEP 2: TRAIN MODEL**

```
┌────────────────────────────────────────┐
│  Google Teachable Machine              │
│  teachablemachine.withgoogle.com       │
└────────────────────────────────────────┘

1. Click "Get Started"
         ↓
2. Click "Image Project"
         ↓
3. Click "Standard image model"
         ↓
4. Rename "Class 1" → "algae"
         ↓
5. Click "Upload" → Select all algae photos
         ↓
6. Rename "Class 2" → "background"
         ↓
7. Click "Upload" → Select all background photos
         ↓
8. Click "Train Model"
         ↓
9. ☕ Wait 15-30 minutes
         ↓
10. Click "Export Model"
         ↓
11. Select "TensorFlow Lite" tab
         ↓
12. ✅ Check "Quantized" ⭐⭐⭐ IMPORTANT!
         ↓
13. Click "Download my model"
         ↓
14. Save: converted_tflite.zip
         ↓
15. Extract ZIP
         ↓
16. You now have: model.tflite ✓
```

---

### **STEP 3: UPLOAD TO LIMELIGHT**

```
┌────────────────────────────────────────┐
│  Limelight Web Interface               │
│  http://limelight.local:5801           │
└────────────────────────────────────────┘

1. Turn on robot
         ↓
2. Connect laptop to robot WiFi
   (Network: 1234_2025 or your team number)
         ↓
3. Open browser
         ↓
4. Go to: http://limelight.local:5801
         ↓
5. Click "Neural Networks" tab (left sidebar)
         ↓
6. Click [+] button (bottom of page)
         ↓
7. Fill in form:
   ├─ Model Name: algae_detector_2025
   ├─ Model File: [Browse] → model.tflite
   └─ Labels File: [Browse] → labels.txt
         ↓
8. Click "Upload"
         ↓
9. Wait for "Converting..."
         ↓
10. Status shows: "Ready" ✓
```

---

### **STEP 4: CONFIGURE PIPELINE**

```
Still in Limelight web interface:

1. Click "Pipelines" tab (left sidebar)
         ↓
2. Select "Pipeline 0"
         ↓
3. Find "Type" dropdown at top
         ↓
4. Select: "Neural Detector" ⭐
         ↓
5. Scroll down to settings:
   ├─ Selected Model: algae_detector_2025
   ├─ Confidence: 0.5
   ├─ Max Detections: 10
   ├─ Sort Mode: Largest
   └─ LED Mode: Force On
         ↓
6. Click "Save" (top right)
         ↓
7. Pipeline configured! ✓
```

---

### **STEP 5: TEST DETECTION**

```
Still in Limelight web interface:

1. Click "Output" tab (left sidebar)
         ↓
2. You should see: Live camera view
         ↓
3. Hold algae in front of camera
         ↓
4. Look for:
   ├─ ✅ Green box around algae
   ├─ ✅ Label "algae" shown
   ├─ ✅ Confidence score (0.5-1.0)
   └─ ✅ FPS counter (20-30)
         ↓
5. Move algae around
   └─ Box should follow ✓
         ↓
6. Remove algae
   └─ Box should disappear ✓
         ↓
7. Detection working! ✓
```

---

### **STEP 6: DEPLOY CODE**

```
On your laptop:

1. Open Terminal (Mac) or Command Prompt (Windows)
         ↓
2. Navigate to project:
   cd /Users/27johnhen/Documents/GitHub/2025-Everybot-swerve
         ↓
3. Deploy to robot:
   ./gradlew deploy
         ↓
4. Wait for output:
   > Task :deploy
   BUILD SUCCESSFUL
         ↓
5. Code deployed! ✓
```

---

### **STEP 7: TEST WITH ROBOT**

```
┌────────────────────────────────────────┐
│  FRC Driver Station                    │
└────────────────────────────────────────┘

1. Open Driver Station
         ↓
2. Enable robot (TeleOp mode)
         ↓
3. Open SmartDashboard or Shuffleboard
         ↓
4. Check values:
   ├─ Vision/HasTarget: false (no algae yet)
   ├─ HybridVision/Mode: FUSION
   └─ HybridVision/LimelightActive: true
         ↓
5. Hold algae in front of Limelight
         ↓
6. Check SmartDashboard:
   ├─ Vision/HasTarget: true ✓
   ├─ Vision/TX: [some angle] ✓
   └─ Vision/Status: "Target Acquired" ✓
         ↓
7. Press and HOLD B button on driver controller
         ↓
8. Robot should:
   ├─ ✅ Start rotating
   ├─ ✅ Turn toward algae
   ├─ ✅ Center algae in camera
   └─ ✅ Stop when centered
         ↓
9. Move algae left/right
   └─ Robot should follow ✓
         ↓
10. Release B button
    └─ Robot stops ✓
         ↓
11. SUCCESS! 🎉
```

---

## 🎯 Verification Checklist

After completing all steps, verify:

### **Limelight Checks:**
- [ ] Model status: "Ready"
- [ ] Pipeline 0: Neural Detector
- [ ] Model selected: algae_detector_2025
- [ ] Confidence: 0.5
- [ ] Green boxes appear in Output tab
- [ ] FPS: 20-30

### **NetworkTables Checks:**
- [ ] `limelight/tv` = 1 (when algae visible)
- [ ] `limelight/tx` = angle value
- [ ] `limelight/tclass` = "algae"

### **Robot Code Checks:**
- [ ] Code deploys without errors
- [ ] Robot enables successfully
- [ ] `Vision/HasTarget` = true when algae visible
- [ ] `Vision/TX` value updates

### **Tracking Checks:**
- [ ] B button press detected
- [ ] Robot rotates toward algae
- [ ] Robot centers algae
- [ ] Robot stops when centered
- [ ] Tracking is smooth (not jerky)

### **If ALL checked ✅:**
```
┌────────────────────────────────────────┐
│                                        │
│    🎉 YOU'RE READY FOR COMPETITION! 🎉 │
│                                        │
└────────────────────────────────────────┘
```

---

## 🔧 Common Adjustments

### **Tracking Too Slow?**
```
File: TrackTargetCommand.java (line 24)
Change: ROTATION_KP = 0.04
To:     ROTATION_KP = 0.06 or 0.08
Redeploy: ./gradlew deploy
```

### **Tracking Too Fast?**
```
File: TrackTargetCommand.java (line 24)
Change: ROTATION_KP = 0.04
To:     ROTATION_KP = 0.02 or 0.03
Redeploy: ./gradlew deploy
```

### **Not Detecting Reliably?**
```
Limelight: Pipelines → Pipeline 0
Change: Confidence = 0.5
To:     Confidence = 0.4 or 0.3
Save
```

### **Too Many False Detections?**
```
Limelight: Pipelines → Pipeline 0
Change: Confidence = 0.5
To:     Confidence = 0.6 or 0.7
Save
```

---

## 📊 Troubleshooting Decision Tree

```
Is Limelight showing green boxes?
├─ NO
│  └─ Check:
│     ├─ Pipeline is "Neural Detector"?
│     ├─ Correct model selected?
│     ├─ Coral USB plugged in?
│     ├─ Confidence too high?
│     └─ Reboot Limelight
│
└─ YES
   └─ Is limelight/tv = 1 in NetworkTables?
      ├─ NO
      │  └─ Check:
      │     ├─ NetworkTables enabled?
      │     ├─ Robot connected?
      │     └─ Restart robot code
      │
      └─ YES
         └─ Does B button work?
            ├─ NO
            │  └─ Check:
            │     ├─ Robot enabled?
            │     ├─ B button bound?
            │     └─ Vision/HasTarget true?
            │
            └─ YES
               └─ 🎉 WORKING!
```

---

## 🏁 Pre-Match Quick Check (60 seconds)

```
Before EVERY match:

[ ] 1. Limelight green LED on                    (5 sec)
[ ] 2. Wipe camera lens                          (5 sec)
[ ] 3. Hold algae → green box appears?           (5 sec)
[ ] 4. Check limelight/tv toggles               (5 sec)
[ ] 5. Enable robot                             (5 sec)
[ ] 6. Hold B button → robot tracks?            (10 sec)
[ ] 7. Battery > 12V                            (5 sec)
[ ] 8. Driver comfortable with controls         (20 sec)

Total time: ~60 seconds
```

---

## 📈 Performance Timeline

```
Week 1:
├─ Day 1: Setup complete
├─ Day 2-3: Basic testing
├─ Day 4-5: Collect more photos
└─ Day 6-7: Retrain with better model

Week 2:
├─ Day 8-10: Practice driving with tracking
├─ Day 11-12: Test in competition conditions
└─ Day 13-14: Final tuning

Week 3+:
└─ Competition ready! 🏆
```

---

## 🎉 Success Metrics

**You know it's working when:**

✅ Detection: Green boxes appear 95%+ of the time
✅ Accuracy: Correct object identified (not false positives)
✅ Speed: FPS stays above 20
✅ Tracking: Robot smoothly centers algae
✅ Reliability: Works in bright AND dim lighting
✅ Driver: Can use B button without thinking

---

## 📚 Reference Documents

**For each stage:**

- **Training:** [CORAL_SETTINGS_GUIDE.md](CORAL_SETTINGS_GUIDE.md)
- **Setup:** [STEP_BY_STEP_AFTER_TRAINING.md](STEP_BY_STEP_AFTER_TRAINING.md)
- **Competition:** [QUICK_START_CHEATSHEET.md](QUICK_START_CHEATSHEET.md)
- **Overview:** [README_VISION.md](README_VISION.md)
- **Architecture:** [VISION_SYSTEM_OVERVIEW.md](VISION_SYSTEM_OVERVIEW.md)

---

**Follow this workflow and you'll be tracking algae in no time!** 🤖🪸
