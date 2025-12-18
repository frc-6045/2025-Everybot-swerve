package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.LimelightConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.Constants.RollerConstants;
import frc.robot.Constants.SwerveConstants;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.LimelightHelpers;
import frc.robot.subsystems.RollerSubsystem;
import frc.robot.subsystems.SwerveSubsystem;

import java.util.function.DoubleSupplier;

/**
 * Command that automatically detects and intakes algae game pieces.
 *
 * When running:
 * - Limelight searches for algae using the configured pipeline
 * - Robot strafes and rotates to align with the target
 * - Driver retains forward/backward control
 * - Arm moves to intake position
 * - Rollers run until current spike detected (game piece acquired)
 * - After detection, arm raises to stow position
 * - Command ends when arm reaches stow position
 */
public class AlgaeIntakeCommand extends Command {
    private enum State {
        INTAKING,   // Searching for and intaking game piece
        STOWING     // Game piece acquired, raising arm
    }

    private final SwerveSubsystem m_drive;
    private final ArmSubsystem m_arm;
    private final RollerSubsystem m_roller;
    private final DoubleSupplier m_forwardSupplier;

    private final PIDController strafePID;
    private final PIDController rotationPID;

    private State m_state = State.INTAKING;
    private int m_currentSpikeCount = 0;
    private static final int SPIKE_THRESHOLD_CYCLES = 3; // Debounce cycles

    public AlgaeIntakeCommand(
            SwerveSubsystem drive,
            ArmSubsystem arm,
            RollerSubsystem roller,
            DoubleSupplier forwardSupplier) {
        m_drive = drive;
        m_arm = arm;
        m_roller = roller;
        m_forwardSupplier = forwardSupplier;

        strafePID = new PIDController(
            LimelightConstants.STRAFE_KP,
            LimelightConstants.STRAFE_KI,
            LimelightConstants.STRAFE_KD
        );

        rotationPID = new PIDController(
            LimelightConstants.ROTATION_KP,
            LimelightConstants.ROTATION_KI,
            LimelightConstants.ROTATION_KD
        );

        // Set tolerances
        strafePID.setTolerance(LimelightConstants.TX_TOLERANCE);
        rotationPID.setTolerance(LimelightConstants.TX_TOLERANCE);

        System.out.println("Starting algae intake command!");

        addRequirements(drive, arm, roller);
    }

    @Override
    public void initialize() {
        m_state = State.INTAKING;
        m_currentSpikeCount = 0;

        // Set Limelight to algae detection pipeline
        LimelightHelpers.setPipeline(LimelightConstants.LIMELIGHT_NAME, LimelightConstants.ALGAE_PIPELINE);
        LimelightHelpers.setLEDMode(LimelightConstants.LIMELIGHT_NAME, 3); // LEDs on

        // Reset PID controllers
        strafePID.reset();
        rotationPID.reset();
    }

    @Override
    public void execute() {
        switch (m_state) {
            case INTAKING:
                executeIntaking();
                break;
            case STOWING:
                executeStowing();
                break;
        }
    }

    private void executeIntaking() {
        // --- Vision Alignment ---
        double forwardInput = m_forwardSupplier.getAsDouble();
        // Apply deadband to forward input
        forwardInput = MathUtil.applyDeadband(forwardInput, OperatorConstants.DEADBAND);
        double forwardSpeed = forwardInput * SwerveConstants.MAX_SPEED;

        double strafeSpeed = 0.0;
        double rotationSpeed = 0.0;

        boolean hasTarget = LimelightHelpers.getTV(LimelightConstants.LIMELIGHT_NAME);

        if (hasTarget) {
            double tx = LimelightHelpers.getTX(LimelightConstants.LIMELIGHT_NAME);

            // Calculate strafe and rotation from vision
            // tx > 0 means target is to the right, need to strafe right (negative in field coords)
            strafeSpeed = -strafePID.calculate(tx, 0) * SwerveConstants.MAX_SPEED;
            rotationSpeed = -rotationPID.calculate(tx, 0);

            // Clamp speeds for safety
            strafeSpeed = MathUtil.clamp(strafeSpeed, -2.0, 2.0);
            rotationSpeed = MathUtil.clamp(rotationSpeed, -2.0, 2.0);
        }
        // If no target, strafe and rotation stay at 0 (driver still controls forward/backward)

        // Apply chassis speeds (field-oriented)
        m_drive.driveFieldOriented(new ChassisSpeeds(forwardSpeed, strafeSpeed, rotationSpeed));

        // --- Arm Control ---
        m_arm.setPosition(ArmConstants.ARM_ALGAE_INTAKE_ANGLE);
/* 
        // --- Roller Control ---
        m_roller.runRoller(RollerConstants.ROLLER_ALGAE_INTAKE_SPEED);

        // --- Game Piece Detection with Debounce ---
        if (m_roller.getOutputCurrent() > RollerConstants.ALGAE_DETECTION_CURRENT_THRESHOLD) {
            m_currentSpikeCount++;
        } else {
            m_currentSpikeCount = 0;
        }

        if (m_currentSpikeCount >= SPIKE_THRESHOLD_CYCLES) {
            // Game piece acquired, transition to stowing
            m_state = State.STOWING;
        }*/
    }

    private void executeStowing() {
        // Stop driving - let driver take back control after command ends
        m_drive.drive(new ChassisSpeeds(0, 0, 0));

        // Raise arm to stow position
        m_arm.setPosition(ArmConstants.ARM_STOW_ANGLE);

        // Stop the rollers now that we have the game piece
        m_roller.stop();
    }

    @Override
    public void end(boolean interrupted) {
        // Stop all mechanisms
        m_drive.drive(new ChassisSpeeds(0, 0, 0));
        m_arm.stop();
        m_roller.stop();

        // Reset Limelight LEDs to pipeline default
        LimelightHelpers.setLEDMode(LimelightConstants.LIMELIGHT_NAME, 0);
    }

    @Override
    public boolean isFinished() {
        // Finish when in STOWING state and arm has reached stow position
        return m_state == State.STOWING &&
               m_arm.isAtPosition(ArmConstants.ARM_STOW_ANGLE, ArmConstants.ARM_POSITION_TOLERANCE);
    }
}
