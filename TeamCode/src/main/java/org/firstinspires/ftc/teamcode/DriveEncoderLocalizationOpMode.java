package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Drive Encoder Localization", group = "Test")
public class DriveEncoderLocalizationOpMode extends OpMode {
    private DcMotor motorFL;
    private DcMotor motorFR;
    private DcMotor motorBL;
    private DcMotor motorBR;
    private DriveEncoderLocalization localization;
    private IMU imu;

    @Override
    public void init() {
        motorFL = hardwareMap.get(DcMotor.class, "motorTL");
        motorFR = hardwareMap.get(DcMotor.class, "motorTR");
        motorBL = hardwareMap.get(DcMotor.class, "motorBL");
        motorBR = hardwareMap.get(DcMotor.class, "motorBR");

        motorFL.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBL.setDirection(DcMotorSimple.Direction.REVERSE);

        motorFL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorFR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        localization = new DriveEncoderLocalization(
                motorFL,
                motorFR,
                motorBL,
                motorBR,
                4.0,
                14.5,
                14.5
        );

        try {
            imu = hardwareMap.get(IMU.class, "imu");
            localization.attachImu(imu);
            telemetry.addData("IMU", "attached");
        } catch (Exception ignored) {
            telemetry.addData("IMU", "not found, using encoder-only heading");
        }

        telemetry.addData("Status", "Init complete");
        telemetry.update();
    }

    @Override
    public void loop() {
        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y;
        double turn = gamepad1.right_stick_x;

        if (gamepad1.left_trigger > 0.5) {
            x *= 0.4;
            y *= 0.4;
            turn *= 0.4;
        }

        double fl = y + x + turn;
        double fr = y - x - turn;
        double bl = y - x + turn;
        double br = y + x - turn;

        double max = Math.max(
                Math.max(Math.abs(fl), Math.abs(fr)),
                Math.max(Math.abs(bl), Math.abs(br))
        );
        if (max > 1.0) {
            fl /= max;
            fr /= max;
            bl /= max;
            br /= max;
        }

        motorFL.setPower(Range.clip(fl, -1.0, 1.0));
        motorFR.setPower(Range.clip(fr, -1.0, 1.0));
        motorBL.setPower(Range.clip(bl, -1.0, 1.0));
        motorBR.setPower(Range.clip(br, -1.0, 1.0));

        localization.update();

        telemetry.addData("Pose X (in)", "%.2f", localization.getXInches());
        telemetry.addData("Pose Y (in)", "%.2f", localization.getYInches());
        telemetry.addData("Heading (deg)", "%.1f", localization.getHeadingDegrees());
        telemetry.addData("Mode", imu != null ? "IMU+Encoders" : "Encoder-only");
        telemetry.update();
    }
}
