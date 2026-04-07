package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class DeadWheels {
    // Hardware
    private DcMotor leftEncoder, rightEncoder, centerEncoder;

    // Constants
    private final double TRACK_WIDTH; // Distance between left and right wheels in inches
    private final double FORWARD_OFFSET; // Distance from center of robot to center wheel in inches
    private final double WHEEL_RADIUS; // Radius of odometry wheels in inches
    private final double TICKS_PER_REV; // Encoder ticks per revolution
    private final double INCHES_PER_TICK; // Calculated: (2 * PI * WHEEL_RADIUS) / TICKS_PER_REV

    // Position tracking
    private double x_pos = 0.0; // Current X position in inches
    private double y_pos = 0.0; // Current Y position in inches
    private double heading = 0.0; // Current heading in radians

    // Previous encoder positions
    private int prev_left_encoder_pos = 0;
    private int prev_right_encoder_pos = 0;
    private int prev_center_encoder_pos = 0;

    // Timer for update rate control
    private ElapsedTime timer = new ElapsedTime();

    /**
     * Constructor for DeadWheels localization
     * @param hardwareMap Hardware map from OpMode
     * @param trackWidth Distance between left and right wheels (inches)
     * @param forwardOffset Distance from robot center to center wheel (inches)
     * @param wheelRadius Radius of odometry wheels (inches)
     * @param ticksPerRev Encoder ticks per revolution
     * @param leftEncoderName Hardware name for left encoder motor
     * @param rightEncoderName Hardware name for right encoder motor
     * @param centerEncoderName Hardware name for center encoder motor
     */
    public DeadWheels(HardwareMap hardwareMap, double trackWidth, double forwardOffset,
                     double wheelRadius, double ticksPerRev,
                     String leftEncoderName, String rightEncoderName, String centerEncoderName) {
        this.TRACK_WIDTH = trackWidth;
        this.FORWARD_OFFSET = forwardOffset;
        this.WHEEL_RADIUS = wheelRadius;
        this.TICKS_PER_REV = ticksPerRev;
        this.INCHES_PER_TICK = (2 * Math.PI * WHEEL_RADIUS) / TICKS_PER_REV;

        // Initialize motors
        leftEncoder = hardwareMap.get(DcMotor.class, leftEncoderName);
        rightEncoder = hardwareMap.get(DcMotor.class, rightEncoderName);
        centerEncoder = hardwareMap.get(DcMotor.class, centerEncoderName);

        // Set encoders to run without encoders (we just read position)
        leftEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        centerEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        centerEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Initialize previous positions
        prev_left_encoder_pos = leftEncoder.getCurrentPosition();
        prev_right_encoder_pos = rightEncoder.getCurrentPosition();
        prev_center_encoder_pos = centerEncoder.getCurrentPosition();

        timer.reset();
    }

    /**
     * Update the robot's position based on encoder readings
     * Call this method regularly (e.g., in a loop) to track position
     */
    public void update() {
        // Get current encoder positions
        int left_encoder_pos = leftEncoder.getCurrentPosition();
        int right_encoder_pos = rightEncoder.getCurrentPosition();
        int center_encoder_pos = centerEncoder.getCurrentPosition();

        // Calculate change in encoder positions
        double delta_left_encoder_pos = (left_encoder_pos - prev_left_encoder_pos) * INCHES_PER_TICK;
        double delta_right_encoder_pos = (right_encoder_pos - prev_right_encoder_pos) * INCHES_PER_TICK;
        double delta_center_encoder_pos = (center_encoder_pos - prev_center_encoder_pos) * INCHES_PER_TICK;

        // Calculate change in heading (phi)
        double phi = (delta_left_encoder_pos - delta_right_encoder_pos) / TRACK_WIDTH;

        // Calculate local movement
        double delta_middle_pos = (delta_left_encoder_pos + delta_right_encoder_pos) / 2.0;
        double delta_perp_pos = delta_center_encoder_pos - FORWARD_OFFSET * phi;

        // Convert to global coordinates
        double delta_x = delta_middle_pos * Math.cos(heading) - delta_perp_pos * Math.sin(heading);
        double delta_y = delta_middle_pos * Math.sin(heading) + delta_perp_pos * Math.cos(heading);

        // Update position
        x_pos += delta_x;
        y_pos += delta_y;
        heading += phi;

        // Normalize heading to [-pi, pi]
        heading = normalizeAngle(heading);

        // Update previous positions
        prev_left_encoder_pos = left_encoder_pos;
        prev_right_encoder_pos = right_encoder_pos;
        prev_center_encoder_pos = center_encoder_pos;
    }

    /**
     * Set the current position of the robot
     * @param x New X position in inches
     * @param y New Y position in inches
     * @param headingRadians New heading in radians
     */
    public void setPosition(double x, double y, double headingRadians) {
        this.x_pos = x;
        this.y_pos = y;
        this.heading = normalizeAngle(headingRadians);
    }

    /**
     * Get current X position
     * @return X position in inches
     */
    public double getX() {
        return x_pos;
    }

    /**
     * Get current Y position
     * @return Y position in inches
     */
    public double getY() {
        return y_pos;
    }

    /**
     * Get current heading
     * @return Heading in radians
     */
    public double getHeading() {
        return heading;
    }

    /**
     * Get current heading in degrees
     * @return Heading in degrees
     */
    public double getHeadingDegrees() {
        return Math.toDegrees(heading);
    }

    /**
     * Get current position as a Pose2d-like object (x, y, heading)
     * @return Array containing [x, y, heading] in inches and radians
     */
    public double[] getPose() {
        return new double[]{x_pos, y_pos, heading};
    }

    /**
     * Reset position to (0, 0, 0)
     */
    public void reset() {
        setPosition(0, 0, 0);
        // Reset encoder positions
        leftEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        centerEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        centerEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        prev_left_encoder_pos = 0;
        prev_right_encoder_pos = 0;
        prev_center_encoder_pos = 0;
    }

    /**
     * Normalize angle to [-pi, pi]
     * @param angle Angle in radians
     * @return Normalized angle in radians
     */
    private double normalizeAngle(double angle) {
        while (angle > Math.PI) angle -= 2 * Math.PI;
        while (angle < -Math.PI) angle += 2 * Math.PI;
        return angle;
    }

    /**
     * Get time since last update
     * @return Time in seconds
     */
    public double getTimeSinceLastUpdate() {
        return timer.seconds();
    }

    /**
     * Reset the update timer
     */
    public void resetTimer() {
        timer.reset();
    }
}
