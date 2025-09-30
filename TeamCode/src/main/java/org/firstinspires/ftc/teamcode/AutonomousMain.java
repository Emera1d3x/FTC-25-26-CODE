/*
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name = "AutonomousMain")
public class AutonomousMain extends LinearOpMode {
    DcMotor motorTopLeft;
    DcMotor motorTopRight;
    DcMotor motorBottomLeft;
    DcMotor motorBottomRight;

    @Override
    public void runOpMode() {
        motorTopRight = hardwareMap.get(DcMotor.class, "motorTopRight");
        motorTopLeft = hardwareMap.get(DcMotor.class, "motorTopLeft");
        motorBottomRight = hardwareMap.get(DcMotor.class, "motorBottomRight");
        motorBottomLeft = hardwareMap.get(DcMotor.class, "motorBottomLeft");

        motorTopRight.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBottomRight.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            // Example: drive forward for 2 seconds
            setAllPower(0.5);
            sleep(2000);
            setAllPower(0);
        }
    }

    private void setAllPower(double power) {
        motorTopLeft.setPower(power);
        motorTopRight.setPower(power);
        motorBottomLeft.setPower(power);
        motorBottomRight.setPower(power);
    }
}
*/