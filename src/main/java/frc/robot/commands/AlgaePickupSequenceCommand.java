package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj.DataLogManager;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.RollerSubsystem;
import frc.robot.subsystems.HybridVisionSubsystem;
import frc.robot.subsystems.SwerveSubsystem;
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.RollerConstants;

/**
 * Comprehensive command for automated algae pickup
 *
 * Sequence:
 * 1. Move arm to intake position
 * 2. Start running intake roller
 * 3. Align robot with detected algae using Limelight
 * 4. Driver drives forward manually
 * 5. When motor current drops (algae picked up), stop intake
 *
 * This command handles steps 1-3, then monitors for current drop during driver control
 */
public class AlgaePickupSequenceCommand extends SequentialCommandGroup {

    /**
     * Creates the full algae pickup sequence
     *
     * @param arm the arm subsystem
     * @param roller the roller/intake subsystem
     * @param vision the hybrid vision subsystem
     * @param drive the swerve drive subsystem
     */
    public AlgaePickupSequenceCommand(
            ArmSubsystem arm,
            RollerSubsystem roller,
            HybridVisionSubsystem vision,
            SwerveSubsystem drive) {

        addCommands(
            // Log start of sequence
            new InstantCommand(() ->
                DataLogManager.log("AlgaePickupSequence: Starting automated algae pickup")),

            // Step 1: Move arm to intake position and align with target in parallel
            new ParallelCommandGroup(
                // Move arm to intake position
                new MoveArmToPositionCommand(arm, ArmConstants.ARM_INTAKE_POSITION)
                    .withTimeout(3.0),  // 3 second timeout for safety

                // Align robot with algae target
                new TrackTargetCommand(vision, drive, "algae")
                    .withTimeout(5.0)  // 5 second timeout
            ),

            // Step 2: Start intake roller
            new InstantCommand(() -> {
                roller.runRoller(RollerConstants.ROLLER_ALGAE_IN);
                DataLogManager.log("AlgaePickupSequence: Intake started, waiting for driver to drive forward");
            }),

            // Step 3: Wait for current drop indicating algae pickup
            // This monitors the roller current while driver manually drives forward
            new WaitUntilCommand(() -> {
                // Use calibrated values from Constants (see RollerConstants for calibration instructions)
                boolean pickedUp = roller.hasCurrentDropped(
                    RollerConstants.ROLLER_BASELINE_CURRENT,
                    RollerConstants.ROLLER_CURRENT_DROP_THRESHOLD
                );
                if (pickedUp) {
                    DataLogManager.log("AlgaePickupSequence: Current drop detected - algae picked up!");
                }
                return pickedUp;
            }).withTimeout(10.0),  // 10 second timeout for safety

            // Step 4: Stop intake and retract arm
            new InstantCommand(() -> {
                roller.runRoller(0);
                DataLogManager.log("AlgaePickupSequence: Intake stopped");
            }),

            // Move arm back to safe position
            new MoveArmToPositionCommand(arm, ArmConstants.ARM_SAFE_POSITION)
                .withTimeout(3.0),

            // Log completion
            new InstantCommand(() ->
                DataLogManager.log("AlgaePickupSequence: Sequence complete!"))
        );
    }
}
