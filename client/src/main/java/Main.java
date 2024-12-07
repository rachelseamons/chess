import chess.*;
import ui.Repl;

import java.util.Scanner;

import server.Server;

public class Main {
    public static void main(String[] args) {
        var server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        new Repl("http://localhost:" + port).run();
    }
}