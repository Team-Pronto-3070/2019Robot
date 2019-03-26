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
    TalonSRX shoulderTal, elbowTal;
    DoubleSolenoid succSol, tiltSol;
    Solenoid vacuumSol;
    AnalogInput succSensor;
    boolean sucking = false;
    boolean vacuum = true;

    public static double SUCC_MIN = 900; // TODO Assign value to placeholder for minimum vacuum value
    public static double SUCC_MAX = 1400; // TODO Assign value to placeholder for maximum vacuum value

    public ArmControl() {
        armController = new XboxController(ARMCONT_PORT);

        shoulderTal = new TalonSRX(SHOULDER_TAL_PORT); // Talon for shoulder joint
        elbowTal = new TalonSRX(ELBOW_TAL_PORT); // Talon for elbow joint

        succSol = new DoubleSolenoid(SUCCSOL_PORT1, SUCCSOL_PORT2);
        tiltSol = new DoubleSolenoid(TILTSOL_PORT1, TILTSOL_PORT2);
        vacuumSol = new Solenoid(VACUSOL_PORT);

        succSensor = new AnalogInput(SUCC_SENSOR_PORT);

        configTal(false, shoulderTal);
        configTal(true, elbowTal);

        tuneTalon(shoulderTal, 0.2481, 0, 0, 0);
        tuneTalon(elbowTal, 0.2481, 0, 0, 0);
    }

    /**
     * Stops the arm
     */
    public void stop() {
        shoulderTal.set(ControlMode.PercentOutput, 0);
        elbowTal.set(ControlMode.PercentOutput, 0);
    }

    /**
     * Combines all the arm methods into one easy to use method
     */
    public void controlArm() {
        if (getWantedState() == null) {
            manualArmControl();
        } else {
            moveArm(getWantedState());
            // tiltSol.set()
        }

        if (armController.getBumperPressed(Hand.kRight)) {
            tiltSol.set(tiltSol.get() == Value.kReverse ? Value.kForward : Value.kReverse);
        }
        if (armController.getBackButtonPressed()) {
            shoulderTal.setSelectedSensorPosition(0);
            elbowTal.setSelectedSensorPosition(0);
        }
        if (armController.getBumperPressed(Hand.kLeft)) {
            sucking = !sucking;
            if (!sucking) {
                SmartDashboard.putBoolean("forward", true);
                succSol.set(Value.kForward);
                // succSol.set(succSol.get() == Value.kReverse ? Value.kForward :
                // Value.kReverse);
            }
            vacuumSol.set(sucking);
            if (sucking) { // When right trigger is pressed, suction is on. When it isn't pressed it turns
                suctionTimer();
                SmartDashboard.putBoolean("forward", false);
                armController.setRumble(RumbleType.kLeftRumble, 0.2);
            } else {
                armController.setRumble(RumbleType.kLeftRumble, 0);

            }
        }
    }

    /**
     * takes in the joystick values from both of the xbox joysticks and moves the
     * corresponding talons
     */
    public void manualArmControl() { 
        if (armController.getY(GenericHID.Hand.kLeft) < -DEADZONE) { // if joystick is being used
            shoulderTal.set(ControlMode.PercentOutput, armController.getY(GenericHID.Hand.kLeft));
        } else {
            shoulderTal.set(ControlMode.PercentOutput, 0);
        }
        if ((armController.getY(GenericHID.Hand.kRight)) < -DEADZONE) { // if joystick is being used
            elbowTal.set(ControlMode.PercentOutput, armController.getY(GenericHID.Hand.kRight));
        } else {
            elbowTal.set(ControlMode.PercentOutput, 0);
        }
        if (Math.abs(armController.getY(GenericHID.Hand.kLeft)) > DEADZONE) { // if joystick is being used
            shoulderTal.set(ControlMode.PercentOutput, armController.getY(GenericHID.Hand.kLeft));
        } else {
            shoulderTal.set(ControlMode.PercentOutput, 0);
        }
        if (Math.abs(armController.getY(GenericHID.Hand.kRight)) > DEADZONE) { // if joystick is being used
            elbowTal.set(ControlMode.PercentOutput, armController.getY(GenericHID.Hand.kRight));
        } else {
            elbowTal.set(ControlMode.PercentOutput, 0);
        }
    }
    

    public void suctionTimer() {
        if (getSuccValue() > SUCC_MAX) {
            succSol.set(Value.kReverse);
        } else if (getSuccValue() < SUCC_MIN) {
            succSol.set(Value.kForward);
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

    /**
     * sets up a timer for vacuum-hold solenoid
     */
    public void vacuumThing() {

    }

    public void configTal(boolean inverted, TalonSRX talon) {
        talon.configFactoryDefault();
        talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);
        talon.setInverted(inverted);
        talon.configNominalOutputForward(0, PID_TIMEOUT);
        talon.configNominalOutputReverse(0, PID_TIMEOUT);
        talon.configPeakOutputForward(1, PID_TIMEOUT);
        talon.configPeakOutputReverse(-1, PID_TIMEOUT);
        talon.selectProfileSlot(PID_SLOT_IDX, PID_LOOP_IDX);
        talon.configMotionCruiseVelocity(3092, PID_TIMEOUT);
        talon.configMotionAcceleration(3092, PID_TIMEOUT);
    }

    public void tuneTalon(TalonSRX talon, double f, double p, double i, double d) {
        talon.config_kF(PID_SLOT_IDX, f, PID_TIMEOUT);
        talon.config_kP(PID_SLOT_IDX, p, PID_TIMEOUT);
        talon.config_kI(PID_SLOT_IDX, i, PID_TIMEOUT);
        talon.config_kD(PID_SLOT_IDX, d, PID_TIMEOUT);
    }

    public void moveArm(double[] encValues) {

        double joint1 = shoulderTal.getSelectedSensorPosition();
        double joint2 = elbowTal.getSelectedSensorPosition();
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

        double elbowRatio = .15 + ((encValues[1] - joint2) / 50);
        SmartDashboard.putNumber("elbowRatio", elbowRatio);
        if (elbowRatio >= 1) {
            elbowRatio = 1;
        } else if (elbowRatio <= -1) {
            elbowRatio = -1;
        }

        if (Math.abs(joint2 - encValues[1]) < ARM_MOE) {
            elbowRatio = 0;
        }
        SmartDashboard.putNumber("encvalues1", encValues[0]);
        SmartDashboard.putNumber("encvalue2", encValues[1]);
        SmartDashboard.putNumber("joint1", joint1);
        SmartDashboard.putNumber("joint2", joint2);

        shoulderTal.set(ControlMode.Position, shoulderRatio);
        elbowTal.set(ControlMode.Position, -elbowRatio);

    }
    public void moveArmV2(double[] encValues){
        if(Math.abs(shoulderTal.getSelectedSensorPosition() - encValues[0]) > ARM_MOE){
            shoulderTal.set(ControlMode.PercentOutput, .7);
        }else{
            shoulderTal.set(ControlMode.PercentOutput, 0);
        }
        if(Math.abs(elbowTal.getSelectedSensorPosition() - encValues[0]) > ARM_MOE){
            elbowTal.set(ControlMode.PercentOutput, .7);
        }else{
            elbowTal.set(ControlMode.PercentOutput, 0);
        }
    }

    public double getSuccValue(){
        return succSensor.getValue() * SUCC_CONSTANT;

    }
}