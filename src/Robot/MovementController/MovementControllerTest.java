package Robot.MovementController;
import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;

public class MovementControllerTest {
  public static void main(String[] args) {
    System.out.println("Hello!");

    EV3 ev3 = (EV3) BrickFinder.getLocal();
    ev3.getTextLCD().drawString("Hello China", 4, 4);
    //      Delay.msDelay(100000);

    float cm = 20; // Test distance variable.
    int degrees = 180; // Test value for angular rotation.

    System.out.println("World");

    MovementController movement = new MovementController();
    System.out.println("This is me");

    // test robot travel/movement function - expected move distance 'cm'
    //  movement.moveForward(cm);
    System.out.println("Moving.");

    // Wait for 3 seconds
    //  Delay.msDelay(3000);
    System.out.println("Waiting.");

    // test robot rotation function - expected 90 degree rotation;
    movement.rotateRobot(degrees);
    System.out.println("Rotating.");
  }
}
