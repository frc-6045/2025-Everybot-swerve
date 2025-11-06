package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;

/**
 * Data class for storing game piece detection information from Limelight
 */
public class GamePieceData {
    private final boolean detected;
    private final double x;
    private final double y;
    private final double z;
    private final double theta;
    private final double timestamp;

    public GamePieceData(boolean detected, double x, double y, double z, double theta, double timestamp) {
        this.detected = detected;
        this.x = x;
        this.y = y;
        this.z = z;
        this.theta = theta;
        this.timestamp = timestamp;
    }

    /**
     * Creates a GamePieceData instance when no game piece is detected
     */
    public static GamePieceData noDetection() {
        return new GamePieceData(false, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    /**
     * @return true if a game piece was detected
     */
    public boolean isDetected() {
        return detected;
    }

    /**
     * @return X coordinate in meters (lateral position)
     */
    public double getX() {
        return x;
    }

    /**
     * @return Y coordinate in meters (forward/backward position)
     */
    public double getY() {
        return y;
    }

    /**
     * @return Z coordinate in meters (height)
     */
    public double getZ() {
        return z;
    }

    /**
     * @return Theta angle in degrees (rotation)
     */
    public double getTheta() {
        return theta;
    }

    /**
     * @return Timestamp when this detection occurred
     */
    public double getTimestamp() {
        return timestamp;
    }

    /**
     * Converts this game piece data to a 3D pose
     * @return Pose3d representation of the game piece position
     */
    public Pose3d toPose3d() {
        return new Pose3d(
            new Translation3d(x, y, z),
            new Rotation3d(0, 0, Math.toRadians(theta))
        );
    }

    /**
     * @return Distance to the game piece in meters (2D plane)
     */
    public double getDistance2d() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * @return Distance to the game piece in meters (3D space)
     */
    public double getDistance3d() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * @return Angle to the game piece in degrees
     */
    public double getAngleToPiece() {
        return Math.toDegrees(Math.atan2(x, y));
    }

    @Override
    public String toString() {
        if (!detected) {
            return "GamePieceData{NO DETECTION}";
        }
        return String.format("GamePieceData{x=%.2f, y=%.2f, z=%.2f, theta=%.2f, dist=%.2f}",
            x, y, z, theta, getDistance2d());
    }
}
