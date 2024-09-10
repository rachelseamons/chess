package chess;

import java.util.HashSet;
import java.util.Set;

public class ChessRules {
    private final ChessBoard board;
    private ChessPiece piece;
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
        if (piece.getPieceType() == ChessPiece.PieceType.QUEEN || piece.getPieceType() == ChessPiece.PieceType.KING) {
            return moveRoyalty(startPosition);
        }
        return new HashSet<>();
    }

    private Set<ChessMove> movePawn(ChessPosition startPosition) {
        Set<ChessMove> possible = new HashSet<>();

        //move one space forward for black
        ChessPosition endPosition = startPosition.decrementRow();
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK && endPosition.onBoard() && board.at(endPosition) == null) {
            if (endPosition.getRow() == 1) {
                possible.addAll(promotePawn(startPosition, endPosition));
            } else {
                possible.add(new ChessMove(startPosition, endPosition));
            }
        }

        //move one space forward for white
        endPosition = startPosition.incrementRow();
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && endPosition.onBoard() && board.at(endPosition) == null) {
            if (endPosition.getRow() == 8) {
                possible.addAll(promotePawn(startPosition, endPosition));
            } else {
                possible.add(new ChessMove(startPosition, endPosition));
            }
        }

        //add captures
        possible.addAll(pawnCapture(startPosition));

        //add double move from initial positions
        if ((piece.getTeamColor() == ChessGame.TeamColor.BLACK && startPosition.getRow() == 7)
                || (piece.getTeamColor() == ChessGame.TeamColor.WHITE && startPosition.getRow() == 2)) {
            possible.addAll(pawnInitial(startPosition));
        }

        return possible;
    }
}
