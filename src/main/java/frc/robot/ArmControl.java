package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID;
import com.ctre.phoenix.motorcontrol.*;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;


public class ArmControl implements Pronstants{

    XboxController armController;
    TalonSRX armTal1, armTal2;
    Solenoid handSol;
    boolean toggle = true;

    public ArmControl(){
        armController = new XboxController(ARMCONT_PORT);

        armTal1 = new TalonSRX(ARMTAL1_PORT); //Talon for shoulder joint
        armTal2 = new TalonSRX(ARMTAL2_PORT); //Talon for elbow joint 

        handSol = new Solenoid(HANDSOL_PORT);
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
        giveEmTheSucc();
    }

    public void giveEmTheSucc(){
        if(toggle){
            handSol.set(armController.getBButton());
        } else {
            if(armController.getBButtonPressed()){
                handSol.set(handSol.get());
            }
        }
        if(armController.getStartButton()){
            toggle = !toggle;
        }
    }
}