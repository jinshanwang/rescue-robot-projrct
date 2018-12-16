package Robot.SensorController;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import Robot.SensorControllerInterface;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;

public class SensorController implements SensorControllerInterface {
  // Declaring class members.
  private EV3UltrasonicSensor ultrasonicSensor = null;
  private float[] rangeSample;
  private SampleProvider distanceReading;
  private EV3ColorSensor colourSensor = null;
  private float[] colourSample;
  private SampleProvider colourReading;
  private EV3TouchSensor touchSensor = null;
  private float[] touchSample;
  private SampleProvider touchReading;

  public SensorController() {
    // Defining US sensor and sampler.
    ultrasonicSensor = new EV3UltrasonicSensor(SensorPort.S4);

    distanceReading = ultrasonicSensor.getDistanceMode();
    rangeSample = new float[distanceReading.sampleSize()];

    // Defining colour sensor and sample provider.
    colourSensor = new EV3ColorSensor(SensorPort.S3);

    colourReading = colourSensor.getColorIDMode();
    colourSample = new float[colourReading.sampleSize()];

    // Defining touch sensor.
    touchSensor = new EV3TouchSensor(SensorPort.S1);

    touchReading = touchSensor.getTouchMode();
    touchSample = new float[touchReading.sampleSize()];
  }

  public Colour getColour() {
    // Obtain reading from colour sensor.
    colourReading.fetchSample(colourSample, 0);

    Colour colourValue = Colour.NONE;

    // Setting enum value.
    if (colourSample[0] == 0) {
      colourValue = Colour.NONE;
    } else if (colourSample[0] == 1) {
      colourValue = Colour.BLACK;
    } else if (colourSample[0] == 2) {
      colourValue = Colour.BLUE;
    } else if (colourSample[0] == 3) {
      colourValue = Colour.GREEN;
    } else if (colourSample[0] == 4) {
      colourValue = Colour.YELLOW;
    } else if (colourSample[0] == 5) {
      colourValue = Colour.RED;
    } else if (colourSample[0] == 6) {
      colourValue = Colour.WHITE;
    } else if (colourSample[0] == 7) {
      colourValue = Colour.BROWN;
    } else {
      System.out.println("Error in colour detection.");
      colourValue = Colour.ERROR;
    }

    return colourValue;
  }

  public float getDistanceToObject() {
    distanceReading.fetchSample(rangeSample, 0);
    return rangeSample[0] * 100;
  }

  public float getTouchSensor() {
    touchReading.fetchSample(touchSample, 0);

    // For debugging output.
    if (touchSample[0] == 0) {
      System.out.println("Touch sensor is not pressed.");
    } else {
      System.out.println("Touch sensor is pressed.");
    }
    return touchSample[0];
  }
}