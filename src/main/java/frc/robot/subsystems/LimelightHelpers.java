package frc.robot.subsystems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * Helper class for Limelight communication via NetworkTables.
 */
public class LimelightHelpers {

    private static NetworkTable getTable(String tableName) {
        return NetworkTableInstance.getDefault().getTable(tableName);
    }

    private static NetworkTableEntry getEntry(String tableName, String entryName) {
        return getTable(tableName).getEntry(entryName);
    }

    /**
     * Gets the horizontal offset from crosshair to target.
     * @param limelightName Name of the Limelight (e.g., "limelight")
     * @return tx value in degrees (-27 to 27)
     */
    public static double getTX(String limelightName) {
        return getEntry(limelightName, "tx").getDouble(0.0);
    }

    /**
     * Gets the vertical offset from crosshair to target.
     * @param limelightName Name of the Limelight
     * @return ty value in degrees (-20.5 to 20.5)
     */
    public static double getTY(String limelightName) {
        return getEntry(limelightName, "ty").getDouble(0.0);
    }

    /**
     * Gets the target area (0% to 100% of image).
     * @param limelightName Name of the Limelight
     * @return ta value (0 to 100)
     */
    public static double getTA(String limelightName) {
        return getEntry(limelightName, "ta").getDouble(0.0);
    }

    /**
     * Checks if the Limelight has any valid targets.
     * @param limelightName Name of the Limelight
     * @return true if a valid target is detected
     */
    public static boolean getTV(String limelightName) {
        return getEntry(limelightName, "tv").getDouble(0.0) == 1.0;
    }

    /**
     * Sets the pipeline index (0-9).
     * @param limelightName Name of the Limelight
     * @param pipeline Pipeline index
     */
    public static void setPipeline(String limelightName, int pipeline) {
        getEntry(limelightName, "pipeline").setNumber(pipeline);
    }

    /**
     * Sets the LED mode.
     * @param limelightName Name of the Limelight
     * @param mode 0=pipeline default, 1=off, 2=blink, 3=on
     */
    public static void setLEDMode(String limelightName, int mode) {
        getEntry(limelightName, "ledMode").setNumber(mode);
    }

    /**
     * Sets the camera mode.
     * @param limelightName Name of the Limelight
     * @param mode 0=Vision processor, 1=Driver camera
     */
    public static void setCamMode(String limelightName, int mode) {
        getEntry(limelightName, "camMode").setNumber(mode);
    }
}
