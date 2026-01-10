package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
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
    private boolean lastChopstick = false;
    private final double SERVO_DOWN = 0.6; //Adjusting bounds.
    private final double SERVO_UP = 0.2;
    private double FLY_SPEED;
    private ElapsedTime servoTimer = new ElapsedTime();

    public FlyWheelLauncherTool(HardwareMap hardwareMap, int type) {
        if (type == 1){
            this.type = 1;
            initializeTool1(hardwareMap);
            FLY_SPEED = 0.75;
        } else if (type == 2){
            this.type = 2;
            initializeTool1(hardwareMap);
            FLY_SPEED = 1.0;
            motorFly.setDirection(DcMotorSimple.Direction.REVERSE);
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
        S3.setPosition(SERVO_DOWN);
    }

    public void launcherControl(Gamepad gamepad) {
        if (type == 0){
        } else if (type == 1 || type == 2){
            if (gamepad.left_bumper){
                S1.setPower(0.5);
                S2.setPower(0.5);
            } else if (gamepad.a) {
                S1.setPower(-0.5);
                S2.setPower(-0.5);
            } else {
                S1.setPower(0);
                S2.setPower(0);
            }
            boolean chopstick = gamepad.left_trigger > 0.8;
            if (chopstick && !lastChopstick && !servoActive) {
                S3.setPosition(SERVO_UP);
                servoTimer.reset();
                servoActive = true;
            }
            lastChopstick = chopstick;
            if (servoActive && servoTimer.seconds() >= 1.0) {
                S3.setPosition(SERVO_DOWN);
                servoActive = false;
            }

            if (gamepad.right_bumper) {
                motorFly.setPower(FLY_SPEED);
            } else {
                motorFly.setPower(0);
            }
        }
    }
    public void autoShoot(int x){
        for(int y = x; y<3; y++){
            s1.setPower(0.5);
            s1.setPower(0.5);
            delay(750);
            motorFly.setPower(0.5);
            delay(100);
            motorFly.setPower(0.5);
            s3.setPosition(SERVO_UP);
            delay(1000);
            s3.setPosition(SERVO_DOWN);
            delay(1000);
        }
    }
}
