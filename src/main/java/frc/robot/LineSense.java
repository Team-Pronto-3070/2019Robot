    package frc.robot.java;

    import edu.wpi.first.wpilibj.DigitalInput;

    public class LineSense implements Pronstants{
    
    Drive drive;


    boolean linedUp = false;
    DigitalInput lineSensorL, lineSensorR;
    ADIS116448 imu;

    public LineSense(Drive drive, ADIS116448 imu) {
        
        lineSensorL = new DigitalInput(LINE_PORT_1);
        lineSensorR = new DigitalInput(LINE_PORT_2);


    }


    public void LineSense( double angle) {    
       

        if(!lineSensorL){
            drive.leftDrive(-.5);
            drive.rightDrive(-.4);
        }
        if(!lineSensorR){
            drive.leftDrive(-.4);
            drive.rightDrive(-.5);
        }
        if(lineSensorL && lineSensorR){
            driveToAngle(angle);
            linedUp = true;
        }
        if(linedUp){

        }

    }

}