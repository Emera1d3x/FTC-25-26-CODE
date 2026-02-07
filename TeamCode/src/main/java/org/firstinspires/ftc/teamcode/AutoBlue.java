package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "AutoBlue", preselectTeleOp = "RobotMain")
public class AutoBlue extends AutonomousMain {
    @Override
    int getSide() { return 0; }
}
