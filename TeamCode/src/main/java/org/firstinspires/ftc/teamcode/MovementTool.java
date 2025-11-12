package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MovementTool {
    private DcMotor motorTopLeft;
    private DcMotor motorTopRight;
    private DcMotor motorBottomLeft;
    private DcMotor motorBottomRight;

    public MovementTool(DcMotor motorTopLeft, DcMotor motorTopRight, DcMotor motorBottomLeft, DcMotor motorBottomRight) {
        this.motorTopLeft = motorTopLeft;
        this.motorTopRight = motorTopRight;
        this.motorBottomLeft = motorBottomLeft;
        this.motorBottomRight = motorBottomRight;
    }

    public MovementTool(HardwareMap hardwareMap) {
        motorTopRight = hardwareMap.get(DcMotor.class, "motorTopRight");
        motorTopLeft = hardwareMap.get(DcMotor.class, "motorTopLeft");
        motorBottomRight = hardwareMap.get(DcMotor.class, "motorBottomRight");
        motorBottomLeft = hardwareMap.get(DcMotor.class, "motorBottomLeft");
        motorTopRight.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBottomRight.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void mecanumDrive(Gamepad gamepad) {
        double x = gamepad.left_stick_x;
        double y = -gamepad.left_stick_y;
        double turn = gamepad.right_stick_x;

        double theta = Math.atan2(y, x);
        double power = Math.sqrt(x * x + y * y);

        mecanumDrive(Math.toDegrees(theta), power, turn);
    }

    /**
     * Drive in a given direction and spin with a given speed
     * @param angle The angle to drive, in degrees. 0 is up, 90 is right
     * @param power The drive power. From 0.0 to 1.0
     * @param turn The amount of spin, from -1.0 (CCW full speed) to 1.0 (CW full speed)
     */
    public void mecanumDrive(double angle, double power, double turn) {
        double theta = Math.toRadians(angle);

        double sin = Math.sin(theta);
        double cos = Math.cos(theta);

        double topLeftPower = power * -(sin + cos) - turn;
        double topRightPower = power * (cos - sin) + turn;
        double bottomLeftPower = power * (cos - sin) - turn;
        double bottomRightPower = power * -(sin + cos) + turn;

        motorTopLeft.setPower(topLeftPower);
        motorTopRight.setPower(topRightPower);
        motorBottomLeft.setPower(bottomLeftPower);
        motorBottomRight.setPower(bottomRightPower);
    }

    public void driveMotorTopLeft(double power) { motorTopLeft.setPower(power); }
    public void driveMotorTopRight(double power) { motorTopRight.setPower(power); }
    public void driveMotorBottomLeft(double power) { motorBottomLeft.setPower(power); }
    public void driveMotorBottomRight(double power) { motorBottomRight.setPower(power); }
}