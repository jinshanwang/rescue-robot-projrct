package Robot;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import Robot.PathFinderInterface;
import Robot.PathFinderInterface.Status;
import Robot.MovementController.MovementController;
import Robot.PathFinder.PathFinder;
import Robot.SensorController.SensorController;
import Robot.DynamicMap.DynamicMap;
import lejos.hardware.Sound;
import lejos.utility.Delay;

class Driver {
  public static void main(String[] args) {
    DynamicMap map = new DynamicMap();
    map.init();
    map.setRobotLocation(new Point(0, 0));

    MovementController mv = new MovementController();
    SensorController sc = new SensorController();
    PathFinderInterface pf = new PathFinder(map, mv, sc);

    ServerSocket server = null;
    Socket sock = null;
    DataInputStream inputStream = null;
    DataOutputStream outputStream = null;

    try {
      server = new ServerSocket(1111);
      System.out.println("Waiting for connection...");
      sock = server.accept();

      inputStream = new DataInputStream(sock.getInputStream());
      String syn = inputStream.readUTF();
      if (!syn.equals("synchronise")) {
        Sound.beepSequence();
        sock.close();
        server.close();
      }

      outputStream = new DataOutputStream(sock.getOutputStream());
      outputStream.writeUTF("acknowledge");
    } catch (Exception e) {
      Sound.beepSequence();
      e.printStackTrace();
      return;
    }

    while (true) {
      try {
        String inputString = inputStream.readUTF();
        if (!inputString.equals("explore_next")) {
          Sound.beepSequence();
          System.err.println("Unknown command: " + inputString);
          Delay.msDelay(10000);
          return;
        }

        pf.sequentialExplore();

        String encodedMap = map.toString();
        System.out.println("Sending map:\n" + encodedMap);
        outputStream.writeUTF("encoded_map");
        outputStream.writeUTF(encodedMap);
        outputStream.writeUTF("encoded_map_end");

        if (pf.getStatus() == Status.COMPLETED) {
          System.out.println("Completed scanning!");
          outputStream.writeUTF("completed");
          break;
        }
      } catch (Exception e) {
        e.printStackTrace();
        Sound.beepSequence();
        return;
      }
    }

    Delay.msDelay(10000);
    try {
      outputStream.flush();
      outputStream.close();
      inputStream.close();
      sock.close();
      server.close();
    } catch (Exception e) {
    }
  }
}
