package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.Constants.ArmConstants;

/**
 * Command to move the arm to a specific encoder position
 * Uses simple proportional control to reach target
 */
public class MoveArmToPositionCommand extends Command {
    private final ArmSubsystem armSubsystem;
    private final double targetPosition;
    private final double tolerance;
    private static final double kP = 0.1;  // Proportional gain - tune this value!

    /**
     * Create command to move arm to target position
     *
     * @param armSubsystem the arm subsystem
     * @param targetPosition target encoder position in rotations
     */
    public MoveArmToPositionCommand(ArmSubsystem armSubsystem, double targetPosition) {
        this.armSubsystem = armSubsystem;
        this.targetPosition = targetPosition;
        this.tolerance = ArmConstants.ARM_POSITION_TOLERANCE;
        addRequirements(armSubsystem);
    }

    @Override
    public void initialize() {
        // Nothing to initialize
    }

    @Override
    public void execute() {
        double currentPosition = armSubsystem.getArmPosition();
        double error = targetPosition - currentPosition;

        // Simple proportional control
        double speed = error * kP;

        // Clamp speed to safe values
        speed = Math.max(-0.3, Math.min(0.3, speed));

        armSubsystem.runArm(speed);
    }

    @Override
    public void end(boolean interrupted) {
        // Stop the arm when command ends
        armSubsystem.runArm(0);
    }

    @Override
    public boolean isFinished() {
        // Command finishes when we're within tolerance of target
        return armSubsystem.atPosition(targetPosition, tolerance);
    }
}
