package Client.UserInterface;
import Robot.DynamicMapInterface;

import java.awt.*;

public interface Gui {
  public static final Color SILVER = new Color(192, 192, 192);
  public static final Color DARK_GREY = new Color(105, 105, 105);
  public static final Color LIGHT_BLUE = new Color(135, 206, 250);
  public static final Color LIGHT_GREY = new Color(245, 245, 245);
  public static final Color DARKEST_GREY = new Color(32, 32, 32);
  public static final Color LIGHT_RED = new Color(240, 128, 128);

  // Connect gui to other classes
  public void connect(DisplayControllerInterface _displayController);

  // Initialise the gui
  public void init();

  // Changes console text
  public void setConsoleLabel(String test);

  // Changes button colour
  public void setButtonColor(String button, Color colour);

  // Changes button text
  public void setButtonText(String button, String text);

  // Prints the map
  public void setMap(DynamicMapInterface map);
}
