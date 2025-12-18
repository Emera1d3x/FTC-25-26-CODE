package org.firstinspires.ftc.teamcode;

import static android.os.SystemClock.sleep;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import static org.firstinspires.ftc.teamcode.CalibrationTool.*;

public class MovementTool {
    private DcMotor motorTL, motorTR, motorBL, motorBR;

    private void setMotorMode(DcMotor.RunMode mode) {
        motorTL.setMode(mode);
        motorTR.setMode(mode);
        motorBL.setMode(mode);
        motorBR.setMode(mode);
    }

    public MovementTool(HardwareMap hardwareMap) {
        motorTL = hardwareMap.get(DcMotor.class, "motorTL"); // Pin: 1
        motorTR = hardwareMap.get(DcMotor.class, "motorTR"); // Pin: 3
        motorBL = hardwareMap.get(DcMotor.class, "motorBL"); // Pin: 0
        motorBR = hardwareMap.get(DcMotor.class, "motorBR"); // Pin: 2

        assert motorTL != null && motorTR != null && motorBL != null && motorBR != null;

        motorTL.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBL.setDirection(DcMotorSimple.Direction.REVERSE);

        if (USE_DRIVE_ENCODERS) {
            setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else {
            setMotorMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        motorTL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorTR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void mecanumDriveControl(Gamepad gamepad) {
        double x = gamepad.left_stick_x;
        double y = -gamepad.left_stick_y;
        double turn = gamepad.right_stick_x;

        double theta = Math.atan2(x, y);
        double power = Math.sqrt(x*x + y*y);

        mecanumDriveMove(Math.toDegrees(theta), power, turn);
    }

    /**
     * Drive in a given direction and spin with a given speed
     * @param angle The angle to drive, in degrees. 0 is up, 90 is right
     * @param power The drive power. From 0.0 to 1.0. This controls both rotational and translational power
     * @param turn The amount of spin, from -1.0 (CCW full speed) to 1.0 (CW full speed)
     */
    public void mecanumDriveMove(double angle, double power, double turn) {
        double theta = Math.toRadians(angle);

        double strafe = Math.sin(theta);
        double drive = Math.cos(theta);

        mecanumDriveMove(drive, turn, strafe, power);
    }

    public void mecanumDriveMove(double drive, double turn, double strafe, double power) {
        double topLeftPower = power * (drive + strafe + turn);
        double topRightPower = power * (drive - strafe - turn);
        double bottomLeftPower = power * (drive - strafe + turn);
        double bottomRightPower = power * (drive + strafe - turn);

        // Normalize big values
        // If any is greater than 1.0 or less than -1.0, preserve direction
        double max = Math.max(
                Math.max(Math.abs(topLeftPower), Math.abs(topRightPower)),
                Math.max(Math.abs(bottomLeftPower), Math.abs(bottomRightPower))
        );

        if (max > 1.0) {
            topLeftPower /= max;
            topRightPower /= max;
            bottomLeftPower /= max;
            bottomRightPower /= max;
        }

        motorTL.setPower(topLeftPower);
        motorTR.setPower(topRightPower);
        motorBL.setPower(bottomLeftPower);
        motorBR.setPower(bottomRightPower);
    }

    public void brake() {
        motorTL.setPower(0);
        motorTR.setPower(0);
        motorBL.setPower(0);
        motorBR.setPower(0);
    }

    // NOTE: `power` is IGNORED when `USE_DRIVE_ENCODERS == false`
    public void relativeMove(double power, double leftInches, double rightInches) {
        if (USE_DRIVE_ENCODERS) {
            int encoderTargetTL = motorTL.getCurrentPosition() + (int) (leftInches * DRIVE_ENCODER_CPI);
            int encoderTargetBL = motorBL.getCurrentPosition() + (int) (leftInches * DRIVE_ENCODER_CPI);
            int encoderTargetTR = motorTR.getCurrentPosition() + (int) (rightInches * DRIVE_ENCODER_CPI);
            int encoderTargetBR = motorBR.getCurrentPosition() + (int) (rightInches * DRIVE_ENCODER_CPI);

            motorTL.setTargetPosition(encoderTargetTL);
            motorTR.setTargetPosition(encoderTargetTR);
            motorBL.setTargetPosition(encoderTargetBL);
            motorBR.setTargetPosition(encoderTargetBR);

            setMotorMode(DcMotor.RunMode.RUN_TO_POSITION);

            motorTL.setPower(power);
            motorTR.setPower(power);
            motorBL.setPower(power);
            motorBR.setPower(power);

            while (motorTL.isBusy() || motorTR.isBusy() || motorBL.isBusy() || motorBR.isBusy())
                sleep(1);

            brake();

            setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else { // USE_DRIVE_ENCODERS

            // inches / inch per millisecond
            double leftDriveTime = Math.abs(leftInches) / (MOTOR_RPM / 60000 * WHEEL_CIRCUMFERENCE);
            double rightDriveTime = Math.abs(rightInches) / (MOTOR_RPM / 60000 * WHEEL_CIRCUMFERENCE);

            ElapsedTime elapsed = new ElapsedTime();
            elapsed.reset();

            boolean leftDone = false, rightDone = false;

            motorTL.setPower(Math.signum(leftInches));
            motorTR.setPower(Math.signum(rightInches));
            motorBL.setPower(Math.signum(leftInches));
            motorBR.setPower(Math.signum(rightInches));

            while (!leftDone || !rightDone) {
                double ms = elapsed.milliseconds();

                if (ms >= leftDriveTime) {
                    motorTL.setPower(0);
                    motorBL.setPower(0);
                    leftDone = true;
                }

                if (ms >= rightDriveTime) {
                    motorTR.setPower(0);
                    motorBR.setPower(0);
                    rightDone = true;
                }

                Thread.yield();
            }
        } // USE_DRIVE_ENCODERS
    }

    /**
     * Move to a certain position relative to an object
     * @param currX The current x coordinate of the object
     * @param currY The current y coordinate of the object
     * @param targetX The desired x coordinate of the object
     * @param targetY The desired y coordinate of the object
     * @param toleranceX The maximum allowed x deviation
     * @param toleranceY Same thing for Y
     * @param power The driving power, from 0.0 to 1.0
     */
    void driveToTarget(int currX, int currY, int targetX, int targetY, int toleranceX, int toleranceY, double power) {
        int dX = (int) Math.copySign(Math.max(Math.abs(currX - targetX) - toleranceX, 0), currX - targetX);
        int dY = (int) Math.copySign(Math.max(Math.abs(currY - targetY) - toleranceY, 0), currY - targetY);

        double theta = Math.atan2(dY, dX);

        // Angle is a value representing the desired turn, disregarding forward/backward
        int angle = (int) Math.toDegrees(theta); // (-)180 left, -90 up, 0 right, 90 down
        angle = Math.abs(angle); // 180 left, 90 up/down, 0 right
        angle = 180 - angle; // 0 left, 90 up/down, 180 right
        angle = angle - 90; // -90 left, 0 up/down, 90 right

        // Drive slower when turning more sharply
        double divisor = 180 / (power + 0.001); // prevent /0
        power = power - Math.abs(angle) / divisor;

        // Drive slower when closer
        power *= Math.min(Math.hypot(dX, dY) / 100, 1.0);

        power *= Integer.signum(-dY);

        mecanumDriveMove(0, power, angle);
    }
}