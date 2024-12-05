package ui;

import exception.ResponseException;
import server.ServerFacade;

import java.util.Arrays;

public class Client {
    private final ServerFacade server;
    private final String serverUrl;

    private State state = State.LOGGEDOUT;

    public Client(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            //if length > 0, sets cmd to tokens[0]; else sets it to "help"
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                //TODO:: add all your functions here using these formats as an example
//                case "signin" -> signIn(params);
//                case "rescue" -> rescuePet(params);
//                case "list" -> listPets();
//                case "signout" -> signOut();
//                case "adopt" -> adoptPet(params);
//                case "adoptall" -> adoptAllPets();
                case "Register" -> registerUser(params);
                case "Quit" -> "Quit"; //TODO:: might need to change state or smthg here
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String registerUser(String... params) throws ResponseException {
        return null;
    }

    public String help() {
        if (state == State.LOGGEDOUT) {
            //TODO:: update this text with what should print when you enter "help"
            return """
                    - signIn <yourname>
                    - quit
                    """;
        }
        return """
                - list
                - adopt <pet id>
                - rescue <name> <CAT|DOG|FROG|FISH>
                - adoptAll
                - signOut
                - quit
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.LOGGEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}
