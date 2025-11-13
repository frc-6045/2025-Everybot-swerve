# HSV Settings for TEAL/CYAN Algae

## ✅ Algae Appears GREEN-BLUE (Teal/Cyan)

Based on visual observation, the algae has a **teal/cyan** color, NOT pure green.

This means the Hue range is different from standard green!

---

## ⚠️ IMPORTANT: H and S Sliders Don't Work - Use Eyedropper Instead!

**CONFIRMED ISSUE:** In the Limelight web interface, the **Hue (H) and Saturation (S) manual sliders do NOT update the threshold preview** in real-time. Only the **Value (V) slider works** manually.

This appears to be a firmware limitation/bug where manual H/S adjustments don't trigger image reprocessing.

### ✅ WORKING Solution: Use the Eyedropper Tool

Instead of manually adjusting H/S sliders, use the color picker tools:

1. **Place algae in front of camera**
2. **In Thresholding tab, click the "eyedropper" button** (or color picker icon)
3. **Click directly ON the algae** in the camera preview
4. **Limelight automatically sets correct H/S values** ✅
5. **Use "include pixel" button** to click on different parts of algae to expand the range
6. **Use "ignore pixel" button** to click on non-algae objects to exclude those colors
7. **Manually adjust Value (V) slider** if needed (this one works)
8. **Save the pipeline**

### 🎯 Working HSV Values (Tuned via Eyedropper)

These values were found using the eyedropper tool on actual algae:

```
Hue:        0-139
Saturation: 184-228
Value:      184-228
```

**These values are saved in Constants.java for reference.**

---

## 🎨 Correct HSV Values

### For TEAL/CYAN Algae (Recommended):

```
Hue:        80-100   ← Cyan range (NOT 50-70!)
Saturation: 100-255
Value:      70-255
```

**Important:** Limelight uses OpenCV HSV format where Hue ranges from 0-180 (not 0-360)!

---

## 📊 Color Reference (OpenCV HSV)

```
Hue Range | Color Description
----------|----------------------------------
0-10      | Red
20-35     | Orange/Yellow
40-60     | Yellow-Green
50-70     | PURE GREEN (not your algae)
75-95     | GREEN-BLUE/CYAN ← Your algae!
95-110    | Cyan-Blue
120-140   | Blue
150-170   | Purple/Magenta
```

---

## 🔧 Tuning Steps

### Option 1: Start with Cyan Range
```
1. Set Hue:        80-100
2. Set Saturation: 100-255
3. Set Value:      70-255
4. Check if algae lights up green in camera view
```

### Option 2: Sample the Color Directly

**Use Limelight's color picker:**
1. Look for "Sample" or eyedropper icon in Limelight
2. Click on the algae in camera view
3. Note the Hue value (probably 85-95)
4. Set range ±10 around that (e.g., 75-105)

### Option 3: Cover Full Green-to-Cyan

**If algae color varies:**
```
Hue:        55-100   (wide range covering green to cyan)
Saturation: 100-255
Value:      70-255
```

---

## 🧪 Testing Different Values

Try each of these in order until you get good detection:

### Test 1: Pure Cyan
```
Hue: 85-95
Saturation: 120-255
Value: 80-255
```
**Best for:** Bright teal algae under arena lights

### Test 2: Wide Cyan
```
Hue: 75-105
Saturation: 80-255
Value: 60-255
```
**Best for:** Variable teal color or dim lighting

### Test 3: Green-to-Cyan
```
Hue: 60-100
Saturation: 100-255
Value: 70-255
```
**Best for:** Algae that looks greenish-blue

### Test 4: Very Wide (Fallback)
```
Hue: 50-110
Saturation: 80-255
Value: 50-255
```
**Best for:** When nothing else works, but may catch false positives

---

## ✅ Success Checklist

Good HSV tuning means:
- [ ] ONLY algae lights up green in Limelight view
- [ ] Stable green box around algae (not flickering)
- [ ] `tv = 1.0` in Limelight interface
- [ ] Floor, walls, robot parts stay dark
- [ ] Works at different distances (0.5m to 3m)
- [ ] Works when algae moves/rotates
- [ ] `Algae/HasTarget = true` in SmartDashboard

---

## 🎯 Most Likely Working Value

Based on "green-blue" description:

```
Hue:        85-100
Saturation: 100-255
Value:      70-255
```

**Start here and adjust based on what you see!**

---

## 📸 What You Should See

**In Limelight camera view:**
```
Before tuning HSV:
┌────────────────┐
│                │
│   ALGAE        │  ← No highlight
│                │
└────────────────┘

After tuning HSV correctly:
┌────────────────┐
│                │
│  ┏━━━━━━┓     │
│  ┃ALGAE ┃     │  ← Green box!
│  ┗━━━━━━┛     │
│                │
└────────────────┘
tv: 1.0 ✅
```

---

## 🔄 Common Adjustments

**Problem:** Algae not detected
→ **Solution:** Widen Hue range (70-110)

**Problem:** Everything turns green
→ **Solution:** Narrow Hue range (85-95)

**Problem:** Detection flickers
→ **Solution:** Increase Saturation min (150-255)

**Problem:** Only bright spots detected
→ **Solution:** Lower Value min (50-255)

**Problem:** Shadows/dark areas detected
→ **Solution:** Raise Value min (100-255)

---

**Summary: Use Hue 80-100 for teal/cyan algae, NOT 50-70!** 🟦
