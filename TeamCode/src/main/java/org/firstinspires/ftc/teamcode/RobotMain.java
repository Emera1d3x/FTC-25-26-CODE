package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.Locale;

import android.util.Size;

@TeleOp(name = "RobotMain")
public class RobotMain extends OpMode {

    private MovementTool movementTool;
    private FlyWheelLauncherTool launcherTool;

    AprilTagProcessor tagProcessor;

    @Override
    public void init() {

        HardwareMap hardwareMap = this.hardwareMap;
        tagProcessor = new AprilTagProcessor.Builder().build();


        movementTool = new MovementTool(hardwareMap); // Edit boolean to switch on/off movementTool
        launcherTool = new FlyWheelLauncherTool(hardwareMap);

        telemetry.addData("Launch Test:", "Successful");
        telemetry.addData("System Version", "1.0");
        telemetry.addData("Controller One", gamepad1.toString());
        telemetry.update();
    }

    @Override
    public void loop() {
        telemetry.addData("Launch Test:", "Successful");
        telemetry.addData("System Version", "1.0");
        telemetry.addData("Controller One", gamepad1.toString());
        telemetry.addData("Cur Speed", launcherTool.FLY_SPEED);

        telemetry.update();
        if (movementTool != null)
            movementTool.mecanumDriveControl(gamepad1);
        if (launcherTool != null) {
            launcherTool.launcherControl(gamepad1);
        }
        telemetry.update();
    }
}