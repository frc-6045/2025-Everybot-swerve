# 🚀 Step-by-Step: After Training Your Model

## You just downloaded model.tflite from Teachable Machine - Now what?

---

## 📁 Step 1: Extract Your Files (30 seconds)

1. **Find the downloaded file:**
   ```
   Downloads folder → converted_tflite.zip
   ```

2. **Extract the ZIP file:**
   - Double-click `converted_tflite.zip`
   - You should now see a folder with these files:
     - `model.tflite` ← You need this!
     - `labels.txt` ← You need this too!
     - `metadata.json` ← You can ignore this

3. **Keep these files handy** - you'll upload them in the next step

---

## 🌐 Step 2: Connect to Your Robot (1 minute)

1. **Turn on your robot** (or just the roboRIO + Limelight)

2. **Connect your laptop to robot WiFi:**
   ```
   WiFi Network: 1234_2025 (or your team number)
   Password: (your robot password)
   ```

3. **Verify connection:**
   - Open browser
   - Go to: **http://limelight.local:5801**
   - OR try: **http://10.TE.AM.11:5801** (replace TE.AM with team number)
     - Example for team 1234: http://10.12.34.11:5801

4. **You should see:** Limelight web interface with camera view

---

## 📤 Step 3: Upload Model to Limelight (2 minutes)

### **A. Access Neural Networks:**

1. **In Limelight web interface:**
   - Look at **left sidebar**
   - Click **"Neural Networks"** tab

2. **You should see:**
   ```
   Neural Networks
   ├─ List of models (might be empty)
   └─ [+] button at bottom
   ```

### **B. Upload Your Model:**

1. **Click the [+] button** (Add New Model)

2. **Fill in the form:**
   ```
   Model Name: algae_detector_2025

   Model File: [Browse]
     → Navigate to your Downloads/converted_tflite folder
     → Select: model.tflite

   Labels File: [Browse] (optional but recommended)
     → Select: labels.txt

   Framework: (leave as Auto-detect)
   ```

3. **Click "Upload"**

4. **Wait for conversion:**
   ```
   Progress bar will show:
   "Uploading..." → "Converting..." → "Ready"

   This takes 30 seconds - 2 minutes
   ```

5. **Verify:**
   - Status should show: ✅ **"Ready"**
   - You should see "algae_detector_2025" in the model list

---

## ⚙️ Step 4: Configure Pipeline (3 minutes)

### **A. Go to Pipelines Tab:**

1. **Click "Pipelines" tab** (left sidebar)

2. **Select Pipeline 0** (or any empty pipeline)

3. **You should see:** Pipeline configuration screen

### **B. Set Pipeline Type:**

1. **Find "Type" dropdown** at the top

2. **Select: "Neural Detector"** ⭐
   ```
   Options might include:
   - Fiducial
   - Color
   - Neural Detector ← SELECT THIS!
   - Python
   - 3D
   ```

### **C. Configure Neural Network Settings:**

Scroll down to find these settings and set them:

```
═══════════════════════════════════════════
MODEL:
═══════════════════════════════════════════

Selected Model: algae_detector_2025 ⭐
  (Pick from dropdown - your uploaded model)

═══════════════════════════════════════════
DETECTION:
═══════════════════════════════════════════

Confidence Threshold: 0.5 ⭐
  (Use slider or type number)

Max Detections: 10

Multi-Class: ✅ Enabled

═══════════════════════════════════════════
TARGETING:
═══════════════════════════════════════════

Sort Mode: Largest ⭐
  (Tracks biggest algae first)

═══════════════════════════════════════════
LED:
═══════════════════════════════════════════

LED Mode: Force On ⭐

Brightness: 100%
```

### **D. Save Pipeline:**

1. **Click "Save" button** (usually top-right)

2. **Pipeline 0 is now configured!**

---

## 🎯 Step 5: Test Detection (2 minutes)

### **A. Visual Test in Limelight:**

1. **Stay in Limelight web interface**

2. **Click "Output" tab** (left sidebar)

3. **Hold algae in front of camera**

4. **You should see:**
   ```
   ✅ Green bounding box around algae
   ✅ Label "algae" above the box
   ✅ Confidence score (e.g., "0.87")
   ✅ FPS counter (should be 20-30)
   ```

5. **Move algae around:**
   - Box should follow the algae
   - tx/ty values should update

### **B. Check NetworkTables:**

1. **Open another browser tab**

2. **Go to Limelight Settings:**
   ```
   http://limelight.local:5801 → Settings tab
   ```

3. **Verify NetworkTables enabled:**
   ```
   NetworkTables: Enabled ✅
   Team Number: [Your team number]
   ```

4. **You can check values in Driver Station too**

---

## 💻 Step 6: Deploy Robot Code (2 minutes)

Now your Limelight is ready - deploy your code!

1. **Open terminal/command prompt**

2. **Navigate to your project:**
   ```bash
   cd /Users/27johnhen/Documents/GitHub/2025-Everybot-swerve
   ```

3. **Deploy to robot:**
   ```bash
   ./gradlew deploy
   ```

4. **Wait for deployment:**
   ```
   > Task :deploy
   BUILD SUCCESSFUL
   ```

---

## 🎮 Step 7: Test with Robot (2 minutes)

### **A. Enable Robot:**

1. **Open FRC Driver Station**

2. **Enable robot in TeleOp mode**

3. **You should see in SmartDashboard:**
   ```
   Vision/HasTarget: false (no algae visible)
   HybridVision/Mode: FUSION
   HybridVision/LimelightActive: true/false
   ```

### **B. Test Auto-Tracking:**

1. **Hold algae in front of Limelight**

2. **Check SmartDashboard:**
   ```
   Vision/HasTarget: true ✅
   Vision/TX: [some angle]
   Vision/Status: "Target Acquired"
   ```

3. **Press and hold B button** on driver controller

4. **Robot should:**
   ```
   ✅ Rotate toward the algae
   ✅ Center algae in camera view
   ✅ Stop rotating when centered
   ```

5. **Move algae left/right:**
   - Robot should follow!

6. **Release B button:**
   - Robot stops tracking

---

## ✅ Step 8: Verify Everything Works

### **Checklist:**

- [ ] Model uploaded to Limelight (shows "Ready")
- [ ] Pipeline 0 set to "Neural Detector"
- [ ] Model "algae_detector_2025" selected in pipeline
- [ ] Confidence set to 0.5
- [ ] Green boxes appear when algae visible
- [ ] `limelight/tv` = 1 in NetworkTables
- [ ] Code deployed successfully
- [ ] Robot enabled
- [ ] SmartDashboard shows `Vision/HasTarget` = true
- [ ] B button makes robot track algae
- [ ] Robot centers algae automatically

### **If all checked ✅ - YOU'RE DONE!** 🎉

---

## 🔧 Step 9: Fine-Tuning (Optional - 5 minutes)

### **If tracking is too slow:**

1. **Open:** `src/main/java/frc/robot/commands/TrackTargetCommand.java`

2. **Change line 24:**
   ```java
   // OLD:
   private static final double ROTATION_KP = 0.04;

   // NEW (faster):
   private static final double ROTATION_KP = 0.06;  // or 0.08
   ```

3. **Redeploy:** `./gradlew deploy`

### **If tracking is too fast/oscillates:**

1. **Change line 24:**
   ```java
   // NEW (slower):
   private static final double ROTATION_KP = 0.02;  // or 0.03
   ```

2. **Redeploy**

### **If detection is unreliable:**

1. **Go back to Limelight web interface**

2. **Lower confidence threshold:**
   ```
   Pipelines → Pipeline 0 → Confidence: 0.4 (or 0.3)
   ```

3. **Save and test again**

---

## 📊 Monitoring Dashboard

### **What to Watch During Testing:**

**SmartDashboard Values:**
```
Vision/
├─ HasTarget (boolean)     → Should be true when algae visible
├─ TX (number)             → Angle to algae (-27 to 27)
├─ Status (string)         → "Target Acquired" or "No Target"

HybridVision/
├─ Mode (string)           → "FUSION" (default)
├─ LimelightActive (bool)  → true when Limelight sees target

limelight/
├─ tv (number)             → 1 when target, 0 when no target
├─ tx (number)             → Raw angle from Limelight
└─ tclass (string)         → Should show "algae"
```

---

## 🎯 Quick Test Procedure

**30-Second Test:**

1. ✅ Put algae in front of Limelight
2. ✅ Check green box appears in Limelight Output tab
3. ✅ Check `Vision/HasTarget` = true in SmartDashboard
4. ✅ Hold B button
5. ✅ Robot rotates to center algae
6. ✅ Success!

---

## 🐛 Troubleshooting

### **Problem: No green boxes in Limelight**

**Fix:**
1. Check pipeline is set to "Neural Detector"
2. Check correct model selected
3. Lower confidence to 0.3
4. Check Coral USB is plugged in
5. Reboot Limelight (power cycle)

### **Problem: B button doesn't work**

**Fix:**
1. Check robot is enabled
2. Check `Vision/HasTarget` = true
3. Verify B button binding in RobotContainer.java
4. Check no errors in Driver Station log

### **Problem: Tracking too jerky**

**Fix:**
1. Lower confidence threshold (0.4 or 0.3)
2. Train model with more photos (300-500)
3. Reduce `ROTATION_KP` in code

### **Problem: Wrong objects detected**

**Fix:**
1. Retrain model with more background images
2. Raise confidence threshold (0.6 or 0.7)
3. Add more variety to training data

---

## 📈 Improving Performance

### **To get better detection:**

1. **Collect more training photos:**
   - Take 200-500 photos
   - Include difficult cases (far away, partial, dim lighting)
   - Balance algae vs background (50/50)

2. **Retrain model:**
   - Upload new photos to Teachable Machine
   - Train again
   - Export as Quantized TFLite

3. **Re-upload to Limelight:**
   - Delete old model (optional)
   - Upload new model.tflite
   - Reconfigure pipeline

4. **Test again**

---

## 🏁 Competition Prep

### **Before Each Match:**

1. **Check Limelight:**
   - [ ] Green LED on
   - [ ] Camera lens clean (wipe with microfiber cloth)
   - [ ] Coral USB plugged in

2. **Check Software:**
   - [ ] Pipeline 0 selected
   - [ ] `limelight/tv` toggles when algae appears
   - [ ] SmartDashboard values updating

3. **Quick Test:**
   - [ ] Hold B button near algae
   - [ ] Robot tracks smoothly
   - [ ] Release B, robot stops

4. **Battery:**
   - [ ] Check voltage > 12V

---

## 📚 Next Steps

**You're now ready to:**

✅ Auto-track algae in matches
✅ Fine-tune tracking speed
✅ Train better models with more data
✅ Add more pipelines (color tracking, AprilTags)
✅ Create autonomous routines using vision

---

## 🎉 Success!

**Your vision system is complete and working!**

You can now:
- 🎮 Press B to auto-track algae
- 🎯 Robot automatically aims at game pieces
- 🤖 Use AI-powered detection with Google Coral
- 📊 Monitor everything in SmartDashboard

---

## 📋 Summary Flow

```
1. Download model.tflite from Teachable Machine ✓
         ↓
2. Connect to robot WiFi ✓
         ↓
3. Upload model to Limelight (Neural Networks tab) ✓
         ↓
4. Configure Pipeline 0 (Neural Detector) ✓
         ↓
5. Test in Output tab (see green boxes) ✓
         ↓
6. Deploy robot code (./gradlew deploy) ✓
         ↓
7. Test with B button ✓
         ↓
8. YOU'RE DONE! 🎉
```

---

**For detailed settings, see:** [CORAL_SETTINGS_GUIDE.md](CORAL_SETTINGS_GUIDE.md)

**For troubleshooting, see:** [SIMPLIFIED_VISION_GUIDE.md](SIMPLIFIED_VISION_GUIDE.md)

**For competition day, print:** [QUICK_START_CHEATSHEET.md](QUICK_START_CHEATSHEET.md)

---

🤖 **Good luck at competition!** 🏆
