package frc.robot;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.commands.ArmUpTimedCommand;
import frc.robot.commands.ArmDownTimedCommand;
import frc.robot.commands.CoralOutTimedCommand;
import frc.robot.commands.AlgieInTimedCommand;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.RollerSubsystem;

public class Autos {
    private final RollerSubsystem m_roller;
    private SendableChooser<Command> autoChooser;
    
    public Autos(RollerSubsystem roll, ArmSubsystem arm) {
        m_roller = roll;
        // Named Commands //
        NamedCommands.registerCommand("coralOut", new CoralOutTimedCommand(m_roller, 5));
        NamedCommands.registerCommand("holdArm", new ArmUpTimedCommand(arm, 0.1));
        NamedCommands.registerCommand("armUp", new ArmUpTimedCommand(arm, 1));
        NamedCommands.registerCommand("armTouchUp", new ArmUpTimedCommand(arm, 0.2));
        NamedCommands.registerCommand("armDown", new ArmDownTimedCommand(arm, 1));
        NamedCommands.registerCommand("algieIn", new AlgieInTimedCommand(m_roller, 1));

        // Autos //
        autoChooser = new SendableChooser<Command>();
        autoChooser.addOption("Score Two Coral", AutoBuilder.buildAuto("scoreTwoCoralEFKLSides"));
<<<<<<< Updated upstream
        autoChooser.addOption("Drive Forward", AutoBuilder.buildAuto("DriveForward"));
        autoChooser.addOption("Score One Coral From Side Start", AutoBuilder.buildAuto("AutoThatWillWorkWith6045"));
=======
        autoChooser.addOption("[LEFT] Drive Forward", AutoBuilder.buildAuto("DriveForward-Left"));
        autoChooser.addOption("[MIDDLE] Drive Forward", AutoBuilder.buildAuto("DriveForward"));
        autoChooser.addOption("[RIGHT] Drive Forward", AutoBuilder.buildAuto("DriveForward-Right"));
        autoChooser.addOption("[LEFT] Drive forward and knock down algae", AutoBuilder.buildAuto("scoreOneCoralAndDropAlgae-left"));
        autoChooser.addOption("[MIDDLE] Drive forward and knock down algae", AutoBuilder.buildAuto("scoreOneCoralAndDropAlgae"));
        autoChooser.addOption("[RIGHT] Drive forward and knock down algae", AutoBuilder.buildAuto("scoreOneCoralAndDropAlgae-right"));
        //autoChooser.addOption("[LEFT] Move off of line", AutoBuilder.buildAuto("moveOffOfLine-left"));
        autoChooser.addOption("[ANY] Move off of line", AutoBuilder.buildAuto("moveOffOfLine"));
        //autoChooser.addOption("[RIGHT] Move off of line", AutoBuilder.buildAuto("moveOffOfLine-right"));
        autoChooser.addOption("[LEFT] Score two coral", AutoBuilder.buildAuto("scoreTwoCoral-left"));
        autoChooser.addOption("[MIDDLE] Score two coral", AutoBuilder.buildAuto("scoreTwoCoral"));
        autoChooser.addOption("[RIGHT] Score two coral", AutoBuilder.buildAuto("scoreTwoCoral-right"));
        autoChooser.addOption("[LEFT] Score three coral", AutoBuilder.buildAuto("scoreThreeCoral-left"));
        autoChooser.addOption("[MIDDLE] Score three coral", AutoBuilder.buildAuto("scoreThreeCoral"));
        autoChooser.addOption("[RIGHT] Score three coral", AutoBuilder.buildAuto("scoreThreeCoral-right"));
        autoChooser.addOption("[LEFT] Knock down algae and score one coral", AutoBuilder.buildAuto("dropAlgaeAndScoreOneCoral-left"));
        autoChooser.addOption("[MIDDLE] Knock down algae and score one coral", AutoBuilder.buildAuto("dropAlgaeAndScoreOneCoral"));
        autoChooser.addOption("[RIGHT] Knock down algae and score one coral", AutoBuilder.buildAuto("dropAlgaeAndScoreOneCoral-right"));
        autoChooser.addOption("[FAR RIGHT] (TOOLCATS) Score one coral and pick up lollipop", AutoBuilder.buildAuto("scoreOneCoralPickUpLollipop"));
>>>>>>> Stashed changes

        SmartDashboard.putData("autos", autoChooser);
    }

    public Command getAutonomousCommand() {
        return autoChooser.getSelected();
    }
}
