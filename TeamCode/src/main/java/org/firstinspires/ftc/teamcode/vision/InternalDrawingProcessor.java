package org.firstinspires.ftc.teamcode.vision;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.*;
import org.opencv.imgproc.*;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class InternalDrawingProcessor implements VisionProcessor {
    private final ConcurrentSkipListMap<String, String> onscreenText = new ConcurrentSkipListMap<>();

    public void addText(String label, String data) {
        onscreenText.put(label, data);
    }

    public void removeText(String label) {
        onscreenText.remove(label);
    }

    @Override
    public void init(int width, int height, CameraCalibration calibration) {}

    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasToBmpPx, Object object) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(15 * scaleBmpPxToCanvasPx);
        paint.setAntiAlias(true);

        int line = 1;

        for (Map.Entry<String, String> entry : onscreenText.entrySet()) {
            String text = entry.getKey() + ": " + entry.getValue();
            canvas.drawText(text, 0, 20 * line * scaleBmpPxToCanvasPx, paint);
            line++;
        }
    }

    @Override
    public Object processFrame(Mat input, long captureTime) {
        return null;
    }
}
