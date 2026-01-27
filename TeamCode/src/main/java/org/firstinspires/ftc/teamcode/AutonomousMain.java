
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.vision.VisionTool;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import static org.firstinspires.ftc.teamcode.CalibrationTool.*;

@Autonomous(name = "AutonomousMain")
public class AutonomousMain extends LinearOpMode {
    VisionTool vision;
    MovementTool movement;
    FlyWheelLauncherTool shootballs;

    void collectBall() {
        int x = 0, y = 0;

        while (y < CAMERA_HEIGHT * 0.8 || x < CAMERA_WIDTH * 0.3 || x > CAMERA_WIDTH * 0.7) {
            if (!opModeIsActive())
            {
                movement.brake();
                return;
            }

            x = vision.getBallX();
            y = vision.getBallY();

            movement.driveToTarget(x, y, CAMERA_WIDTH / 2, CAMERA_HEIGHT, CAMERA_HEIGHT / 20, CAMERA_HEIGHT / 20, 0.5);
        }

        movement.relativeMove(1, 96, 96);
    }
    void goToGoal() {
        while (opModeIsActive()) {
            AprilTagDetection tag = vision.getTag(GOAL_ID);
            if (tag != null) {
                double drive = (tag.ftcPose.range - 60) / 20;
                double turn = -tag.ftcPose.bearing / 15;
                double strafe = tag.ftcPose.yaw / 8;

                if (Math.abs(drive) < 0.5 && Math.abs(turn) < 0.5 && Math.abs(strafe) < 0.5)
                    break;

                telemetry.addData("status", "moving towards goal");
                telemetry.addData("drive", tag.ftcPose.range);
                telemetry.addData("turn", -tag.ftcPose.bearing);
                telemetry.addData("strafe", tag.ftcPose.yaw);
                telemetry.update();
                movement.mecanumDriveMove(Math.signum(drive) * 0.5, Math.signum(turn) * 0.35, Math.signum(strafe) * 0.5, 1);
            } else {
                telemetry.addData("status", "finding goal");
                telemetry.update();
                movement.mecanumDriveMove(0, 0, 0.65);
            }
        }

        movement.brake();
    }

    void shootBalls() {
        telemetry.addData("status", "shooting balls");
        telemetry.update();
        shootballs.autoShoot(3);
    }

    @Override
    public void runOpMode() {
        movement = new MovementTool(hardwareMap);
        shootballs = new FlyWheelLauncherTool(hardwareMap);
        vision = new VisionTool(hardwareMap.get(WebcamName.class, "Webcam 1"));
        telemetry.addData("status", "initialized");
        telemetry.update();

        waitForStart();
        telemetry.addData("status", "driving forward");
        telemetry.update();
        movement.relativeMove(0.7, 72, 72);

        goToGoal();
        if (!opModeIsActive())
            return;
        shootBalls();
        if (!opModeIsActive())
            return;
        shootballs.setIntake(true);
        for (int i = 0; i < 3 && opModeIsActive(); ++i)
            collectBall();
        shootballs.setIntake(false);
        if (!opModeIsActive())
            return;
        goToGoal();
        if (!opModeIsActive())
            return;
        shootBalls();
    }
    
}