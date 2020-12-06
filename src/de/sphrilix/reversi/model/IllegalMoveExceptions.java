package de.sphrilix.reversi.model;

/**
 * This class provides the model for a new RuntimeException a
 * IllegalMoveException.
 */
public class IllegalMoveExceptions extends RuntimeException {

    /**
     * Creates a new Instance of a IllegalMoveException
     */
    public IllegalMoveExceptions() {
        super("Illegal move.");
    }

    /**
     * Creates a new Instance of a IllegalMoveException with a given error
     * message.
     *
     * @param error The given error message.
     */
    public IllegalMoveExceptions(String error) {
        super(error);
    }
}
