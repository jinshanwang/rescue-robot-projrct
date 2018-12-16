package Robot.PathFinder;
import java.awt.Point;

import Robot.DynamicMapInterface;
import Robot.MovementControllerInterface;
import Robot.SensorControllerInterface;
import Robot.DynamicMapInterface.Object;
import Robot.SensorControllerInterface.Colour;
import Robot.PathFinderInterface;
import lejos.utility.Delay;

import java.util.Queue;
import java.util.Set;

import java.util.HashSet;
import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.lang.Math;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;

public class PathFinder implements PathFinderInterface {
  // Colour of the survivors.
  public static Colour SURVIVOR_COLOUR = Colour.BLUE;
  // Colour of the obstacles.
  public static Colour OBSTACLE_COLOUR = Colour.RED;
  // Colour of the floor where the border is.
  public static Colour BORDER_COLOUR = Colour.BLACK;
  // A multiplier to the number of grid units that will represent the threshhold for object
  // detection. If the ultrasonic sensor detects an object within (size of grid space) *
  // SCANNING_DISTANCE_THRESHOLD, we have detected an object, at which point we will deduce the type
  // of object using the colour sensor. This will likely have to be tweaked.
  public static float SCANNING_DISTANCE_THRESHOLD = 0.9f;
  // We always move forward in increments to ensure that we scan the black surrounding line that
  // indicates the border. This value represents the number of times we'll make individual movements
  // when moving one grid space.
  public static int MOVEMENT_SCANNING_INCREMENTS = 6;

  private Status currentStatus = Status.WAITING;
  private Orientation currentOrientation = Orientation.NORTH;
  private DynamicMapInterface map;
  private MovementControllerInterface movementController;
  private SensorControllerInterface sensorController;
  private Point currentLocation;

  public PathFinder(
      DynamicMapInterface m, MovementControllerInterface mv, SensorControllerInterface sc) {
    map = m;
    movementController = mv;
    sensorController = sc;

    currentLocation = new Point();
    currentLocation.y = 0;
    currentLocation.x = 0;
  }

  private void addSurroundingPoints(Point source, Queue<Point> queue, Set<Point> visitedNodes) {
    addSurroundingPoints(source, queue, visitedNodes, null, null);
  }

  private void addSurroundingPoint(Point source, Point destination, Queue<Point> queue,
      Set<Point> visitedNodes, Map<Point, Point> predecessors, Map<Point, Integer> costs) {
    if (visitedNodes.contains(destination)) {
      return;
    }

    // Implementation specifics for the generating exploration points.
    if (predecessors == null) {
      queue.add(destination);
      return;
    }

    // Implementation specifics for A* pathfinding.
    int tentativeCost = costs.get(source) + 1;

    // Add the destination to the queue if it's not already there.
    if (!queue.contains(destination)) {
      queue.add(destination);
    }

    // Grab the previous cost if it exists.
    int previousCost = -1;
    if (costs.containsKey(destination)) {
      previousCost = costs.get(destination);
    }

    // If the prevous cost exists, only proceed if our tentative cost is better.
    if (previousCost != -1 && previousCost < tentativeCost) {
      return;
    }

    // Our new source point is part of the best path (so far) to the destination. Record these
    // results.
    predecessors.put(destination, source);
    costs.put(destination, tentativeCost);
  }

  private boolean isPassableObject(Object o) {
    if (o == Object.UNKNOWN || o == Object.EMPTY || o == Object.ROBOT) {
      return true;
    }
    return false;
  }

  // Checks if the point 'p' would be out of bounds in the current map. If it is, it ensures that
  // the map is expanded by setting the object at that location to be UNKNOWN. Returns true if
  // the point was out of bounds, false otherwise.
  private boolean checkMapOOBAndAdd(Point p) {
    if (p.x < map.getMinColumn() || p.x > map.getMaxColumn() || p.y < map.getMinRow()
        || p.y > map.getMaxRow()) {
      map.setObjectAtLocation(p, Object.UNKNOWN);
      return true;
    }
    return false;
  }

  private void addSurroundingPoints(Point source, Queue<Point> queue, Set<Point> visitedNodes,
      Map<Point, Point> predecessors, Map<Point, Integer> costs) {
    Object objectAtSource = map.getObjectAtLocation(source);
    if (objectAtSource == Object.BORDER || objectAtSource == Object.OUT_OF_BOUNDS) {
      return;
    }

    Point p = new Point(source);

    // Should never occur as exploration is banned out of border and OOB locations.
    assert map.getMinColumn() >= p.x - 1;
    assert map.getMaxColumn() <= p.x + 1;
    assert map.getMinRow() >= p.y - 1;
    assert map.getMaxRow() <= p.y + 1;

    // Check NORTH.
    p.y = source.y - 1;
    checkMapOOBAndAdd(p);
    Object objectAtPoint = map.getObjectAtLocation(p);
    if (isPassableObject(objectAtPoint)) {
      addSurroundingPoint(source, new Point(p), queue, visitedNodes, predecessors, costs);
    }

    // Check SOUTH.
    p.y = source.y + 1;
    checkMapOOBAndAdd(p);
    objectAtPoint = map.getObjectAtLocation(p);
    if (isPassableObject(objectAtPoint)) {
      addSurroundingPoint(source, new Point(p), queue, visitedNodes, predecessors, costs);
    }

    // Check EAST.
    p.y = source.y;
    p.x = source.x + 1;
    checkMapOOBAndAdd(p);
    objectAtPoint = map.getObjectAtLocation(p);
    if (isPassableObject(objectAtPoint)) {
      addSurroundingPoint(source, new Point(p), queue, visitedNodes, predecessors, costs);
    }

    // Check WEST.
    p.x = source.x - 1;
    checkMapOOBAndAdd(p);
    objectAtPoint = map.getObjectAtLocation(p);
    if (isPassableObject(objectAtPoint)) {
      addSurroundingPoint(source, new Point(p), queue, visitedNodes, predecessors, costs);
    }
  }

  // A simple implementation of the A* algorithm to route to the destination point. Returns null if
  // no path could be found.
  private ArrayDeque<Point> generatePathToLocation(Point destination) {
    if (destination.equals(currentLocation)) {
      return new ArrayDeque<Point>();
    }

    // Holds a set of already fully-explored nodes.
    Set<Point> visitedNodes = new HashSet<Point>();
    // Holds a KV pair of {location, predecessor's location}.
    Map<Point, Point> predecessors = new HashMap<Point, Point>();
    // Holds a KV pair of {location, cost to reach the location}.
    Map<Point, Integer> costs = new HashMap<Point, Integer>();
    costs.put(currentLocation, 0);

    // The priority queue heuristic is based on manhattan distance.
    Queue<Point> queue = new PriorityQueue<Point>(10, new Comparator<Point>() {
      public int compare(Point a, Point b) {
        int aManhattanDistance =
            Math.abs(currentLocation.x - a.x) + Math.abs(currentLocation.y - a.y);
        int bManhattanDistance =
            Math.abs(currentLocation.x - b.x) + Math.abs(currentLocation.y - b.y);
        return aManhattanDistance - bManhattanDistance;
      }
    });

    Point p = new Point(currentLocation);
    queue.add(p);

    while (queue.peek() != null) {
      p = queue.poll();
      if (p.equals(destination)) {
        // Backtrack to retrieve the shortest path to the destination.
        ArrayDeque<Point> path = new ArrayDeque<Point>();
        path.add(destination);
        for (;;) {
          Point previous = predecessors.get(p);
          if (previous.equals(currentLocation)) {
            return path;
          }

          path.addFirst(previous);
          p = previous;
        }
      }

      visitedNodes.add(p);
      addSurroundingPoints(p, queue, visitedNodes, predecessors, costs);
    }

    return null;
  }

  // Returns the next point for exploration. Returns the origin (Point {0, 0}) if there are no more
  // points to be explored. Implemented using a basic BFS.
  private Point getNextExplorationPoint() {
    Point p = new Point(currentLocation);
    Queue<Point> queue = new ArrayDeque<Point>();
    Set<Point> visitedNodes = new HashSet<Point>();
    queue.add(new Point(p));

    while (queue.peek() != null) {
      p = queue.poll();
      if (map.getObjectAtLocation(p) == Object.UNKNOWN) {
        return p;
      }

      visitedNodes.add(p);
      addSurroundingPoints(p, queue, visitedNodes);
    }

    p.y = 0;
    p.x = 0;
    return p;
  }

  // Orient the robot and make it face the provided orientation.
  private void orientRobot(Orientation orientation) {
    int degreesOfRotation = ((-1 * currentOrientation.toDegrees()) + orientation.toDegrees()) % 360;
    if (degreesOfRotation == 0) {
      return;
    }

    degreesOfRotation = degreesOfRotation % 360;
    if (degreesOfRotation == -270) {
      degreesOfRotation = 90;
    } else if (degreesOfRotation == 270) {
      degreesOfRotation = -90;
    }

    // TODO(mitch): Remove this "is test" hack.
    if (movementController != sensorController) {
      Delay.msDelay(300);
    }

    if (degreesOfRotation == 180 || degreesOfRotation == -180) {
      movementController.rotateRobot(90);
      // TODO(mitch): Remove this "is test" hack.
      if (movementController != sensorController) {
        Delay.msDelay(300);
      }
      movementController.rotateRobot(90);
    } else {
      movementController.rotateRobot(degreesOfRotation);
    }
    // TODO(mitch): Remove this "is test" hack.
    if (movementController != sensorController) {
      Delay.msDelay(300);
    }
    currentOrientation = orientation;
  }

  // Follow the provided route of points. Note that the provided route should start at the next
  // point to be routed to, and not the current location of the robot. All points should be adjacent
  // to each other, and the first point should be adjacent to the current position of the robot.
  // Returns false if the route crossed a border and failed, true otherwise.
  private boolean followRoute(Queue<Point> route) {
    while (route.peek() != null) {
      Point nextLocation = route.poll();
      int xMovement = nextLocation.x - currentLocation.x;
      int yMovement = nextLocation.y - currentLocation.y;

      // Ensure we only move on the x OR y axis, and only a single unit. If we have problems here,
      // the pathfinding went wrong.
      assert((Math.abs(xMovement)) ^ (Math.abs(yMovement))) == 1;

      Orientation desiredOrientation = Orientation.NORTH;
      Orientation reverseOrientation = Orientation.SOUTH;
      if (yMovement == 1) {
        desiredOrientation = Orientation.SOUTH;
        reverseOrientation = Orientation.NORTH;
      } else if (xMovement == -1) {
        desiredOrientation = Orientation.WEST;
        reverseOrientation = Orientation.EAST;
      } else if (xMovement == 1) {
        desiredOrientation = Orientation.EAST;
        reverseOrientation = Orientation.WEST;
      }

      orientRobot(desiredOrientation);

      float incrementalMovement =
          ((float) map.gridUnitsToCentimetres(1)) / MOVEMENT_SCANNING_INCREMENTS;
      for (int i = 0; i < MOVEMENT_SCANNING_INCREMENTS; ++i) {
        movementController.moveForward(incrementalMovement);
        if (sensorController.getColour() != Colour.WHITE) {
          // orientRobot(reverseOrientation);
          // Incremental movement back, for alignment issues.
          for (int j = 0; j < i + 1; ++j) {
            movementController.moveForward(-1 * incrementalMovement);
          }
          //          movementController.moveForward(incrementalMovement * (i + 1));
          map.setObjectAtLocation(nextLocation, Object.BORDER);
          return false;
        }
      }

      map.setObjectAtLocation(currentLocation, Object.EMPTY);
      currentLocation = nextLocation;
      map.setRobotLocation(currentLocation);
    }
    return true;
  }

  // Detect objects currently in front of the robot. The provided point to this function should be
  // the grid space that the robot is currently looking at.
  private void detectObjects(Point currentlyFacingPoint) {
    float distanceThreshold = map.gridUnitsToCentimetres(1) * SCANNING_DISTANCE_THRESHOLD;
    if (sensorController.getDistanceToObject() < distanceThreshold) {
      Colour detectedColour = sensorController.getColour();
      if (detectedColour == SURVIVOR_COLOUR) {
        map.setObjectAtLocation(currentlyFacingPoint, Object.SURVIVOR);
      } else if (detectedColour == OBSTACLE_COLOUR) {
        map.setObjectAtLocation(currentlyFacingPoint, Object.OBSTACLE);
      } else {
        map.setObjectAtLocation(currentlyFacingPoint, Object.ERROR);
      }
    } else {
      map.setObjectAtLocation(currentlyFacingPoint, Object.EMPTY);
    }
  }

  private void inspectAdjacentPoint(Point inspectionPoint) {
    assert map.getObjectAtLocation(inspectionPoint) == Object.UNKNOWN;

    if (inspectionPoint.x == currentLocation.x + 1) {
      orientRobot(Orientation.EAST);
    } else if (inspectionPoint.x == currentLocation.x - 1) {
      orientRobot(Orientation.WEST);
    } else if (inspectionPoint.y == currentLocation.y + 1) {
      orientRobot(Orientation.SOUTH);
    } else if (inspectionPoint.y == currentLocation.y - 1) {
      orientRobot(Orientation.NORTH);
    } else {
      assert false;
    }

    movementController.raiseColourSensor();
    detectObjects(inspectionPoint);
    movementController.lowerColourSensor();
  }

  private void routeAndInspectPoint(Point inspectionPoint) {
    currentStatus = Status.CALCULATING;
    ArrayDeque<Point> route = generatePathToLocation(inspectionPoint);

    // Ensure the generated route is correct.
    assert route != null;
    assert route.size() > 0;
    assert route.peekLast().equals(inspectionPoint);

    // Route to the adjacent location to the inspection point.
    route.removeLast();

    // The route to the inspection point shouldn't cross a border.
    currentStatus = Status.ROUTING;
    boolean routeFailed = followRoute(route);
    assert !routeFailed;
    // Inspect the point that we're observing. Record the data in the map.
    currentStatus = Status.INSPECTING;
    inspectAdjacentPoint(inspectionPoint);

    // If there is nothing in the square, ensure that it's not a border province by trying to move
    // to it.
    currentStatus = Status.INSPECTING;
    if (map.getObjectAtLocation(inspectionPoint) == Object.EMPTY) {
      ArrayDeque<Point> lastMovement = new ArrayDeque<Point>();
      lastMovement.add(inspectionPoint);
      currentStatus = Status.ROUTING;
      followRoute(lastMovement);
    }
    currentStatus = Status.STATIONARY;
  }

  // A simple approach. We find the closest unexplored gridspace using BFS, which also conveniently
  // provides the path to this gridspace. When we arrive to a gridspace, explore the 4 grid spaces
  // around it for objects. If we end up on a border square, explore the grid spaces and set the
  // last known good position so that we can return there on the next iteration.
  public void sequentialExplore() {
    if (currentStatus == Status.COMPLETED) {
      return;
    }

    currentStatus = Status.CALCULATING;
    Point nextExplorationPoint = getNextExplorationPoint();
    if (nextExplorationPoint.x == 0 && nextExplorationPoint.y == 0) {
      currentStatus = Status.COMPLETED;
      return;
    }

    routeAndInspectPoint(nextExplorationPoint);
  }

  public Orientation getRobotOrientation() {
    return currentOrientation;
  }

  public Status getStatus() {
    return currentStatus;
  }
}
