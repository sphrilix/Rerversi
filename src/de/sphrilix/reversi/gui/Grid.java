package de.sphrilix.reversi.gui;

import de.sphrilix.reversi.model.Board;
import de.sphrilix.reversi.model.Player;
import de.sphrilix.reversi.model.Reversi;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Stack;

public class Grid extends JPanel {

    /**
     * The actual selected level in the MainFrame.
     */
    private static int level;
    /**
     * If mouse listener is allowed to listen.
     */
    private boolean allowedToListen;
    /**
     * Board of the Grid.
     */
    private Board board;
    /**
     * Stack of the past moves.
     */
    private Stack<Board> pastMoves;
    /**
     * Thread which executes the machine move.
     */
    private MachineMoveThread thread;
    /**
     * 2d array which implements the gui of the board.
     */
    private Slot[][] slots = new Slot[board.SIZE][board.SIZE];

    /**
     * Creates new instance of Grid, using the GridLayout and adds to all
     * slots the MouseListener.
     */
    public Grid() {
        setLayout(new GridLayout(board.SIZE, board.SIZE));
        pastMoves = new Stack<Board>();
        GridListener gridListener = new GridListener();
        allowedToListen = true;
        board = new Reversi();
        for (int i = 0; i < board.SIZE; i++) {
            for (int j = 0; j < board.SIZE; j++) {
                slots[i][j] = new Slot(i, j);
                add(slots[i][j]);

                // Adding the MouseListener to each Slot.
                slots[i][j].addMouseListener(gridListener);
            }
        }
        update();
    }

    /**
     * Updates the slots according to the actual board of the grid, updates the
     * counter of the human and machine in the MainFrame and sets the undo
     * button to enable if there are moves to undo.
     */
    private void update() {

        // Set the colors of the slots according to actual board.
        for (int i = 0; i < board.SIZE; i++) {
            for (int j = 0; j < board.SIZE; j++) {
                if (board.getSlot(i, j) != null) {
                    switch (board.getSlot(i, j)) {
                        case HUMAN:
                            slots[i][j].setColor(Color.BLUE);
                            break;
                        case MACHINE:
                            slots[i][j].setColor(Color.RED);
                            break;
                        default:
                            throw new IllegalStateException();
                    }
                } else {
                    slots[i][j].setColor(Color.GREEN);
                }
            }

        }

        // Update the MainFrame
        Object source = getTopLevelAncestor();
        if (source instanceof MainFrame) {
            MainFrame mainFrame = (MainFrame) source;

            // Get the components which need to be updated.
            JButton undo = mainFrame.getUndo();
            JLabel humanTiles = mainFrame.getHumanTiles();
            JLabel machineTiles = mainFrame.getMachineTiles();

            // Update the components.
            undo.setEnabled(!pastMoves.empty());
            humanTiles.setText(board.getNumberOfHumanTiles() + "");
            machineTiles.setText(board.getNumberOfMachineTiles() + "");
        }
    }

    /**
     * Executes a move of the human Player on the clicked slot, instantly it
     * executes while the next Player is the bot, the best calculated
     * move for the bot corresponding to the actual level selected level in
     * the MainFrame of the bot.
     *
     * @param row Row of the clicked slot.
     * @param col Column of the clicked slot.
     */
    private void executeMove(int row, int col) {
        if (row < 0 || col < 0 || row > 7 || col > 7) {
            throw new IllegalArgumentException();
        }
        board.setLevel(level);
        if (!gameOver()) {
            Board b = board.clone();
            b = b.move(row + 1, col + 1);

            // Don't execute a illegal move.
            if (b != null) {

                // Push the latest move on the Stack.
                pastMoves.push(board);

                // Execute a move of the human Player.
                board = b.clone();

                // Update the Frame.
                update();
                if (!gameOver()) {
                    if (board.next() == Player.MACHINE) {

                        // Execute a machine move in separate Thread.
                        thread = new MachineMoveThread();

                        // Start the Thread.
                        thread.start();

                        // Update the Frame
                        update();
                    } else {

                        /*
                         * Machine has to miss a turn so you allowed to make
                         * another move.
                         */
                        allowedToListen = true;
                        createMessage(DialogType.MACHINE_HAS_TO_MISS);
                    }
                }
            } else {

                /*
                 * Human made an illegal move, it's allowed to enter another
                 * move.
                 */
                allowedToListen = true;
                createMessage(DialogType.ILLEGAL_MOVE);

            }
        }
    }

    /**
     * Creates a pop up according to a given DialogType.
     *
     * @param dialogType The DialogType for that a pop up is created.
     */
    private void createMessage(DialogType dialogType) {
        switch (dialogType) {
            case ILLEGAL_MOVE:
                JOptionPane.showMessageDialog(null,
                        "Illegal Move! \n Try Again!", "Illegal Move"
                                + "Warning", JOptionPane.WARNING_MESSAGE);
                break;
            case MACHINE_HAS_WON:
                JOptionPane.showMessageDialog(null,
                        "The game is over! \n Machine has won!");
                break;
            case HUMAN_HAS_WON:
                JOptionPane.showMessageDialog(null,
                        "The game is over! \n You have won!");
                break;
            case TIE:
                JOptionPane.showMessageDialog(null,
                        "The game is over! \n It's a tie!");
                break;
            case HUMAN_HAS_TO_MISS:
                JOptionPane.showMessageDialog(null,
                        "You have to miss a turn!");
                break;
            case MACHINE_HAS_TO_MISS:
                JOptionPane.showMessageDialog(null,
                        "Machine has to miss a turn!");
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Creates a new Reversi game.
     */
    public void createNewGame() {
        killThread();
        pastMoves.clear();
        board = new Reversi();
        update();
    }

    /**
     * Creates a new game and switches the Player to start. If the the bot
     * starts the game, the first move of the bot gets execute.
     */
    public void switchStarted() {
        killThread();
        pastMoves.clear();
        switch (board.getFirstPlayer()) {
            case MACHINE:
                board = new Reversi(Player.HUMAN);
                break;
            case HUMAN:
                board = new Reversi(Player.MACHINE);

                // Execute instantly the first move of the bot.
                board = board.machineMove();
                break;
            default:
                throw new IllegalStateException();
        }
        update();
    }

    /**
     * Setter for the actual level of the bot.
     *
     * @param level The level to be set.
     */
    public void setLevel(int level) {
        if (level < 1 || level > 5) {
            throw new IllegalArgumentException();
        }
        this.level = level;
    }

    /**
     * Undo the latest move, by popping the last board of the Stack and
     * setting this the actual board.
     */
    public void undo() {
        killThread();
        Board lastMOve = pastMoves.pop();
        board = lastMOve.clone();
        allowedToListen = true;
        update();
    }

    /**
     * Checks after a machine move if the next player is the machine. If so
     * execute another machine move in a separate Thread.
     */
    private void checkAfterMachineMove() {
        if (!gameOver() && board.next() == Player.MACHINE) {
            thread = new MachineMoveThread();
            thread.start();
        } else if (!gameOver() && board.next() == Player.HUMAN) {
            allowedToListen = true;
        }
    }

    /**
     * Kills a running MachineMoveThread.
     */
    @SuppressWarnings("deprecation")
    public void killThread() {
        if (thread != null) {
            thread.stop();
        }
        allowedToListen = true;
    }

    /**
     * Checks if the game is over or not. If so there's a short pop up which
     * says eho has won.
     *
     * @return Returns true if the game is over and gives out a short message
     *         who has won, else false.
     */
    private boolean gameOver() {
        if (board.gameOver()) {
            switch (board.getWinner()) {
                case MACHINE:
                    createMessage(DialogType.MACHINE_HAS_WON);
                    break;
                case HUMAN:
                    createMessage(DialogType.HUMAN_HAS_WON);
                    break;
                default:
                    createMessage(DialogType.TIE);
                    break;
            }
            allowedToListen = false;
            return true;
        }
        return false;
    }

    /**
     * This class provides the implementation of the mouse listener.
     */
    private class GridListener extends MouseAdapter {

        /**
         * If a slot is clicked execute a move on the clicked slot.
         *
         * @param e The mouse event.
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e == null) {
                throw new IllegalArgumentException();
            }
            if (allowedToListen) {
                super.mouseClicked(e);
                Object source = e.getSource();
                if (source instanceof Slot) {

                    /*
                     * Move being executed, the human isn't allowed to enter
                     * another move.
                     */
                    allowedToListen = false;
                    Slot clicked = (Slot) source;

                    // Execute a move.
                    executeMove(clicked.getRow(), clicked.getCol());
                }
            }
        }
    }

    /**
     * This class provides the implementation for a machine move in a separate
     * Thread.
     */
    private class MachineMoveThread extends Thread {

        /**
         * Execute a machine move in a separate Thread.
         */
        @Override
        public void run() {
            board = board.machineMove();
            SwingUtilities.invokeLater(Grid.this::update);

            // Checks race condition after machine move.
            checkAfterMachineMove();
        }

    }
}
