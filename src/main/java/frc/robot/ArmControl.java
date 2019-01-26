package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.*;
import edu.wpi.first.wpilibj.Encoder;

public class ArmControl implements Pronstants{

    Joystick joyArm;
    TalonSRX ArmTal1, ArmTal2;

    public ArmControl(){
        joyArm = new Joystick(JOYARM_PORT);

        ArmTal1 = new TalonSRX(ARMTAL1_PORT); //Talon for shoulder joint
        ArmTal2 = new TalonSRX(ARMTAL2_PORT); //Talon for elbow joint 
    }

    public void stop(){
        ArmTal1.set(ControlMode.PercentOutput, 0);
        ArmTal2.set(ControlMode.PercentOutput, 0);
    }

    public void arm(){
        if(joyArm.getRawAxis(1) > DEADZONE){ //If joystick is being used
            if(joyArm.getRawButton(JOINT_SELECT_BUTTON)){
                ArmTal2.set(ControlMode.PercentOutput, joyArm.getRawAxis(1));
            } else {
                ArmTal1.set(ControlMode.PercentOutput, joyArm.getRawAxis(1));
            }
        } else {
            stop();
        }
    }
}