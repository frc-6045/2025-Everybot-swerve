# Algae Pickup System - Calibration Guide

This guide will help you calibrate the encoder positions and current thresholds for the automated algae pickup system.

## Prerequisites
1. Wire the REV Through Bore Encoder to DIO ports 0 and 1
2. Deploy code to robot: `./gradlew deploy`
3. Open SmartDashboard or Shuffleboard
4. Enable the robot

---

## Part 1: Calibrate Arm Encoder Positions

### Step 1: Zero the Encoder at Safe Position
1. **Manually move the arm** to your desired "safe" or "stowed" position
2. **In SmartDashboard**, look for the value: `Arm/Encoder Position (rotations)`
3. **Write down this value**: _______________ rotations
4. This will be your `ARM_SAFE_POSITION` in Constants.java

### Step 2: Measure Intake Position
1. **Manually move the arm** to the position where it should intake algae from the ground
2. **In SmartDashboard**, look for: `Arm/Encoder Position (rotations)`
3. **Write down this value**: _______________ rotations
4. This will be your `ARM_INTAKE_POSITION` in Constants.java

### Step 3: Update Constants
Edit `src/main/java/frc/robot/Constants.java` (lines 54-56):

```java
public static final double ARM_INTAKE_POSITION = X.XX;  // Your measured value from Step 2
public static final double ARM_SAFE_POSITION = X.XX;    // Your measured value from Step 1
public static final double ARM_POSITION_TOLERANCE = 0.5;  // Adjust if needed
```

### Step 4: Verify
1. Re-deploy code
2. Move arm manually
3. Watch `Arm/At Intake Position` and `Arm/At Safe Position` indicators turn green when at those positions

---

## Part 2: Calibrate Roller Current Detection

### Step 1: Measure Baseline Current (No Algae)
1. **Run the intake roller** (hold right bumper or run at ROLLER_ALGAE_IN speed)
2. **WITHOUT algae** in the intake
3. **In SmartDashboard**, watch: `Roller/Motor Current (Amps)`
4. **Write down the average current**: _______________ Amps
   - This is your "baseline current" when running freely

### Step 2: Measure Current With Algae
1. **Run the intake roller** again
2. **Place algae** in the intake (simulate pickup)
3. **Watch the current** as algae is pulled in
4. **Observe what happens**:
   - Does current **increase** (resistance/jam)? Or
   - Does current **decrease** (less load after pickup)?
5. **Write down the new current**: _______________ Amps

### Step 3: Calculate Drop Threshold
- **Current difference** = Baseline Current - Current With Algae
- **Example**:
  - Baseline: 20 Amps (running freely)
  - With algae: 15 Amps (after pickup)
  - Drop: 5 Amps

### Step 4: Update Current Detection Values
Edit `src/main/java/frc/robot/commands/AlgaePickupSequenceCommand.java` (line 68-70):

```java
boolean pickedUp = roller.hasCurrentDropped(XX.0, X.0);
//                                          ^^^^  ^^^
//                                          |     └─ Drop threshold (from Step 3)
//                                          └─ Baseline current (from Step 1)
```

**Example:**
```java
boolean pickedUp = roller.hasCurrentDropped(20.0, 5.0);
```

### Step 5: Verify Using Dashboard
1. **In SmartDashboard**, you'll see:
   - `Roller/CALIBRATE: Baseline Current` = 20.0 (current setting)
   - `Roller/CALIBRATE: Drop Threshold` = 5.0 (current setting)
   - `Roller/CALIBRATE: Would Trigger Pickup` = true/false

2. **Run intake and watch** if the trigger indicator matches when algae is picked up
3. **Adjust values** in code if needed

---

## Part 3: Fine-Tuning

### Arm Movement Speed
If the arm moves too fast/slow or oscillates, adjust the proportional gain:

Edit `src/main/java/frc/robot/commands/MoveArmToPositionCommand.java` (line 19):
```java
private static final double kP = 0.1;  // Increase = faster, Decrease = slower
```

**Symptoms:**
- **Oscillating/shaking** → Decrease kP (try 0.05)
- **Too slow** → Increase kP (try 0.15)

### Position Tolerance
If the arm never reaches "at position", increase tolerance:

Edit `Constants.java`:
```java
public static final double ARM_POSITION_TOLERANCE = 0.5;  // Try 1.0 if too strict
```

### Command Timeouts
If sequences time out before completing, adjust timeouts in `AlgaePickupSequenceCommand.java`:
- Line 54: Arm movement timeout (default 3 seconds)
- Line 58: Alignment timeout (default 5 seconds)
- Line 75: Current detection timeout (default 10 seconds)

---

## SmartDashboard Values Reference

### Arm Subsystem
| Key | Description | Use For |
|-----|-------------|---------|
| `Arm/Encoder Position (rotations)` | Current arm position | Measuring positions |
| `Arm/Encoder Raw Count` | Raw encoder counts | Debugging |
| `Arm/Target Intake Position` | Configured intake position | Verification |
| `Arm/Target Safe Position` | Configured safe position | Verification |
| `Arm/At Intake Position` | Green when at intake | Testing |
| `Arm/At Safe Position` | Green when at safe | Testing |

### Roller Subsystem
| Key | Description | Use For |
|-----|-------------|---------|
| `Roller/Motor Current (Amps)` | **CRITICAL** - Current draw | Calibrating detection |
| `Roller/Motor Speed` | Current speed command | Debugging |
| `Roller/CALIBRATE: Baseline Current` | What code is using | Verify settings |
| `Roller/CALIBRATE: Drop Threshold` | What code is using | Verify settings |
| `Roller/CALIBRATE: Would Trigger Pickup` | Detection status | Real-time testing |

---

## Testing Checklist

- [ ] Encoder shows changing values when arm moves
- [ ] Arm moves to intake position when commanded
- [ ] Arm moves to safe position when commanded
- [ ] Roller current reads reasonable values (5-30 Amps typical)
- [ ] Current changes noticeably when algae picked up
- [ ] Full sequence completes: arm down → align → intake → detect → retract
- [ ] Robot aligns with algae using Limelight (B button test first)

---

## Troubleshooting

### Encoder reads 0 or doesn't change
- Check wiring: Green to DIO 0, Yellow to DIO 1
- Check power: Red to 5V, Black to Ground
- Verify DIO port numbers in Constants.java match physical wiring
- Try switching encoder mode switch between A and S

### Arm doesn't move to position
- Check if encoder is updating in dashboard
- Verify target positions are achievable (within arm range)
- Check motor direction (may need to invert)

### Current detection doesn't work
- Verify current is actually changing (watch dashboard)
- Current might increase instead of decrease - reverse the logic
- Baseline/threshold values may need adjustment

### Sequence times out
- Increase timeout values in AlgaePickupSequenceCommand.java
- Check that all subsystems are working individually first

---

## Quick Calibration Workflow

1. ✅ Wire encoder → Deploy → Check dashboard shows values
2. ✅ Move arm to safe position → Record value → Update Constants
3. ✅ Move arm to intake position → Record value → Update Constants
4. ✅ Run intake without algae → Record current → Update AlgaePickupSequenceCommand
5. ✅ Run intake with algae → Record current drop → Update threshold
6. ✅ Re-deploy and test full sequence!

---

## Need Help?

If calibration isn't working:
1. Check all dashboard values are updating
2. Test each subsystem individually first
3. Verify wiring matches code configuration
4. Check for errors in Driver Station console

Good luck! 🎉
