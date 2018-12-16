package Robot;

public interface MovementControllerInterface {
  // Rotates the robot by the set amount of degrees. Positive values represent clockwise rotation,
  // negative values represent counter-clockwise rotation. Values outside the range of [-180, 180]
  // may be clamped.
  public void rotateRobot(int degrees);

  // Move forward by the distance provided in 'cm'.
  public void moveForward(float cm);

  // Methods to actuate motor for mounted colour sensor.
  public void raiseColourSensor();
  public void lowerColourSensor();
}
