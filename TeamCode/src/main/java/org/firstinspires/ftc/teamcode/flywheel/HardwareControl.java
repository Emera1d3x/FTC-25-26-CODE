package org.firstinspires.ftc.teamcode.flywheel;

interface HardwareControl {
    /**
     * Set the speed of the intake motor
     * @param speed The speed, from -1.0 to 1.0, with positive values to take in a ball
     */
    void setIntake(double speed);

    /**
     * Set the position of the ball elevator
     * @param position The new position, with true being UP and false being DOWN
     */
    void setElevator(boolean position);

    /**
     * Set the speed of the flywheel
     * @param speed The speed, from -1.0 to 1.0, with positive values to shoot a ball
     */
    void setFlywheel(double speed);
}
