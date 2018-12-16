package Robot.PathFinder;
import java.awt.Point;

import Robot.PathFinderInterface;
import Robot.PathFinderInterface.Status;
import Robot.DynamicMapInterface;
import Robot.MovementControllerInterface;
import Robot.SensorControllerInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Robot.DynamicMap.DynamicMap;

class ControllerStub implements SensorControllerInterface, MovementControllerInterface {
  ControllerStub(DynamicMapInterface dm, List<String> map) {
    dynamicMap = dm;
    testMap = new ArrayList<String>(map);
    for (int y = 0; y < testMap.size(); ++y) {
      for (int x = 0; x < testMap.get(y).length(); ++x) {
        if (testMap.get(y).charAt(x) == 'R') {
          robotLocation = new Point(x * (int) dynamicMap.gridUnitsToCentimetres(1),
              y * (int) dynamicMap.gridUnitsToCentimetres(1));
        }
      }
    }
  }

  // MovementControllerInterface methods.
  public void rotateRobot(int degrees) {
    robotOrientation = (robotOrientation + degrees) % 360;
    if (robotOrientation == -270) {
      robotOrientation = 90;
    } else if (robotOrientation == 270) {
      robotOrientation = -90;
    }
  }

  // Always assumed moving forward by a single gridspace along a cardinal direction.
  public void moveForward(float cm) {
    if (robotOrientation == 0) {
      robotLocation.y -= cm;
    } else if (robotOrientation == 90) {
      robotLocation.x += cm;
    } else if (robotOrientation == -90) {
      robotLocation.x -= cm;
    } else if (robotOrientation == -180 || robotOrientation == 180) {
      robotLocation.y += cm;
    } else {
      assert false; // Orientation wasn't a cardinal, in bounds direction.
    }
  }

  public void raiseColourSensor() {
    colourSensorUp = true;
  }

  public void lowerColourSensor() {
    colourSensorUp = false;
  }

  // Helpers for SensorControllerInterface.
  public char getObservedObject() {
    float locationDivisor = dynamicMap.gridUnitsToCentimetres(1);
    Point gridLocation = new Point(robotLocation);
    gridLocation.x /= locationDivisor;
    gridLocation.y /= locationDivisor;
    if (robotOrientation == 0) {
      return testMap.get(gridLocation.y - 1).charAt(gridLocation.x);
    } else if (robotOrientation == 90) {
      return testMap.get(gridLocation.y).charAt(gridLocation.x + 1);
    } else if (robotOrientation == -90) {
      return testMap.get(gridLocation.y).charAt(gridLocation.x - 1);
    } else if (robotOrientation == -180 || robotOrientation == 180) {
      return testMap.get(gridLocation.y + 1).charAt(gridLocation.x);
    } else {
      assert false; // Orientation wasn't a cardinal, in bounds direction.
    }
    return ' ';
  }

  // SensorControllerInterface methods.
  public Colour getColour() {
    if (!colourSensorUp) {
      float locationDivisor = dynamicMap.gridUnitsToCentimetres(1);

      // Ensure that we only detect borders in the next square over if we are in a transitional
      // movement.
      if (robotLocation.x % locationDivisor == 0 && robotLocation.y % locationDivisor == 0) {
        return Colour.WHITE;
      }

      // The only inspections we make with the colour sensor down are checking every step distance
      // to see if there's a border.
      Point nextPoint = new Point(robotLocation);

      // Ensure that if we've partially moved to the next space, we round BACK to the square we're
      // moving from.
      if (robotOrientation == -90) {
        nextPoint.x += locationDivisor - 1;
      } else if (robotOrientation == 0) {
        nextPoint.y += locationDivisor - 1;
      }
      nextPoint.x /= locationDivisor;
      nextPoint.y /= locationDivisor;

      if (robotOrientation == 0) {
        nextPoint.y -= 1;
      } else if (robotOrientation == 90) {
        nextPoint.x += 1;
      } else if (robotOrientation == -90) {
        nextPoint.x -= 1;
      } else if (robotOrientation == 180 || robotOrientation == -180) {
        nextPoint.y += 1;
      } else {
        assert false;
      }

      if (testMap.get(nextPoint.y).charAt(nextPoint.x) == 'B') {
        return PathFinder.BORDER_COLOUR;
      }
      return Colour.WHITE;
    }

    char object = getObservedObject();
    if (object == 'S') {
      return PathFinder.SURVIVOR_COLOUR;
    } else if (object == 'O') {
      return PathFinder.OBSTACLE_COLOUR;
    }
    return Colour.WHITE;
  }

  public float getDistanceToObject() {
    char object = getObservedObject();
    if (object == 'S' || object == 'O') {
      return 10.0f;
    }
    return 1000.0f;
  }

  public float getTouchSensor() {
    return 0.0f;
  }

  private ArrayList<String> testMap;
  private boolean colourSensorUp = false;
  private Point robotLocation;
  private int robotOrientation;
  private DynamicMapInterface dynamicMap;
}

public class PathFinderTest {
  public static void main(String[] args) {
    DynamicMap map = new DynamicMap();
    map.init();
    map.setRobotLocation(new Point(0, 0));

    // clang-format off
    List<String> testMap =
        Arrays.asList("BBBBBB",
                      "B--R-B",
                      "B-S-OB",
                      "B----B",
                      "BS---B",
                      "BBBBBB");
    // clang-format on

    // List<String> testMap = Arrays.asList(
    //   "BBBBBB",
    //   "BO-R-B",
    //   "B--S-B",
    //   "BBBBBB");

    ControllerStub stub = new ControllerStub(map, testMap);
    PathFinderInterface pf = new PathFinder(map, stub, stub);

    int i = 0;
    map.printGrid();

    while (pf.getStatus() != Status.COMPLETED) {
      pf.sequentialExplore();
      if (i++ == 100) {
        System.out.println("Exiting manually!");
        break;
      }
      map.printGrid();
      System.out.println("Encoded:\n" + map.toString() + "##########END ENCODED#########");
    }
  }
}