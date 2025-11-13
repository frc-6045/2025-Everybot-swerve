# Limelight HSV Sliders Not Working - Fix Guide

## Problem
- Hue (H) and Saturation (S) sliders update numbers but camera preview doesn't change
- Value (V) slider works correctly
- Setting Hue Min = Hue Max = 0 doesn't turn preview black
- Pipeline Type is correctly set to "Color/Retroreflective"

## Root Cause
The Thresholding tab's camera preview is not updating when H/S values change. This is a Limelight web interface issue, NOT a settings issue.

---

## Solutions to Try (in order)

### Solution 1: View Direct Camera Stream

The web interface preview might be cached/broken. View the actual camera stream instead:

1. **Open new browser tab**
2. **Navigate to:** `http://10.62.44.11:5800`
   - This is the direct MJPEG stream from Limelight
3. **Go back to the Limelight web interface tab** (http://10.62.44.11:5801)
4. **Adjust Hue sliders in Thresholding tab**
5. **Watch the stream in the :5800 tab** - it should update

The :5800 stream shows the actual processed output, bypassing the web UI cache.

---

### Solution 2: Force Stream Refresh

1. **Go to Input tab**
2. **Find "Stream" setting** (might be near "Stream Orientation")
3. **Change it to a different mode**, then change back:
   - If it says "Standard" → Change to "Side by Side" → Change back to "Standard"
4. **Go back to Thresholding tab**
5. **Try adjusting Hue sliders again**

---

### Solution 3: Reboot Limelight

The web interface might be in a bad state:

1. **Go to Settings tab** in Limelight web interface
2. **Scroll to bottom**
3. **Click "Reboot"**
4. **Wait 30-60 seconds** for Limelight to restart
5. **Refresh browser** (F5 or Ctrl+R)
6. **Navigate back to Thresholding tab**
7. **Try HSV sliders again**

---

### Solution 4: Use Different Browser

Sometimes browser caching causes issues:

1. **Open Incognito/Private browsing window**
2. **Go to:** `http://10.62.44.11:5801`
3. **Try Thresholding tab and HSV sliders**

---

### Solution 5: Check Limelight Firmware

If none of the above work, your Limelight might need a firmware update:

1. **Go to Settings tab**
2. **Check "Firmware Version"** at the top
3. **If it's older than 2023**, consider updating:
   - Download latest from: https://limelightvision.io/pages/downloads
   - Follow update instructions

---

## Workaround: Use NetworkTables to Set HSV

If the web interface is completely broken, you can set HSV values programmatically from robot code.

However, **for now just use the :5800 stream** to see the actual threshold output while adjusting values in the :5801 interface.

---

## Testing HSV Values

Once you can see the threshold updating:

### For Teal/Cyan Algae:
```
Hue:        80-100
Saturation: 100-255
Value:      70-255
```

### What You Should See:
- **Raw view (top):** Normal camera feed showing algae
- **Threshold view (bottom):** Only the teal algae appears as WHITE, everything else is BLACK

### Test:
1. Set Hue: 80-100
2. Set Saturation: 100-255
3. Set Value: 70-255
4. Place teal algae in view
5. **Only the algae should appear white** in threshold view
6. Move Hue Min to 120 → Algae should disappear (wrong color range)
7. Move Hue back to 80 → Algae should reappear

---

## Quick Reference

- **Web Interface:** http://10.62.44.11:5801
- **Direct Stream:** http://10.62.44.11:5800
- **Pipeline Type:** Color/Retroreflective ✅
- **Tab for HSV:** Thresholding
- **Algae Hue Range:** 80-100 (cyan)
- **Full Range = No Filter:** Hue 0-180, Sat 0-255 accepts ALL colors

---

## If Everything Fails

The H and S sliders might actually be working, but you can't see it because:

1. **You're viewing Value-only threshold** - Some Limelight modes only show V
2. **No colored object in view** - Put actual teal algae in front of camera
3. **Web UI is completely broken** - Use AdvantageScope or SmartDashboard to view NetworkTables and confirm detection works

The **most important test** is:
- Does `Algae/HasTarget` turn TRUE in SmartDashboard when algae is in view?
- If YES → HSV is working, just web UI preview is broken
- If NO → Need to troubleshoot detection further
