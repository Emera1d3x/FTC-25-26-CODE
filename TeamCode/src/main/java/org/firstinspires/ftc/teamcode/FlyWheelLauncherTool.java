package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class FlyWheelLauncherTool {
    private final DcMotor motor;
    private boolean rotating;

    public FlyWheelLauncherTool(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotor.class, "flywheelMotor");
    }

    public void rotate(boolean rotating) {
        this.rotating = rotating;

        motor.setPower(rotating ? 1.0 : 0.0);
    }

    public void rotate() {
        rotating = !rotating;
    }
}
