package frc.robot;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
//import frc.robot.Constants.ArmConstants;
//import frc.robot.commands.ArmUpCommand;
import frc.robot.commands.CoralOutTimedCommand;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.RollerSubsystem;

public class Autos {
    private final RollerSubsystem m_roller;
    //private final ArmSubsystem m_arm;
    private SendableChooser<Command> autoChooser;
    
    public Autos(RollerSubsystem roll) {
        m_roller = roll;
        //m_arm = arm;
        // Named Commands //
        NamedCommands.registerCommand("coralOut", new CoralOutTimedCommand(m_roller, 1));
        //NamedCommands.registerCommand("armUp", new m_arm.runArm(ArmConstants.ARM_HOLD_UP));

        // Autos //
        autoChooser = new SendableChooser<Command>();
        autoChooser.addOption("Score Two Coral", AutoBuilder.buildAuto("scoreTwoCoralEFKLSides"));
        autoChooser.addOption("Drive Forward", AutoBuilder.buildAuto("DriveForward"));

        SmartDashboard.putData("autos", autoChooser);
    }

    public Command getAutonomousCommand() {
        return autoChooser.getSelected();
    }
}
