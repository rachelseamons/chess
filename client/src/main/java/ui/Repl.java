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
        //TODO:: should I print the "help" list?

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                //TODO:: find a better green
                System.out.print(SET_TEXT_COLOR_GREEN + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_OUT] >>> ");
    }
}
