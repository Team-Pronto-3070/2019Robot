package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.*;
import edu.wpi.first.wpilibj.Encoder;

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

    public Drive(XboxController xbox) {

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
        this.xbox = xbox;

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
        left = (left + xbox.getRawAxis(1)) / 2;// averages the previous value and the current joystick value
        right = (right + xbox.getRawAxis(5)) / 2;

        if (Math.abs(xbox.getRawAxis(1)) > DEADZONE) {// doesn't drive if the joystick is close to zero but not zero
            leftDrive(left/2);// sets the motor to a value 3 times lower than it should be to be calmer
        } else {
            leftDrive(0); // If no input, stop left side
        }

        if (Math.abs(xbox.getRawAxis(5)) > DEADZONE) {// Same as left, but right
            rightDrive(right/2);
        } else {
            rightDrive(0);
        }
    }


    /**
     * This code makes the robot turn to a given angle, angle. It turns until the
     * angle is achieved, and then stops
     * TODO: change to pid code
     * @param angle
     */
    public void driveRamp() { // Non-linear ramping throttle code.
        double left = (joyL.getRawAxis(1) + TAL_MAX_VALUE) / 2;
        double right = (joyR.getRawAxis(1) + TAL_MAX_VALUE) / 2;
        talonBR.set(ControlMode.Velocity, (right * 4096 / 600) * DRIVE_SCALER);
        talonBL.set(ControlMode.Velocity, (-left * 4096 / 600) * DRIVE_SCALER);
        talonFR.set(ControlMode.Follower, TALONBR_PORT);
        talonFL.set(ControlMode.Follower, TALONBL_PORT);
    }
}
