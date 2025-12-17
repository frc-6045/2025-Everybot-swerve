package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.RollerConstants;

public class RollerSubsystem extends SubsystemBase {

    private final SparkMax rollerMotor;
    /**
     * This subsytem that controls the roller.
     */
    public RollerSubsystem () {

    // Set up the roller motor as a brushed motor
    rollerMotor = new SparkMax(RollerConstants.ROLLER_MOTOR_ID, MotorType.kBrushless);

    // Set can timeout. Because this project only sets parameters once on
    // construction, the timeout can be long without blocking robot operation. Code
    // which sets or gets parameters during operation may need a shorter timeout.
    rollerMotor.setCANTimeout(250);

    // Create and apply configuration for roller motor. Voltage compensation helps
    // the roller behave the same as the battery
    // voltage dips. The current limit helps prevent breaker trips or burning out
    // the motor in the event the roller stalls.
    SparkMaxConfig rollerConfig = new SparkMaxConfig();
    rollerConfig.voltageCompensation(RollerConstants.ROLLER_MOTOR_VOLTAGE_COMP);
    rollerConfig.smartCurrentLimit(RollerConstants.ROLLER_MOTOR_CURRENT_LIMIT);
    rollerConfig.idleMode(IdleMode.kBrake);
    rollerMotor.configure(rollerConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    }

    @Override
    public void periodic() {
        // Log motor current for calibration - CRITICAL for algae pickup detection!
        double current = getRollerCurrent();
        SmartDashboard.putNumber("Roller/Motor Current (Amps)", current);
        SmartDashboard.putNumber("Roller/Motor Speed", rollerMotor.get());

        // Show configured speeds for reference
        SmartDashboard.putNumber("Roller/Algae In Speed", RollerConstants.ROLLER_ALGAE_IN);
        SmartDashboard.putNumber("Roller/Algae Out Speed", RollerConstants.ROLLER_ALGAE_OUT);

        // CALIBRATION HELPER: Shows what the current drop detection would trigger at
        SmartDashboard.putNumber("Roller/CALIBRATE: Baseline Current", RollerConstants.ROLLER_BASELINE_CURRENT);
        SmartDashboard.putNumber("Roller/CALIBRATE: Drop Threshold", RollerConstants.ROLLER_CURRENT_DROP_THRESHOLD);
        SmartDashboard.putBoolean("Roller/CALIBRATE: Would Trigger Pickup",
            hasCurrentDropped(RollerConstants.ROLLER_BASELINE_CURRENT, RollerConstants.ROLLER_CURRENT_DROP_THRESHOLD));
    }

    /**
     *  This is a method that makes the roller spin to your desired speed.
     *  Positive values make it spin forward and negative values spin it in reverse.
     *
     * @param speedmotor speed from -1.0 to 1, with 0 stopping it
     */
    public void runRoller(double speed){
        rollerMotor.set(speed);
    }

    /**
     * Get the current draw of the roller motor in amps
     * Can be used to detect when algae has been picked up (current drops)
     *
     * @return current in amps
     */
    public double getRollerCurrent() {
        return rollerMotor.getOutputCurrent();
    }

    /**
     * Check if current has dropped significantly, indicating algae pickup
     * This detects when the roller has less resistance (algae successfully grabbed)
     *
     * @param baselineCurrent the normal operating current when running
     * @param dropThreshold how much current must drop (in amps)
     * @return true if current dropped below threshold
     */
    public boolean hasCurrentDropped(double baselineCurrent, double dropThreshold) {
        return (baselineCurrent - getRollerCurrent()) > dropThreshold;
    }

}