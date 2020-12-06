package de.sphrilix.reversi.gui;

/**
 * Types of dialogs with the user can appear.
 */
public enum DialogType {

    /**
     * Illegal move dialog.
     */
    ILLEGAL_MOVE,

    /**
     * Human has to miss dialog.
     */
    HUMAN_HAS_TO_MISS,

    /**
     * Machine has to miss dialog.
     */
    MACHINE_HAS_TO_MISS,

    /**
     * Machine has won dialog.
     */
    MACHINE_HAS_WON,

    /**
     * Human has won dialog.
     */
    HUMAN_HAS_WON,

    /**
     * Tie dialog.
     */
    TIE
}
