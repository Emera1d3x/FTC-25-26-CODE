package org.firstinspires.ftc.teamcode.flywheel;

import static android.os.SystemClock.sleep;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.CalibrationTool;


public abstract class FlywheelTool {
    FlywheelTool(HardwareControl hardware)
    {
        this.hardware = hardware;
    }

    // Compatibilty
    public void setIntake(boolean on) { hardware.setIntake(on ? INTAKE_SPEED : 0); }
    public void setIntakeReverse(boolean on) { hardware.setIntake(on ? -INTAKE_SPEED : 0); }

    public void launcherControl(Gamepad gamepad) {
        // Flywheel speed adjustment
        boolean buttonUp = gamepad.y, buttonDown = gamepad.x;
        if (buttonUp && !lastFlyAdjButton) {
            FLY_SPEED += 0.02;
        } else if (buttonDown && !lastFlyAdjButton) {
            FLY_SPEED -= 0.02;
        }
        lastFlyAdjButton = buttonUp || buttonDown;

        // Flywheel control
        if (gamepad.left_bumper) {
            hardware.setFlywheel(FLY_SPEED);
        } else if (gamepad.b) {
            hardware.setFlywheel(-0.2);
        } else {
            hardware.setFlywheel(0);
        }

        // Intake control
        if (gamepad.right_bumper) {
            setIntake(true);
        } else if (gamepad.a) {
            setIntakeReverse(true);
        } else {
            setIntake(false);
        }

        // Lifter control
        boolean elevatorBtn = gamepad.right_trigger > 0.75;
        if (elevatorTimer.seconds() >= 0.5) {
            hardware.setElevator(elevatorBtn);
            elevatorTimer.reset();
        }
    }

    public void autoShoot(int count){
        hardware.setFlywheel(FLY_SPEED);
        sleep(1000);
        for (int i = 0; i < count; ++i) {
            // Lift chopsticks up and down
            hardware.setElevator(true);
            sleep(500);
            hardware.setElevator(false);
            sleep(500);

            // Spin intake to push balls forward
            setIntake(true);
            sleep(1000);//Longer push speed
            setIntake(false);

            // wait a bit
            sleep(750);
        }
        hardware.setFlywheel(0);
    }

    public double getFlySpeed() { return FLY_SPEED; }

    private HardwareControl hardware;
    private double INTAKE_SPEED = CalibrationTool.INTAKE_SPEED;
    private double FLY_SPEED = CalibrationTool.FLYWHEEL_SPEED;
    private boolean lastFlyAdjButton = false;
    private final ElapsedTime elevatorTimer = new ElapsedTime();
}
