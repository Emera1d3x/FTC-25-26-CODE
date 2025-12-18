package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.vision.VisionTool;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@Autonomous(name = "April Tag Drive Test")
public class AprilTagDriveTest extends LinearOpMode {
    VisionTool vision;
    MovementTool movement;

    static final int DESIRED_DISTANCE = 30; // inch
    @Override
    public void runOpMode()
    {
        vision = new VisionTool(hardwareMap.get(WebcamName.class, "Webcam 1"));
        movement = new MovementTool(hardwareMap);

        while (opModeInInit())
        {
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

        vision.shutdown();
    }
}
