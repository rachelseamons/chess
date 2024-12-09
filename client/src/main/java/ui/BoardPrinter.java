package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class BoardPrinter {
    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    // Padded characters
    private static final String EMPTY = "   ";
    private static String[] columnLabels;
    private static String[] rowLabels;
    private static boolean drawWhite;

    public BoardPrinter(boolean printWhite) {
        if (printWhite) {
            columnLabels = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
            rowLabels = new String[]{"8", "7", "6", "5", "4", "3", "2", "1"};
            drawWhite = true;
        } else {
            columnLabels = new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};
            rowLabels = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
            drawWhite = false;
        }
    }

    public void print() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        drawColumnLabels(out);
        drawChessBoard(out);
        drawColumnLabels(out);
    }

    private static void drawColumnLabels(PrintStream out) {
        setLabelTheme(out);

        out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawColumnLabel(out, columnLabels[boardCol]);
//            out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
        }
        out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));

        resetTheme(out);
        out.println();
    }

    private static void drawColumnLabel(PrintStream out, String columnLabel) {
        String paddedLabel = String.format(" " + columnLabel + "\u2003");
        printColumnLabel(out, paddedLabel);
    }

    private static void printColumnLabel(PrintStream out, String label) {
        setLabelTheme(out);
        out.print(label);
    }

    private static void drawChessBoard(PrintStream out) {
        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            setLabelTheme(out);
            String rowLabel = String.format(" " + rowLabels[boardRow] + " ");
            out.print(rowLabel);
            if (boardRow == 0 || boardRow == 7) {
                if (drawWhite) {
                    drawWhiteRoyaltyRow(out, boardRow);
                } else {
                    drawBlackRoyaltyRow(out, boardRow);
                }
            } else if (boardRow == 1) {
                if (drawWhite) {
                    drawBlackPawnRow(out, boardRow);
                } else {
                    drawWhitePawnRow(out, boardRow);
                }
            } else if (boardRow == 6) {
                if (drawWhite) {
                    drawWhitePawnRow(out, boardRow);
                } else {
                    drawBlackPawnRow(out, boardRow);
                }
            } else {
                drawEmptyRow(out, boardRow);
            }
            setLabelTheme(out);
            out.print(rowLabel);
            resetTheme(out);
            out.println();
        }
    }

    private static void drawEmptyRow(PrintStream out, Integer boardRow) {
        if (boardRow % 2 == 0) {
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
                if (col % 2 == 0) {
                    greenSquare(out);
                    out.print(SET_TEXT_COLOR_GREEN);
                } else {
                    greySquare(out);
                    out.print(SET_TEXT_COLOR_LIGHT_GREY);
                }
                out.print(BLACK_KING);
            }
        } else {
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
                if (col % 2 == 0) {
                    greySquare(out);
                    out.print(SET_TEXT_COLOR_LIGHT_GREY);
                } else {
                    greenSquare(out);
                    out.print(SET_TEXT_COLOR_GREEN);
                }
                out.print(BLACK_KING);
            }
        }
    }

    private static void drawWhiteRoyaltyRow(PrintStream out, Integer boardRow) {
        if (boardRow == 0) {
            out.print(SET_TEXT_COLOR_BLACK);
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
                if (col % 2 == 0) {
                    greenSquare(out);
                } else {
                    greySquare(out);
                }

                if (col == 0 || col == 7) {
                    out.print(BLACK_ROOK);
                } else if (col == 1 || col == 6) {
                    out.print(BLACK_KNIGHT);
                } else if (col == 2 || col == 5) {
                    out.print(BLACK_BISHOP);
                } else if (col == 3) {
                    out.print(BLACK_QUEEN);
                } else {
                    out.print(BLACK_KING);
                }
            }
        } else {
            out.print(SET_TEXT_COLOR_WHITE);
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
                if (col % 2 == 0) {
                    greySquare(out);
                } else {
                    greenSquare(out);
                }

                if (col == 0 || col == 7) {
                    out.print(BLACK_ROOK);
                } else if (col == 1 || col == 6) {
                    out.print(BLACK_KNIGHT);
                } else if (col == 2 || col == 5) {
                    out.print(BLACK_BISHOP);
                } else if (col == 3) {
                    out.print(BLACK_QUEEN);
                } else {
                    out.print(BLACK_KING);
                }
            }
        }
    }

    private static void drawBlackRoyaltyRow(PrintStream out, Integer boardRow) {
        if (boardRow == 0) {
            out.print(SET_TEXT_COLOR_WHITE);
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
                if (col % 2 == 0) {
                    greenSquare(out);
                } else {
                    greySquare(out);
                }

                if (col == 0 || col == 7) {
                    out.print(BLACK_ROOK);
                } else if (col == 1 || col == 6) {
                    out.print(BLACK_KNIGHT);
                } else if (col == 2 || col == 5) {
                    out.print(BLACK_BISHOP);
                } else if (col == 3) {
                    out.print(BLACK_KING);
                } else {
                    out.print(BLACK_QUEEN);
                }
            }
        } else {
            out.print(SET_TEXT_COLOR_BLACK);
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
                if (col % 2 == 0) {
                    greySquare(out);
                } else {
                    greenSquare(out);
                }

                if (col == 0 || col == 7) {
                    out.print(BLACK_ROOK);
                } else if (col == 1 || col == 6) {
                    out.print(BLACK_KNIGHT);
                } else if (col == 2 || col == 5) {
                    out.print(BLACK_BISHOP);
                } else if (col == 3) {
                    out.print(BLACK_KING);
                } else {
                    out.print(BLACK_QUEEN);
                }
            }
        }
    }

    private static void drawWhitePawnRow(PrintStream out, Integer boardRow) {
        out.print(SET_TEXT_COLOR_WHITE);
        if (boardRow == 1) {
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
                if (col % 2 == 0) {
                    greySquare(out);
                } else {
                    greenSquare(out);
                }

                out.print(BLACK_PAWN);
            }
        } else {
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
                if (col % 2 == 0) {
                    greenSquare(out);
                } else {
                    greySquare(out);
                }

                out.print(BLACK_PAWN);
            }
        }
    }

    private static void drawBlackPawnRow(PrintStream out, Integer boardRow) {
        out.print(SET_TEXT_COLOR_BLACK);
        if (boardRow == 1) {
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
                if (col % 2 == 0) {
                    greySquare(out);
                } else {
                    greenSquare(out);
                }

                out.print(BLACK_PAWN);
            }
        } else {
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
                if (col % 2 == 0) {
                    greenSquare(out);
                } else {
                    greySquare(out);
                }

                out.print(BLACK_PAWN);
            }
        }
    }

    private static void setLabelTheme(PrintStream out) {
        out.print(RESET_BG_COLOR);
        out.print(SET_TEXT_COLOR_GREEN);
    }

    private static void greenSquare(PrintStream out) {
        out.print(SET_BG_COLOR_GREEN);
    }

    private static void greySquare(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
    }

    private static void resetTheme(PrintStream out) {
        out.print(RESET_TEXT_COLOR);
        out.print(RESET_BG_COLOR);
    }
}
