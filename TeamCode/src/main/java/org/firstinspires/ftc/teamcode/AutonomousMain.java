
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
            x = vision.getBallX();
            y = vision.getBallY();

            movement.driveToTarget(x, y, CAMERA_WIDTH / 2, CAMERA_HEIGHT, CAMERA_HEIGHT / 10, CAMERA_HEIGHT / 10, 0.3);
        }

        movement.relativeMove(1.0, 12, 12);
    }
//Not sure if it will work
    void goToGoal(int id) {
         AprilTagDetection tag = vision.getTag(20);
            if (tag != null)
            {
                double  drive      = (tag.ftcPose.range - DESIRED_DISTANCE);
                double  turn    = -tag.ftcPose.bearing;
                double  strafe        = tag.ftcPose.yaw;

                movement.mecanumDriveMove(drive, turn, strafe, 0.1);
            }
            else
            {
                movement.mecanumDriveMove(0, 0.3, 1);
            }
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

        while (opModeIsActive()) {
            goToGoal(20);
            shootBalls();
            collectBall();
        }
    }
}