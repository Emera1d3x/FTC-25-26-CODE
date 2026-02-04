package org.firstinspires.ftc.teamcode;

import static android.os.SystemClock.sleep;
import static org.firstinspires.ftc.teamcode.CalibrationTool.*;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class FlyWheelLauncherTool {
    private DcMotor motorFly;
    private CRServo S1, S2, S4;
    private Servo S3;
    private boolean servoActive = false;
    private boolean lastChopstick = false;
    private final double SERVO_DOWN = 0.6;
    private final double SERVO_UP = 0.1;
    private final double INTAKE_SPEED = 1;
    public double FLY_SPEED;
    private ElapsedTime servoTimer = new ElapsedTime();
    private boolean lastAdjButton = false;

    public FlyWheelLauncherTool(HardwareMap hardwareMap) {
        if (TEAM_NUMBER == 1){
            initializeTool1(hardwareMap);
        } else if (TEAM_NUMBER == 2){
            initializeTool2(hardwareMap);
        }
    }

    private void initializeTool1(HardwareMap hardwareMap) {
        motorFly = hardwareMap.get(DcMotor.class, "motorFly"); // Shooter Pin: 0 (Expansion Hub)
        motorFly.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorFly.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        S1 = hardwareMap.get(CRServo.class, "S1"); // Pin: 0
        S2 = hardwareMap.get(CRServo.class, "S2"); // Pin: 1
        S3 = hardwareMap.get(Servo.class, "S3"); // Pin: 2
        S3.setPosition(SERVO_DOWN);
        FLY_SPEED = 0.64;
    }

    private void initializeTool2(HardwareMap hardwareMap) {
        motorFly = hardwareMap.get(DcMotor.class, "motorFly"); // Shooter Pin: 0 (Expansion Hub)
        motorFly.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorFly.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        S1 = hardwareMap.get(CRServo.class, "S1"); // Pin: 0
        S2 = hardwareMap.get(CRServo.class, "S2"); // Pin: 1
        S4 = hardwareMap.get(Servo.class, "S4"); // Pin: 2
        FLY_SPEED = 1.0;
        motorFly.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void setIntake(boolean on) {
        if (on) {
            S1.setPower(INTAKE_SPEED);
            S2.setPower(INTAKE_SPEED);
        } else {
            S1.setPower(0);
            S2.setPower(0);
        }
    }

    public void setIntakeReverse(boolean on) {
        if (on) {
            S1.setPower(-INTAKE_SPEED);
            S2.setPower(-INTAKE_SPEED);
        } else {
            S1.setPower(0);
            S2.setPower(0);
        }
    }

    public void launcherControl(Gamepad gamepad){
        if (TEAM_NUMBER == 1){
            launcherControl1(gamepad);
        } else {
            launcherControl2(gamepad);
        }
    }
    public void launcherControl1(Gamepad gamepad) {
        boolean buttonUp = gamepad.y, buttonDown = gamepad.x;
        if (buttonUp && !lastAdjButton) {
            FLY_SPEED += 0.02;
        } else if (buttonDown && !lastAdjButton) {
            FLY_SPEED -= 0.02;
        }
        lastAdjButton = buttonUp || buttonDown;
        if (gamepad.right_bumper && s3.getPosition() == 0.6) {
            setIntake(true);
            setIntakeReverse(true);
        } else if (gamepad.a) {
            setIntake(false);
            setIntakeReverse(true);
        } else {
            setIntake(false);
            setIntakeReverse(false);
        }
        boolean chopstick = gamepad.right_trigger > 0.8;
        if (chopstick && !lastChopstick && !servoActive) {
            S3.setPosition(SERVO_UP);
            servoTimer.reset();
            servoActive = true;
        }
        lastChopstick = chopstick;
        if (servoActive && servoTimer.seconds() >= 0.5) {
            S3.setPosition(SERVO_DOWN);
            servoActive = false;
        }
        if (gamepad.left_bumper) {
            motorFly.setPower(FLY_SPEED);
        } else {
            motorFly.setPower(0);
        }
    }

    public void launcherControl2(Gamepad gamepad){
        if (buttonUp && !lastAdjButton) {
            FLY_SPEED += 0.02;
        } else if (buttonDown && !lastAdjButton) {
            FLY_SPEED -= 0.02;
        }
        if (gamepad.right_trigger) {
            setIntake(true);
            setIntakeReverse(true);
        } else if (gamepad.a) {
            setIntake(false);
            setIntakeReverse(true);
        } else {
            setIntake(false);
            setIntakeReverse(false);
        }
        if (gamepad.y){
            S4.setPower(0.05);
        } else if (gamepad.x){
            S4.setPower(-0.05);
        } else {
            S4.setPower(0);
        }
        if (gamepad.left_bumper) {
            motorFly.setPower(FLY_SPEED);
        } else {
            motorFly.setPower(0);
        }
    }

    public void autoShoot(int x){
        // Spin up flywheel
        motorFly.setPower(FLY_SPEED);
        sleep(1000);
        for (int y = 0; y < x; y++) {
            // Lift chopsticks up and down
            S3.setPosition(SERVO_UP);
            sleep(500);
            S3.setPosition(SERVO_DOWN);
            sleep(500);

            // Spin intake to push balls forward
            setIntake(true);
            sleep(1000);//Longer push speed
            setIntake(false);

            // wait a bit
            sleep(750);
        }
        motorFly.setPower(0);
    }
}
