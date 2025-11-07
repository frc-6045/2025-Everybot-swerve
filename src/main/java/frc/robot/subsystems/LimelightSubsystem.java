package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.LimelightHelpers;
import frc.robot.Constants.VisionConstants;

/**
 * Subsystem for interfacing with the Limelight camera to detect and track ALGAE game pieces.
 *
 * Inspired by FRC teams 2056 and 2910's vision approaches.
 * Simplified to only detect algae - no coral detection.
 */
public class LimelightSubsystem extends SubsystemBase {
    private final String limelightName;

    // Cached values
    private GamePieceData lastDetection;
    private double lastUpdateTime;

    // Camera calibration constants
    private static final double CAMERA_HEIGHT_METERS = VisionConstants.CAMERA_HEIGHT_METERS;
    private static final double CAMERA_ANGLE_DEGREES = VisionConstants.CAMERA_PITCH_DEGREES;
    private static final double ALGAE_HEIGHT_METERS = VisionConstants.ALGAE_HEIGHT_METERS;

    public LimelightSubsystem() {
        this(VisionConstants.LIMELIGHT_NAME);
    }

    public LimelightSubsystem(String limelightName) {
        this.limelightName = limelightName;
        lastDetection = GamePieceData.noDetection();
        lastUpdateTime = Timer.getFPGATimestamp();

        // Configure Limelight for algae detection
        LimelightHelpers.setLEDMode_PipelineControl(limelightName);
        LimelightHelpers.setPipelineIndex(limelightName, 0); // Use pipeline 0 for algae
    }

    @Override
    public void periodic() {
        updateDetection();
        updateDashboard();
    }

    /**
     * Updates the algae detection data from Limelight NetworkTables
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
            // Use 3D camera pose estimation (more accurate)
            x = camtran[0];
            y = camtran[1];
            z = camtran[2];
            theta = camtran[4];  // Yaw angle
        } else {
            // Fallback: Calculate approximate position from angles
            double distance = estimateDistanceFromArea(ta);
            double angleRadians = Math.toRadians(tx);

            x = distance * Math.sin(angleRadians);
            y = distance * Math.cos(angleRadians);
            z = calculateHeightDifference(ty);
            theta = tx;
        }

        lastDetection = new GamePieceData(true, x, y, z, theta, currentTime);
        lastUpdateTime = currentTime;
    }

    /**
     * Estimates distance to algae based on target area percentage
     * Calibrate this constant based on actual measurements
     */
    private double estimateDistanceFromArea(double areaPercent) {
        if (areaPercent <= 0) {
            return 5.0;  // Default distance if area is invalid
        }
        // Inverse square relationship: area decreases with square of distance
        // Adjust calibrationConstant based on your algae size and camera setup
        double calibrationConstant = 2.0;
        return calibrationConstant / Math.sqrt(areaPercent / 100.0);
    }

    /**
     * Calculates height difference based on vertical angle offset
     */
    private double calculateHeightDifference(double ty) {
        double angleToTargetRadians = Math.toRadians(CAMERA_ANGLE_DEGREES + ty);
        return CAMERA_HEIGHT_METERS - ALGAE_HEIGHT_METERS +
               Math.tan(angleToTargetRadians) * 1.0;
    }

    /**
     * Gets the latest algae detection data
     * @return GamePieceData object with detection info
     */
    public GamePieceData getGamePieceData() {
        return lastDetection;
    }

    /**
     * Checks if algae is currently detected
     * @return true if algae is detected
     */
    public boolean hasTarget() {
        return lastDetection.isDetected();
    }

    /**
     * Gets the horizontal offset to the algae in degrees
     * @return horizontal offset (-27 to 27 degrees), 0 if no target
     */
    public double getHorizontalOffset() {
        return hasTarget() ? LimelightHelpers.getTX(limelightName) : 0.0;
    }

    /**
     * Gets the vertical offset to the algae in degrees
     * @return vertical offset (-20.5 to 20.5 degrees), 0 if no target
     */
    public double getVerticalOffset() {
        return hasTarget() ? LimelightHelpers.getTY(limelightName) : 0.0;
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
     * Sets LED mode to pipeline control (default)
     */
    public void setLEDModePipeline() {
        LimelightHelpers.setLEDMode_PipelineControl(limelightName);
    }

    /**
     * Updates SmartDashboard with Limelight data
     */
    private void updateDashboard() {
        SmartDashboard.putBoolean("Algae/HasTarget", hasTarget());
        SmartDashboard.putNumber("Algae/TX", getHorizontalOffset());
        SmartDashboard.putNumber("Algae/TY", getVerticalOffset());
        SmartDashboard.putString("Algae/Data", lastDetection.toString());

        if (hasTarget()) {
            SmartDashboard.putNumber("Algae/Distance", lastDetection.getDistance2d());
            SmartDashboard.putNumber("Algae/Angle", lastDetection.getAngleToPiece());
        }
    }

    /**
     * Checks if the robot is aligned with detected algae
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
