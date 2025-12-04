// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.VisionConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Vision subsystem using Limelight's Neural Network Detector for algae detection.
 *
 * <p>This subsystem interfaces with a Limelight camera running a neural network
 * detector (using Coral Edge TPU on Limelight 3). The Limelight publishes detection
 * results to NetworkTables, which this subsystem reads and makes available to commands.
 *
 * <p><b>Limelight Setup:</b>
 * <ul>
 *   <li>Train a neural network model for algae detection</li>
 *   <li>Upload model to Limelight web interface</li>
 *   <li>Configure Limelight pipeline to use Neural Detector mode</li>
 *   <li>Set camera name in NetworkTables (default: "limelight")</li>
 * </ul>
 *
 * <p><b>NetworkTable Structure (from Limelight):</b>
 * <ul>
 *   <li>tv (number) - Has valid target (0 or 1)</li>
 *   <li>tx (number) - Horizontal offset from crosshair (-29.8 to 29.8 degrees)</li>
 *   <li>ty (number) - Vertical offset from crosshair (-24.85 to 24.85 degrees)</li>
 *   <li>ta (number) - Target area (0% to 100% of image)</li>
 *   <li>tclass (string) - Class name from neural network</li>
 *   <li>tid (number) - Class ID</li>
 *   <li>getpipe (number) - Active pipeline index</li>
 *   <li>Detector outputs for multiple targets</li>
 * </ul>
 */
public class LimelightVisionSubsystem extends SubsystemBase {
    private final NetworkTable limelightTable;

    // Limelight NetworkTable entries
    private final NetworkTableEntry tvEntry;  // Valid target
    private final NetworkTableEntry txEntry;  // Horizontal offset
    private final NetworkTableEntry tyEntry;  // Vertical offset
    private final NetworkTableEntry taEntry;  // Target area
    private final NetworkTableEntry tclassEntry;  // Class name
    private final NetworkTableEntry tidEntry;  // Class ID
    private final NetworkTableEntry pipelineEntry;  // Current pipeline

    private List<Detection> detections;
    private boolean hasTarget;
    private String limelightName;

    /**
     * Creates a new LimelightVisionSubsystem with default Limelight name
     */
    public LimelightVisionSubsystem() {
        this("limelight");
    }

    /**
     * Creates a new LimelightVisionSubsystem
     *
     * @param limelightName The name of the Limelight in NetworkTables
     */
    public LimelightVisionSubsystem(String limelightName) {
        this.limelightName = limelightName;

        // Get Limelight NetworkTables
        limelightTable = NetworkTableInstance.getDefault().getTable(limelightName);

        // Get standard Limelight entries
        tvEntry = limelightTable.getEntry("tv");
        txEntry = limelightTable.getEntry("tx");
        tyEntry = limelightTable.getEntry("ty");
        taEntry = limelightTable.getEntry("ta");
        tclassEntry = limelightTable.getEntry("tclass");
        tidEntry = limelightTable.getEntry("tid");
        pipelineEntry = limelightTable.getEntry("getpipe");

        // Initialize detection list
        detections = new ArrayList<>();
        hasTarget = false;

        SmartDashboard.putString("Vision/Status", "Waiting for Limelight...");
    }

    /**
     * Sets the Limelight pipeline
     *
     * @param pipeline Pipeline index (0-9)
     */
    public void setPipeline(int pipeline) {
        limelightTable.getEntry("pipeline").setNumber(pipeline);
    }

    /**
     * Gets the current pipeline index
     *
     * @return Pipeline index
     */
    public int getPipeline() {
        return (int) pipelineEntry.getDouble(0);
    }

    /**
     * Turns Limelight LEDs on/off
     *
     * @param on true to turn on, false to turn off
     */
    public void setLEDs(boolean on) {
        // ledMode: 0=pipeline, 1=off, 2=blink, 3=on
        limelightTable.getEntry("ledMode").setNumber(on ? 3 : 1);
    }

    /**
     * Updates detection data from Limelight NetworkTables
     */
    private void updateDetections() {
        detections.clear();

        // Check if Limelight has a valid target
        hasTarget = tvEntry.getDouble(0) == 1.0;

        if (!hasTarget) {
            return;
        }

        // Get primary target data
        double tx = txEntry.getDouble(0);  // Horizontal offset in degrees
        double ty = tyEntry.getDouble(0);  // Vertical offset in degrees
        double ta = taEntry.getDouble(0);  // Area percentage
        String tclass = tclassEntry.getString("");  // Class name
        int tid = (int) tidEntry.getDouble(-1);  // Class ID

        // Convert from Limelight coordinates to normalized 0-1
        // tx ranges from -29.8 to 29.8 degrees
        // ty ranges from -24.85 to 24.85 degrees
        // Center of image is tx=0, ty=0

        // Normalize to 0-1 where 0.5 is center
        double normalizedX = (tx + 29.8) / 59.6;  // Map -29.8..29.8 to 0..1
        double normalizedY = (ty + 24.85) / 49.7;  // Map -24.85..24.85 to 0..1

        // Estimate width/height from area (simplified)
        // This is approximate - Limelight doesn't provide exact bounding box
        double estimatedSize = Math.sqrt(ta / 100.0);  // Square root of area percentage

        // Create detection with confidence = 1.0 (Limelight doesn't provide confidence)
        Detection detection = new Detection(
            tclass.isEmpty() ? "algae" : tclass,
            1.0f,  // Limelight doesn't provide confidence
            (float) normalizedX,
            (float) normalizedY,
            (float) estimatedSize,
            (float) estimatedSize
        );

        detections.add(detection);

        // TODO: For multiple detections, read additional Limelight outputs
        // Limelight can detect multiple objects - check documentation for array format
    }

    /**
     * Gets the current list of detections
     *
     * @return List of Detection objects (thread-safe copy)
     */
    public synchronized List<Detection> getDetections() {
        return new ArrayList<>(detections);
    }

    /**
     * Gets detections filtered by label
     *
     * @param label The label to filter by (case-insensitive)
     * @return List of detections matching the label
     */
    public synchronized List<Detection> getDetectionsByLabel(String label) {
        List<Detection> filtered = new ArrayList<>();
        for (Detection detection : detections) {
            if (detection.getLabel().equalsIgnoreCase(label)) {
                filtered.add(detection);
            }
        }
        return filtered;
    }

    /**
     * Gets the closest detection by label (based on area)
     *
     * @param label The label to search for
     * @return The closest detection, or null if none found
     */
    public synchronized Detection getClosestDetection(String label) {
        Detection closest = null;
        float largestArea = 0;

        for (Detection detection : detections) {
            if (detection.getLabel().equalsIgnoreCase(label)) {
                float area = detection.getWidth() * detection.getHeight();
                if (area > largestArea) {
                    largestArea = area;
                    closest = detection;
                }
            }
        }
        return closest;
    }

    /**
     * Gets the primary detected algae
     *
     * @return The main detection, or null if none found
     */
    public synchronized Detection getPrimaryAlgae() {
        if (detections.isEmpty()) {
            return null;
        }
        return detections.get(0);  // Limelight returns primary target first
    }

    /**
     * Checks if Limelight has a valid target
     *
     * @return true if valid target detected
     */
    public boolean hasTarget() {
        return hasTarget;
    }

    /**
     * Checks if a specific object is detected
     *
     * @param label The label to search for
     * @return true if at least one detection with this label exists
     */
    public synchronized boolean hasDetection(String label) {
        for (Detection detection : detections) {
            if (detection.getLabel().equalsIgnoreCase(label)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets horizontal offset to target in degrees
     *
     * @return Horizontal offset (-29.8 to 29.8 degrees), 0 if no target
     */
    public double getHorizontalOffset() {
        return hasTarget ? txEntry.getDouble(0) : 0;
    }

    /**
     * Gets vertical offset to target in degrees
     *
     * @return Vertical offset (-24.85 to 24.85 degrees), 0 if no target
     */
    public double getVerticalOffset() {
        return hasTarget ? tyEntry.getDouble(0) : 0;
    }

    /**
     * Gets target area percentage
     *
     * @return Area (0-100%), 0 if no target
     */
    public double getTargetArea() {
        return hasTarget ? taEntry.getDouble(0) : 0;
    }

    /**
     * Gets the distance estimate based on target area
     * You'll need to calibrate this for your specific setup
     *
     * @return Estimated distance in inches (requires calibration)
     */
    public double getEstimatedDistance() {
        if (!hasTarget) {
            return 0;
        }

        double area = getTargetArea();

        // This is a placeholder formula - you MUST calibrate this!
        // Typical approach: distance = k / sqrt(area)
        // where k is a calibration constant

        // Example calibration:
        // At 24 inches, area = 4.0
        // k = 24 * sqrt(4.0) = 48

        double k = 48.0;  // Calibration constant (TUNE THIS!)
        return k / Math.sqrt(Math.max(area, 0.1));  // Prevent divide by zero
    }

    @Override
    public void periodic() {
        // Update detections from Limelight
        updateDetections();

        // Update SmartDashboard
        SmartDashboard.putBoolean("Vision/HasTarget", hasTarget);
        SmartDashboard.putNumber("Vision/Detections", detections.size());
        SmartDashboard.putNumber("Vision/Pipeline", getPipeline());

        if (hasTarget) {
            SmartDashboard.putString("Vision/Status", "Target Acquired");
            SmartDashboard.putNumber("Vision/TX", getHorizontalOffset());
            SmartDashboard.putNumber("Vision/TY", getVerticalOffset());
            SmartDashboard.putNumber("Vision/TA", getTargetArea());
            SmartDashboard.putNumber("Vision/Distance", getEstimatedDistance());

            // Display primary detection
            Detection algae = getPrimaryAlgae();
            if (algae != null) {
                SmartDashboard.putString("Vision/Algae", algae.toString());
                SmartDashboard.putNumber("Vision/AlgaeX", algae.getX());
                SmartDashboard.putNumber("Vision/AlgaeY", algae.getY());
            }
        } else {
            SmartDashboard.putString("Vision/Status", "No Target");
        }
    }
}
