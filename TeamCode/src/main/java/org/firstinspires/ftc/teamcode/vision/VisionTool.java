package org.firstinspires.ftc.teamcode.vision;

import static android.os.SystemClock.sleep;
import static org.firstinspires.ftc.teamcode.CalibrationTool.*;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.opencv.core.*;
import org.opencv.imgproc.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class VisionTool {
    public VisionTool(HardwareMap hardwareMap) {
        ballProcessor = new InternalBallVisionProcessor();
        tagProcessor = new AprilTagProcessor.Builder().build();
        cameraServo = hardwareMap.get(Servo.class, "cameraServo");
        portal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new android.util.Size(CAMERA_WIDTH, CAMERA_HEIGHT))
                .addProcessors(ballProcessor, tagProcessor)
                .enableLiveView(CAMERA_LIVE_VIEW)
                .build();

        // Wait for camera to start streaming
        while (portal.getCameraState() != VisionPortal.CameraState.STREAMING)
            sleep(1);

        tagProcessor.setDecimation(1.0f);

        // Set exposure and gain
        ExposureControl exposure = portal.getCameraControl(ExposureControl.class);
        if (exposure.getMode() != ExposureControl.Mode.Manual)
        {
            exposure.setMode(ExposureControl.Mode.Manual);
            sleep(50);
        }
        exposure.setExposure(CAMERA_EXPOSURE, TimeUnit.MILLISECONDS);
        sleep(20);
        GainControl gain = portal.getCameraControl(GainControl.class);
        gain.setGain(CAMERA_GAIN);
        sleep(20);
    }

    public int getBallX() { return ballProcessor.getResult().getX(); }
    public int getBallY() { return ballProcessor.getResult().getY(); }

    public void switchToBalls() {
        cameraServo.setPosition(0.7);
        portal.setProcessorEnabled(ballProcessor, true);
        portal.setProcessorEnabled(tagProcessor, false);
    }

    public void switchToTags() {
        cameraServo.setPosition(1.0);
        portal.setProcessorEnabled(ballProcessor, false);
        portal.setProcessorEnabled(tagProcessor, true);
    }

    public List<AprilTagDetection> getTags() { return tagProcessor.getDetections(); }

    public AprilTagDetection getTag(int id) {
        for (AprilTagDetection detection : getTags())
            if (detection.id == id)
                return detection;
        return null;
    }

    public void shutdown() { portal.stopStreaming(); }

    private final VisionPortal portal;
    private final InternalBallVisionProcessor ballProcessor;
    private final AprilTagProcessor tagProcessor;
    private final Servo cameraServo;
}
