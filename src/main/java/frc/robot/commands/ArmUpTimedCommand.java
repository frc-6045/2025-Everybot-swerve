// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Constants.ArmConstants;
import frc.robot.subsystems.ArmSubsystem;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;

/** An ArmUpCommand that uses an Arm subsystem. */
public class ArmUpTimedCommand extends Command {
  private final ArmSubsystem m_arm;
  private final Timer m_Timer = new Timer();
  private double m_time;

  /**
   * Powers the arm up, when finished passively holds the arm up.
   * 
   * We recommend that you use this to only move the arm into the hardstop
   * and let the passive portion hold the arm up.
   *
   * @param arm The subsystem used by this command.
   */
  public ArmUpTimedCommand(ArmSubsystem arm, double time) {
    m_arm = arm;
    m_time = time;
    addRequirements(arm);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_Timer.start();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    m_arm.runArm(ArmConstants.ARM_SPEED_UP);
  }

  // Called once the command ends or is interrupted.
  // Here we run a command that will hold the arm up after to ensure the arm does
  // not drop due to gravity.
  @Override
  public void end(boolean interrupted) {
    m_arm.runArm(ArmConstants.ARM_HOLD_UP);
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
