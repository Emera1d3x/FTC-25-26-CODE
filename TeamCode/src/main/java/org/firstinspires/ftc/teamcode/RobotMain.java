// Package
package org.firstinspires.ftc.teamcode;

// Event Loops
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

// Hardware
import com.qualcomm.robotcore.Gamepad;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;


@TeleOp(name = "CyberLyons")
public class RobotMain extends OpMode {
    MovementTool movementTool;
    // MATERIALS
    // HELPER METHODS
    public void controllerDebug() {
        telemetry.addData("Controller One", gamepad1.toString());
        telemetry.addData("Controller Two", gamepad2.toString());
    }
    public void initializeHelper(){
        telemetry.addData("Launch Test:", "Success");
        telemetry.addData("System Version:", "First Test");
        telemetry.update();
        movementTool = new MovementTool();
    }
    @Override
    public void init() {
        initializeHelper();
    }
    @Override
    public void loop() {
        controllerDebug();
        movementTool.useController(gamepad1);
    }
}