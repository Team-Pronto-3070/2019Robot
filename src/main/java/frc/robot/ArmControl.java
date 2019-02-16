package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.GenericHID;
import com.ctre.phoenix.motorcontrol.*;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;



public class ArmControl implements Pronstants{

    XboxController armController;
    TalonSRX armTal1, armTal2;
    DoubleSolenoid succSol, tiltSol;
    boolean canPressTilt = true, manual = true;


    public ArmControl(){
        armController = new XboxController(ARMCONT_PORT);


        armTal1 = new TalonSRX(ARMTAL1_PORT); //Talon for shoulder joint
        armTal2 = new TalonSRX(ARMTAL2_PORT); //Talon for elbow joint 

        succSol = new DoubleSolenoid(SUCCSOL_PORT1, SUCCSOL_PORT2); 
        tiltSol = new DoubleSolenoid(TILTSOL_PORT1, TILTSOL_PORT2);
        

        armTal1.configFactoryDefault();
        armTal2.configFactoryDefault();
        armTal1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);
        armTal2.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);

        armTal1.setInverted(false);
        armTal2.setInverted(true);//TODO: Check which talon of the arm needs to be inverted

    }

    public void stop(){
        armTal1.set(ControlMode.PercentOutput, 0);
        armTal2.set(ControlMode.PercentOutput, 0);
    }


    public void controlArm(){
            // if (getWantedState() == NO_THING){
                 manualArmControl();
            // }else{
            //     moveArm(getWantedState());
            // }
                

            tilt();  
            armTal1.setSelectedSensorPosition(armController.getBackButtonPressed() ? 0 : armTal1.getSelectedSensorPosition());
            armTal2.setSelectedSensorPosition(armController.getBackButtonPressed() ? 0 : armTal2.getSelectedSensorPosition());
            succSol.set(armController.getBButton() ? Value.kReverse : Value.kForward); //When B button is pressed, suction is on. When it isn't pressed it turns off
    }

    public void tilt(){
        if(armController.getBumperPressed(Hand.kRight)){//if right bumper is pressed
            if(canPressTilt){//if button press will tilt
                //set it to the opposite value
                tiltSol.set(tiltSol.get() == Value.kReverse ? Value.kForward : Value.kReverse);
            }
        canPressTilt = false;//button press will no longer tilt
    }else{//right bumper isnt pressed 
        canPressTilt = true;//button press is able to tilt
    }
        }  

    public void manualArmControl(){
        if(Math.abs(armController.getY(GenericHID.Hand.kLeft)) > DEADZONE){ //if joystick is being used
            armTal1.set(ControlMode.PercentOutput, armController.getY(GenericHID.Hand.kLeft));
        }
        else {
            armTal1.set(ControlMode.PercentOutput, 0);
        }
        if(Math.abs(armController.getY(GenericHID.Hand.kRight)) > DEADZONE){ //if joystick is being used
            armTal2.set(ControlMode.PercentOutput, armController.getY(GenericHID.Hand.kRight));
        } else {
            armTal2.set(ControlMode.PercentOutput, 0);
        }
    }

    public double[] getWantedState(){
        if(armController.getAButtonPressed()){
           return PREPARE_HATCH_GROUND;
        }else if(armController.getXButtonPressed()){
           return PREPARE_BALL_GROUND;
        }else if(armController.getPOV() == 0){
           return FIRST_LEVEL_HATCH;
        }else if(armController.getPOV() == 180){
           return SECOND_LEVEL_HATCH;
        }else if(armController.getPOV() == 270){
           return FIRST_LEVEL_BALL;
        }else if(armController.getPOV() == 90){
           return SECOND_LEVEL_BALL;
        }else if(armController.getStartButtonPressed()){
           return RESET;
        }else{
            return NO_THING;
        }
    }
    
    

    public void moveArm(double[] encValues){
        double joint1 = armTal1.getSelectedSensorPosition();
        double joint2 = armTal2.getSelectedSensorPosition();
        if(joint1==0){
            joint1 = 1;
        }
        if(joint2==0){
            joint2 = 1;
        }
                                //50        -   45  / 45
        double shoulderRatio = (encValues[0] - joint1) / joint1;
        if(shoulderRatio > 1){
            shoulderRatio = 1;
        } else if (shoulderRatio < -1){
            shoulderRatio = -1;
        }

        double elbowRatio = encValues[1] - joint2 / joint2;
        if(elbowRatio > 1){
            elbowRatio = 1;
        } else if (elbowRatio < -1){
            elbowRatio = -1;
        }
        
        SmartDashboard.putNumber("shoulderRatio", shoulderRatio);
        SmartDashboard.putNumber("elbowRatio", elbowRatio);

        armTal1.set(ControlMode.PercentOutput, -shoulderRatio);
        armTal2.set(ControlMode.PercentOutput, -elbowRatio);
        tiltSol.set(encValues[2] == 1 ? Value.kForward : Value.kReverse);

    }
}