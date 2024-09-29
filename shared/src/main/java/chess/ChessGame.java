package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor turn = TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Set<ChessMove> possible = new HashSet<>();
        Set<ChessMove> valid = new HashSet<>();

        if (startPosition == null || !startPosition.onBoard()) {
            return possible;
        }

        ChessPiece piece = board.at(startPosition);
        if (piece == null) {
            return possible;
        }

        ChessRules rules = new ChessRules(board,piece);
        possible = rules.getPossibleMoves(startPosition);

        //make sure no moves enter check
        for (ChessMove move : possible) {
            if (notEnterCheck(move)) {
                valid.add(move);
            }
        }

        return valid;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //if not team turn, throw error
        ChessPiece piece = board.at(move.getStartPosition());
        if (turn != piece.getTeamColor()) {
            throw (new InvalidMoveException());
        }

        if (!isValidMove(move)) {
            throw (new InvalidMoveException());
        }

        //promote pawn
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if ((piece.getTeamColor() == TeamColor.BLACK && move.getEndPosition().getRow() == 1)
                || (piece.getTeamColor() == TeamColor.WHITE && move.getEndPosition().getRow() == 8)) {
                piece = new ChessPiece(turn, move.getPromotionPiece());
            }
        }

        //execute move
        board.addPiece(move.getEndPosition(), piece);
        board.removePiece(move.getStartPosition());

        //if game is now in check, not a valid move
        //this should be a redundant line, as the isValidMove check above checks if the potential
        //move will enter check
        //TODO:: consider removing
        if (isInCheck(turn)) {
            throw (new InvalidMoveException());
        }

        //pass turn
        if (piece.getTeamColor() == TeamColor.BLACK) {
            turn = TeamColor.WHITE;
        } else {
            turn = TeamColor.BLACK;
        }
    }

    /**
     * Checks if a move is allowed according to game rules
     *
     * @param move chess move to test
     * @return if move is allowed
     */
    public boolean isValidMove(ChessMove move) {
        if (move.getStartPosition() == null || !move.getStartPosition().onBoard() || !move.getEndPosition().onBoard()) {
            return false;
        }

        ChessPiece piece = board.at(move.getStartPosition());
        if (piece == null) {
            return false;
        }

        //get all possible moves
        ChessRules rules = new ChessRules(board, piece);
        Set<ChessMove> moves = rules.getPossibleMoves(move.getStartPosition());

        //check if move is possible and if so, if it enters check
        for (ChessMove possibleMove : moves) {
            if (move.equals(possibleMove) && notEnterCheck(move)) {
                return true;
            }
        }

        return false;
    }

    /**
     * checks whether a move will enter check
     *
     * @param move chess move to test
     * @return true if move does NOT enter check
     */
    public boolean notEnterCheck(ChessMove move) {
        ChessBoard testBoard = board.copy();
        ChessPiece piece = testBoard.at(move.getStartPosition());

        //execute move on testBoard
        //doesn't check as many conditions for this move being valid because it's only affecting a temporary board
        //only checks conditions that could make subsequent function calls impossible
        if (testBoard.at(move.getEndPosition()) == null
                || testBoard.at(move.getEndPosition()).getTeamColor() != piece.getTeamColor()) {
            testBoard.addPiece(move.getEndPosition(), piece);
            testBoard.removePiece(move.getStartPosition());
        }

        //check if in check
        ChessRules testCheck = new ChessRules(testBoard, null);
        return !testCheck.isInCheck(piece.getTeamColor());
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        throw new RuntimeException("Not implemented");
    }
}
