package org.firstinspires.ftc.teamcode;

import static android.os.SystemClock.sleep;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.vision.VisionTool;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "CameraTest")
public class CameraTest extends OpMode {
    private VisionTool visionTool;
    private final ElapsedTime runTime = new ElapsedTime();

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        // Initialize VisionTool
        visionTool = new VisionTool(hardwareMap.get(WebcamName.class, "Webcam 1"));

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
        visionTool.addText("Ball Angle", String.valueOf(visionTool.getBallAngle()));
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        visionTool.shutdown();
    }
}
