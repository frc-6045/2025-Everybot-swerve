# Test Algae Detection WITHOUT Robot Movement

## ✅ Auto-Drive is DISABLED for Testing

The B button is currently **commented out** so the robot **will NOT move**.
You can safely test detection and tune the Limelight without the robot driving.

---

## 🧪 How to Test Detection

### Step 1: Deploy Code
```cmd
gradlew deploy
```

### Step 2: Access Limelight Web Interface

**Open browser:**
```
http://limelight.local:5801
```
Or if that doesn't work:
```
http://10.TE.AM.11:5801
```
(Replace TE.AM with your team number, e.g., 10.60.45.11)

### Step 3: Configure Pipeline 0 for Algae

1. **Select Pipeline 0** from dropdown

2. **Set to "Color" mode**

3. **Place algae in front of camera**

4. **Tune HSV sliders until ONLY algae is highlighted green:**

   **Starting values for GREEN algae:**
   ```
   Hue:        40-80
   Saturation: 150-255
   Value:      80-255
   ```

5. **Look for the green box/crosshair around algae in the camera view** ✅

6. **Tune contours:**
   - Area Min: ~1%
   - Area Max: ~100%
   - Aspect Ratio: Adjust based on algae shape
   - Fullness: Adjust based on algae density

### Step 4: Verify Detection

**In Limelight interface, check:**
- `tv` = 1.0 (target valid - algae detected)
- `tx` = horizontal offset (should change as you move algae left/right)
- `ty` = vertical offset (should change as you move algae up/down)
- `ta` = target area (percentage of screen)

**You should see:**
- ✅ Green box drawn around the algae
- ✅ Crosshair on center of algae
- ✅ Values updating in real-time as you move algae

### Step 5: Check SmartDashboard (Optional)

**Open SmartDashboard/Shuffleboard:**

Look for these values:
```
Algae/HasTarget  → true (when algae detected)
Algae/TX         → -27 to +27 (horizontal angle)
Algae/TY         → -20 to +20 (vertical angle)
Algae/Distance   → Distance in meters
Algae/Angle      → Angle to algae
```

**These values should update as you move the algae around!**

---

## 🎯 Good Detection Checklist

- [ ] Green box appears around algae in Limelight view
- [ ] Crosshair centered on algae
- [ ] `tv` = 1.0 in Limelight interface
- [ ] `tx` changes when algae moves left/right
- [ ] `ty` changes when algae moves up/down
- [ ] SmartDashboard `Algae/HasTarget` = true
- [ ] SmartDashboard values update in real-time

---

## 🔧 Troubleshooting Detection

### Problem: No green box around algae

**Solutions:**
1. HSV range too narrow → Widen Hue range (try 35-85)
2. Too dark → Lower Value minimum (try 50-255)
3. Wrong color → Adjust Hue (green should be 40-80)

### Problem: Everything is highlighted green

**Solutions:**
1. HSV range too wide → Narrow Hue range (try 50-70)
2. Too bright → Raise Value minimum (try 100-255)
3. Increase Saturation minimum (try 180-255)

### Problem: Box flickers on/off

**Solutions:**
1. Inconsistent lighting → Adjust exposure in Input tab
2. Area threshold too high → Lower Area Min to 0.5%
3. Add filtering → Increase Fullness threshold

### Problem: Multiple boxes appear

**Solutions:**
1. Set "Sort Mode" to "Largest" (detect biggest target only)
2. Increase Area Min threshold
3. Tune Fullness to filter noise

---

## ✅ When Detection is Working

**You should see:**
- Stable green box around algae
- Crosshair in center of algae
- `tv` = 1.0 consistently
- Values smooth and stable (not flickering)

**Then you're ready to test with robot movement!**

---

## 🚀 Enable Auto-Drive (After Testing)

When you're confident detection is working:

1. **Open RobotContainer.java**

2. **Find line 134:**
   ```java
   // m_driverController.b().whileTrue(new DriveToGamePieceCommand(m_drive, m_limelight));
   ```

3. **Uncomment it (remove the //):**
   ```java
   m_driverController.b().whileTrue(new DriveToGamePieceCommand(m_drive, m_limelight));
   ```

4. **Also uncomment line 16:**
   ```java
   import frc.robot.commands.DriveToGamePieceCommand;
   ```

5. **Redeploy:**
   ```cmd
   gradlew deploy
   ```

6. **Now B button will work** - Robot will drive to algae!

---

## 📸 Visual Testing

**Good HSV tuning looks like:**

```
Camera View:
┌─────────────────────────┐
│                         │
│    ┌─────────┐         │  ← Green box around ONLY the algae
│    │ ALGAE   │         │
│    │    +    │         │  ← Crosshair centered
│    └─────────┘         │
│                         │
│  Everything else dark   │
└─────────────────────────┘

tv: 1.0  ✅
tx: -12.5 (example)
ty: 3.2 (example)
ta: 8.5 (example)
```

---

## 💡 Pro Tips

1. **Test in match lighting** - Bring algae to arena if possible
2. **Use lower exposure** (10-20ms) to reduce motion blur
3. **Save your pipeline** - Name it "Algae" and save to slot 0
4. **Test with different algae pieces** - Make sure it works with all of them
5. **Move algae around** - Verify detection works at different distances/angles

---

**Ready to test! Robot will NOT move. Just tune and verify detection.** 🟢
