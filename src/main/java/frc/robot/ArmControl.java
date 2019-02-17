package frc.robot;

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


public class ArmControl implements Pronstants{

    XboxController armController;
    TalonSRX armTal1, armTal2;
    DoubleSolenoid succSol, tiltSol;
    Solenoid vacuumSol;
    Timer timer;
    boolean canPressTilt = true, manual = true;


    public ArmControl(){
        armController = new XboxController(ARMCONT_PORT);

        armTal1 = new TalonSRX(ARMTAL1_PORT); //Talon for shoulder joint
        armTal2 = new TalonSRX(ARMTAL2_PORT); //Talon for elbow joint 

        succSol = new DoubleSolenoid(SUCCSOL_PORT1, SUCCSOL_PORT2); 
        tiltSol = new DoubleSolenoid(TILTSOL_PORT1, TILTSOL_PORT2);
        vacuumSol = new Solenoid(VACUSOL_PORT);

        timer = new Timer();

        armTal1.configFactoryDefault();
        armTal2.configFactoryDefault();
        armTal1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);
        armTal2.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);

        armTal1.setInverted(false);
        armTal2.setInverted(true);//TODO: Check which talon of the arm needs to be inverted

        
		armTal1.configNominalOutputForward(0, kTimeoutMs);
		armTal1.configNominalOutputReverse(0, kTimeoutMs);
		armTal1.configPeakOutputForward(1, kTimeoutMs);
		armTal1.configPeakOutputReverse(-1, kTimeoutMs);

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
            if(armController.getBackButtonPressed()){
                armTal1.setSelectedSensorPosition(0);
                armTal2.setSelectedSensorPosition(0);
            }
            if(armController.getTriggerAxis(Hand.kRight) == 1){  //When right trigger is pressed, suction is on. When it isn't pressed it turns off
                succSol.set(Value.kForward);
                timer.start();
                succAppointment();
            } else {
                succSol.set(Value.kReverse);
            }
            // succSol.set(armController.getTriggerAxis(Hand.kRight) == 1 ? Value.kReverse : Value.kForward);
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
    //armTal1.set()
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
            
        }
    }
    
    public void succAppointment(){
        succSol.set(Value.kForward);
        vacuumSol.set(true);

        if(timer.hasPeriodPassed(1)){
            succSol.set(Value.kReverse);
            vacuumSol.set(false);
        }
        if(timer.hasPeriodPassed(4)){
            succSol.set(Value.kForward);
            vacuumSol.set(true);
        }
    }

    // public void moveArm(double[] encValues){

    //     double joint1 = armTal1.getSelectedSensorPosition();
    //     double joint2 = armTal2.getSelectedSensorPosition();
    //     if(joint1==0){
    //         joint1 = 1;
    //     }
    //     if(joint2==0){
    //         joint2 = 1;
    //     }
    //                             //50        -   45  / 45
    //     double shoulderRatio = 2*((encValues[0] - joint1) / joint1);
    //     SmartDashboard.putNumber("shoulderRatio", shoulderRatio);
    //     if(shoulderRatio > 0){
    //         shoulderRatio = 1;
    //     } else if (shoulderRatio < 0){
    //         shoulderRatio = -1;
    //     }
    //     if(Math.abs(joint1-encValues[0])<ARM_MOE){
    //         shoulderRatio = 0;
    //     }


    //     double elbowRatio = 2*((encValues[1] - joint2) / joint2);
    //     SmartDashboard.putNumber("elbowRatio", elbowRatio);
    //     if(elbowRatio > 0){
    //         elbowRatio = 1;
    //     } else if (elbowRatio < 0){
    //         elbowRatio = -1;
    //     }
        
    //     if(Math.abs(joint2-encValues[1])<ARM_MOE){
    //         elbowRatio = 0;
    //     }
    //     SmartDashboard.putNumber("encvalues1", encValues[0]);
    //     SmartDashboard.putNumber("encvalue2", encValues[1]);
    //     SmartDashboard.putNumber("joint1", joint1);
    //     SmartDashboard.putNumber("joint2", joint2);

    //     armTal1.set(ControlMode.Position, shoulderRatio);
    //     armTal2.set(ControlMode.Position, -elbowRatio);
    //     tiltSol.set(encValues[2] == 1 ? Value.kForward : Value.kReverse);

    // }
}