package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import com.ctre.phoenix.motorcontrol.can.*;


public class Drive implements Pronstants {

    Talon talonFL, talonBL, talonFR, talonBR, talon1, talon2;
    Joystick joyL, joyR;

        

    public Drive(ADIS16448 imu)  {
        
        talon1 = new TalonSRX(TALON1_PORT);
        talon2 = new TalonSRX(TALON2_PORT); 

        talonFL = new TalonSRX(TALONFL_PORT);
        talonBL = new TalonSRX(TALONBL_PORT);
        talonFR = new TalonSRX(TALONFR_PORT);
        talonBR = new TalonSRX(TALONBR_PORT);

        joyL = new Joystick(JOYL_PORT);
        joyR = new Joystick(JOYR_PORT);

        imu= new ADIS16448();

    }


    public void leftDrive(double power) {

    talonFL.set(power);
    talonBL.set(ControlMode.Follower, TALONFL_PORT);
    }

    public void rightDrive(double power){
    talonFR.set(power);
    talonBR.set(ControlMode.Follower, TALONFR_PORT);
    }
    
    public void stop() {
        rightDrive(0);
        leftDrive(0);
    }

    public void tankDrive(double power) {

        if(Math.abs(joyL.getRawAxis(1)) > DEADZONE){
            leftDrive(joyL.getRawAxis(power));
        }
        if(Math.abs(joyR.getRawAxis(1)) > DEADZONE){
            rightDrive(joyR.getRawAxis(power));
        }
    }

    public void driveToAngle(double angle) {
        imu.reset();
        if((gyro.getAngle() - angle) >= GYRO_DEADZONE){
                rightDrive(-TURN_SPEED);
                leftDrive(TURN_SPEED);
            }          
        }
        else if(gyro.getAngle() - angle < GYRO_DEADZONE){
            rightDrive(TURN_SPEED);
            leftDrive(-TURN_SPEED);
        }
        else{
            stop();
        }
    }

}
