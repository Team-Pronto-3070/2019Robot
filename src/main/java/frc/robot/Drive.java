package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.*;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Encoder;


public class Drive implements Pronstants {

    TalonSRX talonFL, talonBL, talonFR, talonBR, talon1, talon2;
    Joystick joyL, joyR;
    ADIS16448_IMU imu;
    Encoder encL, encR;
        

    public Drive(ADIS16448_IMU imu)  {
        
        // talon1 = new TalonSRX(TALON1_PORT);
        // talon2 = new TalonSRX(TALON2_PORT); 

        talonFL = new TalonSRX(TALONFL_PORT);
        talonBL = new TalonSRX(TALONBL_PORT);
        talonFR = new TalonSRX(TALONFR_PORT);
        talonBR = new TalonSRX(TALONBR_PORT);

        talonFL.setInverted(true);
        talonBL.setInverted(true);
        talonFR.setInverted(false);
        talonBR.setInverted(false);

        talonFL.configFactoryDefault();
        talonBL.configFactoryDefault();
        talonFR.configFactoryDefault();
        talonBR.configFactoryDefault();

        talonFL.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        talonFR.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);


        joyL = new Joystick(JOYL_PORT);
        joyR = new Joystick(JOYR_PORT);
        this.imu = imu;

    }


    public void leftDrive(double power) {

    talonFL.set(ControlMode.PercentOutput, power);
    talonBL.set(ControlMode.Follower, TALONFL_PORT);
    }

    public void rightDrive(double power){
    talonFR.set(ControlMode.PercentOutput, power);
    talonBR.set(ControlMode.Follower, TALONFR_PORT);
    }
    
    public void stop() {
        rightDrive(0);
        leftDrive(0);
    }

    public void tankDrive(double joyL, double joyR) {

        if(Math.abs(joyL) > DEADZONE){
            leftDrive(joyL/3.0);
        }else{
            leftDrive(0);
        }
        if(Math.abs(joyR) > DEADZONE){
            rightDrive(joyR/3.0);
        }else{
            rightDrive(0);
        }
    }

    public void driveToAngle(double angle) {
        if((imu.getYaw() - angle) >= GYRO_DEADZONE){
                rightDrive(-TURN_SPEED);
                leftDrive(TURN_SPEED);
            }          
        
        else if((imu.getYaw() - angle) < GYRO_DEADZONE){
            rightDrive(TURN_SPEED);
            leftDrive(-TURN_SPEED);
        }
        else{
            stop();
        }


    }
    public double getJoystickValue(boolean leftRight) {
        if (leftRight) {
            return joyL.getRawAxis(1);
        } else {
            return joyR.getRawAxis(1);
        }
    }

    public void driveRamp() {
        double left = getJoystickValue(LEFT) * TAL_MAX_VALUE / 2;
        double right = getJoystickValue(RIGHT) * TAL_MAX_VALUE / 2;
        talRM.set(ControlMode.Velocity, right * 4096 / 600);
        talLM.set(ControlMode.Velocity, -left * 4096 / 600);
        talLF.set(ControlMode.Follower, TALLM_PORT);
        talRF.set(ControlMode.Follower, TALRM_PORT);
    }
}
