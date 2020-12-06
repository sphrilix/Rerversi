package de.sphrilix.reversi.gui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * This class provides the implementation of a Slot on the Grid.
 */
public class Slot extends JPanel {

    /**
     * Scale of a Tile in a Slot.
     */
    private static final double SCALE_OF_TILE = 0.9;

    /**
     * Padding of a Tile in a Slot.
     */
    private static final double PADDING_OF_TILE = (1 - SCALE_OF_TILE) / 2;

    /**
     * Standard background color.
     */
    private static final Color BACKGROUND_COLOR = Color.GREEN;

    /**
     * Standard Tile size.
     */
    private static final int STANDARD_TILE_SIZE = 100;

    /**
     * Row index of the actual Slot
     */
    private int row;

    /**
     * Column index of the actual Slot
     */
    private int col;

    /**
     * Color of the set in the Slot.
     */
    private Color color;

    /**
     * Creates a new Slot and set the index of the column and the row by the
     * given parameters.
     *
     * @param row Given row index of the slot
     * @param col Given column index of the slot
     */
    public Slot(int row, int col) {
        if (row < 0 || col < 0 || row > 7 || col > 7) {
            throw new IllegalArgumentException();
        }
        this.row = row;
        this.col = col;
        setPreferredSize(new Dimension(STANDARD_TILE_SIZE,
                STANDARD_TILE_SIZE));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    /**
     * Getter for the color of the Tile on the Slot.
     *
     * @return Returns the color of the Tile on the Slot.
     */
    public int getCol() {
        return col;
    }

    /**
     * Getter for the row index.
     *
     * @return Returns the row index.
     */
    public int getRow() {
        return row;
    }

    /**
     * Setter for the color of the Tile on the Slot.
     *
     * @param color The color to be set.
     */
    public void setColor(Color color) {
        if (color != BACKGROUND_COLOR && color != Color.BLUE
                && color != Color.RED) {
            throw new IllegalArgumentException();
        }
        if (this.color != color) {
            this.color = color;
            repaint();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics g) {
        if (g == null) {
            throw new IllegalArgumentException();
        }
        super.paintComponent(g);
        int height = getHeight();
        int width = getWidth();
        g.setColor(color);
        g.fillOval((int) (width * PADDING_OF_TILE),
                (int) (height * PADDING_OF_TILE),
                (int) (width * SCALE_OF_TILE),
                (int) (height * SCALE_OF_TILE));
    }
}
