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
        private int x = CAMERA_WIDTH, y = CAMERA_HEIGHT;

        int getX() { return x; }
        int getY() { return y; }
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

        Imgproc.rectangle(mask, new Point(0, 0), new Point(CAMERA_WIDTH, CAMERA_HEIGHT/2), new Scalar(0), -1);

        // Blur to reduce noise
        Imgproc.blur(mask, mask, BALL_BLUR_SIZE);
        Core.inRange(mask, new Scalar(100), new Scalar(255), mask);

        // Find the blobs
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        hierarchy.release();
        mask.release();

        // Sort by descending contour size
        contours.sort(Comparator.comparingDouble((MatOfPoint c) -> Imgproc.contourArea(c)).reversed());

        boolean found = false;

        for (MatOfPoint contour : contours) {
            // Check if size is too small
            Moments moments = Imgproc.moments(contour);
            double area = moments.m00;
            if (area < BALL_MIN_SIZE)
                continue;

            // Check if contour is circular enough
            MatOfPoint2f contour2f = new MatOfPoint2f(); // allocate new matofpoint2f
            contour.convertTo(contour2f, CvType.CV_32F); // convert
            double perimeter = Imgproc.arcLength(contour2f, true);
            contour2f.release(); // deallocate
            double circularity =
                    (perimeter * perimeter) > 0 ? // Check for division by zero
                            (4 * Math.PI * area) / (perimeter * perimeter) // calculate circularity (0.0 to 1.0, 1.0 is perfect circle)
                            : 0;
            if (circularity < BALL_MIN_CIRCULARITY) // skip if not circular enough
                continue;

            // Found OK ball, store coordinates
            result.x = (int) (moments.m10 / area);
            result.y = (int) (moments.m01 / area);
            found = true;
            break;
        }

        if (!found) {
            // Nothing found, spin in circle
            result.x = CAMERA_WIDTH;
            result.y = CAMERA_HEIGHT;
        }

        // clean up
        for (MatOfPoint contour : contours) contour.release();
        return null;
    }
}
