package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.MovementTool;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.opencv.core.*;
import org.opencv.imgproc.*;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@TeleOp(name = "OpenCV Testing")
public class CameraTest extends LinearOpMode {
    private OpenCvCamera controlHubCam;  // Use OpenCvCamera class from FTC SDK
    private MovementTool movementTool;
    private static final int CAMERA_WIDTH = 640; // width  of wanted camera resolution
    private static final int CAMERA_HEIGHT = 360; // height of wanted camera resolution

    @Override
    public void runOpMode() {

        movementTool = new MovementTool(hardwareMap);

        initOpenCV();

        waitForStart();

        while (opModeIsActive()) {
            telemetry.update();

            // The OpenCV pipeline automatically processes frames and handles detection

            // To stop:
            // controlHubCam.stopStreaming();
        }

        // Release resources
        controlHubCam.stopStreaming();
    }

    private void initOpenCV() {

        // Create an instance of the camera
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        // Use OpenCvCameraFactory class from FTC SDK to create camera instance
        controlHubCam = OpenCvCameraFactory.getInstance().createWebcam(
                hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);

        controlHubCam.setPipeline(new CVDetectionPipeline(movementTool));

        controlHubCam.openCameraDevice();
        controlHubCam.startStreaming(CAMERA_WIDTH, CAMERA_HEIGHT, OpenCvCameraRotation.UPRIGHT);
    }
    class CVDetectionPipeline extends OpenCvPipeline {

        class ContourComparator implements Comparator<MatOfPoint> {
            @Override
            public int compare(MatOfPoint m1, MatOfPoint m2) {
                return Double.compare(Imgproc.contourArea(m1), Imgproc.contourArea(m2));
            }
        }
        MovementTool movementTool;

        public CVDetectionPipeline(MovementTool movementTool) {
            this.movementTool = movementTool;
        }

        @Override
        public Mat processFrame(Mat input) {
            Mat hsv = new Mat();
            Imgproc.cvtColor(input, hsv, Imgproc.COLOR_BGR2HSV);

            Mat mask_green = new Mat(), mask_purple = new Mat();
            Core.inRange(hsv, new Scalar(30, 90, 40), new Scalar(90, 255, 255), mask_green);
            Core.inRange(hsv, new Scalar(125, 90, 40), new Scalar(170, 255, 255), mask_purple);

            Mat mask = new Mat();
            Core.bitwise_and(mask_green, mask_purple, mask);

            int width, height;
            width = mask.width();
            height = mask.height();

            Imgproc.blur(mask, mask, new Size((double) width / 50, (double) height / 50));
            Core.inRange(mask, new Scalar(100), new Scalar(255), mask);

            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            if (!contours.isEmpty()) {
                MatOfPoint largest_contour = Collections.max(contours, new ContourComparator());

                Moments moments = Imgproc.moments(largest_contour);
                if (moments.m00 > ((double) (width * height) / 50)) {
                    int cX = (int) (moments.m10 / moments.m00);
                    int cY = (int) (moments.m01 / moments.m00);

                    List<MatOfPoint> largest_contour_list = new ArrayList<>();
                    largest_contour_list.add(largest_contour);
                    Imgproc.drawContours(input, largest_contour_list, -1, new Scalar(0, 255, 0), 2);

                    Imgproc.circle(input, new Point(cX, cY), 5, new Scalar(0, 255, 0), -1);

                    int originX = width / 2;
                    int originY = height;

                    int dx = cX - originX;
                    int dy = cY - originY;

                    double theta = Math.atan2(dy, dx);

                    double steering_angle = Math.toDegrees(theta);

                    // Move!
                    movementTool.mecanumDrive(0, 0.75, steering_angle);

                    return input;
                }
            }

            // Nothing found, spin in circle
            movementTool.mecanumDrive(0, 0, 1);

            return input;
        }
    }
}
