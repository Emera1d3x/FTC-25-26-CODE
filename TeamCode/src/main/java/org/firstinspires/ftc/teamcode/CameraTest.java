package org.firstinspires.ftc.teamcode;

import static android.os.SystemClock.sleep;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.vision.VisionTool;
import org.opencv.core.Point;

import static org.firstinspires.ftc.teamcode.CalibrationTool.*;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "CameraTest")
public class CameraTest extends OpMode {
    private VisionTool visionTool;
    private MovementTool movementTool;
    private final ElapsedTime runTime = new ElapsedTime();
    private boolean isCollectingBalls = true;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        // Initialize VisionTool
        visionTool = new VisionTool(hardwareMap.get(WebcamName.class, "Webcam 1"));

        movementTool = new MovementTool(hardwareMap);

        visionTool.addText("Hello", "World!");

        telemetry.addData("Status", "Initialized");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit START
     */
    @Override
    public void init_loop() {
        sleep(20);
    }

    /*
     * Code to run ONCE when the driver hits START
     */
    @Override
    public void start() {
        runTime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits START but before they hit STOP
     */
    @Override
    public void loop() {
        sleep(100);

        visionTool.addText("Runtime", runTime.toString());
        visionTool.addText("Ball Location", "X: " + visionTool.getBallX() + ", Y: " + visionTool.getBallY());

        if (gamepad1.a) {
            // stop, toggle, then wait 1 second
            movementTool.mecanumDriveMove(0, 0, 0);
            isCollectingBalls = !isCollectingBalls;
            if (isCollectingBalls) {
                visionTool.switchToBalls();
            } else {
                visionTool.switchToTags();
            }
            sleep(1000);
        }

        if (isCollectingBalls) {
            movementTool.driveToTarget(
                    visionTool.getBallX(), visionTool.getBallY(), // Ball XY
                    CAMERA_WIDTH / 2, CAMERA_HEIGHT, // Target XY
                    CAMERA_WIDTH / 5, CAMERA_HEIGHT / 5, // Tolerance XY
                    0.5, // Max Power
                    MovementTool.TargetLocation.FLOOR // Location
            );
        } else {
            Point tagCenter = visionTool.getTagCenter(20);
            movementTool.driveToTarget(
                    (int) tagCenter.x, (int) tagCenter.y, // Tag XY
                    CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2, // Target XY
                    CAMERA_WIDTH / 3, CAMERA_HEIGHT / 3, // Tolerance XY
                    0.5, // Max Power
                    MovementTool.TargetLocation.WALL); // Location
        }
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        visionTool.shutdown();
    }
}
