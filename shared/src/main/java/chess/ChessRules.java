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
        return new HashSet<>();
    }

    private Set<ChessMove> movePawn(ChessPosition startPosition) {
        Set<ChessMove> possible = new HashSet<>();
        currMove = new ChessMove(startPosition, startPosition);

        //move one space forward for black
        ChessPosition endPosition = startPosition.decrementRow();
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK && endPosition.onBoard() && board.at(endPosition) == null) {
            currMove = new ChessMove(startPosition, endPosition);
            possible.add(currMove);
        }

        //move one space forward for white
        endPosition = startPosition.incrementRow();
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && endPosition.onBoard() && board.at(endPosition) == null) {
            possible.add(currMove);
        }

        //add captures
        possible.addAll(pawnCapture(startPosition));

        return possible;
    }

    private Set<ChessMove> pawnCapture(ChessPosition startPosition) {
        Set<ChessMove> possible = new HashSet<>();
        var endPosition = startPosition;
        var currMove = new ChessMove(startPosition, endPosition);

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            endPosition = startPosition.incrementRow();
        } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            endPosition = startPosition.decrementRow();
        }

        //check right
        ChessPosition test = endPosition.incrementCol();
        if (test.onBoard() && board.at(test) != null && board.at(test).getTeamColor() != piece.getTeamColor()) {
            currMove = new ChessMove(startPosition, test);
            possible.add(currMove);
        }

        //check left
        test = endPosition.decrementCol();
        if (test.onBoard() && board.at(test) != null && board.at(test).getTeamColor() != piece.getTeamColor()) {
            currMove = new ChessMove(startPosition, test);
            possible.add(currMove);
        }

        return possible;
    }

    private Set<ChessMove> moveRook(ChessPosition startPosition) {
        Set<ChessMove> possible = new HashSet<>();

        return possible;
    }

    private Set<ChessMove> moveKnight(ChessPosition startPosition) {
        Set<ChessMove> possible = new HashSet<>();

        return possible;
    }

    private Set<ChessMove> moveBishop(ChessPosition startPosition) {
        Set<ChessMove> possible = new HashSet<>();

        return possible;
    }

    private Set<ChessMove> moveQueen(ChessPosition startPosition) {
        Set<ChessMove> possible = new HashSet<>();

        return possible;
    }

    private Set<ChessMove> moveKing(ChessPosition startPosition) {
        Set<ChessMove> possible = new HashSet<>();

        return possible;
    }
}
