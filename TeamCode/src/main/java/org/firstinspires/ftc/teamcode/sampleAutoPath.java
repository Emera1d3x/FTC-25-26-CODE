package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

/*
 * This is a sample Road Runner autonomous path demonstrating various trajectory types
 */
@Config
@Autonomous(group = "drive")
public class sampleAutoPath extends LinearOpMode {

    // Starting position (adjust based on your robot's starting position)
    public static Pose2d START_POSE = new Pose2d(0, 0, 0);

    // Path waypoints (adjust these coordinates for your field layout)
    public static Vector2d WAYPOINT_1 = new Vector2d(24, 24);    // First spline point
    public static Vector2d WAYPOINT_2 = new Vector2d(48, 0);     // Second spline point
    public static double TURN_ANGLE = Math.toRadians(90);       // 90 degree turn
    public static double FORWARD_DISTANCE = 36;                 // Forward distance in inches

    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize telemetry with FTC Dashboard
        Telemetry telemetry = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());

        // Initialize drive
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        // Set initial pose
        drive.setPoseEstimate(START_POSE);

        // Build trajectories
        TrajectorySequence mainPath = drive.trajectorySequenceBuilder(START_POSE)
                // Example 1: Spline to a point (smooth curved path)
                .splineTo(WAYPOINT_1, Math.toRadians(45))

                // Example 2: Forward movement
                .forward(FORWARD_DISTANCE)

                // Example 3: Turn in place
                .turn(TURN_ANGLE)

                // Example 4: Strafe movement
                .strafeRight(12)

                // Example 5: Another spline to second waypoint
                .splineTo(WAYPOINT_2, Math.toRadians(-90))

                // Example 6: Back up
                .back(24)

                // Example 7: Line to a specific point (straight line)
                .lineTo(new Vector2d(0, 0))

                .build();

        // Alternative: Build individual trajectories for more control
        Trajectory forwardTraj = drive.trajectoryBuilder(new Pose2d(0, 0, 0))
                .forward(30)
                .build();

        Trajectory splineTraj = drive.trajectoryBuilder(forwardTraj.end())
                .splineTo(new Vector2d(30, 30), Math.toRadians(90))
                .build();

        telemetry.addData("Status", "Initialized");
        telemetry.addData("Starting Pose", START_POSE);
        telemetry.update();

        waitForStart();

        if (isStopRequested()) return;

        // Execute the main trajectory sequence
        telemetry.addData("Status", "Following trajectory sequence");
        telemetry.update();

        drive.followTrajectorySequence(mainPath);

        // Alternative: Execute individual trajectories with actions in between
        /*
        drive.followTrajectory(forwardTraj);

        // Do something while moving (like intake control)
        // intake.setPower(1.0);

        drive.followTrajectory(splineTraj);
        */

        // Get final position
        Pose2d finalPose = drive.getPoseEstimate();
        telemetry.addData("Final X", finalPose.getX());
        telemetry.addData("Final Y", finalPose.getY());
        telemetry.addData("Final Heading", Math.toDegrees(finalPose.getHeading()));
        telemetry.addData("Status", "Path Complete");
        telemetry.update();

        // Keep opmode alive for telemetry viewing
        while (!isStopRequested() && opModeIsActive()) {
            // Update drive continuously for odometry
            drive.update();

            // Optional: Add live position telemetry
            Pose2d currentPose = drive.getPoseEstimate();
            telemetry.addData("Current X", currentPose.getX());
            telemetry.addData("Current Y", currentPose.getY());
            telemetry.addData("Current Heading", Math.toDegrees(currentPose.getHeading()));
            telemetry.update();
        }
    }
}
