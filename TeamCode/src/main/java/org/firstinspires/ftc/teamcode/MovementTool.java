package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
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
        motorTL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorTR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorTL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorTR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

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

        motorTL.setPower(topLeftPower);
        motorTR.setPower(topRightPower);
        motorBL.setPower(bottomLeftPower);
        motorBR.setPower(bottomRightPower);
    }
}