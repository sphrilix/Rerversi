package de.sphrilix.reversi.gui;

import de.sphrilix.reversi.model.Board;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

/**
 * This class provides the implementation of frame for a gui for Reversi.
 */
public final class MainFrame extends JFrame {

    /**
     * Window title
     */
    private static final String WINDOW_TITLE = "Reversi";

    /**
     * Array of the allowed levels
     */
    private static final Integer[] ALLOWED_LEVELS = {1, 2, 3, 4, 5};

    /**
     * Standard size of a border.
     */
    private static final int STANDARD_BORDER = 10;

    /**
     * Grid of the actual MainFrame
     */
    private static Grid grid;

    /**
     * Undo button of the MainFrame
     */
    private static JButton undo;

    /**
     * Counter for the human Tiles
     */
    private static JLabel humanTiles;

    /**
     * Counter for the machine Tiles.
     */
    private static JLabel machineTiles;

    /**
     * Drop down menu for the level selection
     */
    private static JComboBox<Integer> level;

    /**
     * Starts the gui and the game.
     *
     * @param args Params needed to start.
     */
    public static void main(String[] args) {
        new MainFrame();

    }

    /**
     * Creates the whole MainFrame using the BorderLayout. Sets the Grid in
     * the center of the frame, the vertical coordinate axis on the left, the
     * horizontal coordinate axis at the top and the control unit at the bottom.
     *
     * @return Returns the finished MainFrame.
     */
    private MainFrame() {
        super(WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create and the components to the Frame.
        JPanel controlUnit = createControlUnit();
        JPanel yAxis = createYAxis();
        JPanel xAxis = createXAxis();
        grid = new Grid();
        add(grid, BorderLayout.CENTER);
        add(controlUnit, BorderLayout.SOUTH);
        add(yAxis, BorderLayout.WEST);
        add(xAxis, BorderLayout.NORTH);

        // Needed to center the x coordinate axis.
        xAxis.add(Box.createHorizontalStrut(yAxis.getWidth()));

        // Set the standard level.
        level.setSelectedIndex(2);
        grid.setLevel((int) level.getSelectedItem());

        // Set the frame correct size and make him visible.
        pack();
        setVisible(true);
    }

    /**
     * Creates the whole control unit, by using the BorderLayout.
     * Sets the control buttons in the center of the control unit, the
     * counter of the human Tiles in the left corner and the counter of the
     * machine Tiles in the right corner.
     *
     * @return Returns created control unit as a JPanel.
     */
    private JPanel createControlUnit() {
        JPanel controlUnit = new JPanel();
        controlUnit.setLayout(new BorderLayout());

        // Create the counters of the Tiles.
        humanTiles = new JLabel("2");
        machineTiles = new JLabel("2");
        humanTiles.setForeground(Color.BLUE);
        machineTiles.setForeground(Color.RED);
        humanTiles.setBorder(new EmptyBorder(STANDARD_BORDER, STANDARD_BORDER,
                STANDARD_BORDER, STANDARD_BORDER));
        machineTiles.setBorder(new EmptyBorder(STANDARD_BORDER,
                STANDARD_BORDER, STANDARD_BORDER, STANDARD_BORDER));

        //Set the counters in the correct position.
        controlUnit.add(humanTiles, BorderLayout.WEST);
        controlUnit.add(machineTiles, BorderLayout.EAST);

        //Add the buttons to the mid of the control unit.
        controlUnit.add(createControlButtons(), BorderLayout.CENTER);
        return controlUnit;
    }

    /**
     * Creates the control buttons for a Reversi game, by adding to all
     * buttons a ActionListener and implements the behaviour if they're
     * clicked. Also it adds the all the control buttons to a JPanel.
     *
     * @return Returns the JPanel with the buttons.
     */
    private JPanel createControlButtons() {
        JPanel controlButtons = new JPanel();

        // Create button for new game and set the behaviour if clicked.
        JButton newGame = new JButton("NEW");
        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                grid.createNewGame();
            }
        });

        // Add "Alt + N" as an shortcut for a new game.
        newGame.setMnemonic(KeyEvent.VK_N);

        // Create button for switch and set the behaviour if clicked.
        JButton switchStarted = new JButton("SWITCH");
        switchStarted.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                grid.switchStarted();
            }
        });

        // Add "Alt + S" as an shortcut for changing beginning player.
        switchStarted.setMnemonic(KeyEvent.VK_S);

        //Create button to quit and set the behaviour if clicked.
        JButton quit = new JButton("QUIT");
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                grid.killThread();
            }
        });

        // Add "Alt + Q" as an shortcut for terminating the game.
        quit.setMnemonic(KeyEvent.VK_Q);

        // Create level selector and set the behaviour if clicked.
        level = new JComboBox<Integer>(ALLOWED_LEVELS);
        level.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                //int selectedLevel = Integer.parseInt((String) e.getItem());
                int selectedLevel = (int) e.getItem();
                grid.setLevel(selectedLevel);
            }
        });

        // Create the undo Button and set the behaviour if clicked.
        undo = new JButton("UNDO");
        undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                grid.undo();
            }
        });

        // Add "Alt + U" as an shortcut for undoing the latest move.
        undo.setMnemonic(KeyEvent.VK_U);
        undo.setEnabled(false);

        // Add them all to the JPanel.
        controlButtons.add(level);
        controlButtons.add(newGame);
        controlButtons.add(switchStarted);
        controlButtons.add(undo);
        controlButtons.add(quit);

        return controlButtons;
    }

    /**
     * Creates the vertical coordinate axis, using the BoxLayout.
     *
     * @return Returns the vertical coordinate axis.
     */
    private JPanel createYAxis() {
        JPanel verticalAxis = new JPanel();
        verticalAxis.setLayout(new BoxLayout(verticalAxis, BoxLayout.Y_AXIS));
        for (int i = 1; i < Board.SIZE + 1; i++) {
            verticalAxis.add(Box.createVerticalGlue());
            verticalAxis.add(new JLabel(String.valueOf(i)));
            verticalAxis.add(Box.createVerticalGlue());
        }
        return verticalAxis;
    }

    /**
     * Creates the horizontal coordinate axis, using the BoxLayout.
     *
     * @return Returns the horizontal coordinate axis.
     */
    private JPanel createXAxis() {
        JPanel horizontalAxis = new JPanel();
        horizontalAxis.setLayout(new BoxLayout(horizontalAxis,
                BoxLayout.X_AXIS));
        for (int i = 1; i < Board.SIZE + 1; i++) {
            horizontalAxis.add(Box.createHorizontalGlue());
            horizontalAxis.add(new JLabel(String.valueOf(i)));
            horizontalAxis.add(Box.createHorizontalGlue());
        }
        return horizontalAxis;
    }

    /**
     * Getter for the undo button.
     *
     * @return Returns the undo button.
     */
    public JButton getUndo() {
        return undo;
    }

    /**
     * Getter for the counter of the human Tiles.
     *
     * @return Returns the counter of the human Tiles.
     */
    public JLabel getHumanTiles() {
        return humanTiles;
    }

    /**
     * Getter for the counter of the machine Tiles.
     *
     * @return Returns the counter of the machine Tiles.
     */
    public JLabel getMachineTiles() {
        return machineTiles;
    }
}
