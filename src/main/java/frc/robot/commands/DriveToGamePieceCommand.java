package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.VisionConstants;
import frc.robot.subsystems.GamePieceData;
import frc.robot.subsystems.LimelightSubsystem;
import frc.robot.subsystems.SwerveSubsystem;

/**
 * Command to automatically drive the robot to a detected game piece using vision.
 *
 * This command:
 * 1. Uses the Limelight to detect game pieces
 * 2. Calculates the distance and angle to the game piece
 * 3. Uses PID controllers to drive toward and align with the game piece
 * 4. Ends when the robot is close enough to the game piece
 */
public class DriveToGamePieceCommand extends Command {
    private final SwerveSubsystem m_swerve;
    private final LimelightSubsystem m_limelight;

    // PID controllers for autonomous driving
    private final PIDController m_turnController;
    private final PIDController m_driveController;

    private final double m_targetDistance;  // How close to get (meters)

    /**
     * Creates a new DriveToGamePieceCommand
     * @param swerve The swerve drive subsystem
     * @param limelight The limelight subsystem
     * @param targetDistance Target distance to stop from game piece (meters)
     */
    public DriveToGamePieceCommand(SwerveSubsystem swerve, LimelightSubsystem limelight, double targetDistance) {
        m_swerve = swerve;
        m_limelight = limelight;
        m_targetDistance = targetDistance;

        // Configure PID controllers
        // Turn controller: aligns robot with game piece
        m_turnController = new PIDController(0.02, 0.0, 0.001);
        m_turnController.setTolerance(VisionConstants.ALIGNMENT_TOLERANCE_DEGREES);
        m_turnController.enableContinuousInput(-180, 180);

        // Drive controller: controls forward/backward movement
        m_driveController = new PIDController(0.5, 0.0, 0.05);
        m_driveController.setTolerance(VisionConstants.DISTANCE_TOLERANCE_METERS);

        addRequirements(swerve, limelight);
    }

    /**
     * Creates a command with default target distance of 0.3 meters
     */
    public DriveToGamePieceCommand(SwerveSubsystem swerve, LimelightSubsystem limelight) {
        this(swerve, limelight, 0.3);
    }

    @Override
    public void initialize() {
        m_turnController.reset();
        m_driveController.reset();
    }

    @Override
    public void execute() {
        GamePieceData gamePiece = m_limelight.getGamePieceData();

        if (!gamePiece.isDetected()) {
            // No target detected - stop the robot
            m_swerve.drive(new ChassisSpeeds(0, 0, 0));
            return;
        }

        // Calculate distance error (how far we need to move forward)
        double currentDistance = gamePiece.getDistance2d();

        // Calculate turn error (horizontal alignment)
        double turnError = gamePiece.getAngleToPiece();

        // Calculate drive speed using PID controller
        double forwardSpeed = m_driveController.calculate(currentDistance, m_targetDistance);

        // Calculate turn speed using PID controller
        double turnSpeed = m_turnController.calculate(turnError, 0.0);

        // Clamp speeds to safe maximums
        forwardSpeed = Math.max(-VisionConstants.AUTO_DRIVE_SPEED,
                               Math.min(VisionConstants.AUTO_DRIVE_SPEED, forwardSpeed));
        turnSpeed = Math.max(-VisionConstants.AUTO_TURN_SPEED,
                            Math.min(VisionConstants.AUTO_TURN_SPEED, turnSpeed));

        // Create chassis speeds (robot-relative)
        // X is forward/backward, Y is left/right (strafe), omega is rotation
        ChassisSpeeds speeds = new ChassisSpeeds(
            forwardSpeed,  // Forward toward game piece
            0.0,           // No strafing
            turnSpeed      // Turn to align
        );

        // Drive the robot
        m_swerve.drive(speeds);
    }

    @Override
    public void end(boolean interrupted) {
        // Stop the robot when command ends
        m_swerve.drive(new ChassisSpeeds(0, 0, 0));
    }

    @Override
    public boolean isFinished() {
        GamePieceData gamePiece = m_limelight.getGamePieceData();

        // End if no target is detected
        if (!gamePiece.isDetected()) {
            return false;  // Keep running to find a target
        }

        // End when we're at the target distance and aligned
        boolean atTargetDistance = Math.abs(gamePiece.getDistance2d() - m_targetDistance)
                                   < VisionConstants.DISTANCE_TOLERANCE_METERS;
        boolean aligned = Math.abs(gamePiece.getAngleToPiece())
                          < VisionConstants.ALIGNMENT_TOLERANCE_DEGREES;

        return atTargetDistance && aligned;
    }
}
