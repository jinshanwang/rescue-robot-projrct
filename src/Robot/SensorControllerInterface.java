package Robot;

public interface SensorControllerInterface {
  // Variable defining colour registered by colour sensor.
  public enum Colour {
    NONE, // Corresponds to colour ID 0
    BLACK, // Corresponds to colour ID 1
    BLUE, // Corresponds to colour ID 2
    GREEN, // Corresponds to colour ID 3
    YELLOW, // Corresponds to colour ID 4
    RED, // Corresponds to colour ID 5
    WHITE, // Corresponds to colour ID 6
    BROWN, // Corresponds to colour ID 7
    ERROR, // Colour sensor fails
  }

  // Method obtains and returns the colour registered by the colour sensor.
  public Colour getColour();

  // Method returns the distance obtained by the ultrasonic sensor.
  public float getDistanceToObject();

  // Method returns status of the tacticle touch sensor.
  public float getTouchSensor();
}
