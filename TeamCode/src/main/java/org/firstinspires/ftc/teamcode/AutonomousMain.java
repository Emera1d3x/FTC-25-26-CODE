
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.vision.VisionTool;
import static org.firstinspires.ftc.teamcode.CalibrationTool.*;

@Autonomous(name = "AutonomousMain")
public class AutonomousMain extends LinearOpMode {
    VisionTool vision;
    MovementTool movement;

    void collectBall() {
        int x = 0, y = 0;

        while (y < CAMERA_HEIGHT * 0.8 || x < CAMERA_WIDTH * 0.3 || x > CAMERA_WIDTH * 0.7) {
            x = vision.getBallX();
            y = vision.getBallY();

            movement.driveToTarget(x, y, CAMERA_WIDTH / 2, CAMERA_HEIGHT, CAMERA_HEIGHT / 10, CAMERA_HEIGHT / 10, 0.3);
        }

        movement.relativeMove(1.0, 12, 12);
    }

    void goToGoal(int id) {
        // TODO
    }

    void shootBalls() {
        // TODO
    }

    @Override
    public void runOpMode() {
        movement = new MovementTool(hardwareMap);
        vision = new VisionTool(hardwareMap.get(WebcamName.class, "Webcam 1"));

        waitForStart();

        while (opModeIsActive()) {
            goToGoal(20);
            shootBalls();
            collectBall();
        }
    }
}