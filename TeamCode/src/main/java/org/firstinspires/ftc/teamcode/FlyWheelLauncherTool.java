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
    private final double SERVO_DOWN = 0.45;
    private final double SERVO_UP = 0.0;
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
        motorFly.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motorFly.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        S1 = hardwareMap.get(CRServo.class, "S1"); // Pin: 0
        S2 = hardwareMap.get(CRServo.class, "S2"); // Pin: 1
        S3 = hardwareMap.get(Servo.class, "S3"); // Pin: 2
        S3.setPosition(SERVO_DOWN);
        S4 = null;
        FLY_SPEED = 0.64;
    }

    private void initializeTool2(HardwareMap hardwareMap) {
        motorFly = hardwareMap.get(DcMotor.class, "motorFly"); // Shooter Pin: 0 (Expansion Hub)
        motorFly.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motorFly.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        S1 = hardwareMap.get(CRServo.class, "S1"); // Pin: 0
        S2 = null;
        S3 = hardwareMap.get(CRServo.class, "S3"); // Pin: 2
        S4 = hardwareMap.get(Servo.class, "S4"); // Pin: 3
        FLY_SPEED = 1.0;
        motorFly.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void setIntake(boolean on) {
        if (on) {
            S1.setPower(INTAKE_SPEED);
            if (S2 != null) {
                S2.setPower(INTAKE_SPEED);
            }
        } else {
            S1.setPower(0);
            if (S2 != null) {
                S2.setPower(0);
            }
        }
    }

    public void setIntakeReverse(boolean on) {
        if (on) {
            S1.setPower(-INTAKE_SPEED);
            if (S2 != null) {
                S2.setPower(-INTAKE_SPEED);
            }
        } else {
            S1.setPower(0);
            if (S2 != null) {
                S2.setPower(0);
            }
        }
    }

    public void launcherControl(Gamepad gamepad){
        // Process flywheel speed change
        boolean buttonUp = gamepad.y, buttonDown = gamepad.x;
        if (buttonUp && !lastAdjButton) {
            FLY_SPEED += 0.02;
        } else if (buttonDown && !lastAdjButton) {
            FLY_SPEED -= 0.02;
        }
        lastAdjButton = buttonUp || buttonDown;

        // Process flywheel control
        if (gamepad.left_bumper) {
            motorFly.setPower(FLY_SPEED);
        } else if (gamepad.b) {
            motorFly.setPower(-0.2);
        } else {
            motorFly.setPower(0);
        }

        // Process intake control
        if (gamepad.right_bumper && !servoActive) {
            setIntake(true);
        } else if (gamepad.a) {
            setIntakeReverse(true);
        } else {
            setIntake(false);
        }

        if (TEAM_NUMBER == 1){
            launcherControl1(gamepad);
        } else if (TEAM_NUMBER == 2){
            launcherControl2(gamepad);
        }
    }
    public void launcherControl1(Gamepad gamepad) {
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
    }

    public void launcherControl2(Gamepad gamepad) {
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
