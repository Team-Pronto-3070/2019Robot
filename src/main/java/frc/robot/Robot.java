/*----------------------   be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.*;
import java.util.Map;
import java.lang.Double;

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
public class Robot extends IterativeRobot implements Pronstants {
  public static final ADIS16448_IMU imu = new ADIS16448_IMU();
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  Drive drive;
  ArmControl arm;
  Joystick joyL, joyR, joyArm;
  AnalogInput pressure;
  // ShuffleboardTab shuffleboardtab;
  private NetworkTableEntry gyroYawEntry;

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    SmartDashboard.putNumber("Angle", 0);

    drive = new Drive(imu);
    arm = new ArmControl();
    pressure = new AnalogInput(0);

    gyroYawEntry = Shuffleboard.getTab("Gyro").add("Gyro Yaw", new Double(1)).withWidget(BuiltInWidgets.kDial)
        .withProperties(Map.of("min", -180, "max", 180)).getEntry();

    // Get the UsbCamera from CameraServer
    UsbCamera camera = CameraServer.getInstance().startAutomaticCapture("Front Camera", 0);
    UsbCamera line = CameraServer.getInstance().startAutomaticCapture("Line Camera", 1);
    // Set the resolution
    camera.setResolution(320, 240);
    camera.setExposureAuto();
    // Get a CvSink. This will capture Mats from the camera
    CvSink cvSink = CameraServer.getInstance().getVideo();
    // Setup a CvSource. This will send images back to the Dashboard
    CvSource outputStream = CameraServer.getInstance().putVideo("Rectangle", 320, 240);
    // imu.reset();
    imu.calibrate();
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
    SmartDashboard.putNumber("Gyro-X", imu.getAngleX());
    SmartDashboard.putNumber("Gyro-Y", imu.getAngleY());
    SmartDashboard.putNumber("Gyro-Z", imu.getAngleZ());

    SmartDashboard.putNumber("Accel-X", imu.getAccelX());
    SmartDashboard.putNumber("Accel-Y", imu.getAccelY());
    SmartDashboard.putNumber("Accel-Z", imu.getAccelZ());

    SmartDashboard.putNumber("Pitch", imu.getPitch());
    SmartDashboard.putNumber("Roll", imu.getRoll());
    SmartDashboard.putNumber("Yaw", imu.getYaw());

    SmartDashboard.putNumber("Pressure: ", imu.getBarometricPressure());
    SmartDashboard.putNumber("Temperature: ", imu.getTemperature());
    SmartDashboard.putNumber("pressure: ", pressure.getValue());

    gyroYawEntry.setDouble(imu.getYaw());

    SmartDashboard.putNumber("FR talon current", drive.talonFR.getOutputCurrent());
    SmartDashboard.putNumber("FL talon current", drive.talonFL.getOutputCurrent());
    SmartDashboard.putNumber("BR talon current", drive.talonBR.getOutputCurrent());
    SmartDashboard.putNumber("BL talon current", drive.talonBL.getOutputCurrent());

  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString line to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure below with additional strings. If using the SendableChooser
   * make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // autoSelected = SmartDashboard.getString("Auto Selector",
    // defaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
    case kCustomAuto:
      // Put custom auto code here
      break;
    case kDefaultAuto:
    default:
      // Put default auto code here
      break;
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

    // System.out.println("the angle is " + imu.getAngleX()) ;

    // if(joyL.getRawButton(3)) {
    // drive.driveToAngle(90);

    // }else{
    drive.driveRamp();  //Takes joystick inputs, curves inputs
                        // and sets motors to curved amount
    arm.radialNerve(); //Arm control method

    // }
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
