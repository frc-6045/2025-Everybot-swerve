// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.VisionConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Hybrid Vision Subsystem that combines Limelight and Coral coprocessor
 *
 * <p>This subsystem uses both vision systems for complementary capabilities:
 * <ul>
 *   <li><b>Limelight</b>: Fast, low-latency targeting for real-time tracking.
 *       Great for quick reflexes and aiming during teleop.</li>
 *   <li><b>Coral Coprocessor</b>: Advanced neural network detection with high accuracy.
 *       Can detect multiple objects and provide detailed classification.</li>
 * </ul>
 *
 * <p><b>Usage Strategies:</b>
 * <ul>
 *   <li><b>Primary-Fallback</b>: Use Limelight primarily, fall back to Coral if Limelight fails</li>
 *   <li><b>Verification</b>: Use Coral to verify Limelight detections before critical actions</li>
 *   <li><b>Complementary</b>: Use Limelight for tracking, Coral for classification</li>
 *   <li><b>Fusion</b>: Combine both for increased confidence and accuracy</li>
 * </ul>
 */
public class HybridVisionSubsystem extends SubsystemBase {
    private final LimelightVisionSubsystem limelight;
    private final CoralVisionSubsystem coral;

    /**
     * Vision mode selection
     */
    public enum VisionMode {
        LIMELIGHT_ONLY,      // Use only Limelight (fastest)
        CORAL_ONLY,          // Use only Coral (most accurate)
        LIMELIGHT_PRIMARY,   // Use Limelight, fallback to Coral
        CORAL_PRIMARY,       // Use Coral, fallback to Limelight
        FUSION               // Use both and combine results
    }

    private VisionMode currentMode;

    /**
     * Creates a new HybridVisionSubsystem
     */
    public HybridVisionSubsystem() {
        limelight = new LimelightVisionSubsystem();
        coral = new CoralVisionSubsystem();
        currentMode = VisionMode.FUSION;  // Default mode - uses both systems for best accuracy

        SmartDashboard.putString("HybridVision/Mode", currentMode.toString());
    }

    /**
     * Sets the vision mode
     *
     * @param mode The vision mode to use
     */
    public void setVisionMode(VisionMode mode) {
        this.currentMode = mode;
        SmartDashboard.putString("HybridVision/Mode", mode.toString());
    }

    /**
     * Gets the current vision mode
     *
     * @return Current vision mode
     */
    public VisionMode getVisionMode() {
        return currentMode;
    }

    /**
     * Checks if any vision system has detected a target
     *
     * @return true if either system has a valid target
     */
    public boolean hasTarget() {
        switch (currentMode) {
            case LIMELIGHT_ONLY:
                return limelight.hasTarget();
            case CORAL_ONLY:
                return coral.isCoprocessorConnected() && !coral.getDetections().isEmpty();
            case LIMELIGHT_PRIMARY:
                return limelight.hasTarget() ||
                       (coral.isCoprocessorConnected() && !coral.getDetections().isEmpty());
            case CORAL_PRIMARY:
                return (coral.isCoprocessorConnected() && !coral.getDetections().isEmpty()) ||
                       limelight.hasTarget();
            case FUSION:
                return limelight.hasTarget() &&
                       coral.isCoprocessorConnected() &&
                       !coral.getDetections().isEmpty();
            default:
                return false;
        }
    }

    /**
     * Gets horizontal offset to target in degrees
     * Uses the active vision system based on mode
     *
     * @return Horizontal offset in degrees
     */
    public double getHorizontalOffset() {
        switch (currentMode) {
            case LIMELIGHT_ONLY:
                return limelight.getHorizontalOffset();

            case CORAL_ONLY:
                Detection coralDet = coral.getClosestDetection("algae");
                if (coralDet != null) {
                    return convertNormalizedToAngle(coralDet.getX());
                }
                return 0;

            case LIMELIGHT_PRIMARY:
                if (limelight.hasTarget()) {
                    return limelight.getHorizontalOffset();
                }
                Detection det1 = coral.getClosestDetection("algae");
                return det1 != null ? convertNormalizedToAngle(det1.getX()) : 0;

            case CORAL_PRIMARY:
                Detection det2 = coral.getClosestDetection("algae");
                if (det2 != null) {
                    return convertNormalizedToAngle(det2.getX());
                }
                return limelight.getHorizontalOffset();

            case FUSION:
                // Average both systems for smoother tracking
                double limelightTx = limelight.hasTarget() ? limelight.getHorizontalOffset() : 0;
                Detection coralDet2 = coral.getClosestDetection("algae");
                double coralTx = coralDet2 != null ? convertNormalizedToAngle(coralDet2.getX()) : 0;

                if (limelight.hasTarget() && coralDet2 != null) {
                    return (limelightTx + coralTx) / 2.0;  // Average
                } else if (limelight.hasTarget()) {
                    return limelightTx;
                } else {
                    return coralTx;
                }

            default:
                return 0;
        }
    }

    /**
     * Gets vertical offset to target in degrees
     *
     * @return Vertical offset in degrees
     */
    public double getVerticalOffset() {
        switch (currentMode) {
            case LIMELIGHT_ONLY:
            case LIMELIGHT_PRIMARY:
            case FUSION:
                return limelight.getVerticalOffset();

            case CORAL_ONLY:
            case CORAL_PRIMARY:
                Detection det = coral.getClosestDetection("algae");
                if (det != null) {
                    return convertNormalizedToAngleVertical(det.getY());
                }
                return 0;

            default:
                return 0;
        }
    }

    /**
     * Gets target area/size
     *
     * @return Target area (Limelight: 0-100%, Coral: normalized size)
     */
    public double getTargetArea() {
        if (currentMode == VisionMode.LIMELIGHT_ONLY ||
            currentMode == VisionMode.LIMELIGHT_PRIMARY ||
            (currentMode == VisionMode.FUSION && limelight.hasTarget())) {
            return limelight.getTargetArea();
        }

        Detection det = coral.getClosestDetection("algae");
        if (det != null) {
            // Convert Coral normalized size to percentage
            return (det.getWidth() * det.getHeight()) * 100.0;
        }
        return 0;
    }

    /**
     * Gets all detections from both systems
     *
     * @return Combined list of detections
     */
    public List<Detection> getAllDetections() {
        List<Detection> combined = new ArrayList<>();

        // Add Coral detections
        if (coral.isCoprocessorConnected()) {
            combined.addAll(coral.getDetections());
        }

        // Add Limelight detection if available
        if (limelight.hasTarget()) {
            Detection limelightDet = limelight.getPrimaryAlgae();
            if (limelightDet != null) {
                combined.add(limelightDet);
            }
        }

        return combined;
    }

    /**
     * Gets detections filtered by label
     *
     * @param label The label to filter by
     * @return List of matching detections
     */
    public List<Detection> getDetectionsByLabel(String label) {
        List<Detection> filtered = new ArrayList<>();

        if (currentMode == VisionMode.LIMELIGHT_ONLY) {
            return limelight.getDetectionsByLabel(label);
        } else if (currentMode == VisionMode.CORAL_ONLY) {
            return coral.getDetectionsByLabel(label);
        }

        // For other modes, combine both
        filtered.addAll(coral.getDetectionsByLabel(label));

        if (limelight.hasTarget()) {
            Detection limelightDet = limelight.getPrimaryAlgae();
            if (limelightDet != null && limelightDet.getLabel().equalsIgnoreCase(label)) {
                filtered.add(limelightDet);
            }
        }

        return filtered;
    }

    /**
     * Gets the best detection for a given label using the current mode strategy
     *
     * @param label The label to search for
     * @return The best detection, or null if none found
     */
    public Detection getBestDetection(String label) {
        switch (currentMode) {
            case LIMELIGHT_ONLY:
                return limelight.hasTarget() ? limelight.getPrimaryAlgae() : null;

            case CORAL_ONLY:
                return coral.getClosestDetection(label);

            case LIMELIGHT_PRIMARY:
                if (limelight.hasTarget()) {
                    return limelight.getPrimaryAlgae();
                }
                return coral.getClosestDetection(label);

            case CORAL_PRIMARY:
                Detection coralDet = coral.getClosestDetection(label);
                if (coralDet != null) {
                    return coralDet;
                }
                return limelight.hasTarget() ? limelight.getPrimaryAlgae() : null;

            case FUSION:
                // Use Coral's classification with Limelight's targeting
                Detection coral_det = coral.getMostConfidentDetection(label);
                if (coral_det != null && limelight.hasTarget()) {
                    // Verify they're looking at the same thing (rough position check)
                    Detection lime_det = limelight.getPrimaryAlgae();
                    if (lime_det != null && areDetectionsSimilar(coral_det, lime_det)) {
                        return coral_det;  // Use Coral's classification
                    }
                }
                // Fallback to best available
                return coral_det != null ? coral_det :
                       (limelight.hasTarget() ? limelight.getPrimaryAlgae() : null);

            default:
                return null;
        }
    }

    /**
     * Checks if target is centered within a deadband
     *
     * @param deadbandDegrees Deadband in degrees
     * @return true if target is centered
     */
    public boolean isTargetCentered(double deadbandDegrees) {
        return hasTarget() && Math.abs(getHorizontalOffset()) < deadbandDegrees;
    }

    /**
     * Gets the Limelight subsystem directly for advanced use
     *
     * @return Limelight subsystem
     */
    public LimelightVisionSubsystem getLimelight() {
        return limelight;
    }

    /**
     * Gets the Coral subsystem directly for advanced use
     *
     * @return Coral subsystem
     */
    public CoralVisionSubsystem getCoral() {
        return coral;
    }

    /**
     * Converts normalized X coordinate (0-1) to angle in degrees
     * Assumes ~60 degree horizontal FOV (typical for Limelight)
     *
     * @param normalizedX Normalized X coordinate (0-1, where 0.5 is center)
     * @return Angle in degrees
     */
    private double convertNormalizedToAngle(double normalizedX) {
        double fovDegrees = 59.6;  // Limelight horizontal FOV
        return (normalizedX - 0.5) * fovDegrees;
    }

    /**
     * Converts normalized Y coordinate (0-1) to angle in degrees
     *
     * @param normalizedY Normalized Y coordinate (0-1, where 0.5 is center)
     * @return Angle in degrees
     */
    private double convertNormalizedToAngleVertical(double normalizedY) {
        double fovDegrees = 49.7;  // Limelight vertical FOV
        return (normalizedY - 0.5) * fovDegrees;
    }

    /**
     * Checks if two detections are likely the same object
     *
     * @param det1 First detection
     * @param det2 Second detection
     * @return true if detections are similar enough to be the same object
     */
    private boolean areDetectionsSimilar(Detection det1, Detection det2) {
        // Check if positions are within 20% of frame
        double xDiff = Math.abs(det1.getX() - det2.getX());
        double yDiff = Math.abs(det1.getY() - det2.getY());
        return xDiff < 0.2 && yDiff < 0.2;
    }

    @Override
    public void periodic() {
        // Both subsystems update themselves

        // Update SmartDashboard with hybrid info
        SmartDashboard.putBoolean("HybridVision/HasTarget", hasTarget());
        SmartDashboard.putNumber("HybridVision/TX", getHorizontalOffset());
        SmartDashboard.putNumber("HybridVision/TY", getVerticalOffset());
        SmartDashboard.putNumber("HybridVision/Area", getTargetArea());

        SmartDashboard.putBoolean("HybridVision/LimelightActive", limelight.hasTarget());
        SmartDashboard.putBoolean("HybridVision/CoralActive",
            coral.isCoprocessorConnected() && !coral.getDetections().isEmpty());

        SmartDashboard.putNumber("HybridVision/TotalDetections", getAllDetections().size());
    }
}
