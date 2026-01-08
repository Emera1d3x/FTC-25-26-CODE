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

    final boolean USING_WEBCAM = false;
    final BuiltinCameraDirection INTERNAL_CAM_DIR = BuiltinCameraDirection.BACK;
    final int RESOLUTION_WIDTH = 640;
    final int RESOLUTION_HEIGHT = 480;

    boolean lastX;
    int frameCount;
    long capReqTime;

    VisionPortal portal;

    private MovementTool movementTool;
    private FlyWheelLauncherTool launcherTool;

    AprilTagProcessor tagProcessor;

    @Override
    public void init() {

        HardwareMap hardwareMap = this.hardwareMap;
        tagProcessor = new AprilTagProcessor.Builder().build();

        if (USING_WEBCAM) {
            portal = new VisionPortal.Builder()
                    .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                    .setCameraResolution(new Size(RESOLUTION_WIDTH, RESOLUTION_HEIGHT))
                    .addProcessor(tagProcessor)
                    .build();
        } else {
            portal = new VisionPortal.Builder()
                    .setCamera(INTERNAL_CAM_DIR)
                    .setCameraResolution(new Size(RESOLUTION_WIDTH, RESOLUTION_HEIGHT))
                    .addProcessor(tagProcessor)
                    .build();
        }

        movementTool = (true) ? new MovementTool(hardwareMap) : null; // Edit boolean to switch on/off movementTool
        int launcherType = 1; // Edit number to change launcher type
        switch (launcherType) {
            case 1: // Team 1
            case 2:
                launcherTool = new FlyWheelLauncherTool(hardwareMap, launcherType);
                break;
            default: // Unavailable
                launcherTool = null;
        }     

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
        boolean x = gamepad1.x;

        if (x && !lastX) {
            portal.saveNextFrameRaw(String.format(Locale.US, "CameraFrameCapture-%06d", frameCount++));
            capReqTime = System.currentTimeMillis();
        }

        lastX = x;

        telemetry.addLine("######## Camera Capture Utility ########");
        telemetry.addLine(String.format(Locale.US, " > Resolution: %dx%d", RESOLUTION_WIDTH, RESOLUTION_HEIGHT));
        telemetry.addLine(" > Press X (or Square) to capture a frame");
        telemetry.addData(" > Camera Status", portal.getCameraState());

        if (capReqTime != 0) {
            telemetry.addLine("\nCaptured Frame!");
        }

        if (capReqTime != 0 && System.currentTimeMillis() - capReqTime > 1000) {
            capReqTime = 0;
        }

        telemetry.update();
        if (movementTool != null)
            movementTool.mecanumDriveControl(gamepad1);
        if (launcherTool != null) {
            launcherTool.launcherControl(gamepad1);
        }
        telemetry.update();
    }
}