package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.CalibrationTool.TICKS_PER_REV;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.AngleUnit;

/**
 * Estimate robot position using drive motor encoders.
 * <p>
 * This class is written for a mecanum-drive configuration and does not require separate dead
 * wheels. It uses all four drive encoders and optionally an IMU for heading.
 */
public class DriveEncoderLocalization {
    private final DcMotor motorFL;
    private final DcMotor motorFR;
    private final DcMotor motorBL;
    private final DcMotor motorBR;
    private final double countsPerInch;
    private final double robotRadiusInches;

    private IMU imu;
    private double headingOffsetDegrees = 0.0;

    private int lastFL;
    private int lastFR;
    private int lastBL;
    private int lastBR;

    private double xInches;
    private double yInches;
    private double headingRadians;

    /**
     * @param frontLeft  front-left drive motor
     * @param frontRight front-right drive motor
     * @param backLeft   back-left drive motor
     * @param backRight  back-right drive motor
     * @param wheelDiameterInches drive wheel diameter in inches
     * @param trackWidthInches   distance between left and right wheels
     * @param wheelBaseInches    distance between front and back wheels
     */
    public DriveEncoderLocalization(
            DcMotor frontLeft,
            DcMotor frontRight,
            DcMotor backLeft,
            DcMotor backRight,
            double wheelDiameterInches,
            double trackWidthInches,
            double wheelBaseInches
    ) {
        motorFL = frontLeft;
        motorFR = frontRight;
        motorBL = backLeft;
        motorBR = backRight;

        countsPerInch = TICKS_PER_REV / (Math.PI * wheelDiameterInches);
        robotRadiusInches = (trackWidthInches + wheelBaseInches) / 2.0;

        resetEncoders();
        resetPose();
    }

    /**
     * Optionally attach an IMU for absolute heading.
     */
    public void attachImu(IMU imu) {
        this.imu = imu;
        headingOffsetDegrees = 0.0;
    }

    public void resetEncoders() {
        motorFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        motorFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorFR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void resetPose() {
        xInches = 0.0;
        yInches = 0.0;
        headingRadians = 0.0;
        headingOffsetDegrees = 0.0;
        syncEncoderPositions();
    }

    public void resetPose(double xStartInches, double yStartInches, double headingDegrees) {
        xInches = xStartInches;
        yInches = yStartInches;
        headingRadians = Math.toRadians(headingDegrees);
        headingOffsetDegrees = 0.0;
        if (imu != null) {
            headingOffsetDegrees = angleWrap(headingDegrees - getRawImuYaw());
        }
        syncEncoderPositions();
    }

    public void update() {
        int currentFL = motorFL.getCurrentPosition();
        int currentFR = motorFR.getCurrentPosition();
        int currentBL = motorBL.getCurrentPosition();
        int currentBR = motorBR.getCurrentPosition();

        double deltaFL = (currentFL - lastFL) / countsPerInch;
        double deltaFR = (currentFR - lastFR) / countsPerInch;
        double deltaBL = (currentBL - lastBL) / countsPerInch;
        double deltaBR = (currentBR - lastBR) / countsPerInch;

        lastFL = currentFL;
        lastFR = currentFR;
        lastBL = currentBL;
        lastBR = currentBR;

        double forward = (deltaFL + deltaFR + deltaBL + deltaBR) / 4.0;
        double strafe = (-deltaFL + deltaFR + deltaBL - deltaBR) / 4.0;
        double dHeading;

        if (imu != null) {
            double newHeadingRadians = Math.toRadians(angleWrap(getRawImuYaw() + headingOffsetDegrees));
            dHeading = smallestAngleDifference(headingRadians, newHeadingRadians);
            headingRadians = newHeadingRadians;
        } else {
            dHeading = (-deltaFL + deltaFR - deltaBL + deltaBR) / (4.0 * robotRadiusInches);
            headingRadians += dHeading;
        }

        double cosHeading = Math.cos(headingRadians);
        double sinHeading = Math.sin(headingRadians);

        xInches += strafe * cosHeading - forward * sinHeading;
        yInches += strafe * sinHeading + forward * cosHeading;
    }

    public double getXInches() {
        return xInches;
    }

    public double getYInches() {
        return yInches;
    }

    public double getHeadingRadians() {
        return headingRadians;
    }

    public double getHeadingDegrees() {
        return Math.toDegrees(headingRadians);
    }

    public void setPose(double xInches, double yInches, double headingDegrees) {
        this.xInches = xInches;
        this.yInches = yInches;
        this.headingRadians = Math.toRadians(headingDegrees);
        if (imu != null) {
            headingOffsetDegrees = angleWrap(headingDegrees - getRawImuYaw());
        }
        syncEncoderPositions();
    }

    private void syncEncoderPositions() {
        lastFL = motorFL.getCurrentPosition();
        lastFR = motorFR.getCurrentPosition();
        lastBL = motorBL.getCurrentPosition();
        lastBR = motorBR.getCurrentPosition();
    }

    private double getRawImuYaw() {
        if (imu == null) {
            return 0.0;
        }
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    private static double angleWrap(double degrees) {
        while (degrees > 180.0) {
            degrees -= 360.0;
        }
        while (degrees <= -180.0) {
            degrees += 360.0;
        }
        return degrees;
    }

    private static double smallestAngleDifference(double fromRadians, double toRadians) {
        double diff = toRadians - fromRadians;
        while (diff > Math.PI) {
            diff -= 2.0 * Math.PI;
        }
        while (diff <= -Math.PI) {
            diff += 2.0 * Math.PI;
        }
        return diff;
    }
}
