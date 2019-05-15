package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.*;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.XboxController;

public class Drive implements Pronstants {
    // Imported objects
    TalonSRX talonFL, talonBL, talonFR, talonBR; // Talon MC objects
    Joystick joyL, joyR; // Joystick objects
    ADIS16448_IMU imu; // Gyro object
    double left = 0.0; // Left side ramp
    double right = 0.0; // Right side ramp
    boolean turned = false; // For the driveTo angle command
    double angleOriginal; // initilializes the angle offset
    XboxController xbox;
    double turbo = .7;

    public Drive(ADIS16448_IMU imu) {

        talonFL = new TalonSRX(TALONFL_PORT); // Defines Talon objects
        talonBL = new TalonSRX(TALONBL_PORT);
        talonFR = new TalonSRX(TALONFR_PORT);
        talonBR = new TalonSRX(TALONBR_PORT);

        talonFL.setInverted(true); // Inverts Talon outputs to correctly orient joystick values
        talonBL.setInverted(true);
        talonFR.setInverted(false);
        talonBR.setInverted(false);

        talonFL.configFactoryDefault(); // Sets talons to factory defaults
        talonBL.configFactoryDefault();
        talonFR.configFactoryDefault();
        talonBR.configFactoryDefault();

        talonFL.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, PID_LOOP_IDX, PID_TIMEOUT);
        talonFR.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, PID_LOOP_IDX, PID_TIMEOUT);

        joyL = new Joystick(JOYL_PORT); // Defines joysticks
        joyR = new Joystick(JOYR_PORT);
        xbox = new XboxController(XBOX_PORT);

        this.imu = imu; // Sets gyro obj from arg obj

        angleOriginal = imu.getAngleZ();// sets up

    }

    public void leftDrive(double power) { // Left side drive. Used in other methods

        talonFL.set(ControlMode.PercentOutput, power);
        talonBL.set(ControlMode.Follower, TALONFL_PORT);
    }

    public void rightDrive(double power) { // Right side drive. Used in other methods
        talonFR.set(ControlMode.PercentOutput, power);
        talonBR.set(ControlMode.Follower, TALONFR_PORT);
    }

    public void stop() { // Kill motors
        rightDrive(0);
        leftDrive(0);
    }

    public void tankDrive() {
        left = (left + joyL.getRawAxis(1)) / 3;// averages the previous value and the current joystick value
        right = (right + joyR.getRawAxis(1)) / 3;

        if (Math.abs(joyL.getRawAxis(1)) > DEADZONE) {// doesn't drive if the joystick is close to zero but not zero
            leftDrive(left*turbo);// sets the motor to a value 3 times lower than it should be to be calmer
        } else {
            leftDrive(0); // If no input, stop left side
        }

        if (Math.abs(joyR.getRawAxis(1)) > DEADZONE) {// Same as left, but right
            rightDrive(right*turbo);
        } else {
            rightDrive(0);
        }
        if(joyL.getRawButton(1)||joyR.getRawButton(1)){
            turbo = .5;
        }else{
            turbo = .3;
        }
    }

    public double getAngle() {
        return (angleOriginal - imu.getAngleZ()) % 360;// gets an angle relative to the robots starting position from
                                                       // 0-360

    }
    public void xboxDrive(){
        left = (left + xbox.getRawAxis(1)) / 2;// averages the previous value and the current joystick value
        right = (right + xbox.getRawAxis(5)) / 2;

        if (Math.abs(xbox.getRawAxis(1)) > DEADZONE) {// doesn't drive if the joystick is close to zero but not zero
            leftDrive(left*turbo);// sets the motor to a value 3 times lower than it should be to be calmer
        } else {
            leftDrive(0); // If no input, stop left side
        }

        if (Math.abs(xbox.getRawAxis(5)) > DEADZONE) {// Same as left, but right
            rightDrive(right*turbo);
        } else {
            rightDrive(0);
        }
        if(xbox.getRawAxis(2)>.9||(xbox.getRawAxis(3)>.9)){
            turbo = 1;
        }else{
            turbo = .7;
        }
    }

    /**
     * This code makes the robot turn to a given angle, angle. It turns until the
     * angle is achieved, and then stops
     * TODO: change to pid code
     * @param angle
     */
    public void driveToAngle(double angle) {
        if ((getAngle() - angle) >= GYRO_DEADZONE) {
            rightDrive(-TURN_SPEED);
            leftDrive(TURN_SPEED);
        }

        else if ((getAngle() - angle) < GYRO_DEADZONE) {
            rightDrive(TURN_SPEED);
            leftDrive(-TURN_SPEED);
        } else {
            stop();
            turned = true;
        }
    }

    public void driveRamp() { // Non-linear ramping throttle code.
        double left = (joyL.getRawAxis(1) + TAL_MAX_VALUE) / 2;
        double right = (joyR.getRawAxis(1) + TAL_MAX_VALUE) / 2;
        talonBR.set(ControlMode.Velocity, (right * 4096 / 600) * DRIVE_SCALER);
        talonBL.set(ControlMode.Velocity, (-left * 4096 / 600) * DRIVE_SCALER);
        talonFR.set(ControlMode.Follower, TALONBR_PORT);
        talonFL.set(ControlMode.Follower, TALONBL_PORT);
    }
}
