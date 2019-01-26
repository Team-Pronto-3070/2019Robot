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
        
    }
}