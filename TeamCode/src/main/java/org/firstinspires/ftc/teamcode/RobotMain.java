package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "RobotMain")
public class RobotMain extends OpMode {
    DcMotor motorTopLeft;
    DcMotor motorTopRight;
    DcMotor motorBottomLeft;
    DcMotor motorBottomRight;

    private MovementTool movementTool;
    private  FlyWheelLauncherTool launcherTool;

    @Override
    public void init() {
        motorTopRight = hardwareMap.get(DcMotor.class, "motorTopRight");
        motorTopLeft = hardwareMap.get(DcMotor.class, "motorTopLeft");
        motorBottomRight = hardwareMap.get(DcMotor.class, "motorBottomRight");
        motorBottomLeft = hardwareMap.get(DcMotor.class, "motorBottomLeft");

        // motorA = hardwareMap.get(DcMotor.class, "motorA");
        // motorB = hardwareMap.get(DcMotor.class, "motorB");
        

        motorTopRight.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBottomRight.setDirection(DcMotorSimple.Direction.REVERSE);

        movementTool = new MovementTool(motorTopLeft, motorTopRight, motorBottomLeft, motorBottomRight);
        //launcherTool = new MovementTool(motorA, motorB);

        telemetry.addData("Launch Test:", "Successful");
        telemetry.addData("System Version", "1.0");
        telemetry.update();
    }

    @Override
    public void loop() {
        movementTool.mecanumDrive(gamepad1);
        if (gamepad1.a) {launcherTool.rotate();}
    }
}