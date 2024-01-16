package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final int BOARD_HEIGHT = 8;
    private final int BOARD_WIDTH = 8;
    private ChessPiece[][] board = new ChessPiece[BOARD_HEIGHT][BOARD_WIDTH];

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return at(position);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //emptying board
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                board[i][j] = null;
            }
        }
    }

    /**
     * returns the ChessPiece object from the board at a ChessPosition
     * @param position the position to get the piece from
     * @return Either the piece at the position or null if there is no piece
     */
    public ChessPiece at(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }
}
