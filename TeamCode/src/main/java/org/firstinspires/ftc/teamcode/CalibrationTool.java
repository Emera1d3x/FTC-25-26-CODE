package org.firstinspires.ftc.teamcode;

import android.graphics.Color;
import android.util.Pair;

import org.opencv.core.Scalar;
import org.opencv.core.Size;

public final class CalibrationTool {
    public static final double WHEEL_DIAMETER = 4.0;
    public static final double TICKS_PER_REV = 560.0;
    public static final int CAMERA_WIDTH = 640;
    public static final int CAMERA_HEIGHT = 480;
    public static final boolean CAMERA_LIVE_VIEW = true;
    public static final int VISION_MARKING_COLOR = Color.GREEN;

    // Ball color ranges: LOW(Hue, Sat, Val) to HIGH(Hue, Sat, Val)
    public static final Pair<Scalar, Scalar> BALL_GREEN_RANGE = new Pair<>(new Scalar(30, 50, 40), new Scalar(90, 255, 255));
    public static final Pair<Scalar, Scalar> BALL_PURPLE_RANGE = new Pair<>(new Scalar(125, 50, 40), new Scalar(170, 255, 255));
    public static final Size BALL_BLUR_SIZE = new Size((double) CAMERA_WIDTH / 100, (double) CAMERA_HEIGHT / 100);
    public static final double BALL_MIN_SIZE = (double)(CAMERA_WIDTH * CAMERA_HEIGHT) / 150;
    public static final double BALL_MIN_CIRCULARITY = 0.8; // 0.0 to 1.0
}
