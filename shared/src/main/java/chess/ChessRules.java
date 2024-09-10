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

    private Set<ChessMove> promotePawn(ChessPosition startPosition, ChessPosition endPosition) {
        Set<ChessMove> possible = new HashSet<>();

        possible.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.QUEEN));
        possible.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.KNIGHT));
        possible.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.BISHOP));
        possible.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.ROOK));

        return possible;
    }

    private Set<ChessMove> pawnInitial(ChessPosition startPosition) {
        Set<ChessMove> possible = new HashSet<>();
        var endPosition = startPosition;
        var testBlocked = startPosition;

        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            testBlocked = startPosition.decrementRow();
            endPosition = testBlocked.decrementRow();
        } else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            testBlocked = startPosition.incrementRow();
            endPosition = testBlocked.incrementRow();
        }

        if (endPosition.onBoard() && board.at(endPosition) == null && board.at(testBlocked) == null) {
            possible.add(new ChessMove(startPosition, endPosition));
        }
        return possible;
    }

    private Set<ChessMove> pawnCapture(ChessPosition startPosition) {
        Set<ChessMove> possible = new HashSet<>();
        var endPosition = startPosition;

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            endPosition = startPosition.incrementRow();
        } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            endPosition = startPosition.decrementRow();
        }

        //check right
        ChessPosition test = endPosition.incrementCol();
        if (test.onBoard() && board.at(test) != null && board.at(test).getTeamColor() != piece.getTeamColor()) {
            if ((test.getRow() == 1 || test.getRow() == 8)
                    && board.at(test).getPieceType() != ChessPiece.PieceType.KING) {
                //tests for king because if king is captured, game ends and pawn won't be promoted
                possible.addAll((promotePawn(startPosition, test)));
            } else {
                possible.add(new ChessMove(startPosition, test));
            }
        }

        //check left
        test = endPosition.decrementCol();
        if (test.onBoard() && board.at(test) != null && board.at(test).getTeamColor() != piece.getTeamColor()) {
            if ((test.getRow() == 1 || test.getRow() == 8)
                    && board.at(test).getPieceType() != ChessPiece.PieceType.KING) {
                possible.addAll((promotePawn(startPosition, test)));
            } else {
                possible.add(new ChessMove(startPosition, test));
            }
        }

        return possible;
    }

    private Set<ChessMove> moveKnight(ChessPosition start) {
        Set<ChessMove> possible = new HashSet<>();
        Set<ChessPosition> tests = new HashSet<>();

        //move northeast
        tests.add(start.incrementRow().incrementCol().incrementCol());
        tests.add(start.incrementRow().incrementRow().incrementCol());

        //move northwest
        tests.add(start.incrementRow().decrementCol().decrementCol());
        tests.add(start.incrementRow().incrementRow().decrementCol());

        //move southeast
        tests.add(start.decrementRow().incrementCol().incrementCol());
        tests.add(start.decrementRow().decrementRow().incrementCol());

        //move southwest
        tests.add(start.decrementRow().decrementCol().decrementCol());
        tests.add(start.decrementRow().decrementRow().decrementCol());

        for (ChessPosition test : tests) {
            if (test.onBoard() && (board.at(test) == null || board.at(test).getTeamColor() != piece.getTeamColor())) {
                possible.add(new ChessMove(start, test));
            }
        }

        return possible;
    }
}
