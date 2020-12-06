package de.sphrilix.reversi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the implementation of the leaves of the game tree.
 */
public class TreeNode {

    /**
     * Score of the part tree under the actual leave.
     */
    private double score;

    /**
     * Parent of the actual leave.
     */
    private TreeNode parent;

    /**
     * Board of the actual leave.
     */
    private Board board;

    /**
     * All possible moves of the actual leave.
     */
    private List<TreeNode> children = new ArrayList<>();

    /**
     * Creates a new instance of a GameLeave.
     *
     * @param board The Board of the new GameLeave.
     * @param parent The parent of the new GameLeave.
     */
    public TreeNode(Board board, TreeNode parent) {
        if (board == null) {
            throw new IllegalArgumentException();
        }
        this.parent = parent;
        this.board = board;
    }

    /**
     * Setter for the children.
     *
     * @param children The children to be set for the actual leave.
     */
    public void setChildren(List<TreeNode> children) {
        if (children == null) {
            throw new IllegalArgumentException();
        }
        this.children = children;
    }

    /**
     * Setter for the score.
     *
     * @param score The score to be set.
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * Getter for the score.
     *
     * @return Returns the score.
     */
    public double getScore() {
        return score;
    }

    /**
     * Getter for the Board.
     *
     * @return Returns the Board of the actual leave.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Getter for the children.
     *
     * @return Returns the list of children of the actual leave.
     */
    public List<TreeNode> getChildren() {
        return children;
    }

    /**
     * Getter for the parent.
     *
     * @return Returns the parent of the actual leave.
     */
    public TreeNode getParent() {
        return parent;
    }

    /**
     * Method which calculates if the actual leave have any children.
     *
     * @return Returns true if so, else false.
     */
    public boolean hasChildren() {
        return children.size() > 0;
    }
}

