package frc.robot.subsystems;

import com.revrobotics.spark.SparkAbsoluteEncoder;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ArmConstants;

public class ArmSubsystem extends SubsystemBase {

    private final SparkMax armMotor;
    private final SparkAbsoluteEncoder absoluteEncoder;
    private final SparkClosedLoopController closedLoopController;

    /**
     * This subsytem that controls the arm.
     */
    public ArmSubsystem () {

    // Set up the arm motor as a brushless motor
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
    armConfig.voltageCompensation(ArmConstants.ARM_MOTOR_VOLTAGE_COMP);
    armConfig.smartCurrentLimit(ArmConstants.ARM_MOTOR_CURRENT_LIMIT);
    armConfig.idleMode(IdleMode.kBrake);

    // Configure absolute encoder
    armConfig.absoluteEncoder
        .positionConversionFactor(1.0) // 1 rotation = 1.0
        .velocityConversionFactor(1.0)
        .inverted(false); // Adjust based on physical setup

    // Configure closed-loop control using the absolute encoder
    armConfig.closedLoop
        .feedbackSensor(FeedbackSensor.kAbsoluteEncoder)
        .pid(ArmConstants.ARM_POSITION_KP, ArmConstants.ARM_POSITION_KI, ArmConstants.ARM_POSITION_KD)
        .outputRange(-ArmConstants.ARM_MAX_OUTPUT, ArmConstants.ARM_MAX_OUTPUT);

    armMotor.configure(armConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    // Get encoder and controller references
    absoluteEncoder = armMotor.getAbsoluteEncoder();
    closedLoopController = armMotor.getClosedLoopController();
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Arm/Position", getPosition());
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
     * Gets the current position from the absolute encoder.
     * @return Position in rotations (0-1)
     */
    public double getPosition() {
        return absoluteEncoder.getPosition();
    }

    /**
     * Sets the arm to a target position using closed-loop control.
     * @param targetPosition Target position in rotations
     */
    public void setPosition(double targetPosition) {
        closedLoopController.setReference(targetPosition, ControlType.kPosition);
    }

    /**
     * Checks if the arm is at the target position within tolerance.
     * @param targetPosition Target position to check against
     * @param tolerance Acceptable error in rotations
     * @return true if within tolerance
     */
    public boolean isAtPosition(double targetPosition, double tolerance) {
        return Math.abs(getPosition() - targetPosition) < tolerance;
    }

    /**
     * Stops the arm motor.
     */
    public void stop() {
        armMotor.set(0);
    }
}