# Shadow Compensation for Algae Detection

## Problem: Shadows Causing False Detections

Shadows can interfere with algae detection by:
- ❌ Creating false positives (detecting dark spots as algae)
- ❌ Making the algae appear darker and harder to detect
- ❌ Causing flickering detection as lighting changes

## ✅ Solution: Shadow Filtering

I've added shadow compensation constants that filter out dark areas.

---

## 🔧 How Shadow Compensation Works

### Three-Layer Defense Against Shadows:

**1. Value Threshold (Brightness Filter)**
```
MIN_VALUE_THRESHOLD = 80
```
- Filters out anything darker than this brightness level (0-255 scale)
- Shadows = low brightness → gets filtered out ✅
- Bright algae = high brightness → gets detected ✅

**2. Saturation Threshold (Color Intensity Filter)**
```
MIN_SATURATION_THRESHOLD = 100
```
- Filters out gray/desaturated objects (shadows often appear grayish)
- Shadows = low saturation → gets filtered out ✅
- Vivid teal algae = high saturation → gets detected ✅

**3. Area Threshold (Size Filter)**
```
MIN_TARGET_AREA = 1.0%
```
- Filters out tiny detections (noise, small shadow spots)
- Small shadows = < 1% screen → gets filtered out ✅
- Algae game piece = larger → gets detected ✅

---

## 📋 Limelight Configuration for Shadow Compensation

### In Limelight Interface:

**1. Set HSV for Teal Algae:**
```
Hue:        80-100   (teal/cyan color)
Saturation: 100-255  (ignores gray shadows)
Value:      80-255   (ignores dark shadows)
```

**2. Configure Contour Filtering:**

Find "Contour Filtering" section and set:

```
Area:
  Min: 1.0%   ← Filters small shadow spots
  Max: 100%

Fullness (Solidity):
  Min: 50-70% ← Shadows often have irregular edges
  Max: 100%

Aspect Ratio:
  Based on algae shape (e.g., 0.8 to 1.2 for squarish algae)
```

**3. Advanced: Enable Erosion/Dilation (Optional)**

If available in your Limelight version:
- **Erosion:** Removes small noise/shadow spots
- **Dilation:** Fills in gaps in the detection

---

## 🎯 HSV Settings Explained

### Why These Values Filter Shadows:

**Value: 80-255** (not 0-255)
```
0-79:   Dark areas (shadows, dark floor) ← FILTERED OUT
80-255: Bright areas (lit algae)         ← DETECTED
```

**Saturation: 100-255** (not 0-255)
```
0-99:    Grayish (shadows appear gray)   ← FILTERED OUT
100-255: Vivid colors (teal algae)       ← DETECTED
```

**Hue: 80-100** (teal/cyan only)
```
Other colors (red, blue, yellow) ← FILTERED OUT
80-100: Teal/cyan (algae color)  ← DETECTED
```

---

## 🧪 Testing Shadow Compensation

### Test 1: Direct Light
```
1. Place algae in bright, direct light
2. Check threshold view - should show WHITE
3. Check ta value - should be > 0
```
✅ Should detect clearly

### Test 2: Shadow Nearby
```
1. Place algae in light
2. Create a shadow next to algae (use your hand)
3. Check threshold view
   - Algae should be WHITE
   - Shadow should be BLACK (filtered out)
```
✅ Should detect algae, ignore shadow

### Test 3: Algae in Partial Shadow
```
1. Place algae partially in shadow
2. If algae is too dark:
   - Lower Value Min (try 60 instead of 80)
   - Check if still filtering shadows
```
⚠️ May need to adjust thresholds

### Test 4: Moving Shadows
```
1. Detect algae successfully
2. Move around creating shadows
3. Detection should remain stable (not flicker)
```
✅ Should be stable with proper thresholds

---

## 🔧 Tuning for Your Arena

### If Shadows Still Detected:

**Problem: Dark floor spots detected**
→ **Solution:** Increase Value Min (try 100-120)

**Problem: Gray objects detected**
→ **Solution:** Increase Saturation Min (try 120-150)

**Problem: Small shadow spots detected**
→ **Solution:** Increase Area Min (try 2.0% or 3.0%)

### If Algae NOT Detected:

**Problem: Algae too dark in your arena**
→ **Solution:** Lower Value Min (try 60-70)

**Problem: Algae color not vivid enough**
→ **Solution:** Lower Saturation Min (try 80-90)

**Problem: Algae appears too small**
→ **Solution:** Lower Area Min (try 0.5%)

---

## 💡 Recommended Settings by Lighting

### Bright Arena (Strong overhead lights):
```
Hue:        80-100
Saturation: 120-255  (higher to filter washed-out colors)
Value:      100-255  (higher to filter dark shadows)
Area Min:   1.0%
```

### Medium Arena (Normal gym lighting):
```
Hue:        80-100
Saturation: 100-255
Value:      80-255   (balanced)
Area Min:   1.0%
```

### Dim Arena (Low light):
```
Hue:        80-100
Saturation: 80-255   (lower to catch less saturated colors)
Value:      60-255   (lower to detect darker algae)
Area Min:   1.0%
```

### Outdoor (Sunlight):
```
Hue:        85-95    (narrower - sunlight makes colors vivid)
Saturation: 150-255  (higher - sunlight increases saturation)
Value:      120-255  (higher - bright sunlight)
Area Min:   0.5%     (algae may appear smaller in distance)
```

---

## 📊 Visual Comparison

**WITHOUT Shadow Compensation (Value 0-255):**
```
Threshold View:
┌─────────────────┐
│ ████            │  ← Floor shadow detected (bad!)
│ ████  ████      │  ← Algae detected
│       ████      │
│ ████            │  ← Another shadow (bad!)
└─────────────────┘
ta: 15.2 (detecting too much!)
```

**WITH Shadow Compensation (Value 80-255):**
```
Threshold View:
┌─────────────────┐
│                 │  ← Floor shadow filtered out ✅
│       ████      │  ← Only algae detected
│       ████      │
│                 │  ← Shadow filtered out ✅
└─────────────────┘
ta: 5.2 (correct!)
```

---

## 🚀 Quick Setup

**For immediate shadow filtering:**

1. **In Limelight, set HSV:**
   ```
   Hue: 80 to 100
   Saturation: 100 to 255
   Value: 80 to 255
   ```

2. **Set Area filter:**
   ```
   Area Min: 1.0%
   ```

3. **Test with shadows:**
   - Place algae in light
   - Create shadows nearby
   - Only algae should be white in threshold view

4. **Adjust if needed:**
   - Too many shadows? → Increase Value Min
   - Missing algae? → Decrease Value Min

---

## 📝 Summary

**Shadow Compensation Constants Added:**
- `MIN_VALUE_THRESHOLD = 80` - Filters dark shadows
- `MIN_SATURATION_THRESHOLD = 100` - Filters gray shadows
- `MIN_TARGET_AREA = 1.0` - Filters small shadow spots

**Limelight HSV Settings:**
- Value Min: **80** (not 0) ← Key for shadow filtering!
- Saturation Min: **100** (not 0) ← Filters gray areas
- Area Min: **1.0%** ← Filters noise

**Result:** Detects bright, colorful algae while ignoring dark shadows! ✅
