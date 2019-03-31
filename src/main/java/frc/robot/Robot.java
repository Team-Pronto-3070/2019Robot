/*----------------------   be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
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
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
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

  boolean canPressComp = true;
  boolean compGo = true;

  Drive drive;
  // LineSense lineSense;

  ArmControl arm;
  Joystick joyL, joyR, joyArm;
  // DigitalInput lightSensor;
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

    // lineSense = new LineSense(drive, imu);
    joyL = new Joystick(0);
    joyR = new Joystick(1);
    drive = new Drive(imu, comp);
    arm = new ArmControl();
    comp = new Compressor(0);
    // lightSensor = new DigitalInput(0);
    comp.start();
    arm.succSol.set(Value.kForward);

    // Get the UsbCamera from CameraServer

    UsbCamera front = CameraServer.getInstance().startAutomaticCapture("Front Camera", 0);
    UsbCamera back = CameraServer.getInstance().startAutomaticCapture("Back Camera", 1);
    // Set the resolution
    front.setResolution(320, 240);
    back.setResolution(320, 240);
    front.setExposureManual(80);
    back.setExposureManual(80);
    // Get a CvSink. This will capture Mats from the camera
    CvSink cvSink = CameraServer.getInstance().getVideo();
    // Setup a CvSource. This will send images back to the Dashboard
    CvSource outputStream = CameraServer.getInstance().putVideo("Rectangle", 320, 240);
    // imu.reset();
    // imu.calibrate();

    arm.shoulderTal.setSelectedSensorPosition(0);
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
    SmartDashboard.putNumber("Accel-Z", imu.getAccelZ());// not used currently, might be used later

    SmartDashboard.putNumber("FR talon current", drive.talonFR.getOutputCurrent());//outputs the current of the talons to the dashboard
    SmartDashboard.putNumber("FL talon current", drive.talonFL.getOutputCurrent());
    SmartDashboard.putNumber("BR talon current", drive.talonBR.getOutputCurrent());
    SmartDashboard.putNumber("BL talon current",drive.talonBL.getOutputCurrent());

    SmartDashboard.putNumber("Shoulder talon current", arm.shoulderTal.getOutputCurrent());

    SmartDashboard.putNumber("Shoulder Encoder", arm.shoulderTal.getSelectedSensorPosition());

    // SmartDashboard.putBoolean("light", lightSensor.get());//used for testing if
    // the light sensor is detecting light or not
    // SmartDashboard.putNumber("right encoder",
    // drive.talonFR.getSelectedSensorPosition());
    // SmartDashboard.putBoolean("Talons:", drive.turned);//tells the driver if the
    // robot has turned
    SmartDashboard.putNumber("Angle of the z axis", drive.getAngle());// this gives the angle of the robot relative to
    //                                                                   // how it started
    // SmartDashboard.putNumber("pressure", (pressure.getVoltage() - VOLTS_OFFSET) * VOLT_PSI_RATIO);
    SmartDashboard.putNumber("joy value", arm.armController.getY(GenericHID.Hand.kRight));

    SmartDashboard.putBoolean("sucking", arm.sucking);
    

    SmartDashboard.putBoolean("vacuum", arm.vacuumSol.get());
    SmartDashboard.putBoolean("succ", arm.succSol.get() == Value.kReverse);

    SmartDashboard.putNumber("vaccuum sensor", arm.succSensor.getValue());
  }

  public void teleopInit() {
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    arm.controlArm();
    drive.xboxDrive(); // Takes joystick inputs, curves inputs
    // and sets motors to curved amount

    if (arm.armController.getTriggerAxis(Hand.kRight) == 1) {// if right bumper is pressed
      if (canPressComp) {// if button press will tilt
        // set it to the opposite value
        compGo = !compGo;
        if (compGo) {
          comp.start();
        } else {
          comp.stop();
        }
      }
      canPressComp = false;// button press will no longer tilt
    } else {// right bumper isnt pressed
      canPressComp = true;// button press is able to tilt
    }
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  public void disabledPeriodic() {
    arm.succSol.set(Value.kForward);
  }

  public void autonomousPeriodic() {
    arm.controlArm();
    drive.xboxDrive();
  }
}