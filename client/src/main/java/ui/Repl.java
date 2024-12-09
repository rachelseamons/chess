package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    public void run() {
        System.out.printf("Welcome to chess 240. Type \"Help\" to get started.%n");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                if (result.startsWith("Error:")) {
                    System.out.println(SET_TEXT_COLOR_RED + result);
                } else {
                    System.out.print(SET_TEXT_COLOR_GREEN + result);
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void printPrompt() {
        var state = "";
        if (client.getState().equals("LOGGEDOUT")) {
            state = "[Logged out]";
        } else {
            state = "[Logged in]";
        }
        System.out.print("\n" + RESET_TEXT_COLOR + state + " >>> ");
    }
}
