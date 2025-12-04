// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

/**
 * Represents a single object detection from the Coral USB Accelerator
 */
public class Detection {
    private final String label;
    private final float confidence;
    private final float x;
    private final float y;
    private final float width;
    private final float height;

    /**
     * Creates a new Detection
     *
     * @param label      The class label of the detected object
     * @param confidence The confidence score (0.0 to 1.0)
     * @param x          The x coordinate of the bounding box center (normalized 0-1)
     * @param y          The y coordinate of the bounding box center (normalized 0-1)
     * @param width      The width of the bounding box (normalized 0-1)
     * @param height     The height of the bounding box (normalized 0-1)
     */
    public Detection(String label, float confidence, float x, float y, float width, float height) {
        this.label = label;
        this.confidence = confidence;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String getLabel() {
        return label;
    }

    public float getConfidence() {
        return confidence;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f%%) at (%.2f, %.2f)",
            label, confidence * 100, x, y);
    }
}
