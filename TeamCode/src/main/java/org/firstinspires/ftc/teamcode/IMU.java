import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.Axis;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;

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

    public double getYaw(AngleUnit angleUnit) {
        return imu.getRobotYawPitchRollAngles().getYaw(angleUnit);
    }

    public double getRoll(AngleUnit angleUnit) {
        return imu.getRobotYawPitchRollAngles().getRoll(angleUnit);
    }
    public double getPitch(AngleUnit angleUnit) {
        return imu.getRobotYawPitchRollAngles().getPitch(angleUnit);
    }
    public double getAngularVelocity(Axis axis, AngleUnit angleUnit) {
        return imu.getRobotAngularVelocity().getRotationRate(axis, angleUnit);
    }
}