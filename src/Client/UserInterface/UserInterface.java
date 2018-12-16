package Client.UserInterface;
import java.awt.Point;
import java.io.IOException;
import java.net.UnknownHostException;

import Robot.DynamicMapInterface;
import Robot.DynamicMap.DynamicMap;

public class UserInterface {
  public static void main(String[] args) throws UnknownHostException, IOException {
    System.out.println("Hello world!");

    Gui display = new Display();
    DisplayControllerInterface displayController = new DisplayController();

    display.connect(displayController);
    displayController.connect(display);

    display.init();
    
    DynamicMap map = new DynamicMap();
    map.init();
    map.fromString(
    		"-BBB--\n" + 
    		"B   S-\n" + 
    		"B S RB\n" + 
    		"B    B\n" + 
    		"B O  B\n" + 
    		"-B-BB-");
//    map.setObjectAtLocation(new Point(0, 0), DynamicMapInterface.Object.UNKNOWN);
    System.out.println(map.toString());

    while (displayController.tick()) {
    }

    displayController.close();
  }
}
