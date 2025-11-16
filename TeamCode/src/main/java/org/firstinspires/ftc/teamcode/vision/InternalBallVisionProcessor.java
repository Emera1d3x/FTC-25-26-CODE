package org.firstinspires.ftc.teamcode.vision;

import static org.firstinspires.ftc.teamcode.CalibrationTool.*;

import android.graphics.Canvas;
import android.graphics.Paint;

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
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasToBmpPx, Object object) {
        // Make paint for the circle
        Paint paint = new Paint();
        paint.setColor(VISION_MARKING_COLOR);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        // Calculate circle location and size
        float drawX = result.x * scaleBmpPxToCanvasPx;
        float drawY = result.y * scaleBmpPxToCanvasPx;
        float radius = 15 * scaleBmpPxToCanvasPx;

        // Draw the circle on the ball
        canvas.drawCircle(drawX, drawY, radius, paint);
    }

    @Override
    public Object processFrame(Mat input, long captureTime) {
        // Convert the input to HSV for color processing
        Mat hsv = new Mat();
        Imgproc.cvtColor(input, hsv, Imgproc.COLOR_BGR2HSV);

        // Filter green and purple blobs
        Mat maskGreen = new Mat(), maskPurple = new Mat();
        Core.inRange(hsv, BALL_GREEN_RANGE.first, BALL_GREEN_RANGE.second, maskGreen);
        Core.inRange(hsv, BALL_PURPLE_RANGE.first, BALL_PURPLE_RANGE.second, maskPurple);
        hsv.release();

        // Combine green and purple masks into a single mask
        Mat mask = new Mat();
        Core.bitwise_or(maskGreen, maskPurple, mask);

        maskGreen.release();
        maskPurple.release();

        // Blur to reduce noise
        Imgproc.blur(mask, mask, BALL_BLUR_SIZE);
        Core.inRange(mask, new Scalar(100), new Scalar(255), mask);

        // Find the blobs
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        hierarchy.release();
        mask.release();

        boolean found = false;

        if (!contours.isEmpty()) {
            // Comparator of blobs
            class ContourComparator implements Comparator<MatOfPoint> {
                @Override
                public int compare(MatOfPoint m1, MatOfPoint m2) {
                    return Double.compare(Imgproc.contourArea(m1), Imgproc.contourArea(m2));
                }
            }

            // Find biggest blob
            MatOfPoint largestContour = Collections.max(contours, new ContourComparator());

            // Check if size exceeds threshold
            Moments moments = Imgproc.moments(largestContour);
            if (moments.m00 > BALL_MIN_SIZE) {
                // Store coordinates
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

        // clean up
        for (MatOfPoint contour : contours) contour.release();
        return null;
    }
}
