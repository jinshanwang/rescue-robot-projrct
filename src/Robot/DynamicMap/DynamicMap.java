package Robot.DynamicMap;
import Robot.DynamicMapInterface;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class DynamicMap implements DynamicMapInterface {
  private int minRow;
  private int maxRow;
  private int minColumn;
  private int maxColumn;
  private int xExpansions;
  private int yExpansions;
  private List<List<Object>> grid = new ArrayList<List<Object>>();
  private Point origin = new Point(0, 0);
  private Point robotLocation = new Point();
  private Point robotGridLocation = new Point();

  public void init() {
    minRow = 0;
    maxRow = 0;
    minColumn = 0;
    maxColumn = 0;
    grid.add(new ArrayList<Object>());
    grid.get(0).add(Object.ROBOT);
    robotLocation.setLocation(0, 0);
    robotGridLocation.setLocation(0, 0);
  }

  public int getMinRow() {
    return minRow;
  }

  public int getMinColumn() {
    return minColumn;
  }

  public int getMaxRow() {
    return maxRow;
  }

  public int getMaxColumn() {
    return maxColumn;
  }

  public float gridUnitsToCentimetres(float gridUnits) {
    return gridUnits * 24;
  }

  public float centimetresToGridUnits(float cm) {
    return cm / 24;
  }

  public Point getRobotLocation() {
    return robotLocation;
  }

  public void setRobotLocation(Point p) {
    if ((p.x < minColumn) || (p.y < minRow) || (p.x > maxColumn) || (p.y > maxRow)) {
      expandGridTo(p);
    }

    int xCoord = p.x + origin.x;
    int yCoord = p.y + origin.y;
    robotGridLocation.setLocation(robotLocation.x + origin.x, robotLocation.y + origin.y);
    grid.get(robotGridLocation.x).set(robotGridLocation.y, Object.EMPTY);
    grid.get(xCoord).set(yCoord, Object.ROBOT);
    robotLocation.setLocation(p);
  }

  public Object getObjectAtLocation(Point p) {
    int xCoord = p.x + origin.x;
    int yCoord = p.y + origin.y;
    return grid.get(xCoord).get(yCoord);
  }

  public void setObjectAtLocation(Point p, Object o) {
    if ((p.x < minColumn) || (p.y < minRow) || (p.x > maxColumn) || (p.y > maxRow)) {
      expandGridTo(p);
    }

    int xCoord = p.x + origin.x;
    int yCoord = p.y + origin.y;
    grid.get(xCoord).set(yCoord, o);
  }

  public boolean isFullyExplored() {
    for (int i = 0; i < grid.size(); ++i) {
      for (int j = 0; j < grid.get(i).size(); ++j) {
        if (grid.get(i).get(j) == Object.UNKNOWN) {
          return false;
        }
      }
    }
    return true;
  }

  private void expandGridTo(Point p) {
	System.out.println(p.toString() + " caused expansion.");
    if (p.x < minColumn) {
      xExpansions = minColumn - p.x;
      minColumn = p.x;
      origin.translate(xExpansions, 0);
      for (int i = xExpansions; i > 0; --i) {
        grid.add(0, new ArrayList<Object>());
        for (int j = 0; j <= grid.get(1).size(); ++j) {
          grid.get(0).add(Object.UNKNOWN);
        }
      }
    }

    if (p.x > maxColumn) {
      xExpansions = p.x - maxColumn;
      maxColumn = p.x;
      for (int i = 0; i < xExpansions; ++i) {
        grid.add(new ArrayList<Object>());
        for (int j = 0; j <= grid.get(0).size(); ++j) {
          grid.get(grid.size() - 1).add(Object.UNKNOWN);
        }
      }
    }

    if (p.y < minRow) {
      yExpansions = minRow - p.y;
      minRow = p.y;
      origin.translate(0, yExpansions);
      for (int i = yExpansions; i > 0; --i) {
        for (int j = 0; j < grid.size(); ++j) {
          grid.get(j).add(0, Object.UNKNOWN);
        }
      }
    }

    if (p.y > maxRow) {
      yExpansions = p.y - maxRow;
      maxRow = p.y;
      for (int i = 0; i <= grid.size() - 1; ++i) {
        for (int j = 0; j < yExpansions; ++j) {
          grid.get(i).add(Object.UNKNOWN);
        }
      }
    }
  }

  public void printGrid() {
    System.err.print("Origin: (" + origin.x + ", " + origin.y + ")\n");
    System.err.print("MinR: " + minRow + ", MinC: " + minColumn + ", MaxR: " + maxRow);
    System.err.print(", MaxC: " + maxRow + "\n\n");

    for (int i = 0; i <= maxRow - minRow; ++i) {
      System.err.print("[");
      for (int j = 0; j <= maxColumn - minColumn; ++j) {
        System.err.print(grid.get(j).get(i) + ",");
      }
      System.err.print("]\n");
    }
    System.err.print("******************************************************\n\n");
  }

  public void fromString(String str) {
    int row = 0, column = 0;
    for (char c : str.toCharArray()) {
      System.out.println(new Point(row, column).toString() + " ---");
    	Point p = new Point(0, 0);
      System.out.println(p.toString() + " is now " + grid.get(0).get(0));
      
      switch (c) {
        case '-':
          setObjectAtLocation(new Point(row, column), Object.UNKNOWN);
          break;
        case ' ':
          setObjectAtLocation(new Point(row, column), Object.EMPTY);
          break;
        case 'S':
          setObjectAtLocation(new Point(row, column), Object.SURVIVOR);
          break;
        case 'O':
          setObjectAtLocation(new Point(row, column), Object.OBSTACLE);
          break;
        case 'X':
          setObjectAtLocation(new Point(row, column), Object.OUT_OF_BOUNDS);
          break;
        case 'U':
          setObjectAtLocation(new Point(row, column), Object.UNREACHABLE);
          break;
        case 'B':
          setObjectAtLocation(new Point(row, column), Object.BORDER);
          break;
        case 'R':
          Point fauxRobotLocation = new Point(getRobotLocation());
          Object objectAtFauxRobot = getObjectAtLocation(getRobotLocation());
          setRobotLocation(new Point(row, column));
          setObjectAtLocation(fauxRobotLocation, objectAtFauxRobot);
          break;
        case 'E':
          setObjectAtLocation(new Point(row, column), Object.ERROR);
          break;
        case '\n':
          row++;
          column = -1;
          break;
      }
      column++;
    }
  }

  // TODO(mitch): Add toString/fromString to interface.
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int i = getMinColumn(); i <= getMaxColumn(); ++i) {
      for (int j = getMinRow(); j <= getMaxRow(); ++j) {
        switch (getObjectAtLocation(new Point(i, j))) {
          case UNKNOWN:
            builder.append('-');
            break;
          case EMPTY:
            builder.append(' ');
            break;
          case SURVIVOR:
            builder.append('S');
            break;
          case OBSTACLE:
            builder.append('O');
            break;
          case OUT_OF_BOUNDS:
            builder.append('X');
            break;
          case UNREACHABLE:
            builder.append('U');
            break;
          case BORDER:
            builder.append('B');
            break;
          case ROBOT:
            builder.append('R');
            break;
          case ERROR:
            builder.append('E');
            break;
        }
      }
      builder.append('\n');
    }
    return builder.toString();
  }
}