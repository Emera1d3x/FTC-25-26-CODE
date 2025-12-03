package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MovementTool {
    private DcMotor motorTL, motorTR, motorBL, motorBR;

    public MovementTool(HardwareMap hardwareMap) {
        motorTL = hardwareMap.get(DcMotor.class, "motorTL"); // 1
        motorTR = hardwareMap.get(DcMotor.class, "motorTR"); // 3
        motorBL = hardwareMap.get(DcMotor.class, "motorBL"); // 0
        motorBR = hardwareMap.get(DcMotor.class, "motorBR"); // 2

        assert motorTL != null && motorTR != null && motorBL != null && motorBR != null;

        motorTL.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBL.setDirection(DcMotorSimple.Direction.REVERSE);

        motorTL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorTR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        motorTL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorTR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void mecanumDriveControl(Gamepad gamepad) {
        double x = gamepad.left_stick_x;
        double y = -gamepad.left_stick_y;
        double turn = gamepad.right_stick_x;

        // Note flipped x and y
        double theta = Math.atan2(x, y);
        double power = Math.sqrt(x * x + y * y);

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

    public enum TargetLocation {
        FLOOR,
        WALL,
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
     * @param location The location, needed to calculate forward/backward
     */
    void driveToTarget(int currX, int currY, int targetX, int targetY, int toleranceX, int toleranceY, double power, TargetLocation location) {
        int dX = (int) Math.copySign(Math.max(Math.abs(currX - targetX) - toleranceX, 0), currX - targetX);
        int dY = (int) Math.copySign(Math.max(Math.abs(currY - targetY) - toleranceY, 0), currY - targetY);

        double theta = Math.atan2(dY, dX);

        // Angle is a value representing the desired turn, disregarding forward/backward
        int angle = (int) Math.toDegrees(theta); // (-)180 left, -90 up, 0 right, 90 down
        angle = Math.abs(angle); // 180 left, 90 up/down, 0 right
        angle = 180 - angle; // 0 left, 90 up/down, 180 right
        angle = angle - 90; // -90 left, 0 up/down, 90 right

        // Drive slower when turning more sharply
        double divisor = 90 / (power + 0.001); // prevent /0
        power = power - Math.abs(angle) / divisor;

        // Drive slower when closer
        power *= Math.min(Math.hypot(dX, dY) / 100, 1.0);

        // Calculate forward/backward, accounting for camera POV
        power *= (location == TargetLocation.FLOOR ? 1 : -1) * Integer.signum(-dY);

        mecanumDriveMove(0, power, angle);
    }
}