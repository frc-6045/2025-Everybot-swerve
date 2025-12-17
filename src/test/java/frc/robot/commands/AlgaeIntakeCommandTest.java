package frc.robot.commands;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.Constants.LimelightConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AlgaeIntakeCommand logic.
 * Note: Full command tests require hardware simulation, so these tests focus on
 * the algorithmic components like PID calculations and state transitions.
 */
class AlgaeIntakeCommandTest {

    @BeforeEach
    void setUp() {
        HAL.initialize(500, 0);
        NetworkTableInstance.getDefault().startLocal();
    }

    @AfterEach
    void tearDown() {
        NetworkTableInstance.getDefault().stopLocal();
    }

    @Test
    void strafePID_calculatesCorrectOutput() {
        // Test the PID controller configuration matches what the command uses
        PIDController strafePID = new PIDController(
            LimelightConstants.STRAFE_KP,
            LimelightConstants.STRAFE_KI,
            LimelightConstants.STRAFE_KD
        );
        strafePID.setTolerance(LimelightConstants.TX_TOLERANCE);

        // PID calculate(measurement, setpoint) returns output to drive measurement toward setpoint
        // If measurement > setpoint, output is negative to reduce measurement
        // Target to the right (positive tx=10, setpoint=0) produces negative raw output
        double rawOutput = strafePID.calculate(10.0, 0);
        assertTrue(rawOutput < 0, "Positive tx should produce negative raw PID output");

        // Command negates this: strafeSpeed = -strafePID.calculate(tx, 0)
        double strafeSpeed = -rawOutput;
        assertTrue(strafeSpeed > 0, "After negation, should strafe right (positive)");

        // Target to the left (negative tx) should produce positive raw output
        rawOutput = strafePID.calculate(-10.0, 0);
        assertTrue(rawOutput > 0, "Negative tx should produce positive raw PID output");

        // Centered target should produce near-zero output
        rawOutput = strafePID.calculate(0.0, 0);
        assertEquals(0.0, rawOutput, 0.001, "Zero tx should produce zero output");
    }

    @Test
    void strafePID_atSetpointWhenWithinTolerance() {
        PIDController strafePID = new PIDController(
            LimelightConstants.STRAFE_KP,
            LimelightConstants.STRAFE_KI,
            LimelightConstants.STRAFE_KD
        );
        strafePID.setTolerance(LimelightConstants.TX_TOLERANCE);

        // Calculate to update internal state
        strafePID.calculate(1.0, 0); // Within typical tolerance of 2 degrees
        assertTrue(strafePID.atSetpoint(), "Should be at setpoint when within tolerance");

        strafePID.calculate(5.0, 0); // Outside tolerance
        assertFalse(strafePID.atSetpoint(), "Should not be at setpoint when outside tolerance");
    }

    @Test
    void rotationPID_calculatesCorrectOutput() {
        PIDController rotationPID = new PIDController(
            LimelightConstants.ROTATION_KP,
            LimelightConstants.ROTATION_KI,
            LimelightConstants.ROTATION_KD
        );

        // Same as strafe: calculate(measurement, setpoint) returns negative when measurement > setpoint
        // Target to the right (positive tx) produces negative raw output
        double rawOutput = rotationPID.calculate(15.0, 0);
        assertTrue(rawOutput < 0, "Positive tx should produce negative raw rotation PID output");

        // Command negates: rotationSpeed = -rotationPID.calculate(tx, 0)
        double rotationSpeed = -rawOutput;
        assertTrue(rotationSpeed > 0, "After negation, should rotate to face right");

        rawOutput = rotationPID.calculate(-15.0, 0);
        assertTrue(rawOutput > 0, "Negative tx should produce positive raw rotation PID output");
    }

    @Test
    void currentSpikeDebounce_requiresMultipleCycles() {
        // Simulate the debounce logic from the command
        int currentSpikeCount = 0;
        final int SPIKE_THRESHOLD_CYCLES = 3;
        final double CURRENT_THRESHOLD = 35.0;

        // First spike
        double currentReading = 40.0;
        if (currentReading > CURRENT_THRESHOLD) {
            currentSpikeCount++;
        } else {
            currentSpikeCount = 0;
        }
        assertFalse(currentSpikeCount >= SPIKE_THRESHOLD_CYCLES,
            "Should not detect game piece after 1 spike");

        // Second spike
        currentReading = 38.0;
        if (currentReading > CURRENT_THRESHOLD) {
            currentSpikeCount++;
        } else {
            currentSpikeCount = 0;
        }
        assertFalse(currentSpikeCount >= SPIKE_THRESHOLD_CYCLES,
            "Should not detect game piece after 2 spikes");

        // Third spike - should trigger
        currentReading = 42.0;
        if (currentReading > CURRENT_THRESHOLD) {
            currentSpikeCount++;
        } else {
            currentSpikeCount = 0;
        }
        assertTrue(currentSpikeCount >= SPIKE_THRESHOLD_CYCLES,
            "Should detect game piece after 3 consecutive spikes");
    }

    @Test
    void currentSpikeDebounce_resetsOnLowCurrent() {
        int currentSpikeCount = 0;
        final double CURRENT_THRESHOLD = 35.0;

        // Two spikes
        currentSpikeCount++;
        currentSpikeCount++;
        assertEquals(2, currentSpikeCount);

        // Current drops below threshold
        double currentReading = 20.0;
        if (currentReading > CURRENT_THRESHOLD) {
            currentSpikeCount++;
        } else {
            currentSpikeCount = 0;
        }
        assertEquals(0, currentSpikeCount, "Spike count should reset when current drops");
    }

    @Test
    void speedClamping_limitsOutput() {
        // Test the clamping logic used in the command
        double maxSpeed = 2.0;

        // Test upper bound
        double unclamped = 5.0;
        double clamped = Math.max(-maxSpeed, Math.min(maxSpeed, unclamped));
        assertEquals(maxSpeed, clamped, 0.001);

        // Test lower bound
        unclamped = -5.0;
        clamped = Math.max(-maxSpeed, Math.min(maxSpeed, unclamped));
        assertEquals(-maxSpeed, clamped, 0.001);

        // Test within bounds
        unclamped = 1.5;
        clamped = Math.max(-maxSpeed, Math.min(maxSpeed, unclamped));
        assertEquals(1.5, clamped, 0.001);
    }

    @Test
    void deadbandApplication_filtersSmallInputs() {
        double deadband = 0.1;

        // Input below deadband should be zeroed
        double input = 0.05;
        double result = Math.abs(input) < deadband ? 0 : input;
        assertEquals(0, result, 0.001);

        // Input above deadband should pass through
        input = 0.5;
        result = Math.abs(input) < deadband ? 0 : input;
        assertEquals(0.5, result, 0.001);

        // Negative input below deadband
        input = -0.05;
        result = Math.abs(input) < deadband ? 0 : input;
        assertEquals(0, result, 0.001);
    }
}
