package Robot;

// Implementations of the PathFinderInterface are reposible for using the required sensors to
// explore the area, and record details of the exploration in the linked DynamicMap. This class is
// primarily driven through repeated calls to sequentialExplore().
public interface PathFinderInterface {
  public enum Status { WAITING, CALCULATING, STATIONARY, ROUTING, COMPLETED, INSPECTING, FAIL }

  public enum Orientation {
    NORTH(0),
    SOUTH(180),
    EAST(90),
    WEST(-90);

    private int degrees;

    Orientation(int degrees) {
      this.degrees = degrees;
    }

    public int toDegrees() {
      return this.degrees;
    }
  }

  // Does a single step of exploration. This function should be called repeatedly to explore an
  // entire map, propagating the map with information during its journey. This function is designed
  // to be called until the status is either FAIL or COMPLETED, at which point the exploration is
  // complete.
  // For this function to work correctly, the robot must be placed initially along a cardinal
  // direction relative to the map (i.e. movements forward/back must be perpendicular or parallel to
  // the map edges). The robot may start facing any direction.
  // Furthermore, this function is expected to maintain alignment of the robot on grid spaces. If
  // realignment is required, this function is required to make sure that the robot is grid aligned
  // before returning.
  public void sequentialExplore();

  // Returns the current orientation of the robot. Note that the cardinal directions returned by
  // this method are not relative to the real world, but are relative to the starting direction of
  // the robot. In this sense, the robot always starts facing "south", and subsequent rotations will
  // adjust the orientation of the robot.
  public Orientation getRobotOrientation();

  // Returns the current status of the robot.
  public Status getStatus();
}
