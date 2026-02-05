// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Constants.RollerConstants;
import frc.robot.subsystems.RollerSubsystem;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;

/** A command to take Algae into the robot. */
public class AlgieInTimedCommand extends Command {
  private final RollerSubsystem m_roller;
  private final Timer m_Timer = new Timer();
  private double m_time;

  /**
   * Rolls Algae into the intake.
   *
   * @param roller The subsystem used by this command.
   */
  public AlgieInTimedCommand(RollerSubsystem roller, double time) {
    m_roller = roller;
    m_time = time;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(roller);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_Timer.start();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    m_roller.runRoller(RollerConstants.ROLLER_ALGAE_IN);
  }

  // Called once the command ends or is interrupted. This ensures the roller is not running when not intented.
  @Override
  public void end(boolean interrupted) {
    m_roller.runRoller(0);
    m_Timer.stop();
    m_Timer.reset();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if (m_Timer.get()>m_time) return true;
    return false;
  }
}
