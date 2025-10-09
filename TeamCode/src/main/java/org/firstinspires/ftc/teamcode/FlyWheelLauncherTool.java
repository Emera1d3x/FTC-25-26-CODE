package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

public class FlyWheelLauncherTool {
    private DcMotor motorA;
    private DcMotor motorB;
    private boolean rotating;

    public FlyWheelLauncherTool(DcMotor motorA, DcMotor motorB) {
        this.motorA = motorA;
        this.motorB = motorB;
    }

    public void rotate(){
        if (rotating){
            // pre calculations of trajectory, angle, power, & all that stuff
            // will probably have constants within CalibrationTool
            motorA.setPower(1);
            motorA.setPower(-1);
        } else {
            motorA.setPower(0);
            motorB.setPower(0);
        }
        rotating = !rotating;
    }
}
