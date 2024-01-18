package chess;

import java.util.HashSet;
import java.util.Set;

public class ChessRules {
    private final ChessBoard board;
    private ChessPiece piece;
    private ChessMove currMove;
    private boolean repeatable = true;

    public ChessRules(ChessBoard board, ChessPiece piece) {
        this.board = board;
        this.piece = piece;
    }

    Set<ChessMove> getPossibleMoves(ChessPosition startPosition) {
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            return movePawn(startPosition);
        }
        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            return moveRook(startPosition);
        }
        if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            return moveKnight(startPosition);
        }
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            return moveBishop(startPosition);
        }
        if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            return moveQueen(startPosition);
        }
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            return moveKing(startPosition);
        }
    }

    private Set<ChessMove> movePawn(ChessPosition startPosition) {
        Set<ChessMove> possible = new HashSet<>();
        currMove = new ChessMove(startPosition, startPosition);

        //move one space forward for black
        ChessPosition endPosition = startPosition.decrementRow();
        if (piece.getPieceType() == ChessGame.TeamColor.BLACK && endPosition.onBoard() && board.at(endPosition) == null)
            ;

        return possible;
    }

    private Set<ChessMove> moveRook(ChessPosition startPosition) {
    }

    private Set<ChessMove> moveKnight(ChessPosition startPosition) {
    }

    private Set<ChessMove> moveBishop(ChessPosition startPosition) {
    }

    private Set<ChessMove> moveQueen(ChessPosition startPosition) {
    }

    private Set<ChessMove> moveKing(ChessPosition startPosition) {
    }
}
