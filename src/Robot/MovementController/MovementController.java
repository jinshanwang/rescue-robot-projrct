package Robot.MovementController;
import Robot.MovementControllerInterface;
import lejos.hardware.motor.Motor;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;

public class MovementController implements MovementControllerInterface {
  // Declaring constants.
  private static final double TRACK_WIDTH =
      12; // Defining constant distance between wheels of the robot.
  private static final int DEFAULT_ANGLE = 0; // Default angle of colour sensor motor (degrees).
  private static final int LIMIT_ANGLE = 90; // Limit angle of colour sensor motor (degrees).
  private static final double WHEEL_SIZE = 5.6;

  // Declaring class members.
  private Wheel leftWheel;
  private Wheel rightWheel;
  private Chassis chassis;
  private MovePilot pilot;
  // private EV3MediumRegulatedMotor sensorMotor;

  public MovementController() {
    // Defining robot wheels and associated motor assignment.
    leftWheel = WheeledChassis.modelWheel(Motor.A, WHEEL_SIZE).offset(-TRACK_WIDTH);
    rightWheel = WheeledChassis.modelWheel(Motor.C, WHEEL_SIZE).offset(TRACK_WIDTH);

    // Creating robot chassis as differential and declaring a pilot.
    chassis =
        new WheeledChassis(new Wheel[] {leftWheel, rightWheel}, WheeledChassis.TYPE_DIFFERENTIAL);
    pilot = new MovePilot(chassis);

    // Defining motor for colour sensor rotation.
    // sensorMotor = new EV3MediumRegulatedMotor(MotorPort.B);

    // Setting motor speeds for travel, rotate and colour sensor lower/raise functions.
    pilot.setLinearSpeed(10); // Speed in cm/second.
    pilot.setAngularSpeed(45); // Rotation in degrees/second.
    // sensorMotor.setSpeed(180);  //Rotation speed in degrees/second.
  }

  // Rotate robot a defined number of degrees.
  public void rotateRobot(int degrees) {
    float modifier = -0.9f;
    if (degrees > 0) {
      modifier = 0.7f;
    }
    pilot.rotate(modifier + (degrees / 2) * ((float) 7.0 / 8));
  }

  // Move robot the defined distance (cm is a float value) forward.
  public void moveForward(float cm) {
    double distance = cm; // Convert float to double for use in travel method.
    pilot.travel(distance);
  }

  public void raiseColourSensor() {
    // sensorMotor.rotate(DEFAULT_ANGLE);
  }

  public void lowerColourSensor() {
    // sensorMotor.rotate(LIMIT_ANGLE);
  }
}
