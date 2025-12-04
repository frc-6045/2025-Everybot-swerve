// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.util.Units;
import swervelib.math.Matter;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean
 * constants. This class should not be used for any other purpose. All constants
 * should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>
 * It is advised to statically import this class (or one of its inner classes)
 * wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static final class SwerveConstants {
    public static final double ROBOT_MASS = (148 - 20.3) * 0.453592; // 32lbs * kg per pound
    public static final Matter CHASSIS    = new Matter(new Translation3d(0, 0, Units.inchesToMeters(8)), ROBOT_MASS);
    public static final double LOOP_TIME  = 0.13; //s, 20ms + 110ms sprk max velocity lag
    public static final double MAX_SPEED  = Units.feetToMeters(14.5);
  }

  public static final class RollerConstants {
    public static final int ROLLER_MOTOR_ID = 9;
    public static final int ROLLER_MOTOR_CURRENT_LIMIT = 60;
    public static final double ROLLER_MOTOR_VOLTAGE_COMP = 10;
    public static final double ROLLER_CORAL_OUT = -.2;
    public static final double ROLLER_ALGAE_IN = -0.4;
    public static final double ROLLER_ALGAE_OUT = 0.4;
    public static final double ROLLER_CORAL_STACK = -1;
  }

  public static final class ArmConstants {
    public static final int ARM_MOTOR_ID = 10;
    public static final int ARM_MOTOR_CURRENT_LIMIT = 60;
    public static final double ARM_MOTOR_VOLTAGE_COMP = 10;
    public static final double ARM_SPEED_DOWN = -0.2;
    public static final double ARM_SPEED_UP = 0.2;
    public static final double ARM_HOLD_DOWN = -0.05;
    public static final double ARM_HOLD_UP = 0.05;
  }

  public static final class ClimberConstants {
    public static final int CLIMBER_MOTOR_ID = 11;
    public static final int CLIMBER_MOTOR_CURRENT_LIMIT = 60;
    public static final double CLIMBER_MOTOR_VOLTAGE_COMP = 12;
    public static final double CLIMBER_SPEED_DOWN = -0.5;
    public static final double CLIMBER_SPEED_UP = 0.5;
  }

  public static final class VisionConstants {
    // Limelight name in NetworkTables (change if you renamed your Limelight)
    public static final String LIMELIGHT_NAME = "limelight";

    // Limelight pipeline indices
    public static final int ALGAE_PIPELINE = 0;  // Pipeline for algae detection
    public static final int CORAL_PIPELINE = 1;  // Pipeline for coral detection (if needed)

    // Vision tracking constants
    public static final double ROTATION_KP = 0.04;  // Proportional gain for rotation tracking
    public static final double MIN_ROTATION_COMMAND = 0.05;  // Minimum command to overcome friction
    public static final double TRACKING_DEADBAND = 1.0;  // Deadband in degrees for "centered"

    // Distance estimation constants (MUST BE CALIBRATED!)
    public static final double DISTANCE_CALIBRATION_CONSTANT = 48.0;  // k in distance = k / sqrt(area)

    // Target height for distance calculation (if using angle-based distance)
    public static final double TARGET_HEIGHT_INCHES = 14.0;  // Height of algae/coral from ground
    public static final double CAMERA_HEIGHT_INCHES = 8.0;   // Height of camera from ground
    public static final double CAMERA_MOUNT_ANGLE_DEGREES = 15.0;  // Camera tilt angle

    // Detection settings
    public static final float CONFIDENCE_THRESHOLD = 0.5f;
    public static final int MAX_DETECTIONS = 10;
  }

  public static final class OperatorConstants {
    public static final int DRIVER_CONTROLLER_PORT = 0;
    public static final int OPERATOR_CONTROLLER_PORT = 1;
    public static final int GOD_CONTROLLER_PORT = 2;

    public static final double DEADBAND        = 0.1;
    public static final double LEFT_Y_DEADBAND = 0.1;
    public static final double RIGHT_X_DEADBAND = 0.1;
    public static final double TURN_CONSTANT    = 6;
  }
}
