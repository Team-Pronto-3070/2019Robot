package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.*;
import edu.wpi.first.wpilibj.Encoder;


public class Drive implements Pronstants {

    //Imported objects
    TalonSRX talonFL, talonBL, talonFR, talonBR; //Talon MC objects
    Joystick joyL, joyR; //Joystick objects
    ADIS16448_IMU imu; //Gyro object
    Encoder encL, encR; //Encoder objects
        

    public Drive(ADIS16448_IMU imu)  {
        
        // talon1 = new TalonSRX(TALON1_PORT);
        // talon2 = new TalonSRX(TALON2_PORT); 

        talonFL = new TalonSRX(TALONFL_PORT); //Defines Talon objects
        talonBL = new TalonSRX(TALONBL_PORT);
        talonFR = new TalonSRX(TALONFR_PORT);
        talonBR = new TalonSRX(TALONBR_PORT);

        talonFL.setInverted(true); //Inverts Talon outputs to correctly orient joystick values
        talonBL.setInverted(true);
        talonFR.setInverted(false);
        talonBR.setInverted(false);

        talonFL.configFactoryDefault();
        talonBL.configFactoryDefault();
        talonFR.configFactoryDefault();
        talonBR.configFactoryDefault();

        talonFL.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        talonFR.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);


        joyL = new Joystick(JOYL_PORT); //Defines joysticks
        joyR = new Joystick(JOYR_PORT);
        this.imu = imu; //Sets gyro obj from arg obj

    }


    public void leftDrive(double power) { //Left side drive. Used in other methods 

    talonFL.set(ControlMode.PercentOutput, power);
    talonBL.set(ControlMode.Follower, TALONFL_PORT);
    }

    public void rightDrive(double power){ //Right side drive. Used in other methods
    talonFR.set(ControlMode.PercentOutput, power);
    talonBR.set(ControlMode.Follower, TALONFR_PORT);
    }
    
    public void stop() { //Kill motors
        rightDrive(0);
        leftDrive(0);
    }

    public void tankDrive(double joyL, double joyR) { //Takes joystick outputs, 

        if(Math.abs(joyL) > DEADZONE){ //If the joystick input is greater than deadzone 
            leftDrive(joyL/3.0); //Set motors to dampened joystick input 
        }else{
            leftDrive(0); //If no input, stop left side
        }
        if(Math.abs(joyR) > DEADZONE){//Same as left, but right
            rightDrive(joyR/3.0);
        }else{
            rightDrive(0);
        }
    }

    public void driveToAngle(double angle) { //Rotates bot to given angle
        if((imu.getYaw() - angle) >= GYRO_DEADZONE){ //If wanted angle is to right of bot
                rightDrive(-TURN_SPEED); //Turn right
                leftDrive(TURN_SPEED);
            }          
        
        else if((imu.getYaw() - angle) < GYRO_DEADZONE){ //If wanted angle is to left of bot
            rightDrive(TURN_SPEED); //Turn left
            leftDrive(-TURN_SPEED);
        }
        else{
            stop(); //When completed, stop
        }
    }

    public void driveRamp() { //Non-linear ramping throttle code. 
        double left = (joyL.getRawAxis(1) + TAL_MAX_VALUE) / 2;
        double right = (joyR.getRawAxis(1) + TAL_MAX_VALUE) / 2;
        talonBR.set(ControlMode.Velocity, right * 4096 / 600);
        talonBL.set(ControlMode.Velocity, -left * 4096 / 600);
        talonFR.set(ControlMode.Follower, TALONBR_PORT);
        talonFL.set(ControlMode.Follower, TALONBL_PORT);
    }
}
