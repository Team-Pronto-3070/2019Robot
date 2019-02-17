/*----------------------   be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.*;
import java.util.Map;
import java.lang.Double;
import edu.wpi.first.wpilibj.Compressor;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.*;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot implements Pronstants {
  public static final ADIS16448_IMU imu = new ADIS16448_IMU();
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  boolean canPressComp = true;
  boolean compGo = true;
  double[] position = {0,0,0}; 

  Drive drive;
  //LineSense lineSense;

  ArmControl arm;
  Joystick joyL, joyR, joyArm;
  AnalogInput pressure;
  //DigitalInput lightSensor;
  // ShuffleboardTab shuffleboardtab;
  private NetworkTableEntry gyroYawEntry;
  Compressor comp;

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    SmartDashboard.putNumber("Angle", 0);

    
    //lineSense = new LineSense(drive, imu);
    joyL = new Joystick(0);
    joyR = new Joystick(1);
    drive = new Drive(imu);
    arm = new ArmControl();
    pressure = new AnalogInput(0);
    comp = new Compressor(0);
  //  lightSensor = new DigitalInput(0);
    comp.start();
    arm.succSol.set(Value.kReverse);

    gyroYawEntry = Shuffleboard.getTab("Gyro")
      .add("Gyro Yaw", new Double(1))
      .withWidget(BuiltInWidgets.kDial)
      .withProperties(Map.of("min", -180, "max", 180))
      .getEntry();
      
	// Get the UsbCamera from CameraServer

  UsbCamera front = CameraServer.getInstance().startAutomaticCapture("Front Camera", 0);
  UsbCamera back = CameraServer.getInstance().startAutomaticCapture("Line Camera", 1);
  // Set the resolution
  front.setResolution(320, 240);
  back.setResolution(320, 240);
  // Get a CvSink. This will capture Mats from the camera
  CvSink cvSink = CameraServer.getInstance().getVideo();
  // Setup a CvSource. This will send images back to the Dashboard
  CvSource outputStream = CameraServer.getInstance().putVideo("Rectangle", 320, 240);
  //imu.reset();
  //imu.calibrate();

  arm.armTal1.setSelectedSensorPosition(0);
  arm.armTal2.setSelectedSensorPosition(0);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    SmartDashboard.putNumber("Accel-Z", imu.getAccelZ());//not used currently, might be used later

    // SmartDashboard.putNumber("FR talon current", drive.talonFR.getOutputCurrent());//outputs the current of the talons to the dashboard
    // SmartDashboard.putNumber("FL talon current", drive.talonFL.getOutputCurrent());
    // SmartDashboard.putNumber("BR talon current", drive.talonBR.getOutputCurrent());
    // SmartDashboard.putNumber("BL talon current", drive.talonBL.getOutputCurrent());

    SmartDashboard.putNumber("Bottom joing talon current", arm.armTal1.getOutputCurrent());
     SmartDashboard.putNumber("Top joint talon current", arm.armTal2.getOutputCurrent());

    SmartDashboard.putNumber("Arm Encoder 1", arm.armTal1.getSelectedSensorPosition());
    SmartDashboard.putNumber("Arm Encoder 2", arm.armTal2.getSelectedSensorPosition());
   
    //SmartDashboard.putBoolean("light", lightSensor.get());//used for testing if the light sensor is detecting light or not
    
    SmartDashboard.putNumber("left encoder", drive.talonFL.getSelectedSensorPosition());//puts the encoder values on the drive 
    //SmartDashboard.putNumber("right encoder", drive.talonFR.getSelectedSensorPosition());
   // SmartDashboard.putBoolean("Talons:", drive.turned);//tells the driver if the robot has turned
    SmartDashboard.putNumber("Angle of the z axis", drive.getAngle());//this gives the angle of the robot relative to how it started
    SmartDashboard.putNumber("pressure", pressure.getValue());
    SmartDashboard.putNumber("joy value", arm.armController.getY(GenericHID.Hand.kRight));
  }

  public void teleopInit(){
    SmartDashboard.putNumber("tal1", 0);
    SmartDashboard.putNumber("tal2", 0);
    SmartDashboard.putNumber("eh", 0);
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    arm.controlArm(); //Arm control method
    
    drive.tankDrive(joyL.getRawAxis(1), joyR.getRawAxis(1));  //Takes joystick inputs, curves inputs
    // and sets motors to curved amount
    if(joyL.getRawButton(8)){//if right bumper is pressed
      if(canPressComp){//if button press will tilt
        //set it to the opposite value
        compGo = !compGo;
        if(compGo){
         comp.start();
        }else{
          comp.stop();
        }
      }
  canPressComp = false;//button press will no longer tilt
}else{//right bumper isnt pressed 
  canPressComp = true;//button press is able to tilt
}
position[0] = SmartDashboard.getNumber("tal1", 0.0);
position[1] = SmartDashboard.getNumber("tal2", 0.0);
position[2] = SmartDashboard.getNumber("eh", 0.0);
  }


  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
