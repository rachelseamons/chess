import chess.*;
import ui.Repl;

import java.util.Scanner;

public class Main {
    //TODO:: getting code for this from https://github.com/softwareconstruction240/softwareconstruction/blob/main/instruction/console-ui/console-ui.md
    public static void main(String[] args) {
        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        new Repl(serverUrl).run();
    }
}