package org.firstinspires.ftc.teamcode;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.*;
import org.opencv.imgproc.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@TeleOp(name = "OpenCV Testing")
public class CameraTest extends LinearOpMode {
    private static final int CAMERA_WIDTH = 640; // width  of wanted camera resolution
    private static final int CAMERA_HEIGHT = 360; // height of wanted camera resolution

    @Override
    public void runOpMode() {

        MovementTool movementTool = new MovementTool(hardwareMap);

        CVDetectionPipeline detector = new CVDetectionPipeline();

        VisionPortal portal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new android.util.Size(CAMERA_WIDTH, CAMERA_HEIGHT))
                .addProcessor(detector)
                .build();

        waitForStart();

        while (opModeIsActive()) {
            telemetry.update();

            movementTool.mecanumDrive(0, 0.5, detector.get_target_angle());
        }
    }
    static class CVDetectionPipeline implements VisionProcessor {

        private int cX = 0, cY = 0;

        public int get_target_angle() {
            int originX = CAMERA_WIDTH / 2;
            int originY = CAMERA_HEIGHT;

            int dx = cX - originX;
            int dy = cY - originY;

            double theta = Math.atan2(dy, dx);

            return (int) Math.toDegrees(theta) + 90;
        }

        static class ContourComparator implements Comparator<MatOfPoint> {
            @Override
            public int compare(MatOfPoint m1, MatOfPoint m2) {
                return Double.compare(Imgproc.contourArea(m1), Imgproc.contourArea(m2));
            }
        }

        @Override
        public void init(int width, int height, CameraCalibration calibration) {
            // Empty...
        }

        @Override
        public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasToBmpPx, Object object) {
            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);

            // Scale coordinates to match displayed frame
            float drawX = cX * scaleBmpPxToCanvasPx;
            float drawY = cY * scaleBmpPxToCanvasPx;

            // Larger radius to make it more visible
            float radius = 10 * scaleBmpPxToCanvasPx;

            canvas.drawCircle(drawX, drawY, radius, paint);
        }

        @Override
        public Object processFrame(Mat input, long captureTime) {

            Mat hsv = new Mat();
            Imgproc.cvtColor(input, hsv, Imgproc.COLOR_BGR2HSV);

            Mat mask_green = new Mat(), mask_purple = new Mat();
            Core.inRange(hsv, new Scalar(30, 90, 40), new Scalar(90, 255, 255), mask_green);
            Core.inRange(hsv, new Scalar(125, 90, 40), new Scalar(170, 255, 255), mask_purple);
            hsv.release();

            Mat mask = new Mat();
            Core.bitwise_or(mask_green, mask_purple, mask);

            mask_green.release();
            mask_purple.release();

            Imgproc.blur(mask, mask, new Size((double) CAMERA_WIDTH / 50, (double) CAMERA_HEIGHT / 50));
            Core.inRange(mask, new Scalar(100), new Scalar(255), mask);

            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
            hierarchy.release();
            mask.release();

            if (!contours.isEmpty()) {
                MatOfPoint largest_contour = Collections.max(contours, new ContourComparator());

                Moments moments = Imgproc.moments(largest_contour);
                if (moments.m00 > ((double) (CAMERA_WIDTH * CAMERA_HEIGHT) / 50)) {
                    cX = (int) (moments.m10 / moments.m00);
                    cY = (int) (moments.m01 / moments.m00);

                    for (MatOfPoint contour : contours) contour.release();

                    return null;
                }
            }

            // Nothing found, spin in circle
            cX = CAMERA_WIDTH;
            cY = CAMERA_HEIGHT;

            for (MatOfPoint contour : contours) contour.release();

            return null;
        }
    }
}
