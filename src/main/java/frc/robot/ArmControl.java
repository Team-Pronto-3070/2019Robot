package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID;
import com.ctre.phoenix.motorcontrol.*;
import edu.wpi.first.wpilibj.Encoder;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;


public class ArmControl implements Pronstants{

    XboxController armController;
    TalonSRX ArmTal1, ArmTal2;

    public ArmControl(){
        armController = new XboxController(ARMCONT_PORT);

        ArmTal1 = new TalonSRX(ARMTAL1_PORT); //Talon for shoulder joint
        ArmTal2 = new TalonSRX(ARMTAL2_PORT); //Talon for elbow joint 

        ArmTal1.configFactoryDefault();
        ArmTal2.configFactoryDefault();

        ArmTal1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        ArmTal2.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
    }

    public void stop(){
        ArmTal1.set(ControlMode.PercentOutput, 0);
        ArmTal2.set(ControlMode.PercentOutput, 0);
    }

    public void radialNerve(){
        if(armController.getY(GenericHID.Hand.kLeft) > DEADZONE){ //If joystick is being used
            ArmTal1.set(ControlMode.PercentOutput, armController.getY(GenericHID.Hand.kLeft));
        } else {
            ArmTal1.set(ControlMode.PercentOutput, 0);
        }
        if(armController.getY(GenericHID.Hand.kRight) > DEADZONE){ //If joystick is being used
            ArmTal1.set(ControlMode.PercentOutput, armController.getY(GenericHID.Hand.kRight));
        } else {
            ArmTal1.set(ControlMode.PercentOutput, 0);
        }
    }
}