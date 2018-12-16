package Client.UserInterface;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import Robot.DynamicMapInterface;
import Robot.DynamicMap.DynamicMap;

class DisplayController implements DisplayControllerInterface {
  boolean isRunning = false;
  private Socket socket = null;
  private DataOutputStream outputStream = null;
  private DataInputStream inputStream = null;

  private Gui gui;

  // Connect controller to gui
  public void connect(Gui _gui) {
    gui = _gui;
  }

  private void closeInternal() {
    if (socket != null && !socket.isClosed()) {
      try {
        socket.close();
      } catch (Exception e) {
      }
    }
    if (inputStream != null) {
      try {
        inputStream.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (outputStream != null) {
      try {
        outputStream.flush();
        outputStream.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    socket = null;
    inputStream = null;
    outputStream = null;
    isRunning = false;
  }

  public void close() {
    synchronized (this) {
      closeInternal();
    }
  }

  // Connects GUI to the robot
  public void connectButtonPressed() {
    synchronized (this) {
      if (!isRunning) {
        gui.setConsoleLabel("   CONNECTING TO ROBOT...");
        gui.setButtonColor("connectButton", Gui.LIGHT_GREY);

        // Connect to the robot.
        try {
          socket = new Socket("10.0.1.1", 1111);
        } catch (Exception e) {
          closeInternal();
          gui.setConsoleLabel("    CONNECTION FAILED");
          gui.setButtonColor("connectButton", Gui.LIGHT_GREY);
          return;
        }

        // Write our first syn-ack to confirm communication.
        try {
          outputStream = new DataOutputStream(socket.getOutputStream());
          outputStream.writeUTF("synchronise");
          outputStream.flush();

          inputStream = new DataInputStream(socket.getInputStream());
          String ack = inputStream.readUTF();
          if (!ack.equals("acknowledge")) {
            closeInternal();
          }
          System.out.println("Ack recieved: " + ack);
        } catch (Exception e) {
          closeInternal();
          e.printStackTrace();
        }

        // connect to the robot
        // if it works
        gui.setConsoleLabel("   READY FOR MAPPING");

        gui.setButtonColor("connectButton", Gui.LIGHT_BLUE);
        gui.setButtonText("connectButton", "CONNECTED");

        gui.setButtonColor("runAutomaticButton", Gui.LIGHT_RED);
      }
    }
  }

  // Commands the robot to run automatically
  public void runAutomaticButtonPressed() {
    synchronized (this) {
      if (!isRunning) {
        isRunning = true;
        gui.setConsoleLabel("   PREPARING FOR AUTOMATION");

        gui.setButtonColor("runAutomaticButton", Color.GREEN);
        gui.setButtonText("runAutomaticButton", "RUNNING");

        /*
				int[][] map = new int[][] {
					new int[] { 1, 1, 5, 5, 5, 5},
					new int[] { 1, 0, 5, 5, 5, 5},
					new int[] { 5, 5, 5, 5, 5, 5},
					new int[] { 5, 5, 5, 5, 5, 5},
					new int[] { 5, 5, 5, 5, 5, 5},
				};b
				*/ /*
                                 int[][] map = new int[][] {
                                         new int[] { 1, 1, 1, 1, 1, 1},
                                         new int[] { 1, 4, 4, 4, 5, 1},
                                         new int[] { 1, 4, 2, 4, 5, 1},
                                         new int[] { 1, 2, 3, 0, 5, 1},
                                         new int[] { 1, 1, 1, 1, 1, 1},
                                 };
                                 gui.setMap(map);*/
      }
    }
  }

  // Called to deliver a tick to the controller
  public boolean tick() {
    synchronized (this) {
      if (isRunning) {
        try {
          System.out.println("Sending explore_next");
          outputStream.writeUTF("explore_next");
          outputStream.flush();
        } catch (Exception e) {
          e.printStackTrace();
          closeInternal();
          return false;
        }

        try {
          System.out.println("Message Received:");
          String message = inputStream.readUTF();
          StringBuilder encodedMap = null;
          for (String line : message.split("\n")) {
            System.out.println("Line: " + line);
            if (line.equals("encoded_map")) {
              encodedMap = new StringBuilder();
            } else if (encodedMap != null) {
              if (line.equals("encoded_map_end")) {
                DynamicMap map = new DynamicMap();
                map.init();
                map.fromString(encodedMap.toString());
                gui.setMap(map);
                encodedMap = null;
              } else {
                encodedMap.append(line);
              }
            } else if (line.equals("completed")) {
              return false;
            } else {
              System.err.println("Unknown command: " + line);
              return false;
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
          closeInternal();
          return false;
        }
      }

      return true;
    }
  }
}
