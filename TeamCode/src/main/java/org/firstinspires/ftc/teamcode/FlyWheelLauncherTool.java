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
    // Team 1
    private DcMotor motorFly;
    private CRServo S1, S2;
    private Servo S3;
    private boolean servoActive = false;
    private boolean lastChopstick = false;
    private final double SERVO_DOWN = 0.6; //Adjusting bounds.
    private final double SERVO_UP = 0.2;
    private final double INTAKE_SPEED = 0.7;
    public double FLY_SPEED;
    private ElapsedTime servoTimer = new ElapsedTime();
    private boolean lastAdjButton = false;

    public FlyWheelLauncherTool(HardwareMap hardwareMap) {
        if (TEAM_NUMBER == 1){
            initializeTool1(hardwareMap);
            FLY_SPEED = 0.64;
        } else { // 2
            initializeTool1(hardwareMap);
            FLY_SPEED = 1.0;
            motorFly.setDirection(DcMotorSimple.Direction.REVERSE);
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
    }

    public void launcherControl(Gamepad gamepad) {
        boolean buttonUp = gamepad.x, buttonDown = gamepad.y;
        if (buttonUp && !lastAdjButton) {
            FLY_SPEED += 0.02;
        } else if (buttonDown && !lastAdjButton) {
            FLY_SPEED -= 0.02;
        }
        lastAdjButton = buttonUp || buttonDown;

        if (gamepad.right_bumper){
            S1.setPower(INTAKE_SPEED);
            S2.setPower(INTAKE_SPEED);
        } else if (gamepad.a) {
            S1.setPower(-INTAKE_SPEED);
            S2.setPower(-INTAKE_SPEED);
        } else {
            S1.setPower(0);
            S2.setPower(0);
        }
        boolean chopstick = gamepad.right_trigger > 0.8;
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

        if (gamepad.left_bumper) {
            motorFly.setPower(FLY_SPEED);
        } else {
            motorFly.setPower(0);
        }
    }
    public void autoIntake(){//What I personally call the being stupid special
        S1.setPower(INTAKE_SPEED);
        S2.setPower(INTAKE_SPEED);
        movement.relativeMove(0.5, 24,72);
        s1.setPower(0);
        S2.setPower(0);
        movement.relativeMove(0.5, 72,10);
        movement.relativeMove(0.5, 72,72);
    }
    public void autoShoot(int x){
        // Spin up flywheel
        motorFly.setPower(FLY_SPEED);
        sleep(1000);
        for (int y = 0; y < x; y++) {
            // Lift chopsticks up and down
            S3.setPosition(SERVO_UP);
            sleep(1000);
            S3.setPosition(SERVO_DOWN);
            sleep(1000);

            // Spin intake to push balls forward
            S1.setPower(INTAKE_SPEED);
            S2.setPower(INTAKE_SPEED);
            sleep(750);
            S1.setPower(0);
            S2.setPower(0);

            // wait a bit
            sleep(500);
        }
        motorFly.setPower(0);
    }
}
