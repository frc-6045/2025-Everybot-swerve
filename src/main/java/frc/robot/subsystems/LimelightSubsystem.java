package frc.robot.subsystems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Subsystem for interfacing with the Limelight camera to detect and track game pieces.
 *
 * The Limelight provides:
 * - X position (lateral offset in degrees)
 * - Y position (vertical offset in degrees)
 * - Target detection status
 * - 3D position estimation via camera pose
 */
public class LimelightSubsystem extends SubsystemBase {
    private final NetworkTable limelightTable;
    private final NetworkTableEntry tvEntry;      // Valid target (0 or 1)
    private final NetworkTableEntry txEntry;      // Horizontal offset (-27 to 27 degrees)
    private final NetworkTableEntry tyEntry;      // Vertical offset (-20.5 to 20.5 degrees)
    private final NetworkTableEntry taEntry;      // Target area (0% to 100%)
    private final NetworkTableEntry pipelineEntry; // Current pipeline index
    private final NetworkTableEntry camtranEntry;  // 3D camera transform (6-element array)

    // Cached values
    private GamePieceData lastDetection;
    private double lastUpdateTime;

    // Camera calibration constants (adjust based on your Limelight mounting)
    private static final double CAMERA_HEIGHT_METERS = 0.5;  // Height of camera from ground
    private static final double CAMERA_ANGLE_DEGREES = 0.0;  // Tilt angle of camera

    // Game piece constants (adjust for your game pieces)
    private static final double GAME_PIECE_HEIGHT_METERS = 0.1;  // Height of game piece

    // Pipeline constants
    public static final int CORAL_PIPELINE = 0;
    public static final int ALGAE_PIPELINE = 1;

    public LimelightSubsystem() {
        limelightTable = NetworkTableInstance.getDefault().getTable("limelight");
        tvEntry = limelightTable.getEntry("tv");
        txEntry = limelightTable.getEntry("tx");
        tyEntry = limelightTable.getEntry("ty");
        taEntry = limelightTable.getEntry("ta");
        pipelineEntry = limelightTable.getEntry("pipeline");
        camtranEntry = limelightTable.getEntry("camtran");

        lastDetection = GamePieceData.noDetection();
        lastUpdateTime = Timer.getFPGATimestamp();

        // Set LED mode to pipeline control
        setLEDMode(LEDMode.PIPELINE);
    }

    @Override
    public void periodic() {
        updateDetection();
        updateDashboard();
    }

    /**
     * Updates the game piece detection data from Limelight NetworkTables
     */
    private void updateDetection() {
        double currentTime = Timer.getFPGATimestamp();

        // Check if target is detected
        boolean hasTarget = tvEntry.getDouble(0.0) == 1.0;

        if (!hasTarget) {
            lastDetection = GamePieceData.noDetection();
            lastUpdateTime = currentTime;
            return;
        }

        // Get basic target data
        double tx = txEntry.getDouble(0.0);  // Horizontal offset
        double ty = tyEntry.getDouble(0.0);  // Vertical offset
        double ta = taEntry.getDouble(0.0);  // Target area

        // Try to get 3D position from camtran if available
        double[] camtran = camtranEntry.getDoubleArray(new double[6]);

        double x, y, z, theta;

        if (camtran.length == 6) {
            // Use 3D camera pose estimation
            // camtran format: [x, y, z, pitch, yaw, roll]
            x = camtran[0];
            y = camtran[1];
            z = camtran[2];
            theta = camtran[4];  // Yaw angle
        } else {
            // Fallback: Calculate approximate position from angles
            // This is a simplified calculation - adjust based on your setup
            double distance = estimateDistanceFromArea(ta);
            double angleRadians = Math.toRadians(tx);

            x = distance * Math.sin(angleRadians);
            y = distance * Math.cos(angleRadians);
            z = calculateHeightDifference(ty);
            theta = tx;  // Use horizontal offset as theta
        }

        lastDetection = new GamePieceData(true, x, y, z, theta, currentTime);
        lastUpdateTime = currentTime;
    }

    /**
     * Estimates distance to target based on target area percentage
     * This is a rough approximation - calibrate with actual measurements
     */
    private double estimateDistanceFromArea(double areaPercent) {
        if (areaPercent <= 0) {
            return 5.0;  // Default distance if area is invalid
        }
        // Inverse square relationship: area decreases with square of distance
        // Calibration constant - adjust based on your Limelight and target size
        double calibrationConstant = 2.0;
        return calibrationConstant / Math.sqrt(areaPercent / 100.0);
    }

    /**
     * Calculates height difference based on vertical angle offset
     */
    private double calculateHeightDifference(double ty) {
        double angleToTargetRadians = Math.toRadians(CAMERA_ANGLE_DEGREES + ty);
        return CAMERA_HEIGHT_METERS - GAME_PIECE_HEIGHT_METERS +
               Math.tan(angleToTargetRadians) * 1.0;  // Assume 1m distance for approximation
    }

    /**
     * Gets the latest game piece detection data
     * @return GamePieceData object with detection info
     */
    public GamePieceData getGamePieceData() {
        return lastDetection;
    }

    /**
     * Checks if a game piece is currently detected
     * @return true if a game piece is detected
     */
    public boolean hasTarget() {
        return lastDetection.isDetected();
    }

    /**
     * Gets the horizontal offset to the target in degrees
     * @return horizontal offset (-27 to 27 degrees), 0 if no target
     */
    public double getHorizontalOffset() {
        return hasTarget() ? txEntry.getDouble(0.0) : 0.0;
    }

    /**
     * Gets the vertical offset to the target in degrees
     * @return vertical offset (-20.5 to 20.5 degrees), 0 if no target
     */
    public double getVerticalOffset() {
        return hasTarget() ? tyEntry.getDouble(0.0) : 0.0;
    }

    /**
     * Sets the active vision pipeline
     * @param pipeline Pipeline index (0-9)
     */
    public void setPipeline(int pipeline) {
        pipelineEntry.setNumber(pipeline);
    }

    /**
     * Sets the pipeline for detecting coral game pieces
     */
    public void setCoralPipeline() {
        setPipeline(CORAL_PIPELINE);
    }

    /**
     * Sets the pipeline for detecting algae game pieces
     */
    public void setAlgaePipeline() {
        setPipeline(ALGAE_PIPELINE);
    }

    /**
     * Gets the current pipeline index
     * @return Current pipeline (0-9)
     */
    public int getCurrentPipeline() {
        return (int) pipelineEntry.getDouble(0.0);
    }

    /**
     * Sets the Limelight LED mode
     * @param mode LED mode to set
     */
    public void setLEDMode(LEDMode mode) {
        limelightTable.getEntry("ledMode").setNumber(mode.value);
    }

    /**
     * Sets the camera mode
     * @param mode Camera mode to set
     */
    public void setCamMode(CamMode mode) {
        limelightTable.getEntry("camMode").setNumber(mode.value);
    }

    /**
     * Updates SmartDashboard with Limelight data
     */
    private void updateDashboard() {
        SmartDashboard.putBoolean("Limelight/HasTarget", hasTarget());
        SmartDashboard.putNumber("Limelight/TX", getHorizontalOffset());
        SmartDashboard.putNumber("Limelight/TY", getVerticalOffset());
        SmartDashboard.putNumber("Limelight/Pipeline", getCurrentPipeline());
        SmartDashboard.putString("Limelight/GamePiece", lastDetection.toString());

        if (hasTarget()) {
            SmartDashboard.putNumber("Limelight/Distance", lastDetection.getDistance2d());
            SmartDashboard.putNumber("Limelight/Angle", lastDetection.getAngleToPiece());
        }
    }

    /**
     * Checks if the robot is aligned with a detected game piece
     * @param toleranceDegrees Alignment tolerance in degrees
     * @return true if aligned within tolerance
     */
    public boolean isAligned(double toleranceDegrees) {
        return hasTarget() && Math.abs(getHorizontalOffset()) < toleranceDegrees;
    }

    /**
     * Gets the time since the last detection update
     * @return Time since last update in seconds
     */
    public double getTimeSinceLastUpdate() {
        return Timer.getFPGATimestamp() - lastUpdateTime;
    }

    /**
     * LED mode options for Limelight
     */
    public enum LEDMode {
        PIPELINE(0),    // Use LED mode from current pipeline
        OFF(1),         // Force LEDs off
        BLINK(2),       // Force LEDs to blink
        ON(3);          // Force LEDs on

        public final int value;

        LEDMode(int value) {
            this.value = value;
        }
    }

    /**
     * Camera mode options for Limelight
     */
    public enum CamMode {
        VISION(0),      // Vision processing mode
        DRIVER(1);      // Driver camera mode (no processing)

        public final int value;

        CamMode(int value) {
            this.value = value;
        }
    }
}
