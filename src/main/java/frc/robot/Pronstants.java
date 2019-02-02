package frc.robot;

public interface Pronstants {
    
    public static final int ARMTAL1_PORT = 1; //Talon ports
    public static final int ARMTAL2_PORT = 2; //the arm ports need adjustment
    public static final int TALONFL_PORT = 15;
    public static final int TALONBL_PORT = 14;
    public static final int TALONFR_PORT = 12;//15
    public static final int TALONBR_PORT = 13;//14

    public static final int JOYL_PORT = 0; //Joystick ports
    public static final int JOYR_PORT = 1;
    public static final int ARMCONT_PORT = 2;




    public static final double DEADZONE = .2; //Drive joystick deadzone
    public static final double GYRO_DEADZONE = 1.0;//Gyro deadzone

    public static final double TURN_SPEED = .5; //Bot turning speed

    public static final double TAL_MAX_VALUE = 400; //Talon maximum speed in RPM
}