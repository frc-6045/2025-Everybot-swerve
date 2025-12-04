// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.AlgieInCommand;
import frc.robot.commands.AlgieOutCommand;
import frc.robot.commands.ArmDownCommand;
import frc.robot.commands.ArmUpCommand;
import frc.robot.commands.ClimberDownCommand;
import frc.robot.commands.ClimberUpCommand;
import frc.robot.commands.CoralOutCommand;
import frc.robot.commands.CoralStackCommand;
import frc.robot.commands.TrackTargetCommand;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.ClimberSubsystem;
import frc.robot.subsystems.HybridVisionSubsystem;
import frc.robot.subsystems.HybridVisionSubsystem.VisionMode;
import frc.robot.subsystems.RollerSubsystem;
import frc.robot.subsystems.SwerveSubsystem;
import frc.robot.Autos;

import java.io.File;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {

  // The robot's subsystems and commands are defined here...
  // Replace with CommandPS4Controller or CommandJoystick if needed
  private final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.DRIVER_CONTROLLER_PORT);
  // You can remove this if you wish to have a single driver, note that you
  // may have to change the binding for left bumper.
  private final CommandXboxController m_operatorController = 
      new CommandXboxController(OperatorConstants.OPERATOR_CONTROLLER_PORT);

  private final CommandXboxController m_godController = new CommandXboxController(OperatorConstants.GOD_CONTROLLER_PORT);

  // The autonomous chooser
  SendableChooser<Command> m_chooser = new SendableChooser<>();
  private Autos m_autos;

  public final RollerSubsystem m_roller = new RollerSubsystem();
  public final ArmSubsystem m_arm = new ArmSubsystem();
  public final SwerveSubsystem m_drive = new SwerveSubsystem(new File(Filesystem.getDeployDirectory(),
  "swerve/neo"));
  public final ClimberSubsystem m_climber = new ClimberSubsystem();
  public final HybridVisionSubsystem m_vision = new HybridVisionSubsystem();

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    m_autos = new Autos(m_roller);
    // Set up command bindings
    configureBindings();
    Bindings.initBindings(m_drive, m_driverController);
    // Set the options to show up in the Dashboard for selecting auto modes. If you
    // add additional auto modes you can add additional lines here with
    // autoChooser.addOption
    //m_chooser.addOption("Coral Auto", m_simpleCoralAuto);
    SmartDashboard.putData(m_chooser);
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
   * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {

    m_driverController.start().onTrue(Commands.runOnce(() -> m_drive.zeroGyroWithAlliance()));

    /**
     * Here we declare all of our operator commands, these commands could have been
     * written in a more compact manner but are left verbose so the intent is clear.
     */
    m_driverController.rightBumper().whileTrue(new AlgieInCommand(m_roller));
    
    // Here we use a trigger as a button when it is pushed past a certain threshold
    m_driverController.rightTrigger(.2).whileTrue(new AlgieOutCommand(m_roller));

    /**
     * The arm will be passively held up or down after this is used,
     * make sure not to run the arm too long or it may get upset!
     */
    m_operatorController.leftBumper().whileTrue(new ArmUpCommand(m_arm));
    m_operatorController.leftTrigger(.2).whileTrue(new ArmDownCommand(m_arm));



//zero gyro
    m_driverController.a().onTrue((Commands.runOnce(m_drive::zeroGyro)));



    /**
     * Used to score coral, the stack command is for when there is already coral
     * in L1 where you are trying to score. The numbers may need to be tuned, 
     * make sure the rollers do not wear on the plastic basket.
     */
    m_driverController.x().whileTrue(new CoralOutCommand(m_roller));
    m_driverController.y().whileTrue(new CoralStackCommand(m_roller));

    /**
     * POV is a direction on the D-Pad or directional arrow pad of the controller,
     * the direction of this will be different depending on how your winch is wound
     */
    m_operatorController.pov(0).whileTrue(new ClimberUpCommand(m_climber));
    m_operatorController.pov(180).whileTrue(new ClimberDownCommand(m_climber));

    /**
     * VISION TRACKING - Uses hybrid vision system (Limelight + Coral)
     * Hold B button to automatically track and center on algae
     */
    m_driverController.b().whileTrue(new TrackTargetCommand(m_vision, m_drive, "algae"));

    /**
     * VISION MODE SWITCHING - Change how the vision system works
     * D-Pad Left: Limelight only (fastest)
     * D-Pad Right: Coral only (most accurate classification)
     * D-Pad Up: Fusion mode (uses both - DEFAULT)
     */
    m_driverController.pov(270).onTrue(
      Commands.runOnce(() -> m_vision.setVisionMode(VisionMode.LIMELIGHT_ONLY))
    );
    m_driverController.pov(90).onTrue(
      Commands.runOnce(() -> m_vision.setVisionMode(VisionMode.CORAL_ONLY))
    );
    m_driverController.pov(0).onTrue(
      Commands.runOnce(() -> m_vision.setVisionMode(VisionMode.FUSION))
    );



    /**
     * Here we declare all of our operator commands, these commands could have been
     * written in a more compact manner but are left verbose so the intent is clear.
     */
    m_godController.rightBumper().whileTrue(new AlgieInCommand(m_roller));
    
    // Here we use a trigger as a button when it is pushed past a certain threshold
    m_godController.rightTrigger(.2).whileTrue(new AlgieOutCommand(m_roller));

    /**
     * The arm will be passively held up or down after this is used,
     * make sure not to run the arm too long or it may get upset!
     */
    m_godController.leftBumper().whileTrue(new ArmUpCommand(m_arm));
    m_godController.leftTrigger(.2).whileTrue(new ArmDownCommand(m_arm));

    /**
     * Used to score coral, the stack command is for when there is already coral
     * in L1 where you are trying to score. The numbers may need to be tuned, 
     * make sure the rollers do not wear on the plastic basket.
     */
    m_godController.x().whileTrue(new CoralOutCommand(m_roller));
    m_godController.y().whileTrue(new CoralStackCommand(m_roller));

    /**
     * POV is a direction on the D-Pad or directional arrow pad of the controller,
     * the direction of this will be different depending on how your winch is wound
     */
    m_godController.pov(0).whileTrue(new ClimberUpCommand(m_climber));
    m_godController.pov(180).whileTrue(new ClimberDownCommand(m_climber));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
    public Command getAutonomousCommand() {
    // The selected command will be run in autonomous
    return m_autos.getAutonomousCommand();
  }
}