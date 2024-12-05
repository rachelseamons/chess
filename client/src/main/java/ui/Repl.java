package ui;

import java.util.Scanner;

public class Repl {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    public void run() {
        System.out.printf("Welcome to chess 240. Type \"Help\" to get started.%n");
        System.out.print(client.help());
        System.out.printf("%n[LOGGED_OUT] >>>");
    }
}
