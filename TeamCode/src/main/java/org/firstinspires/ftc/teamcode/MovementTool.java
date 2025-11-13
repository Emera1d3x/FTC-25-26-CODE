package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MovementTool {
    private DcMotor motorTL, motorTR, motorBL, motorBR;

    public MovementTool(HardwareMap hardwareMap) {
        initializeMotors(hardwareMap);
    }

    private void initializeMotors(HardwareMap hardwareMap) {
        motorTL = hardwareMap.get(DcMotor.class, "motorTL"); // 1
        motorTR = hardwareMap.get(DcMotor.class, "motorTR"); // 3
        motorBL = hardwareMap.get(DcMotor.class, "motorBL"); // 0
        motorBR = hardwareMap.get(DcMotor.class, "motorBR"); // 2

        motorTR.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBR.setDirection(DcMotorSimple.Direction.REVERSE);
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

        motorTL.setPower(topLeftPower);
        motorTR.setPower(topRightPower);
        motorBL.setPower(bottomLeftPower);
        motorBR.setPower(bottomRightPower);
    }
}