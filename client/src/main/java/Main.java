import chess.*;

import java.util.Scanner;

public class Main {
    //TODO:: getting code for this from https://github.com/softwareconstruction240/softwareconstruction/blob/main/instruction/console-ui/console-ui.md
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        boolean run = true;
        while (run) {
            System.out.printf("Welcome to chess 240. Type \"Help\" to get started.%n[LOGGED_OUT] >>> ");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();

        }
    }
}