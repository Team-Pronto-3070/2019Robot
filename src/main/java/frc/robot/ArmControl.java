package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID;
import com.ctre.phoenix.motorcontrol.*;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;



public class ArmControl implements Pronstants{

    XboxController armController;
    TalonSRX armTal1, armTal2;
    Solenoid handSol, tiltSol;
    boolean succToggle = true;
    boolean manualToggle = false;

    public enum ArmPosition{
        prepareHatchGround, prepareHatchWall, prepareBall,
        pickupBall, firstLevelHatch,
        secondLevelHatch, firstLevelBall,
        secondLevelBall, reset
    }
    ArmPosition state;

    public ArmControl(){
        armController = new XboxController(ARMCONT_PORT);


        armTal1 = new TalonSRX(ARMTAL1_PORT); //Talon for shoulder joint
        armTal2 = new TalonSRX(ARMTAL2_PORT); //Talon for elbow joint 

        handSol = new Solenoid(HANDSOL_PORT);
        tiltSol = new Solenoid(TILTSOL_PORT);
        

        armTal1.configFactoryDefault();
        armTal2.configFactoryDefault();

        armTal1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        armTal2.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
    }

    public void stop(){
        armTal1.set(ControlMode.PercentOutput, 0);
        armTal2.set(ControlMode.PercentOutput, 0);
    }

    public void radialNerve(){
        if(armController.getY(GenericHID.Hand.kLeft) > DEADZONE){ //If joystick is being used
            armTal1.set(ControlMode.PercentOutput, armController.getY(GenericHID.Hand.kLeft));
        } else {
            armTal1.set(ControlMode.PercentOutput, 0);
        }
        if(armController.getY(GenericHID.Hand.kRight) > DEADZONE){ //If joystick is being used
            armTal1.set(ControlMode.PercentOutput, armController.getY(GenericHID.Hand.kRight));
        } else {
            armTal1.set(ControlMode.PercentOutput, 0);
        }
        if(armController.getBackButtonPressed()){
            manualToggle = !manualToggle;
        }
        if(manualToggle){
            tilt();
            giveEmTheSucc();
        } else {
            autoArmControl();
        }
    }

    public void tilt(){
        if(armController.getBumperPressed(Hand.kRight)){
            tiltSol.set(!tiltSol.get());
        }
    }

    public void giveEmTheSucc(){ //Suction cup method
        if(succToggle){ //If boolean is true
            handSol.set(armController.getBButton()); //When B button is pressed, suction is on. When it isn't pressed it turns off
        } else { //If boolean is false
            if(armController.getBButtonPressed()){ //Press B button once, suction turns on. Press it again, it turns off
                handSol.set(!handSol.get());
            }
        }
        if(armController.getStartButton()){ //Boolean toggle is toggled with Start button on xbox controller
            succToggle = !succToggle;
        }
    }

    
    public void autoArmControl(){
        ArmPosition pos = ArmPosition.reset;
        switch(pos){
            case prepareHatchGround:
                
                break;
            case prepareHatchWall:
                
                break;
            case prepareBall:
                
                break;
            case firstLevelHatch:
                
                break;
            case secondLevelHatch:
                
                break;
            case firstLevelBall:
                
                break;
            case secondLevelBall:
                
                break;
            case reset:
            default:
                
                break;
            }
    }

    public boolean doubleToBool(double d){
        if(d == 1){
            return true;
        } else {
            return false;
        }
    }

    public boolean moveArm(double shoulderAngle, double elbowAngle, double wristState){

        double shoulderRatio = shoulderAngle / armTal1.getSelectedSensorPosition();
        if(shoulderRatio > 1){
            shoulderRatio = 1;
        } else if (shoulderRatio < -1){
            shoulderRatio = -1;
        }
        double elbowRatio = elbowAngle / armTal2.getSelectedSensorPosition();
        if(elbowRatio > 1){
            elbowRatio = 1;
        } else if (elbowRatio < -1){
            elbowRatio = -1;
        }
        
        armTal1.set(ControlMode.PercentOutput, shoulderRatio);
        armTal2.set(ControlMode.PercentOutput, elbowRatio);
        tiltSol.set(doubleToBool(wristState));

        if(Math.abs(armTal1.getSelectedSensorPosition() - shoulderAngle) < ARM_MOE
           && Math.abs(armTal2.getSelectedSensorPosition() - elbowAngle) < ARM_MOE){
            return true;
        } else {
            return false;
        }
    }
}