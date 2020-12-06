package de.sphrilix.reversi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the implementation of Reversi as a single player
 * application, using the given Board interface.
 */
public class Reversi implements Board {

    /**
     * This 2d array consists of the points for each field on the game Board.
     */
    private static int[][] pointsOfEachField = {
            {9999, 5, 500, 200, 200, 500, 5, 9999},
            {5, 1, 50, 150, 150, 50, 1, 5},
            {500, 50, 250, 100, 100, 250, 50, 500},
            {200, 150, 100, 50, 50, 100, 150, 200},
            {200, 150, 100, 50, 50, 100, 150, 200},
            {500, 50, 250, 100, 100, 250, 50, 500},
            {5, 1, 50, 150, 150, 50, 1, 5},
            {9999, 5, 500, 200, 200, 500, 5, 9999}};

    /**
     * The standard level of the bot.
     */
    private static final int STANDARD_LEVEL = 3;

    /**
     * Offset for the row.
     */
    private static final int[] OFFSET_ROW = {-1, -1, -1, 0, 0, 1, 1, 1};

    /**
     * Offset for the column.
     */
    private static final int[] OFFSET_COL = {-1, 0, 1, -1, 1, -1, 0, 1};

    /**
     * Player who is next.
     */
    private Player next;

    /**
     * Player who started the game.
     */
    private Player started;

    /**
     * Current level of the bot.
     */
    private int level;

    /**
     * 2d array which models the board of the game.
     */
    private Tile[][] board;

    /**
     * Creates a new game instance.
     */
    public Reversi() {
        started = Player.HUMAN;
        board = getStartPosition(started);
        next = started;
        level = STANDARD_LEVEL;
    }

    /**
     * Creates a new instance by a given Player who starts next game.
     *
     * @param started The player who starts next game;
     */
    public Reversi(Player started) {
        if (started == null) {
            throw new IllegalArgumentException("Not existing player!");
        }
        level = STANDARD_LEVEL;
        this.started = started;
        next = started;
        board = getStartPosition(started);
    }

    /**
     * Calculates the start position corresponding to a given Player.
     *
     * @param started The player who's start position gets calculated.
     * @return Returns the start position of the given Player.
     */
    private Tile[][] getStartPosition(Player started) {
        assert started != null : "Player must not be null!";
        Tile[][] startPosition = new Tile[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if ((i == 3 && j == 3) || (i == 4 && j == 4)) {
                    startPosition[i][j] = new Tile(getEnemy(started));
                } else if ((i == 3 && j == 4) || (i == 4 && j == 3)) {
                    startPosition[i][j] = new Tile(started);
                } else {
                    startPosition[i][j] = new Tile();
                }
            }
        }
        return startPosition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player getFirstPlayer() {
        return started;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player next() {
        return next;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board move(int row, int col) {
        Reversi b = this.clone();
        if (next == Player.MACHINE || gameOver()) {
            throw new IllegalMoveExceptions();
        } else if (row < 1 || col < 1 || row > SIZE || col > SIZE) {
            throw new IllegalArgumentException("One or both of the params "
                    + "aren't on the grid!");
        } else if (possibleMove(row - 1, col - 1, Player.HUMAN)) {
            b.flip(row - 1, col - 1, Player.HUMAN);
        } else {
            return null;
        }
        b.next = b.calculateNext(Player.HUMAN);
        return b;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Board machineMove() {
        if (next == Player.HUMAN || gameOver()) {
            throw new IllegalMoveExceptions();
        }

        // Build up the game tree.
        TreeNode root = buildUp();

        // Set the scores in the tree.
        setScoresInTree(root);

        // Execute the best move for the bot corresponding to the score.
        return getBestMove(root);
    }

    /**
     * Calculated the next player after a given player made his turn.
     *
     * @param player The plaayer with the last move.
     * @return Returns the player who's next.
     */
    private Player calculateNext(Player player) {
        assert player != null : "Player must not be null!";

        // Normally enemy gets next move.
        Player next = getEnemy(player);

        // Check if next has a possible move.
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (possibleMove(i, j, next)) {
                    return next;
                }
            }
        }

        // If next has no possible move, so he has to miss.
        return player;
    }

    /**
     * Method which flips the Tiles for the given move.
     *
     * @param row    Row of the move.
     * @param col    Column of the move.
     * @param player Player which executed the move.
     */
    private void flip(int row, int col, Player player) {
        assert possibleMove(row, col, player) : "Invalid move!";

        // List of Tile which need to be flipped.
        List<Tile> toBeFlipped = new ArrayList<>();

        // Set the new Tile on the given coordinates.
        board[row][col] = new Tile(player);

        boolean opTileBetween = false;

        // Go through the 8 directions using the offsets.
        for (int i = 0; i < 8; i++) {
            int curRow = row + OFFSET_ROW[i];
            int curCol = col + OFFSET_COL[i];
            while (curCol >= 0 && curCol < SIZE && curRow >= 0
                    && curRow < SIZE) {
                if (getSlot(curRow,
                        curCol) == getEnemy(player)) {

                    /*
                     * Add all Tiles in the same direction to the toBeFlipped
                     * list, in case the need to be flipped.
                     */
                    toBeFlipped.add(board[curRow][curCol]);
                    opTileBetween = true;
                    curRow += OFFSET_ROW[i];
                    curCol += OFFSET_COL[i];

                    /*
                     * If reaching the borders of the Board clear the list,
                     * because there is then a invalid move. (Tiles not
                     * surrounded baa Tiles of the Player)
                     */
                    if (curCol >= SIZE || curRow >= SIZE || curCol < 0
                            || curRow < 0) {
                        toBeFlipped.clear();
                    }
                } else if (getSlot(curRow, curCol) == player
                        && opTileBetween) {

                    // Flip Tiles which should be flipped.
                    for (Tile t : toBeFlipped) {
                        t.setC(player);
                    }
                    toBeFlipped.clear();
                    break;
                } else {

                    /* Clear the list, because the Tiles in this direction
                     * must not be flipped and a new direction begins to check.
                     */
                    opTileBetween = false;
                    toBeFlipped.clear();
                    break;
                }
            }
        }
    }

    /**
     * Calculates all possible moves of given player on the current game
     * instance.
     *
     * @param player The given player who's possible should be calculated.
     * @return Return an List of all possible moves of the given player.
     */
    private List<Reversi> possibleMoves(Player player) {
        assert player != null : "Player must not be null!";
        List<Reversi> possibleMoves = new ArrayList<>();

        /*
         * Going over the complete Board and check if this certain move is a
         * possible move, by using possibleMove(). If so create a deep copy
         * of the Board and execute the move on the copy and add the copy to
         * the list.
         */
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (possibleMove(i, j, player)) {
                    Reversi b = this.clone();
                    b.flip(i, j, player);
                    b.next = b.calculateNext(player);
                    possibleMoves.add(b);
                }
            }
        }
        return possibleMoves;
    }

    /**
     * Calculates if a given move is a possible move for a given Player
     *
     * @param row    Row of to be checked move.
     * @param col    Column of the to be checked move.
     * @param player Player of the to be checked move.
     * @return Return whether it's a possible move or not.
     */
    private boolean possibleMove(int row, int col, Player player) {
        assert player != null : "Player must not be null!";
        assert row < SIZE && row > 0 && col < 8 && col > 0 : "Move not on "
                + "board!";

        // Field where set must be empty.
        if (!(getSlot(row, col) == null)) {
            return false;
        }

        // Go through the 8 directions using offsets.
        for (int i = 0; i < 8; i++) {
            boolean opPieceBetween = false;
            int curRow = row + OFFSET_ROW[i];
            int curCol = col + OFFSET_COL[i];

            // Go same direction as long as possible.
            while (curCol >= 0 && curCol < SIZE && curRow >= 0
                    && curRow < SIZE) {

                /*
                 * While the next Tile in the same direction is a Tile of the
                 * enemy go on.
                 */
                if (getSlot(curRow, curCol) == getEnemy(player)) {
                    opPieceBetween = true;
                    curRow += OFFSET_ROW[i];
                    curCol += OFFSET_COL[i];
                } else if (getSlot(curRow, curCol) == player
                        && opPieceBetween) {

                    /*
                     * A direction found where enemy Tiles are between a
                     * player Tile -> possible move.
                     */
                    return true;
                } else {

                    //Not a possible move in this direction so break the loop.
                    opPieceBetween = false;
                    break;
                }

            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLevel(int level) {
        if (level < 0 || level > 5) {
            throw new IllegalArgumentException("Level must be between 1 and "
                    + "5!");
        }
        this.level = level;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean gameOver() {
        return possibleMoves(Player.HUMAN).size() < 1
                && possibleMoves(Player.MACHINE).size() < 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player getWinner() {
        if (getNumberOfHumanTiles() < getNumberOfMachineTiles()) {
            return Player.MACHINE;
        } else if (getNumberOfMachineTiles() < getNumberOfHumanTiles()) {
            return Player.HUMAN;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfHumanTiles() {
        int count = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j].getC() == 'X') {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfMachineTiles() {
        int count = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j].getC() == 'O') {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player getSlot(int row, int col) {
        if (row >= SIZE || col >= SIZE || row < 0 || col < 0) {
            throw new IllegalArgumentException("One or both params aren't on "
                    + "the grid");
        }
        switch (board[row][col].getC()) {
            case 'X':
                return Player.HUMAN;
            case 'O':
                return Player.MACHINE;
            default:
                return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Reversi clone() {
        Reversi b = new Reversi();
        b.started = this.started;
        b.next = this.next;
        b.setLevel(this.level);
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (getSlot(i, j) != null) {
                    b.board[i][j] = new Tile(this.getSlot(i, j));
                } else {
                    b.board[i][j] = new Tile();
                }
            }
        }
        return b;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                sb.append(board[i][j].toString());
                if (j < SIZE - 1) {
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Calculates the score of the actual instance in view of the bot.
     *
     * @return Returns the score of the actual instance.
     */
    public double score() {
        return scoreT() + scoreM() + scoreP();
    }

    /**
     * Calculates scoreT of the actual instance.
     *
     * @return Return scoreT of the actual instance.
     */
    private double scoreT() {
        int sumOfPlayerTiles = 0;
        int sumOfEnemyTiles = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (getSlot(i, j) == Player.MACHINE) {
                    sumOfPlayerTiles += pointsOfEachField[i][j];
                } else if (getSlot(i, j) == Player.HUMAN) {
                    sumOfEnemyTiles += pointsOfEachField[i][j];
                }
            }

        }
        return sumOfPlayerTiles - sumOfEnemyTiles * 1.5;
    }

    /**
     * Calculates scoreM of the actual instance.
     *
     * @return Return scoreM of the actual instance.
     */
    private double scoreM() {
        int mPlayer = possibleMoves(Player.MACHINE).size();
        int mEnemy = possibleMoves(Player.HUMAN).size();
        return (64.0 / occupiedFields()) * (3.0 * mPlayer - 4.0 * mEnemy);
    }

    /**
     * Calculates scoreP of the actual instance.
     *
     * @return Return scoreP of the actual instance.
     */
    private double scoreP() {
        int freeFieldsPlayer = freeFields(Player.HUMAN);
        int freeFieldsEnemy = freeFields(Player.MACHINE);
        return 64.0 / (2 * occupiedFields()) * (2.5 * freeFieldsPlayer - 3.0
                * freeFieldsEnemy);
    }

    /**
     * Helping method for scoreP which calculates the amount of free fields
     * around the set Tiles.
     *
     * @param player Player who's free fields gets calculated.
     * @return Returns amount of free fields.
     */
    private int freeFields(Player player) {
        assert player != null : "Player must not be null!";
        int freeFields = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (getSlot(i, j) == player) {

                    // Go through the 8 Tiles surrounding the given.
                    for (int k = 0; k < OFFSET_ROW.length; k++) {
                        int row = i + OFFSET_COL[k];
                        int col = j + OFFSET_ROW[k];

                        // If there's one which isn't occupied raise counter.
                        if (row >= 0 && col >= 0 && row < SIZE && col < SIZE
                                && getSlot(row, col) == null) {
                            freeFields++;
                        }
                    }
                }
            }
        }
        return freeFields;
    }


    /**
     * Calculates the amount of the occupied fields.
     *
     * @return Returns the amount of occupied fields.
     */
    private int occupiedFields() {
        return getNumberOfMachineTiles() + getNumberOfHumanTiles();
    }

    private static Reversi getBestMove(TreeNode node) {
        assert node != null : "Node must not be null!";
        List<TreeNode> children = node.getChildren();

        // Get the first child of a Node and executes it's move.
        TreeNode firstChild = children.get(0);
        Reversi firstMove = (Reversi) firstChild.getBoard();

        /*
         * The current best move and best score is the move and score of the
         * first child.
         */
        Reversi bestMove = firstMove.clone();
        double bestScore = firstChild.getScore();

        // Go through the children and search for a better move.
        for (TreeNode g : children) {
            Reversi temp = (Reversi) g.getBoard();

            // Better move found as the current best move.
            if (g.getScore() > bestScore) {
                bestScore = g.getScore();
                bestMove = temp.clone();

            }
        }
        return bestMove;
    }

    /**
     * This method is building up the game tree to evaluate the best move of
     * the bot corresponding to the actual level of the bot.
     * The calculation of the tree is happening recursively.
     *
     * @return Returns the root of the game tree.
     */
    private TreeNode buildUp() {
        TreeNode root = new TreeNode(this, null);
        buildUpHelp(level, root);
        return root;
    }

    /**
     * This method is helping the buildUp() method to build the game tree.
     *
     * @param level  The remaining depth of the game tree.
     * @param parent The parent of the children.
     */
    private static void buildUpHelp(int level, TreeNode parent) {
        assert parent != null : "Parent must be not null!";
        assert level < 6 && level > -1 : "Invalid level!";
        Reversi actualMove = (Reversi) parent.getBoard();
        if (level > 0 && !actualMove.gameOver()) {

            // Calculate all possible move of actual Board.
            List<Reversi> movesOfChildren =
                    actualMove.possibleMoves(actualMove.next());
            List<TreeNode> children = new ArrayList<>();

            // Set the reference for the children of parent.
            parent.setChildren(children);

            /*
             * Set the children's parent and boards and also it's the
             * recursive layer.
             */
            for (Board b : movesOfChildren) {
                Board temp = b.clone();
                TreeNode g = new TreeNode(temp, parent);
                children.add(g);
                buildUpHelp(level - 1, g);
            }
        }
    }

    /**
     * Setter for the scores in a game tree.
     *
     * @param node The root of the game tree where the scores to be set.
     */
    private static void setScoresInTree(TreeNode node) {
        assert node != null : "Node must be not null!";
        if (node.hasChildren()) {

            // Recursive layer to set scores in all part trees.
            for (TreeNode child : node.getChildren()) {
                setScoresInTree(child);
            }

            // If node is root don't set the score (doesn't matter).
            if (node.getParent() != null) {
                Reversi actualMove = (Reversi) node.getBoard();

                // Set the correct score in the given TreeNode.
                miniMax(node);
            }
        } else {
            Reversi actualMove = (Reversi) node.getBoard();

            // Score of a leave is the own score.
            node.setScore(actualMove.score());
        }
    }

    /**
     * Calculates the enemy of the given Player.
     *
     * @param player The given Player, who's enemy gets calculated.
     * @return Returns the enemy of the actual Player.
     */
    private static Player getEnemy(Player player) {
        switch (player) {
            case HUMAN:
                return Player.MACHINE;
            case MACHINE:
                return Player.HUMAN;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Calculates and sets the score of the given TreeNode corresponding to the
     * best or worst score of his children. If the last player (player of the
     * given TreeNode) was the bot, the score of the given TreeNode is the best
     * score of the children + the own score, else if the last Player was the
     * human, the score of the given Node is the worst score of the children +
     * the own score.
     *
     * @param node The TreeNode who's score to be set.
     */
    private static void miniMax(TreeNode node) {
        assert node != null : "Node must not be null!";
        Reversi boardOfNode = (Reversi) node.getBoard();
        switch (boardOfNode.next()) {
            case MACHINE:

                // Calculate the best score of children (last Player was the bot).
                double bestScore = Double.NEGATIVE_INFINITY;
                for (TreeNode child : node.getChildren()) {
                    if (child.getScore() > bestScore) {
                        bestScore = child.getScore();
                    }
                }

                // Set the score in the given TreeNode.
                node.setScore(boardOfNode.score() + bestScore);
                break;
            case HUMAN:

                /*
                 * Calculate the worst score of the children (last Player was
                 * human).
                 */
                double worstScore = Double.POSITIVE_INFINITY;
                for (TreeNode child : node.getChildren()) {
                    if (child.getScore() < worstScore) {
                        worstScore = child.getScore();
                    }
                }

                // Set the score int the given TreeNode.
                node.setScore(boardOfNode.score() + worstScore);
                break;
            default:
                throw new IllegalStateException();
        }
    }
}