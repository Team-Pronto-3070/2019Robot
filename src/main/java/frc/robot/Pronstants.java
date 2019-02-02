package frc.robot;

public interface Pronstants {
    
    public static final int ARMTAL1_PORT = 1; //Talon ports
    public static final int ARMTAL2_PORT = 2;
    public static final int TALONFL_PORT = 15;
    public static final int TALONBL_PORT = 14;
    public static final int TALONFR_PORT = 12;
    public static final int TALONBR_PORT = 13;

    public static final int JOYL_PORT = 0; //Joystick ports
    public static final int JOYR_PORT = 1;
    public static final int ARMCONT_PORT = 2;

    public static final int HANDSOL_PORT = 3; //Placeholder solenoid port

    public static final double DEADZONE = .1; //Drive joystick deadzone
    public static final double GYRO_DEADZONE = 4.6; //Gyro deadzone

    public static final double TURN_SPEED = .5; //Bot turning speed

    public static final double TAL_MAX_VALUE = 400; //Talon maximum speed in RPM

    public static final int JOINT_SELECT_BUTTON = 0;

}