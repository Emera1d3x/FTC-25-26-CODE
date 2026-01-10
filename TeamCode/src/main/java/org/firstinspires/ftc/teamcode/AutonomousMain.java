
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.vision.VisionTool;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import static org.firstinspires.ftc.teamcode.CalibrationTool.*;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "AutonomousMain")
public class AutonomousMain extends LinearOpMode {
    VisionTool vision;
    MovementTool movement;
    FlyWheelLauncherTool shootballs;
    ElapsedTime aprilTagTimer = new ElapsedTime();

    void collectBall() {
        int x = 0, y = 0;

        while (y < CAMERA_HEIGHT * 0.8 || x < CAMERA_WIDTH * 0.3 || x > CAMERA_WIDTH * 0.7) {
            x = vision.getBallX();
            y = vision.getBallY();

            movement.driveToTarget(x, y, CAMERA_WIDTH / 2, CAMERA_HEIGHT, CAMERA_HEIGHT / 10, CAMERA_HEIGHT / 10, 0.3);
        }

        movement.relativeMove(1.0, 12, 12);
    }
//Not sure if it will work
    void goToGoal(int id) {
        aprilTagTimer.reset();
        while (aprilTagTimer.seconds() < 3.0) {
            AprilTagDetection tag = vision.getTag(id);
            if (tag != null) {
                double drive = (tag.ftcPose.range - 35) / 30; // 35in
                double turn = -tag.ftcPose.bearing / 90;
                double strafe = tag.ftcPose.yaw / 90;

                if (Math.abs(drive) < 0.3 && Math.abs(turn) < 0.3 && Math.abs(strafe) < 0.3)
                    break;

                movement.mecanumDriveMove(drive, turn, strafe, 0.3);
            } else {
                movement.mecanumDriveMove(0, 0, 0.3);
            }
        }

        movement.brake();
    }

    void shootBalls() {
        shootballs.autoShoot(0);
    }

    @Override
    public void runOpMode() {
        movement = new MovementTool(hardwareMap);
        shootballs = new FlyWheelLauncherTool(hardwareMap);
        vision = new VisionTool(hardwareMap.get(WebcamName.class, "Webcam 1"));

        waitForStart();
        movement.relativeMove(0.5, 72, 72);

        while (opModeIsActive()) {
            goToGoal(20);
            shootBalls();
            collectBall();
        }
    }
}