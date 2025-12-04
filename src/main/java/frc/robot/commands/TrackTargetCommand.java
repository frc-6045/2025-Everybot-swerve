// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.HybridVisionSubsystem;
import frc.robot.subsystems.SwerveSubsystem;

/**
 * Command that uses hybrid vision (Limelight + Coral) to track algae
 *
 * This command uses the horizontal offset from the hybrid vision system to rotate
 * the robot until the target is centered in the camera frame. The hybrid system
 * combines Limelight's fast targeting with Coral's accurate neural network detection.
 */
public class TrackTargetCommand extends Command {
  private final HybridVisionSubsystem m_vision;
  private final SwerveSubsystem m_drive;
  private final String m_targetLabel;

  private static final double ROTATION_KP = 0.04;  // Proportional gain for rotation (tune this!)
  private static final double MIN_COMMAND = 0.05;  // Minimum rotation command
  private static final double DEADBAND = 1.0;  // Deadband in degrees for "centered"

  /**
   * Creates a new TrackTargetCommand
   *
   * @param vision The hybrid vision subsystem (Limelight + Coral)
   * @param drive The swerve drive subsystem
   * @param targetLabel The label of the object to track (e.g., "algae")
   */
  public TrackTargetCommand(HybridVisionSubsystem vision, SwerveSubsystem drive, String targetLabel) {
    m_vision = vision;
    m_drive = drive;
    m_targetLabel = targetLabel;

    // Add requirements
    addRequirements(vision, drive);
  }

  @Override
  public void initialize() {
    // Turn on Limelight LEDs (if using Limelight mode)
    m_vision.getLimelight().setLEDs(true);
  }

  @Override
  public void execute() {
    // Check if hybrid vision system has a valid target
    if (!m_vision.hasTarget()) {
      // No target found - stop rotating
      m_drive.drive(new Translation2d(0, 0), 0, false);
      return;
    }

    // Get horizontal offset from hybrid vision system (in degrees)
    // This automatically uses the configured vision mode (Limelight/Coral/Both)
    double tx = m_vision.getHorizontalOffset();

    // Apply deadband
    if (Math.abs(tx) < DEADBAND) {
      // Target is centered - stop
      m_drive.drive(new Translation2d(0, 0), 0, false);
      return;
    }

    // Calculate rotation speed using proportional control
    double rotationSpeed = -tx * ROTATION_KP;

    // Apply minimum command to overcome friction
    if (Math.abs(rotationSpeed) < MIN_COMMAND) {
      rotationSpeed = Math.copySign(MIN_COMMAND, rotationSpeed);
    }

    // Clamp rotation speed to safe range
    rotationSpeed = Math.max(-0.5, Math.min(0.5, rotationSpeed));

    // Drive with rotation only (no translation)
    m_drive.drive(new Translation2d(0, 0), rotationSpeed, false);
  }

  @Override
  public void end(boolean interrupted) {
    // Stop the drive when command ends
    m_drive.drive(new Translation2d(0, 0), 0, false);

    // Optionally turn off LEDs
    // m_vision.setLEDs(false);
  }

  @Override
  public boolean isFinished() {
    // Finish when target is centered
    if (!m_vision.hasTarget()) {
      return false;  // Keep running until we find target
    }

    double tx = m_vision.getHorizontalOffset();
    return Math.abs(tx) < DEADBAND;  // Finish when centered
  }
}
