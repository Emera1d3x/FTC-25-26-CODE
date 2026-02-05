package org.firstinspires.ftc.teamcode.flywheel;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Team1Flywheel implements HardwareControl {
    private static final double SERVO_DOWN = 0.45;
    private static final double SERVO_UP = 0.0;

    public Team1Flywheel(HardwareMap hardwareMap) {
        intakeServo0 = hardwareMap.get(CRServo.class, "S1");
        intakeServo1 = hardwareMap.get(CRServo.class, "S2");
        chopstickServo = hardwareMap.get(Servo.class, "S3");
        flywheelMotor = hardwareMap.get(DcMotor.class, "motorFly");
        flywheelMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheelMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void setIntake(double speed) {
        intakeServo0.setPower(speed);
        intakeServo1.setPower(speed);
    }

    @Override
    public void setFlywheel(double speed) {
        flywheelMotor.setPower(speed);
    }

    @Override
    public void setElevator(boolean up) {
        chopstickServo.setPosition(up ? SERVO_UP : SERVO_DOWN);
    }

    private CRServo intakeServo0, intakeServo1;
    private Servo chopstickServo;
    private DcMotor flywheelMotor;
}
