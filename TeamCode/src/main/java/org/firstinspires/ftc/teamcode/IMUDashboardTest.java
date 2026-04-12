package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.AngleUnit;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * OpMode to display IMU telemetry on both phone and FTC Dashboard
 */
@TeleOp(name = "IMU Dashboard Test")
public class IMUDashboardTest extends OpMode {
    private IMU imu;
    private Telemetry telemetry;

    @Override
    public void init() {
        // Initialize IMU
        imu = new IMU(hardwareMap);
        
        // Create telemetry that sends to both phone and dashboard
        telemetry = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());
        
        telemetry.addLine("IMU Connected!");
        telemetry.update();
    }

    @Override
    public void loop() {
        // Display IMU telemetry
        telemetry.addData("Yaw (degrees)", imu.getYaw(AngleUnit.DEGREES));
        telemetry.addData("Yaw (radians)", imu.getYawRadians());
        telemetry.addData("Pitch (degrees)", imu.getPitch(AngleUnit.DEGREES));
        telemetry.addData("Roll (degrees)", imu.getRoll(AngleUnit.DEGREES));
        
        // Angular velocities
        telemetry.addData("Head Velocity (rad/s)", imu.getHeadingVelocity());
        telemetry.addData("Yaw Velocity (deg/s)", imu.getAngularVelocity(com.qualcomm.robotcore.hardware.Axis.Z_AXIS, AngleUnit.DEGREES));
        
        // Control
        telemetry.addData("Press X to reset heading", "");
        
        if (gamepad1.x) {
            imu.resetHeading();
        }
        
        telemetry.update();
    }
}
