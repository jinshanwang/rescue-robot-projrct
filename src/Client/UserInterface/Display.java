package Client.UserInterface;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;

import Robot.DynamicMapInterface;

import java.awt.Point;

import javax.swing.*;

class Display implements Gui {
  private static final int FRAME_WIDTH = 800;
  private static final int FRAME_HEIGHT = 630;
  private static final int INSET = 6;
  private static final int CONSOLE_FONT_SIZE = 20;
  private static final int GRID_BOXES = 30;

  JFrame frame;

  JPanel window;
  JPanel controlPanel;
  JPanel dynamicMapPanel;
  JPanel consolePanel;
  JPanel initializingPanel;
  JPanel proceduralUnitsPanel;
  JPanel keysPanel;
  JPanel mapPanel;

  JButton connectButton;
  JButton runAutomaticButton;

  JLabel keysLabel;
  JLabel keyRobotLabel;
  JLabel keyBorderLabel;
  JLabel keyObstacleLabel;
  JLabel keySurvivorLabel;
  JLabel keyExploredLabel;
  JLabel keyUnknownLabel;
  JLabel consoleLabel;

  JLabel robotImageIcon;

  GridBagConstraints gbc;

  DisplayControllerInterface DCI;

  public void connect(
      DisplayControllerInterface _displayControllerInterface /*connect to other classes?*/) {
    DCI = _displayControllerInterface;
  }

  // Initialise the gui.
  public void init() {
    // initializing Grid Bag Constraints.
    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;

    // Creating Window.
    frame = new JFrame("Robot GUI");
    frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
    frame.setResizable(false);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Creating Window Panel.
    window = new JPanel();
    window.setLayout(null);
    window.setBackground(Color.BLACK);

    //################# PARENT CONTAINERS ##################

    // Creating Control Panel.
    controlPanel = new JPanel(new GridBagLayout());
    controlPanel.setBackground(DARK_GREY);
    controlPanel.setOpaque(true);
    controlPanel.setBounds(INSET, INSET, 200 - (2 * INSET), 600 - (2 * INSET));
    window.add(controlPanel, gbc);

    // Creating Dynamic Map Panel.
    dynamicMapPanel = new JPanel();
    dynamicMapPanel.setLayout(null);
    dynamicMapPanel.setBackground(DARK_GREY);
    dynamicMapPanel.setOpaque(true);
    dynamicMapPanel.setBounds(200, 100, 600 - (2 * INSET), 500 - INSET);
    window.add(dynamicMapPanel, gbc);

    // Creating Console Panel.
    consolePanel = new JPanel(new BorderLayout());
    consolePanel.setBackground(DARKEST_GREY);
    consolePanel.setOpaque(true);
    consolePanel.setBounds(200, INSET, 600 - (2 * INSET), 100 - (2 * INSET));
    window.add(consolePanel, gbc);

    //################# CONTROL CONTAINERS ##################

    // Creating initializing Panel.
    initializingPanel = new JPanel(new GridBagLayout());
    initializingPanel.setBackground(SILVER);
    initializingPanel.setOpaque(true);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 0.25;
    gbc.insets = new Insets(INSET, INSET, INSET, INSET);
    controlPanel.add(initializingPanel, gbc);

    // Creating Keys Panel.
    keysPanel = new JPanel(new GridBagLayout());
    keysPanel.setBackground(SILVER);
    keysPanel.setOpaque(true);
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 0.75;
    gbc.insets = new Insets(0, INSET, INSET, INSET);
    controlPanel.add(keysPanel, gbc);

    //################# CONTROL PANEL CONTENTS ##################

    // Creating Connect Button.
    connectButton = new JButton("CONNECT TO ROBOT");
    connectButton.setBackground(LIGHT_RED);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 1;
    gbc.weighty = 0.5;
    gbc.insets = new Insets(INSET, INSET, INSET, INSET);
    initializingPanel.add(connectButton, gbc);

    // Creating Run Automatically Button.
    runAutomaticButton = new JButton("RUN AUTOMATICALLY");
    runAutomaticButton.setBackground(LIGHT_GREY);
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 1;
    gbc.weighty = 0.5;
    gbc.insets = new Insets(INSET, INSET, INSET, INSET);
    initializingPanel.add(runAutomaticButton, gbc);

    //################# KEYS PANEL CONTENTS ##################

    // Creates each key for the dynamic map.
    keysLabel = new JLabel("KEY", SwingConstants.CENTER);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 1;
    gbc.weighty = 0.01;
    gbc.insets = new Insets(2 * INSET, INSET, 0, INSET);
    keysPanel.add(keysLabel, gbc);

    keyRobotLabel = new JLabel(" Robot");
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 1;
    gbc.weighty = 0.1;
    gbc.insets = new Insets(INSET, INSET, 0, INSET);
    keysPanel.add(keyRobotLabel, gbc);

    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 0.5;
    gbc.weighty = 0.01;
    keysPanel.add(new JLabel(new ImageIcon(getClass().getResource("/MapImages/robotImageKey.png")),
                      SwingConstants.CENTER),
        gbc);

    keyBorderLabel = new JLabel(" Border");
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 1;
    gbc.weighty = 0.1;
    gbc.insets = new Insets(INSET, INSET, 0, INSET);
    keysPanel.add(keyBorderLabel, gbc);

    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 0.5;
    gbc.weighty = 0.01;
    keysPanel.add(new JLabel(new ImageIcon(getClass().getResource("/MapImages/borderImageKey.png")),
                      SwingConstants.CENTER),
        gbc);

    keyObstacleLabel = new JLabel(" Obstacle");
    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 1;
    gbc.weighty = 0.1;
    gbc.insets = new Insets(INSET, INSET, 0, INSET);
    keysPanel.add(keyObstacleLabel, gbc);

    gbc.gridx = 1;
    gbc.gridy = 5;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 0.5;
    gbc.weighty = 0.01;
    keysPanel.add(
        new JLabel(new ImageIcon(getClass().getResource("/MapImages/obstacleImageKey.png")),
            SwingConstants.CENTER),
        gbc);

    keySurvivorLabel = new JLabel(" Survivor");
    gbc.gridx = 0;
    gbc.gridy = 7;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 1;
    gbc.weighty = 0.1;
    gbc.insets = new Insets(INSET, INSET, 0, INSET);
    keysPanel.add(keySurvivorLabel, gbc);

    gbc.gridx = 1;
    gbc.gridy = 7;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 0.5;
    gbc.weighty = 0.01;
    keysPanel.add(
        new JLabel(new ImageIcon(getClass().getResource("/MapImages/survivorImageKey.png")),
            SwingConstants.CENTER),
        gbc);

    keyExploredLabel = new JLabel(" Explored");
    gbc.gridx = 0;
    gbc.gridy = 9;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 1;
    gbc.weighty = 0.1;
    gbc.insets = new Insets(INSET, INSET, 0, INSET);
    keysPanel.add(keyExploredLabel, gbc);

    gbc.gridx = 1;
    gbc.gridy = 9;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 0.5;
    gbc.weighty = 0.01;
    keysPanel.add(
        new JLabel(new ImageIcon(getClass().getResource("/MapImages/exploredImageKey.png")),
            SwingConstants.CENTER),
        gbc);

    keyUnknownLabel = new JLabel(" Unknown");
    gbc.gridx = 0;
    gbc.gridy = 11;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 1;
    gbc.weighty = 0.1;
    gbc.insets = new Insets(INSET, INSET, 3 * INSET, INSET);
    keysPanel.add(keyUnknownLabel, gbc);

    gbc.gridx = 1;
    gbc.gridy = 11;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 0.5;
    gbc.weighty = 0.01;
    keysPanel.add(
        new JLabel(new ImageIcon(getClass().getResource("/MapImages/unknownImageKey.png")),
            SwingConstants.CENTER),
        gbc);

    //################# CONSOLE PANEL CONTENTS ##################

    // Creating Console Label
    consoleLabel = new JLabel("   ROBOT NOT CONNECTED");
    consoleLabel.setFont(new Font("PLAIN", Font.BOLD, CONSOLE_FONT_SIZE));
    consoleLabel.setForeground(Color.WHITE);
    gbc.fill = GridBagConstraints.NONE;
    consolePanel.add(consoleLabel, BorderLayout.CENTER);

    //################# DYNAMIC MAP PANEL CONTENTS ##################

    // Creating Map Panel
    mapPanel = new JPanel(new GridBagLayout());
    mapPanel.setBackground(Color.BLUE);
    mapPanel.setOpaque(false);
    mapPanel.setBounds(2 * INSET, INSET, 600 - (6 * INSET), 500 - (4 * INSET));
    dynamicMapPanel.add(mapPanel);

    //######################################################

    frame.add(window);
    frame.setVisible(true);

    //################# BUTTON LISTENERES ##################

    connectButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        DCI.connectButtonPressed();
      }
    });

    runAutomaticButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        DCI.runAutomaticButtonPressed();
      }
    });
  }

  // Set console display text. (would like to make this act similiar to a command terminal)
  public void setConsoleLabel(String text) {
    consoleLabel.setText(text);
    consoleLabel.setFont(new Font("PLAIN", Font.BOLD, CONSOLE_FONT_SIZE));
  }

  // Set button colour and text.
  public void setButtonColor(String button, Color colour) {
    switch (button) {
      case "connectButton":
        connectButton.setBackground(colour);
        break;
      case "runAutomaticButton":
        runAutomaticButton.setBackground(colour);
        break;
      default:
        // error check?
        break;
    }
  }

  // Set button text colour.
  public void setButtonText(String button, String text) {
    switch (button) {
      case "connectButton":
        connectButton.setText(text);
        break;
      case "runAutomaticButton":
        runAutomaticButton.setText(text);
        break;
      default:
        // error check?
        break;
    }
  }

  // Prints the map
  public void setMap(DynamicMapInterface map) {
    mapPanel.removeAll();

    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.insets = new Insets(0, 0, 0, 0);

    Point p;

    for (int i = map.getMinRow(); i <= map.getMaxRow(); i++) {
      for (int j = map.getMinColumn(); j <= map.getMaxColumn(); j++) {
        gbc.gridx = j;
        gbc.gridy = i;
        p = new Point(i, j);

        switch (map.getObjectAtLocation(p)) {
          case ROBOT:
            mapPanel.add(
                new JLabel(new ImageIcon(getClass().getResource("/MapImages/robotImage.png"))),
                gbc);
            break;
          case BORDER:
            mapPanel.add(
                new JLabel(new ImageIcon(getClass().getResource("/MapImages/borderImage.png"))),
                gbc);
            break;
          case OBSTACLE:
            mapPanel.add(
                new JLabel(new ImageIcon(getClass().getResource("/MapImages/obstacleImage.png"))),
                gbc);
            break;
          case SURVIVOR:
            mapPanel.add(
                new JLabel(new ImageIcon(getClass().getResource("/MapImages/survivorImage.png"))),
                gbc);
            break;
          case EMPTY:
            mapPanel.add(
                new JLabel(new ImageIcon(getClass().getResource("/MapImages/exploredImage.png"))),
                gbc);
            break;
          case UNKNOWN:
            mapPanel.add(
                new JLabel(new ImageIcon(getClass().getResource("/MapImages/unknownImage.png"))),
                gbc);
            break;
          default:
            break;
        }
      }
    }
  }
}
