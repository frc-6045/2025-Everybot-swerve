package frc.robot;

import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.LimelightConstants;
import frc.robot.Constants.RollerConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstantsTest {

    @Test
    void rollerConstants_currentThresholdIsReasonable() {
        // Current threshold should be between 20-50 amps for game piece detection
        assertTrue(RollerConstants.ALGAE_DETECTION_CURRENT_THRESHOLD >= 20.0,
            "Current threshold too low, may cause false positives");
        assertTrue(RollerConstants.ALGAE_DETECTION_CURRENT_THRESHOLD <= 50.0,
            "Current threshold too high, may not trigger");
    }

    @Test
    void rollerConstants_intakeSpeedIsNegative() {
        // Intake speed should be negative based on motor direction
        assertTrue(RollerConstants.ROLLER_ALGAE_INTAKE_SPEED < 0,
            "Algae intake speed should be negative");
    }

    @Test
    void rollerConstants_intakeSpeedWithinBounds() {
        assertTrue(RollerConstants.ROLLER_ALGAE_INTAKE_SPEED >= -1.0,
            "Intake speed should be >= -1.0");
        assertTrue(RollerConstants.ROLLER_ALGAE_INTAKE_SPEED <= 1.0,
            "Intake speed should be <= 1.0");
    }

    @Test
    void armConstants_intakeAngleWithinEncoderRange() {
        // Absolute encoder typically outputs 0-1 for one rotation
        assertTrue(ArmConstants.ARM_ALGAE_INTAKE_ANGLE >= 0.0,
            "Arm angle should be >= 0");
        assertTrue(ArmConstants.ARM_ALGAE_INTAKE_ANGLE <= 1.0,
            "Arm angle should be <= 1.0 for absolute encoder");
    }

    @Test
    void armConstants_positionToleranceIsPositive() {
        assertTrue(ArmConstants.ARM_POSITION_TOLERANCE > 0,
            "Position tolerance must be positive");
    }

    @Test
    void armConstants_positionToleranceIsReasonable() {
        // Tolerance should be small but not too small
        assertTrue(ArmConstants.ARM_POSITION_TOLERANCE >= 0.005,
            "Tolerance too small, may never reach setpoint");
        assertTrue(ArmConstants.ARM_POSITION_TOLERANCE <= 0.1,
            "Tolerance too large, positioning will be imprecise");
    }

    @Test
    void armConstants_pidGainsAreNonNegative() {
        assertTrue(ArmConstants.ARM_POSITION_KP >= 0, "P gain should be non-negative");
        assertTrue(ArmConstants.ARM_POSITION_KI >= 0, "I gain should be non-negative");
        assertTrue(ArmConstants.ARM_POSITION_KD >= 0, "D gain should be non-negative");
    }

    @Test
    void armConstants_hasProportionalGain() {
        assertTrue(ArmConstants.ARM_POSITION_KP > 0,
            "Should have some P gain for position control");
    }

    @Test
    void armConstants_maxOutputIsReasonable() {
        assertTrue(ArmConstants.ARM_MAX_OUTPUT > 0, "Max output should be positive");
        assertTrue(ArmConstants.ARM_MAX_OUTPUT <= 1.0, "Max output should be <= 1.0");
    }

    @Test
    void limelightConstants_pipelineIsValid() {
        assertTrue(LimelightConstants.ALGAE_PIPELINE >= 0,
            "Pipeline should be non-negative");
        assertTrue(LimelightConstants.ALGAE_PIPELINE <= 9,
            "Pipeline should be <= 9 (Limelight supports 0-9)");
    }

    @Test
    void limelightConstants_pidGainsAreNonNegative() {
        assertTrue(LimelightConstants.STRAFE_KP >= 0, "Strafe P gain should be non-negative");
        assertTrue(LimelightConstants.STRAFE_KI >= 0, "Strafe I gain should be non-negative");
        assertTrue(LimelightConstants.STRAFE_KD >= 0, "Strafe D gain should be non-negative");
        assertTrue(LimelightConstants.ROTATION_KP >= 0, "Rotation P gain should be non-negative");
        assertTrue(LimelightConstants.ROTATION_KI >= 0, "Rotation I gain should be non-negative");
        assertTrue(LimelightConstants.ROTATION_KD >= 0, "Rotation D gain should be non-negative");
    }

    @Test
    void limelightConstants_toleranceIsPositive() {
        assertTrue(LimelightConstants.TX_TOLERANCE > 0,
            "TX tolerance should be positive");
    }

    @Test
    void limelightConstants_toleranceIsReasonable() {
        // Tolerance in degrees, should be small but achievable
        assertTrue(LimelightConstants.TX_TOLERANCE >= 0.5,
            "Tolerance too small, robot may oscillate");
        assertTrue(LimelightConstants.TX_TOLERANCE <= 10.0,
            "Tolerance too large, alignment will be imprecise");
    }

    @Test
    void limelightConstants_limelightNameIsNotEmpty() {
        assertNotNull(LimelightConstants.LIMELIGHT_NAME);
        assertFalse(LimelightConstants.LIMELIGHT_NAME.isEmpty(),
            "Limelight name should not be empty");
    }
}
