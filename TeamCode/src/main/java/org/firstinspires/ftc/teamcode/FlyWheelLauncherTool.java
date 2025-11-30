package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class FlyWheelLauncherTool {
    private int type = 0;
    // Team 1
    private DcMotor motorFly;
    private CRServo S1, S2;
    private Servo S3;

    private boolean rotating;
    

    public FlyWheelLauncherTool(HardwareMap hardwareMap, int type) {
        if (type == 1){
            this.type = 1;
            initializeTool1(hardwareMap);
        } else if (type == 2){
            this.type = 2;
        } else {
            this.type = 0;
        }
    }

    private void initializeTool1(HardwareMap hardwareMap) {
        motorFly = hardwareMap.get(DcMotor.class, "motorFly"); // Shooter // 
        S1 = hardwareMap.get(CRServo.class, "S1"); // 1
        S2 = hardwareMap.get(CRServo.class, "S2"); // 2
        S3 = hardwareMap.get(Servo.class, "S3"); // 3
    }

    public void launcherControl(Gamepad gamepad) {
        if (type == 0){

        } else if (type == 1){
            if (gamepad.a){
                S1.setPower(1);
                S2.setPower(1);
            } else {
                S1.setPower(0);
                S2.setPower(0);
            }
            if (gamepad.b){
                S3.setPower(1);
            } else {
                S3.setPower(0);
            }
            if (gamepad.right_trigger){
                motorFly.setPower(1);
            } else {
                motorFly.setPower(0);
            }
        } else if (type == 2){

        }
    }
}
