    package frc.robot;

    import edu.wpi.first.wpilibj.DigitalInput;

    public class LineSense implements Pronstants{
    
    Drive drive;


    boolean linedUp = false;
    DigitalInput lineSensorL, lineSensorR;
    ADIS16448_IMU imu;

    public LineSense(Drive drive, ADIS16448_IMU imu) {
        
        lineSensorL = new DigitalInput(LINE_PORT_1);
        lineSensorR = new DigitalInput(LINE_PORT_2);


    }


    public void LineSense( double angle) {    
       

        if(!lineSensorL.get()){
            drive.leftDrive(-.5);
            drive.rightDrive(-.4);
        }
        if(!lineSensorR.get()){
            drive.leftDrive(-.4);
            drive.rightDrive(-.5);
        }
        if(lineSensorL.get() && lineSensorR.get()){
            drive.driveToAngle(angle);
            linedUp = true;
        }
        if(linedUp){

        }

    }

}