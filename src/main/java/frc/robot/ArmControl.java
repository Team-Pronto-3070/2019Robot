package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID;
import com.ctre.phoenix.motorcontrol.*;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;



public class ArmControl implements Pronstants{

    XboxController armController;
    TalonSRX armTal1, armTal2;
    DoubleSolenoid succSol, tiltSol;
    boolean canPressTilt = true;

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

        succSol = new DoubleSolenoid(SUCCSOL_PORT1, SUCCSOL_PORT2); 
        tiltSol = new DoubleSolenoid(TILTSOL_PORT1, TILTSOL_PORT2);
        

        // armTal1.configFactoryDefault();
        // armTal2.configFactoryDefault();
        armTal2.setSelectedSensorPosition(0);
        // armTal1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        // armTal2.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

        armTal1.setInverted(false);
        armTal2.setInverted(true);//TODO: Check which talon of the arm needs to be inverted

    }

    public void stop(){
        armTal1.set(ControlMode.PercentOutput, 0);
        armTal2.set(ControlMode.PercentOutput, 0);
    }

    public double encToAngle1(){
        return ((armTal1.getSelectedSensorPosition() * DEGREES_PER_TICK) % 360);
    }

    public double encToAngle2(){
        return ((armTal2.getSelectedSensorPosition() * DEGREES_PER_TICK) % 360);
    }



    public void controlArm(){
        
            manualArmControl();
            tilt();           
           autoArmControl();
        
        succSol.set(armController.getBButton() ? Value.kForward : Value.kReverse); //When B button is pressed, suction is on. When it isn't pressed it turns off
    }

    public void tilt(){
        if(armController.getBumperPressed(Hand.kRight)){
            if(canPressTilt){
                tiltSol.set(tiltSol.get() == Value.kReverse ? Value.kForward : Value.kReverse);
            }
        canPressTilt = false;
    }else{
        canPressTilt = true;
    }
        }
    

   

    public void manualArmControl(){
        if(armController.getY(GenericHID.Hand.kLeft) > DEADZONE){ //if joystick is being used
            armTal1.set(ControlMode.PercentOutput, armController.getY(GenericHID.Hand.kLeft));
        } else if(armController.getY(GenericHID.Hand.kLeft) < -DEADZONE){ //if joystick is being used
            armTal1.set(ControlMode.PercentOutput, armController.getY(GenericHID.Hand.kLeft));
        } 
        else {
            armTal1.set(ControlMode.PercentOutput, 0);
        }
        if(armController.getY(GenericHID.Hand.kRight) > DEADZONE){ //if joystick is being used
            armTal2.set(ControlMode.PercentOutput, armController.getY(GenericHID.Hand.kRight));
        } else if(armController.getY(GenericHID.Hand.kRight) < -DEADZONE){
            armTal2.set(ControlMode.PercentOutput, armController.getY(GenericHID.Hand.kRight));
        } 
        else {
            armTal2.set(ControlMode.PercentOutput, 0);
        }
    }
    
    public void autoArmControl(){
        ArmPosition pos = ArmPosition.reset;
         switch(pos){
             case prepareHatchGround:
                 if(moveArm(PREPARE_HATCH_GROUND));
                 break;
             case prepareHatchWall:
                 if(moveArm(PREPARE_HATCH_WALL));
                 break;
             case prepareBall:
                 if(moveArm(PREPARE_BALL));
                 break;
             case firstLevelHatch:
                 if(moveArm(FIRST_LEVEL_HATCH));
                 break;
             case secondLevelHatch:
                 if(moveArm(SECOND_LEVEL_HATCH));
                 break;
             case firstLevelBall:
                 if(moveArm(FIRST_LEVEL_BALL));
                 break;
             case secondLevelBall:
                 if(moveArm(SECOND_LEVEL_BALL));
                 break;
             case reset:
             default:
                 if(moveArm(RESET));
                 break;
             }
     }

    public Value doubleToSol(double d){
        if(d == 1){
            return Value.kForward;
        } else {
            return Value.kReverse;
        }
    }
    

     public boolean moveArm(double[] angles){
         double joint1 = armTal1.getSelectedSensorPosition();
         double joint2 = armTal1.getSelectedSensorPosition();
         if(joint1==0){
             joint1 = 1;
         }
         if(joint2==0){
             joint2 = 1;
         }
        double shoulderRatio = (angles[0] - joint1) / joint1;
         if(shoulderRatio > 1){
             shoulderRatio = 1;
         } else if (shoulderRatio < -1){
             shoulderRatio = -1;
         }
         double elbowRatio = angles[1] - joint2 / joint2;
         if(elbowRatio > 1){
             elbowRatio = 1;
         } else if (elbowRatio < -1){
             elbowRatio = -1;
         }
      
         armTal1.set(ControlMode.PercentOutput, shoulderRatio);
         armTal2.set(ControlMode.PercentOutput, elbowRatio);
         tiltSol.set(doubleToSol(angles[2]));
        if(Math.abs(encToAngle1() - angles[0]) < ARM_MOE
            && Math.abs(encToAngle2() - angles[1]) < ARM_MOE){
             return true;
         } else {
             return false;
         }
     }
}