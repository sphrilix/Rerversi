package de.sphrilix.reversi.model;

/**
 * This class provides the model of the Tile for Reversi.
 */
public class Tile {

    /**
     * Char of the Tile corresponding to the player.
     */
    private char c;

    /**
     * Constructor which creates a new empty Tile.
     */
    public Tile() {
        c = '.';
    }

    /**
     * Creates new Tile corresponding to a given Player.
     *
     * @param player The player who's Tile should be created.
     */
    public Tile(Player player) {
        switch (player) {
            case MACHINE:
                c = 'O';
                break;
            case HUMAN:
                c = 'X';
                break;
            default:
                throw new IllegalArgumentException("Not existing Player!");
        }
    }

    /**
     * Getter for the char of a Tile.
     *
     * @return Return the char of a Tile.
     */
    public char getC() {
        return c;
    }

    /**
     * Setter for the char of a Tile, corresponding to a given player.
     *
     * @param player The player which the Tile belongs.
     */
    public void setC(Player player) {
        switch (player) {
            case MACHINE:
                c = 'O';
                break;
            case HUMAN:
                c = 'X';
                break;
            default:
                throw new IllegalArgumentException("Not existing Player!");
        }
    }

    /**
     * Calculates a String representation of a Tile.
     *
     * @return Returns the String representation of a Tile.
     */
    public String toString() {
        return  c + "";
    }

}
