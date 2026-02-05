package org.firstinspires.ftc.teamcode.flywheel;

import static android.os.SystemClock.sleep;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Team2Flywheel implements HardwareControl {
    private static final double ELEVATOR_SPEED = 1.0;
    private static final int ELEVATOR_TIME = 750; // milliseconds
    public Team2Flywheel(HardwareMap hardwareMap) {
        intakeServo = hardwareMap.get(CRServo.class, "S1");
        elevatorServo = hardwareMap.get(CRServo.class, "S3");
        flywheelMotor = hardwareMap.get(DcMotor.class, "motorFly");
        flywheelMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheelMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheelMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public void setIntake(double speed) {
        intakeServo.setPower(speed);
    }

    @Override
    public void setFlywheel(double speed) {
        flywheelMotor.setPower(speed);
    }

    @Override
    public void setElevator(boolean up) {
        if (up == isUp) return;
        isUp = up;

        // FIXME elevator servo spin direction
        elevatorServo.setPower(up ? ELEVATOR_SPEED : -ELEVATOR_SPEED);
        sleep(ELEVATOR_TIME);
        elevatorServo.setPower(0);
    }

    private CRServo intakeServo, elevatorServo;
    private DcMotor flywheelMotor;
    private boolean isUp = false;
}
