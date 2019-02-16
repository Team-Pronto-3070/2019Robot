package frc.robot;

public interface Pronstants {
    //switch back the arm talons
    public static final int ARMTAL1_PORT = 1; //Talon ports
    public static final int ARMTAL2_PORT = 6; 
    public static final int TALONFR_PORT = 12;
    public static final int TALONBR_PORT = 13;
    public static final int TALONFL_PORT = 14;
    public static final int TALONBL_PORT = 15;

    public static final int JOYL_PORT = 0; //Joystick ports
    public static final int JOYR_PORT = 1;
    public static final int ARMCONT_PORT = 2;

    public static final int SUCCSOL_PORT1 = 0; //TODO assign port number Suction cup solenoid*
    public static final int TILTSOL_PORT1 = 2; //TODO assign port number Tilt piston solenoid*
    public static final int SUCCSOL_PORT2 = 1; //TODO assign port number Suction cup solenoid*
    public static final int TILTSOL_PORT2 = 3; //TODO assign port number Tilt piston solenoid*


    public static final double DEADZONE = .1; //Drive joystick deadzone

    public static final double GYRO_DEADZONE = 1.0;//Gyro deadzone


    public static final int LINE_PORT_1 = 0;
    public static final int LINE_PORT_2 = 1;

    public static final double TURN_SPEED = .5; //Bot turning speed
    public static final double TAL_MAX_VALUE = 400; //Talon maximum speed in RPM

    public static final int JOINT_SELECT_BUTTON = 0;

    public static final double[] PREPARE_HATCH_GROUND = {0,0,0};
    public static final double[] PREPARE_BALL_GROUND = {0,0,0};
    public static final double[] FIRST_LEVEL_HATCH = {166,542,0};//19 inches
    public static final double[] SECOND_LEVEL_HATCH = {-210,-132,0};//47 inches
    public static final double[] FIRST_LEVEL_BALL = {-2,355,0};//27.5 inches
    public static final double[] SECOND_LEVEL_BALL = {-119,-328,0};//55.5 inches
    public static final double[] CARGO_SHIP_BALL = {0,0,0};
    public static final double[] NO_THING = {5,3,6};

    public static final double[] RESET = {0,0,0};

    public static final double ARM_MOE = 2; //Margin of error for arm angles, in degrees

    public static final double DRIVE_SCALER = .3;
}