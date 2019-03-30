package frc.robot;

//imports
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.GenericHID;
import com.ctre.phoenix.motorcontrol.*;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

public class ArmControl implements Pronstants {
    XboxController armController;
    TalonSRX shoulderTal;
    DoubleSolenoid succSol, tiltSol;
    Solenoid vacuumSol;
    AnalogInput succSensor;
    boolean sucking = false;
    boolean vacuum = true;

    public static final double SUCC_MIN = 1200; // TODO Assign value to placeholder for minimum vacuum value
    public static final double SUCC_MAX = 1300; // TODO Assign value to placeholder for maximum vacuum value

    public ArmControl() {
        armController = new XboxController(ARMCONT_PORT);

        shoulderTal = new TalonSRX(SHOULDER_TAL_PORT); // Talon for shoulder joint

        succSol = new DoubleSolenoid(SUCCSOL_PORT1, SUCCSOL_PORT2);
        tiltSol = new DoubleSolenoid(TILTSOL_PORT1, TILTSOL_PORT2);
        vacuumSol = new Solenoid(VACUSOL_PORT);

        succSensor = new AnalogInput(SUCC_SENSOR_PORT);

        configTal(false, shoulderTal);
    }

    /**
     * Stops the arm
     */
    public void stop() {
        shoulderTal.set(ControlMode.PercentOutput, 0);
    }

    /**
     * Combines all the arm methods into one easy to use method
     */
    public void controlArm() {
       manualArmControl();

        if (armController.getBumperPressed(Hand.kRight)) {
            tiltSol.set(tiltSol.get() == Value.kReverse ? Value.kForward : Value.kReverse);
        }
        if(sucking){
            armController.setRumble(RumbleType.kLeftRumble, 0.2);
            suctionTimer();
        }
        if (armController.getBumperPressed(Hand.kLeft)) {
            sucking = !sucking;
            if (sucking) { // When right trigger is pressed, suction is on. When it isn't pressed it turns
                armController.setRumble(RumbleType.kLeftRumble, 0.2);
            } else {
                armController.setRumble(RumbleType.kLeftRumble, 0);
                succSol.set(Value.kForward);
                vacuumSol.set(true);
            }
        }
    }

    /**
     * takes in the joystick values from both of the xbox joysticks and moves the
     * corresponding talons
     */
    public void manualArmControl() {

        if (Math.abs(armController.getY(GenericHID.Hand.kLeft)) > DEADZONE) { // if joystick is being used
            shoulderTal.set(ControlMode.PercentOutput, armController.getY(GenericHID.Hand.kLeft));
        } else {
            shoulderTal.set(ControlMode.PercentOutput, 0);
        }
    }

    public void suctionTimer() {
        if (succSensor.getValue() > SUCC_MAX) {
            SmartDashboard.putBoolean("vaccumed", false);
            succSol.set(Value.kReverse);
            vacuumSol.set(true);
        } else if (succSensor.getValue() < SUCC_MIN) {
            SmartDashboard.putBoolean("vaccumed", true);
            succSol.set(Value.kForward);
            vacuumSol.set(false);
        }
    }

    /**
     * returns the action of the arm based off of the button pressed
     * 
     * @return returns null if no buttons are pressed
     */
    public double[] getWantedState() {
        if (armController.getAButton()) {
            return PREPARE_HATCH_GROUND;// certain angle 1
        } else if (armController.getXButton()) {
            return PREPARE_BALL_GROUND;// certain angle 1
        } else if (armController.getPOV() == 0) {
            return FIRST_LEVEL_HATCH;// certain angle 2
        } else if (armController.getPOV() == 180) {
            return SECOND_LEVEL_HATCH;
        } else if (armController.getPOV() == 270) {
            return FIRST_LEVEL_BALL;
        } else if (armController.getPOV() == 90) {
            return SECOND_LEVEL_BALL;
        } else if (armController.getStartButton()) {
            return RESET;
        } else {
            return null;
        }
    }
    

    public void configTal(boolean inverted, TalonSRX talon) {
        talon.configFactoryDefault();
        talon.setInverted(inverted);
    }


    public void moveArm(double[] encValues) {

        double joint1 = shoulderTal.getSelectedSensorPosition();
        double shoulderRatio = .15 + ((encValues[0] - joint1) / 50);
        SmartDashboard.putNumber("shoulderRatio", shoulderRatio);
        if (shoulderRatio >= 1) {
            shoulderRatio = 1;
        } else if (shoulderRatio <= -1) {
            shoulderRatio = -1;
        }
        if (Math.abs(joint1 - encValues[0]) < ARM_MOE) {
            shoulderRatio = 0;
        }
        SmartDashboard.putNumber("encvalues1", encValues[0]);
        SmartDashboard.putNumber("encvalue2", encValues[1]);
        SmartDashboard.putNumber("joint1", joint1);

        shoulderTal.set(ControlMode.Position, shoulderRatio);
        
    }

    public void moveArmV2(double[] encValues) {
        if (Math.abs(shoulderTal.getSelectedSensorPosition() - encValues[0]) > ARM_MOE) {
            shoulderTal.set(ControlMode.PercentOutput, .7);
        } else {
            shoulderTal.set(ControlMode.PercentOutput, 0);
        }
    }

    public double getSuccValue() {
        return succSensor.getValue() * SUCC_CONSTANT;

    }
}