import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.Axis;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;

/**
 * Wrapper class for FTC IMU sensor with Road Runner integration
 */
public class IMU {
    private IMU imu;
    AngularVelocity angularVelocity;
    
    public IMU(HardwareMap hardwareMap) {
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot imuOrientation = new RevHubOrientationOnRobot(
            RevHubOrientationOnRobot.LogoFacingDirection.UP,
            RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
        );
        imu.initialize(new IMU.Parameters(imuOrientation));
    }

    /**
     * Get yaw angle in the specified unit
     */
    public double getYaw(AngleUnit angleUnit) {
        return imu.getRobotYawPitchRollAngles().getYaw(angleUnit);
    }

    /**
     * Get yaw angle in radians (for Road Runner)
     */
    public double getYawRadians() {
        return getYaw(AngleUnit.RADIANS);
    }

    /**
     * Get roll angle in the specified unit
     */
    public double getRoll(AngleUnit angleUnit) {
        return imu.getRobotYawPitchRollAngles().getRoll(angleUnit);
    }
    
    /**
     * Get pitch angle in the specified unit
     */
    public double getPitch(AngleUnit angleUnit) {
        return imu.getRobotYawPitchRollAngles().getPitch(angleUnit);
    }
    
    /**
     * Get angular velocity around specified axis
     */
    public double getAngularVelocity(Axis axis, AngleUnit angleUnit) {
        return imu.getRobotAngularVelocity().getRotationRate(axis, angleUnit);
    }

    /**
     * Get z-axis angular velocity in radians per second (for Road Runner)
     */
    public double getHeadingVelocity() {
        return imu.getRobotAngularVelocity(AngleUnit.RADIANS).zRotationRate;
    }

    /**
     * Reset IMU heading to zero
     */
    public void resetHeading() {
        imu.resetYaw();
    }
}