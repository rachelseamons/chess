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
    private static final String EMPTY = EscapeSequences.EMPTY;
    private static String[] columnLabels;
    private String[] rowLabels;

    public BoardPrinter(boolean printWhite) {
        if (printWhite) {
            columnLabels = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
        } else {
            columnLabels = new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};
        }
    }

    public void print() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        drawColumnLabels(out);

        out.print(RESET_TEXT_COLOR);
        out.print(RESET_BG_COLOR);
    }

    private static void drawColumnLabels(PrintStream out) {
        setBlack(out);

        out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawColumnLabel(out, columnLabels[boardCol]);
//            out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
        }
        out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
        out.println();
    }

    private static void drawColumnLabel(PrintStream out, String columnLabel) {
        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

        out.print(EMPTY.repeat(prefixLength));
        printColumnLabel(out, columnLabel);
        out.print(EMPTY.repeat(suffixLength));
    }

    private static void printColumnLabel(PrintStream out, String label) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_GREEN);

        out.print(label);

        setBlack(out);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }
}
