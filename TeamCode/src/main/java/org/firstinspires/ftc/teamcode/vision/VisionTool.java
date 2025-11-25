package org.firstinspires.ftc.teamcode.vision;

import static org.firstinspires.ftc.teamcode.CalibrationTool.*;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.opencv.core.*;
import org.opencv.imgproc.*;

import java.util.List;

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
    }

    public int getBallX() { return ballProcessor.getResult().getX(); }
    public int getBallY() { return ballProcessor.getResult().getY(); }

    public void addText(String label, String data) { drawingProcessor.addText(label, data); }
    public void removeText(String label) { drawingProcessor.removeText(label); }

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

    public void shutdown() { portal.stopStreaming(); }

    private final VisionPortal portal;
    private final InternalBallVisionProcessor ballProcessor;
    private final InternalDrawingProcessor drawingProcessor;
    private final AprilTagProcessor tagProcessor;
}
