package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

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

    public void mecanumDrive(Gamepad gamepad) {
        double x = gamepad.left_stick_x;
        double y = -gamepad.left_stick_y;
        double turn = gamepad.right_stick_x;

        double theta = Math.atan2(y, x);
        double power = Math.sqrt(x * x + y * y);
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
}