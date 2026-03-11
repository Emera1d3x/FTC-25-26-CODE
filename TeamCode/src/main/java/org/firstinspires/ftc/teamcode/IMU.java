import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.IMU;
import com.qualcomm.robotcore.hardware.HardwareMap;
public class IMU {
    private IMU imu;
    public IMU(HardwareMap hardwareMap){
        imu = hardwareMap.get(IMU.class,"imu");
        RevHubOrientationOnRobot imuOrientation = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP,RevHubOrientationOnRobot.UsbFacingDirection.Forward);
        imu.initialize(new IMU.Parameters(imuOrientation));
    }
    public double getHeading(int indicator, AngleUnit angleunit){
        if(indicator ==1){
            return imu.getRobotYawPitchRollAngles.getYaw(Angleunit.degrees);
        }
        else if(indicator ==2){
            return imu.getRobotYawPitchRollAngles.getRoll(Angleunit.degrees);
        }
        else if(indicator == 3){
            return imu.getRobotYawPitchRollAngles.getPitch(Angleunit.degrees);
        }
        else{
            break;
        }
    }
}