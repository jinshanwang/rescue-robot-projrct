package Client.UserInterface;
interface DisplayControllerInterface {
  // Connect controller to gui
  public void connect(Gui _gui);

  // Closes all sockets and destroys this process.
  public void close();

  // Connects GUI to the robot
  public void connectButtonPressed();

  // Commands the robot to run automatically
  public void runAutomaticButtonPressed();

  // Called to deliver a TICK to the controller. Returns false when completed.
  public boolean tick();
}