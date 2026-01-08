package org.firstinspires.ftc.teamcode;

import android.graphics.Color;
import android.util.Pair;

import org.opencv.core.Scalar;
import org.opencv.core.Size;

public final class CalibrationTool {
    public static final double WHEEL_DIAMETER = 4.0;
    public static final int CAMERA_WIDTH = 640;
    public static final int CAMERA_HEIGHT = 480;
    public static final boolean CAMERA_LIVE_VIEW = true;
    public static final int VISION_MARKING_COLOR = Color.GREEN;

    // Ball color ranges: LOW(Hue, Sat, Val) to HIGH(Hue, Sat, Val)
    public static final Pair<Scalar, Scalar> BALL_GREEN_RANGE = new Pair<>(new Scalar(30, 50, 40), new Scalar(90, 255, 255));
    public static final Pair<Scalar, Scalar> BALL_PURPLE_RANGE = new Pair<>(new Scalar(125, 50, 40), new Scalar(170, 255, 255));
    public static final Size BALL_BLUR_SIZE = new Size((double) CAMERA_WIDTH / 100, (double) CAMERA_HEIGHT / 100);
    public static final double BALL_MIN_SIZE = (double)(CAMERA_WIDTH * CAMERA_HEIGHT) / 150;

//    public static final double BALL_MIN_CIRCULARITY = 0.8; // 0.0 to 1.0
    public static final double BALL_MIN_CIRCULARITY = 0.0;
    public static final int CAMERA_EXPOSURE = 6; // ms
    public static final int CAMERA_GAIN = 250;
    public static final boolean USE_DRIVE_ENCODERS = false;
    public static final double WHEEL_CIRCUMFERENCE = WHEEL_DIAMETER * 3.14159;
    public static final double MOTOR_RPM = 130;
    public static final double DRIVE_ENCODER_CPI = // Counts Per Inch
            (50.9 * 7) / WHEEL_CIRCUMFERENCE; // Counts per revolution / Wheel Circumference
}
