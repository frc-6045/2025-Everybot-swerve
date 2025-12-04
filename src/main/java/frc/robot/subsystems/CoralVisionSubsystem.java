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
 * Vision subsystem that interfaces with Google Coral USB Accelerator running on a coprocessor.
 *
 * <p>This subsystem receives detection data from a Raspberry Pi or similar coprocessor
 * running TensorFlow Lite with the Coral Edge TPU. The coprocessor publishes detection
 * results to NetworkTables, which this subsystem reads and makes available to commands.
 *
 * <p><b>Coprocessor Setup:</b>
 * <ul>
 *   <li>Install Coral Edge TPU runtime on Raspberry Pi</li>
 *   <li>Run Python/C++ vision code that publishes to NetworkTables at "Coral" table</li>
 *   <li>Expected NetworkTable structure:
 *       <ul>
 *         <li>Coral/num_detections (number)</li>
 *         <li>Coral/labels (string array)</li>
 *         <li>Coral/confidences (number array)</li>
 *         <li>Coral/x_positions (number array)</li>
 *         <li>Coral/y_positions (number array)</li>
 *         <li>Coral/widths (number array)</li>
 *         <li>Coral/heights (number array)</li>
 *       </ul>
 *   </li>
 * </ul>
 */
public class CoralVisionSubsystem extends SubsystemBase {
    private final NetworkTable coralTable;
    private final NetworkTableEntry numDetectionsEntry;
    private final NetworkTableEntry labelsEntry;
    private final NetworkTableEntry confidencesEntry;
    private final NetworkTableEntry xPositionsEntry;
    private final NetworkTableEntry yPositionsEntry;
    private final NetworkTableEntry widthsEntry;
    private final NetworkTableEntry heightsEntry;
    private final NetworkTableEntry timestampEntry;
    private final NetworkTableEntry connectedEntry;

    private List<Detection> detections;
    private long lastUpdateTime;
    private boolean coprocessorConnected;

    /**
     * Creates a new CoralVisionSubsystem
     */
    public CoralVisionSubsystem() {
        // Get NetworkTables instance for Coral coprocessor
        coralTable = NetworkTableInstance.getDefault().getTable("Coral");

        // Get NetworkTable entries
        numDetectionsEntry = coralTable.getEntry("num_detections");
        labelsEntry = coralTable.getEntry("labels");
        confidencesEntry = coralTable.getEntry("confidences");
        xPositionsEntry = coralTable.getEntry("x_positions");
        yPositionsEntry = coralTable.getEntry("y_positions");
        widthsEntry = coralTable.getEntry("widths");
        heightsEntry = coralTable.getEntry("heights");
        timestampEntry = coralTable.getEntry("timestamp");
        connectedEntry = coralTable.getEntry("connected");

        // Initialize detection list
        detections = new ArrayList<>();
        lastUpdateTime = 0;
        coprocessorConnected = false;

        SmartDashboard.putString("Coral/Status", "Waiting for coprocessor...");
    }

    /**
     * Reads detection data from NetworkTables
     */
    private void updateDetections() {
        // Check if coprocessor is connected
        coprocessorConnected = connectedEntry.getBoolean(false);

        if (!coprocessorConnected) {
            detections.clear();
            return;
        }

        // Get timestamp to check for new data
        long currentTimestamp = (long) timestampEntry.getDouble(0);
        if (currentTimestamp == lastUpdateTime) {
            // No new data
            return;
        }
        lastUpdateTime = currentTimestamp;

        // Read detection data from NetworkTables
        int numDetections = (int) numDetectionsEntry.getDouble(0);
        String[] labels = labelsEntry.getStringArray(new String[0]);
        double[] confidences = confidencesEntry.getDoubleArray(new double[0]);
        double[] xPositions = xPositionsEntry.getDoubleArray(new double[0]);
        double[] yPositions = yPositionsEntry.getDoubleArray(new double[0]);
        double[] widths = widthsEntry.getDoubleArray(new double[0]);
        double[] heights = heightsEntry.getDoubleArray(new double[0]);

        // Validate data
        if (labels.length != numDetections ||
            confidences.length != numDetections ||
            xPositions.length != numDetections ||
            yPositions.length != numDetections ||
            widths.length != numDetections ||
            heights.length != numDetections) {
            SmartDashboard.putString("Coral/Status", "Data validation error");
            return;
        }

        // Clear old detections and add new ones
        detections.clear();
        for (int i = 0; i < numDetections && i < VisionConstants.MAX_DETECTIONS; i++) {
            // Filter by confidence threshold
            if (confidences[i] >= VisionConstants.CONFIDENCE_THRESHOLD) {
                Detection detection = new Detection(
                    labels[i],
                    (float) confidences[i],
                    (float) xPositions[i],
                    (float) yPositions[i],
                    (float) widths[i],
                    (float) heights[i]
                );
                detections.add(detection);
            }
        }
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
     * Gets the closest detection by label (based on bounding box area)
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
     * Gets the most confident detection by label
     *
     * @param label The label to search for
     * @return The most confident detection, or null if none found
     */
    public synchronized Detection getMostConfidentDetection(String label) {
        Detection mostConfident = null;
        float highestConfidence = 0;

        for (Detection detection : detections) {
            if (detection.getLabel().equalsIgnoreCase(label)) {
                if (detection.getConfidence() > highestConfidence) {
                    highestConfidence = detection.getConfidence();
                    mostConfident = detection;
                }
            }
        }
        return mostConfident;
    }

    /**
     * Checks if the coprocessor is connected
     *
     * @return true if coprocessor is publishing data
     */
    public boolean isCoprocessorConnected() {
        return coprocessorConnected;
    }

    /**
     * Gets the number of current detections
     *
     * @return Number of detections
     */
    public synchronized int getDetectionCount() {
        return detections.size();
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

    @Override
    public void periodic() {
        // Update detections from NetworkTables
        updateDetections();

        // Update SmartDashboard
        SmartDashboard.putBoolean("Coral/Connected", coprocessorConnected);
        SmartDashboard.putNumber("Coral/Detections", detections.size());

        if (coprocessorConnected) {
            SmartDashboard.putString("Coral/Status", "Connected - " + detections.size() + " detections");

            // Display up to 5 detections on SmartDashboard
            synchronized (this) {
                for (int i = 0; i < Math.min(detections.size(), 5); i++) {
                    Detection det = detections.get(i);
                    SmartDashboard.putString("Coral/Detection" + i, det.toString());
                }
                // Clear old entries if fewer detections
                for (int i = detections.size(); i < 5; i++) {
                    SmartDashboard.putString("Coral/Detection" + i, "");
                }
            }

            // Display specific game piece detections if configured
            displayGamePieceDetections();
        } else {
            SmartDashboard.putString("Coral/Status", "Waiting for coprocessor...");
        }
    }

    /**
     * Helper method to display game piece specific detection info
     * Customize based on your game pieces (e.g., "Note", "Coral", "Algae")
     */
    private void displayGamePieceDetections() {
        // Example for Reefscape 2025 game pieces
        Detection coralDetection = getClosestDetection("coral");
        Detection algaeDetection = getClosestDetection("algae");

        if (coralDetection != null) {
            SmartDashboard.putBoolean("Coral/HasCoral", true);
            SmartDashboard.putNumber("Coral/CoralX", coralDetection.getX());
            SmartDashboard.putNumber("Coral/CoralY", coralDetection.getY());
            SmartDashboard.putNumber("Coral/CoralConf", coralDetection.getConfidence());
        } else {
            SmartDashboard.putBoolean("Coral/HasCoral", false);
        }

        if (algaeDetection != null) {
            SmartDashboard.putBoolean("Coral/HasAlgae", true);
            SmartDashboard.putNumber("Coral/AlgaeX", algaeDetection.getX());
            SmartDashboard.putNumber("Coral/AlgaeY", algaeDetection.getY());
            SmartDashboard.putNumber("Coral/AlgaeConf", algaeDetection.getConfidence());
        } else {
            SmartDashboard.putBoolean("Coral/HasAlgae", false);
        }
    }
}
