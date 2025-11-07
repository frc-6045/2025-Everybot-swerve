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

  public static final class OperatorConstants {
    public static final int DRIVER_CONTROLLER_PORT = 0;
    public static final int OPERATOR_CONTROLLER_PORT = 1;
    public static final int GOD_CONTROLLER_PORT = 2;

    public static final double DEADBAND        = 0.1;
    public static final double LEFT_Y_DEADBAND = 0.1;
    public static final double RIGHT_X_DEADBAND = 0.1;
    public static final double TURN_CONSTANT    = 6;
  }

  public static final class VisionConstants {
    // Limelight network table name
    public static final String LIMELIGHT_NAME = "limelight";

    // Camera mounting configuration (adjust based on your robot)
    public static final double CAMERA_HEIGHT_METERS = 0.5;      // Height from ground to camera lens
    public static final double CAMERA_PITCH_DEGREES = 0.0;      // Camera tilt angle (0 = horizontal)

    // Algae game piece physical properties
    public static final double ALGAE_HEIGHT_METERS = 0.1;       // Height of algae game piece

    // Vision alignment tolerances
    public static final double ALIGNMENT_TOLERANCE_DEGREES = 2.0;  // Acceptable horizontal misalignment
    public static final double DISTANCE_TOLERANCE_METERS = 0.1;    // Acceptable distance error

    // Vision-based auto drive speeds
    public static final double AUTO_DRIVE_SPEED = 0.3;          // Speed when driving to algae
    public static final double AUTO_TURN_SPEED = 0.2;           // Speed when turning to algae

    // Vision update timeout
    public static final double VISION_TIMEOUT_SECONDS = 0.5;    // Max age of vision data
  }
}
