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

        //check right and left
        ChessPosition right = endPosition.incrementCol();
        ChessPosition left = endPosition.decrementCol();
        possible.addAll(testPawnCapturable(startPosition, right));
        possible.addAll(testPawnCapturable(startPosition, left));

        return possible;
    }

    private Set<ChessMove> testPawnCapturable(ChessPosition startPosition, ChessPosition test) {
        Set<ChessMove> possible = new HashSet<>();

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

    private Set<ChessMove> moveRook(ChessPosition start) {
        Set<ChessMove> possible = new HashSet<>();

        possible.addAll(straightLine(start, Direction.N));
        possible.addAll(straightLine(start, Direction.W));
        possible.addAll(straightLine(start, Direction.S));
        possible.addAll(straightLine(start, Direction.E));

        return possible;
    }

    private Set<ChessMove> moveBishop(ChessPosition start) {
        Set<ChessMove> possible = new HashSet<>();

        possible.addAll(straightLine(start, Direction.NW));
        possible.addAll(straightLine(start, Direction.SW));
        possible.addAll(straightLine(start, Direction.SE));
        possible.addAll(straightLine(start, Direction.NE));

        return possible;
    }

    private Set<ChessMove> moveRoyalty(ChessPosition start) {
        Set<ChessMove> possible = new HashSet<>();

        possible.addAll(moveRook(start));
        possible.addAll(moveBishop(start));

        return possible;
    }

    private Set<ChessMove> straightLine(ChessPosition start, Direction direction) {
        Set<ChessMove> possible = new HashSet<>();
        ChessPosition test = start;

        do {
            if (board.at(start).getPieceType() == ChessPiece.PieceType.KING) {
                repeatable = false;
            }
            test = moveDirection(test, direction);
            if (!test.onBoard()) {
                repeatable = false;
            } else if (board.at(test) == null) {
                possible.add(new ChessMove(start, test));
            } else if (board.at(test).getTeamColor() != piece.getTeamColor()) {
                possible.add(new ChessMove(start, test));
                repeatable = false;
            } else {
                repeatable = false;
            }
        } while (repeatable);

        repeatable = true;
        return possible;
    }

    private ChessPosition moveDirection(ChessPosition start, Direction direction) {
        if (direction == Direction.N) {
            return start.incrementRow();
        } else if (direction == Direction.NE) {
            return start.incrementRow().incrementCol();
        } else if (direction == Direction.E) {
            return start.incrementCol();
        } else if (direction == Direction.SE) {
            return start.decrementRow().incrementCol();
        } else if (direction == Direction.S) {
            return start.decrementRow();
        } else if (direction == Direction.SW) {
            return start.decrementRow().decrementCol();
        } else if (direction == Direction.W) {
            return start.decrementCol();
        } else if (direction == Direction.NW) {
            return start.incrementRow().decrementCol();
        }

        return start;
    }

    public boolean isInCheck(ChessGame.TeamColor teamColor) {
        //find position of king
        ChessPosition kingPosition = null;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition test = new ChessPosition(i, j);
                if (board.at(test) != null && board.at(test).getTeamColor() == teamColor
                        && board.at(test).getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = test;
                }
            }
        }

        if (kingPosition == null) {
            return false;
        }

        //test if any piece on opposing team can move to king
        Set<ChessMove> possiblePieceMoves = new HashSet<>();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition test = new ChessPosition(i, j);
                if (board.at(test) != null && board.at(test).getTeamColor() != teamColor) {
                    ChessMove testMove = new ChessMove(test, kingPosition);
                    possiblePieceMoves.add(testMove);
                }
            }
        }

        for (ChessMove testMove : possiblePieceMoves) {
            piece = board.at(testMove.getStartPosition());
            Set<ChessMove> validMoves = getPossibleMoves(testMove.getStartPosition());
            for (ChessMove move : validMoves) {
                if (move.equals(testMove)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    private enum Direction {
        N, NE, E, SE, S, SW, W, NW;
    }
}
