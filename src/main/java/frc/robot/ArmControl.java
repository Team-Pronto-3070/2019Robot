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
                while(moveArm(PREPARE_HATCH_GROUND));
                break;
            case prepareHatchWall:
                while(moveArm(PREPARE_HATCH_WALL));
                break;
            case prepareBall:
                while(moveArm(PREPARE_BALL));
                break;
            case firstLevelHatch:
                while(moveArm(FIRST_LEVEL_HATCH));
                break;
            case secondLevelHatch:
                while(moveArm(SECOND_LEVEL_HATCH));
                break;
            case firstLevelBall:
                while(moveArm(FIRST_LEVEL_BALL));
                break;
            case secondLevelBall:
                while(moveArm(SECOND_LEVEL_BALL));
                break;
            case reset:
            default:
                while(moveArm(RESET));
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

    public boolean moveArm(double[] angles){

        double shoulderRatio = angles[0] - armTal1.getSelectedSensorPosition() / armTal1.getSelectedSensorPosition();
        if(shoulderRatio > 1){
            shoulderRatio = 1;
        } else if (shoulderRatio < -1){
            shoulderRatio = -1;
        }
        double elbowRatio = angles[1] - armTal2.getSelectedSensorPosition() / armTal2.getSelectedSensorPosition();
        if(elbowRatio > 1){
            elbowRatio = 1;
        } else if (elbowRatio < -1){
            elbowRatio = -1;
        }
        
        armTal1.set(ControlMode.PercentOutput, shoulderRatio);
        armTal2.set(ControlMode.PercentOutput, elbowRatio);
        tiltSol.set(doubleToBool(angles[2]));

        if(Math.abs(armTal1.getSelectedSensorPosition() - angles[0]) < ARM_MOE
           && Math.abs(armTal2.getSelectedSensorPosition() - angles[1]) < ARM_MOE){
            return true;
        } else {
            return false;
        }
    }
}