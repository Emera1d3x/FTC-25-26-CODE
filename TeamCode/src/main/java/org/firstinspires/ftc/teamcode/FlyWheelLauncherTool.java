package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class FlyWheelLauncherTool {
    private int type = 0;
    // Team 1
    private DcMotor motorFly;
    private CRServo S1, S2;
    private Servo S3;
    private boolean servoActive = false;
    private boolean lastB = false;
    private final double SERVO_DOWN = 0.5;//Adjusting bounds.
    private final double SERVO_UP = 0.1;
    private ElapsedTime servoTimer = new ElapsedTime();

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
        motorFly = hardwareMap.get(DcMotor.class, "motorFly"); // Shooter Pin: 0 (Expansion Hub)
        motorFly.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorFly.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        S1 = hardwareMap.get(CRServo.class, "S1"); // Pin: 0
        S2 = hardwareMap.get(CRServo.class, "S2"); // Pin: 1
        S3 = hardwareMap.get(Servo.class, "S3"); // Pin: 2
        S3.scaleRange(0.35, 0.8);
        S3.setPosition(SERVO_DOWN);
    }

    public void launcherControl(Gamepad gamepad) {
        if (type == 0){

        } else if (type == 1){
            if (gamepad.a){
                S1.setPower(0.5);
                S2.setPower(0.5);
            } else { S1.setPower(0); S2.setPower(0);}
            boolean bPressed = gamepad.b && !lastB;
            lastB = gamepad.b;
            if (bPressed && !servoActive) {
                S3.setPosition(SERVO_UP);
                servoTimer.reset();
                servoActive = true;
            }
            if (servoActive && servoTimer.seconds() >= 1.0) {
                S3.setPosition(SERVO_DOWN);
                servoActive = false;
            }
            if (gamepad.right_bumper){
                motorFly.setPower(1);
            } else { motorFly.setPower(0);}
        } else if (type == 2){

        }
    }
}
