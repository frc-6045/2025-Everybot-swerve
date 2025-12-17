package frc.robot.subsystems;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LimelightHelpersTest {

    @BeforeEach
    void setUp() {
        // Initialize HAL for NetworkTables
        HAL.initialize(500, 0);
        // Start NetworkTables in test mode
        NetworkTableInstance.getDefault().startLocal();
    }

    @AfterEach
    void tearDown() {
        NetworkTableInstance.getDefault().stopLocal();
    }

    @Test
    void getTX_returnsDefaultWhenNoData() {
        // When no data is set, should return the default value (0.0)
        // Use a unique table name to avoid interference from other tests
        double tx = LimelightHelpers.getTX("limelight-test-tx");
        assertEquals(0.0, tx, 0.001);
    }

    @Test
    void getTY_returnsDefaultWhenNoData() {
        // When no data is set, should return the default value (0.0)
        double ty = LimelightHelpers.getTY("limelight-test-ty");
        assertEquals(0.0, ty, 0.001);
    }

    @Test
    void getTA_returnsZeroWhenNoData() {
        double ta = LimelightHelpers.getTA("limelight");
        assertEquals(0.0, ta, 0.001);
    }

    @Test
    void getTV_returnsFalseWhenNoData() {
        boolean tv = LimelightHelpers.getTV("limelight");
        assertFalse(tv);
    }

    @Test
    void getTX_returnsSetValue() {
        // Simulate Limelight data
        NetworkTableInstance.getDefault()
            .getTable("limelight")
            .getEntry("tx")
            .setDouble(15.5);

        double tx = LimelightHelpers.getTX("limelight");
        assertEquals(15.5, tx, 0.001);
    }

    @Test
    void getTY_returnsSetValue() {
        NetworkTableInstance.getDefault()
            .getTable("limelight")
            .getEntry("ty")
            .setDouble(-10.2);

        double ty = LimelightHelpers.getTY("limelight");
        assertEquals(-10.2, ty, 0.001);
    }

    @Test
    void getTV_returnsTrueWhenTargetPresent() {
        NetworkTableInstance.getDefault()
            .getTable("limelight")
            .getEntry("tv")
            .setDouble(1.0);

        boolean tv = LimelightHelpers.getTV("limelight");
        assertTrue(tv);
    }

    @Test
    void getTV_returnsFalseWhenNoTarget() {
        NetworkTableInstance.getDefault()
            .getTable("limelight")
            .getEntry("tv")
            .setDouble(0.0);

        boolean tv = LimelightHelpers.getTV("limelight");
        assertFalse(tv);
    }

    @Test
    void setPipeline_setsValue() {
        LimelightHelpers.setPipeline("limelight", 2);

        double pipeline = NetworkTableInstance.getDefault()
            .getTable("limelight")
            .getEntry("pipeline")
            .getDouble(-1);

        assertEquals(2.0, pipeline, 0.001);
    }

    @Test
    void setLEDMode_setsValue() {
        LimelightHelpers.setLEDMode("limelight", 3);

        double ledMode = NetworkTableInstance.getDefault()
            .getTable("limelight")
            .getEntry("ledMode")
            .getDouble(-1);

        assertEquals(3.0, ledMode, 0.001);
    }

    @Test
    void setCamMode_setsValue() {
        LimelightHelpers.setCamMode("limelight", 1);

        double camMode = NetworkTableInstance.getDefault()
            .getTable("limelight")
            .getEntry("camMode")
            .getDouble(-1);

        assertEquals(1.0, camMode, 0.001);
    }

    @Test
    void differentLimelightNames_usesDifferentTables() {
        NetworkTableInstance.getDefault()
            .getTable("limelight-front")
            .getEntry("tx")
            .setDouble(5.0);

        NetworkTableInstance.getDefault()
            .getTable("limelight-back")
            .getEntry("tx")
            .setDouble(-5.0);

        assertEquals(5.0, LimelightHelpers.getTX("limelight-front"), 0.001);
        assertEquals(-5.0, LimelightHelpers.getTX("limelight-back"), 0.001);
    }
}
