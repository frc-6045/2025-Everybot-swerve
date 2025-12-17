package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ArmConstants;

public class ArmSubsystem extends SubsystemBase {

    private final SparkMax armMotor;
    private final Encoder armEncoder;  // REV Through Bore Encoder (quadrature mode)

    /**
     * This subsytem that controls the arm.
     */
    public ArmSubsystem () {

    // Set up the arm motor as a brushed motor
    armMotor = new SparkMax(ArmConstants.ARM_MOTOR_ID, MotorType.kBrushless);

    // Set can timeout. Because this project only sets parameters once on
    // construction, the timeout can be long without blocking robot operation. Code
    // which sets or gets parameters during operation may need a shorter timeout.
    armMotor.setCANTimeout(250);

    // Create and apply configuration for arm motor. Voltage compensation helps
    // the arm behave the same as the battery
    // voltage dips. The current limit helps prevent breaker trips or burning out
    // the motor in the event the arm stalls.
    SparkMaxConfig armConfig = new SparkMaxConfig();
    armConfig.voltageCompensation(10);
    armConfig.smartCurrentLimit(ArmConstants.ARM_MOTOR_CURRENT_LIMIT);
    armConfig.idleMode(IdleMode.kBrake);
    armMotor.configure(armConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    // Initialize the REV Through Bore Encoder (quadrature mode - A/B channels)
    armEncoder = new Encoder(ArmConstants.ARM_ENCODER_CHANNEL_A, ArmConstants.ARM_ENCODER_CHANNEL_B);
    armEncoder.setDistancePerPulse(1.0 / 8192.0);  // REV Through Bore: 8192 pulses per rotation
    armEncoder.reset();  // Start at zero
    }

    @Override
    public void periodic() {
        // Log encoder position for calibration
        double position = getArmPosition();
        SmartDashboard.putNumber("Arm/Encoder Position (rotations)", position);
        SmartDashboard.putNumber("Arm/Encoder Raw Count", armEncoder.get());

        // Show target positions for reference
        SmartDashboard.putNumber("Arm/Target Intake Position", ArmConstants.ARM_INTAKE_POSITION);
        SmartDashboard.putNumber("Arm/Target Safe Position", ArmConstants.ARM_SAFE_POSITION);

        // Show if arm is at target positions
        SmartDashboard.putBoolean("Arm/At Intake Position",
            atPosition(ArmConstants.ARM_INTAKE_POSITION, ArmConstants.ARM_POSITION_TOLERANCE));
        SmartDashboard.putBoolean("Arm/At Safe Position",
            atPosition(ArmConstants.ARM_SAFE_POSITION, ArmConstants.ARM_POSITION_TOLERANCE));
    }
    /**
     * This is a method that makes the arm move at your desired speed
     *  Positive values make it spin forward and negative values spin it in reverse
     *
     * @param speed motor speed from -1.0 to 1, with 0 stopping it
     */
    public void runArm(double speed){
        armMotor.set(speed);
    }

    /**
     * Get the current encoder position of the arm
     * Returns position in rotations
     *
     * @return encoder position in rotations
     */
    public double getArmPosition() {
        return armEncoder.getDistance();  // Returns rotations (configured with setDistancePerPulse)
    }

    /**
     * Reset the arm encoder to zero at current position
     */
    public void resetEncoder() {
        armEncoder.reset();
    }

    /**
     * Check if the arm is at the target position within tolerance
     *
     * @param targetPosition target encoder position in rotations
     * @param tolerance acceptable error in rotations
     * @return true if within tolerance
     */
    public boolean atPosition(double targetPosition, double tolerance) {
        return Math.abs(getArmPosition() - targetPosition) < tolerance;
    }
}