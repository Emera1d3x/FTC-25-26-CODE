package org.firstinspires.ftc.teamcode.vision;

import static org.firstinspires.ftc.teamcode.CalibrationTool.CAMERA_HEIGHT;
import static org.firstinspires.ftc.teamcode.CalibrationTool.CAMERA_WIDTH;

import android.graphics.Canvas;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.*;
import org.opencv.imgproc.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InternalBallVisionProcessor implements VisionProcessor {

    public static class Result {
        private int x = CAMERA_WIDTH, y = CAMERA_HEIGHT, angle = 90;

        int getX() { return x; }
        int getY() { return y; }
        int getAngle() { return angle; }

        // Calculates angle from set x and y values
        private void calculateAngle() {
            // Calculate dx and dy from bottom-center
            int dx = x - CAMERA_WIDTH / 2;
            int dy = y - CAMERA_HEIGHT;

            double theta = Math.atan2(dy, dx);

            angle = (int) Math.toDegrees(theta) + 90;
        }
    }

    private final Result result = new Result();

    Result getResult() { return result; }

    @Override
    public void init(int width, int height, CameraCalibration calibration) {}

    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasToBmpPx, Object object) {}

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

        boolean found = false;

        if (!contours.isEmpty()) {
            // Find biggest blob
            class ContourComparator implements Comparator<MatOfPoint> {
                @Override
                public int compare(MatOfPoint m1, MatOfPoint m2) {
                    return Double.compare(Imgproc.contourArea(m1), Imgproc.contourArea(m2));
                }
            }

            MatOfPoint largest_contour = Collections.max(contours, new ContourComparator());

            Moments moments = Imgproc.moments(largest_contour);
            if (moments.m00 > ((double) (CAMERA_WIDTH * CAMERA_HEIGHT) / 50)) {
                result.x = (int) (moments.m10 / moments.m00);
                result.y = (int) (moments.m01 / moments.m00);
                found = true;
            }
        }

        if (!found) {
            // Nothing found, spin in circle
            result.x = CAMERA_WIDTH;
            result.y = CAMERA_HEIGHT;
        }

        result.calculateAngle();

        for (MatOfPoint contour : contours) contour.release();
        return null;
    }
}
