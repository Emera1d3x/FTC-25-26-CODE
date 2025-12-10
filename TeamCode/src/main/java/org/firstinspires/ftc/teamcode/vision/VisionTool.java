package org.firstinspires.ftc.teamcode.vision;

import static android.os.SystemClock.sleep;
import static org.firstinspires.ftc.teamcode.CalibrationTool.*;

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
    public VisionTool(WebcamName webcam) {
        ballProcessor = new InternalBallVisionProcessor();
        tagProcessor = new AprilTagProcessor.Builder().build();
        drawingProcessor = new InternalDrawingProcessor();
        portal = new VisionPortal.Builder()
                .setCamera(webcam)
                .setCameraResolution(new android.util.Size(CAMERA_WIDTH, CAMERA_HEIGHT))
                .addProcessors(ballProcessor, tagProcessor, drawingProcessor)
                .enableLiveView(CAMERA_LIVE_VIEW)
                .build();

        ExposureControl exposure = portal.getCameraControl(ExposureControl.class);
        if (exposure.getMode() != ExposureControl.Mode.Manual)
        {
            exposure.setMode(ExposureControl.Mode.Manual);
            sleep(50);
        }
        exposure.setExposure((long)CAMERA_EXPOSURE, TimeUnit.MILLISECONDS);
        sleep(20);
        GainControl gain = portal.getCameraControl(GainControl.class);
        gain.setGain(CAMERA_GAIN);
        sleep(20);
    }

    public int getBallX() { return ballProcessor.getResult().getX(); }
    public int getBallY() { return ballProcessor.getResult().getY(); }

    public void addText(String label, String data) { drawingProcessor.addText(label, data); }
    public void removeText(String label) { drawingProcessor.removeText(label); }

    public void switchToBalls() {
        portal.setProcessorEnabled(ballProcessor, true);
        portal.setProcessorEnabled(tagProcessor, false);
    }

    public void switchToTags() {
        portal.setProcessorEnabled(ballProcessor, false);
        portal.setProcessorEnabled(tagProcessor, true);
    }

    public List<AprilTagDetection> getTags() { return tagProcessor.getDetections(); }
    public boolean isTagPresent(int id) {
        for (AprilTagDetection detection : getTags())
            if (detection.id == id)
                return true;
        return false;
    }
    public Point getTagCenter(int id) {
        for (AprilTagDetection detection : getTags())
            if (detection.id == id)
                return new Point(detection.center.x, detection.center.y);
        return new Point(CAMERA_WIDTH, CAMERA_HEIGHT);
    }

    public AprilTagDetection getTag(int id) {
        for (AprilTagDetection detection : getTags())
            if (detection.id == id)
                return detection;
        return null;
    }

    public void shutdown() { portal.stopStreaming(); }

    private final VisionPortal portal;
    private final InternalBallVisionProcessor ballProcessor;
    private final InternalDrawingProcessor drawingProcessor;
    private final AprilTagProcessor tagProcessor;
}
