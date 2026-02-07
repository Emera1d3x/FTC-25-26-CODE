package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "AutoRed", preselectTeleOp = "RobotMain")
public class AutoRed extends AutonomousMain {
    @Override
    int getSide() { return 1; }
}
