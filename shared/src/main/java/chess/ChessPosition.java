package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private int row = 0;
    private int col = 0;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    /**
     * @return a position one to the left
     */
    public ChessPosition decrementRow() {
        return new ChessPosition(row - 1, col);
    }

    /**
     * @return a position one to the right
     */
    public ChessPosition incrementRow() {
        return new ChessPosition(row + 1, col);
    }

    /**
     * @return a position one below
     */
    public ChessPosition decrementCol() {
        return new ChessPosition(row, col - 1);
    }

    /**
     * @return a position one above
     */
    public ChessPosition incrementCol() {
        return new ChessPosition(row, col + 1);
    }

    /**
     * @return a boolean indicating if position is on board
     */
    public boolean onBoard() {
        if (row > 0 && row < 9) {
            return col > 0 && col < 9;
        }
        return false;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) object;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
