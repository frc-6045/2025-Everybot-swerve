package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.LimelightHelpers;
import frc.robot.Constants.VisionConstants;

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
    private final String limelightName;

    // Cached values
    private GamePieceData lastDetection;
    private double lastUpdateTime;

    // Camera calibration constants (adjust based on your Limelight mounting)
    private static final double CAMERA_HEIGHT_METERS = VisionConstants.CAMERA_HEIGHT_METERS;
    private static final double CAMERA_ANGLE_DEGREES = VisionConstants.CAMERA_PITCH_DEGREES;

    // Game piece constants (adjust for your game pieces)
    private static final double GAME_PIECE_HEIGHT_METERS = VisionConstants.CORAL_HEIGHT_METERS;

    public LimelightSubsystem() {
        this(VisionConstants.LIMELIGHT_NAME);
    }

    public LimelightSubsystem(String limelightName) {
        this.limelightName = limelightName;
        lastDetection = GamePieceData.noDetection();
        lastUpdateTime = Timer.getFPGATimestamp();

        // Set LED mode to pipeline control
        LimelightHelpers.setLEDMode_PipelineControl(limelightName);
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
        boolean hasTarget = LimelightHelpers.getTV(limelightName);

        if (!hasTarget) {
            lastDetection = GamePieceData.noDetection();
            lastUpdateTime = currentTime;
            return;
        }

        // Get basic target data
        double tx = LimelightHelpers.getTX(limelightName);  // Horizontal offset
        double ty = LimelightHelpers.getTY(limelightName);  // Vertical offset
        double ta = LimelightHelpers.getTA(limelightName);  // Target area

        // Try to get 3D position from camera transform
        double[] camtran = LimelightHelpers.getLimelightNTDoubleArray(limelightName, "camtran");

        double x, y, z, theta;

        if (camtran != null && camtran.length == 6) {
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
        return hasTarget() ? LimelightHelpers.getTX(limelightName) : 0.0;
    }

    /**
     * Gets the vertical offset to the target in degrees
     * @return vertical offset (-20.5 to 20.5 degrees), 0 if no target
     */
    public double getVerticalOffset() {
        return hasTarget() ? LimelightHelpers.getTY(limelightName) : 0.0;
    }

    /**
     * Sets the active vision pipeline
     * @param pipeline Pipeline index (0-9)
     */
    public void setPipeline(int pipeline) {
        LimelightHelpers.setPipelineIndex(limelightName, pipeline);
    }

    /**
     * Sets the pipeline for detecting coral game pieces
     */
    public void setCoralPipeline() {
        setPipeline(VisionConstants.CORAL_PIPELINE);
    }

    /**
     * Sets the pipeline for detecting algae game pieces
     */
    public void setAlgaePipeline() {
        setPipeline(VisionConstants.ALGAE_PIPELINE);
    }

    /**
     * Gets the current pipeline index
     * @return Current pipeline (0-9)
     */
    public int getCurrentPipeline() {
        return (int) LimelightHelpers.getCurrentPipelineIndex(limelightName);
    }

    /**
     * Sets the Limelight LED mode to pipeline control
     */
    public void setLEDModePipeline() {
        LimelightHelpers.setLEDMode_PipelineControl(limelightName);
    }

    /**
     * Forces LEDs off
     */
    public void setLEDModeOff() {
        LimelightHelpers.setLEDMode_ForceOff(limelightName);
    }

    /**
     * Forces LEDs on
     */
    public void setLEDModeOn() {
        LimelightHelpers.setLEDMode_ForceOn(limelightName);
    }

    /**
     * Forces LEDs to blink
     */
    public void setLEDModeBlink() {
        LimelightHelpers.setLEDMode_ForceBlink(limelightName);
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
}
