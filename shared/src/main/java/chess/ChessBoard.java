package chess;

import java.util.Arrays;
import java.util.Objects;

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
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
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
     * returns the ChessPiece object from the board at a ChessPosition
     *
     * @param position the position to get the piece from
     * @return Either the piece at the position or null if there is no piece
     */
    public ChessPiece at(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    public void removePiece(ChessPosition position) {
        board[position.getRow() - 1][position.getColumn() - 1] = null;
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

        addRooks();
        addKnights();
        addBishops();
        addRoyalty();
        addPawns();
    }

    /**
     * helper function for resetBoard
     * adds rooks in starting positions
     */
    private void addRooks() {
        ChessPiece whiteRook = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        ChessPiece blackRook = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);

        board[0][0] = whiteRook;
        board[0][7] = whiteRook;

        board[7][0] = blackRook;
        board[7][7] = blackRook;
    }

    /**
     * helper function for resetBoard
     * adds knights in starting positions
     */
    private void addKnights() {
        ChessPiece whiteKnight = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        ChessPiece blackKnight = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);

        board[0][1] = whiteKnight;
        board[0][6] = whiteKnight;

        board[7][1] = blackKnight;
        board[7][6] = blackKnight;
    }

    /**
     * helper function for resetBoard
     * adds bishops in starting positions
     */
    private void addBishops() {
        ChessPiece whiteBishop = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        ChessPiece blackBishop = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);

        board[0][2] = whiteBishop;
        board[0][5] = whiteBishop;

        board[7][2] = blackBishop;
        board[7][5] = blackBishop;
    }

    /**
     * helper function for resetBoard
     * adds kings and queens in starting positions
     */
    private void addRoyalty() {
        ChessPiece whiteKing = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        ChessPiece blackKing = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        ChessPiece whiteQueen = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        ChessPiece blackQueen = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);

        board[0][3] = whiteQueen;
        board[0][4] = whiteKing;

        board[7][3] = blackQueen;
        board[7][4] = blackKing;
    }

    /**
     * helper function for resetBard
     * adds pawns in starting positions
     */
    private void addPawns() {
        ChessPiece whitePawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        ChessPiece blackPawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);

        for (int i = 0; i < BOARD_WIDTH; i++) {
            board[1][i] = whitePawn;
            board[6][i] = blackPawn;
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ChessBoard that = (ChessBoard)  object;
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                if (!(this.board[i][j] == null) && !(that.board[i][j] == null)
                        && !this.board[i][j].equals(that.board[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    public ChessBoard copy() {
        ChessBoard copy = new ChessBoard();

        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                copy.board[i][j] = this.board[i][j];
            }
        }

        return copy;
    }
}
