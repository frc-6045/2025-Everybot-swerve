package frc.robot;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.commands.CoralOutTimedCommand;
import frc.robot.subsystems.RollerSubsystem;



public class Autos {
    private final RollerSubsystem m_roller;
    private SendableChooser<Command> autoChooser;

    public Autos(RollerSubsystem roll) {
        m_roller = roll;
        // Named Commands //
        NamedCommands.registerCommand("coralOut", new CoralOutTimedCommand(m_roller, 1));

        // Autos //
        autoChooser = new SendableChooser<Command>();
        autoChooser.addOption("select this auto!", AutoBuilder.buildAuto("scoreTwoCoralEFKLSides"));
        SmartDashboard.putData("autos", autoChooser);
    }

    public Command getAutonomousCommand() {
        return autoChooser.getSelected();
    }
}
