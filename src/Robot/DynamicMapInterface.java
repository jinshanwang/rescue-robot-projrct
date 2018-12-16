package Robot;

// Point utilized as Point(x - column, y - row)
import java.awt.Point;

public interface DynamicMapInterface {
  public enum Object {
    UNKNOWN,
    EMPTY,
    SURVIVOR,
    OBSTACLE,
    OUT_OF_BOUNDS,
    UNREACHABLE,
    BORDER,
    ROBOT,
    ERROR,
  }

  // Initializes the grid layout
  public void init();

  // Returns the index of the minimum row in the grid
  public int getMinRow();

  // Returns the index of the minimum row in the grid
  public int getMinColumn();

  // Returns the index of the maximum row in the grid
  public int getMaxRow();

  // Returns the index of the maximum row in the grid
  public int getMaxColumn();

  // Returns grid units in CM
  public float gridUnitsToCentimetres(float gridUnits);

  // Returns CM in grid units
  public float centimetresToGridUnits(float cm);

  // Returns the robot's location
  public Point getRobotLocation();

  // Sets the robot's location in the grid
  // Previous robot location is set to EMPTY
  public void setRobotLocation(Point p);

  // Returns the object occupying space at location given by point p(row, column)
  public Object getObjectAtLocation(Point p);

  // Sets the location given by point p(row, column) to object o
  public void setObjectAtLocation(Point p, Object o);

  // Returns true / false depending on whether the grid is fully explored (no unknowns in map)
  // This function is potentially unnecessary and may be removed at later date
  public boolean isFullyExplored();
}