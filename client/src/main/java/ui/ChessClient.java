package ui;

import exception.ResponseException;
import model.UserData;
import server.ServerFacade;

import java.util.Arrays;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;

    private State state = State.LOGGEDOUT;
    private String authToken;

    public ChessClient(String serverUrl) {
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
                //TODO:: make sure "logout" deletes the authToken after
                case "register" -> registerUser(params);
                case "quit" -> "quit"; //TODO:: might need to change state or smthg here
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String registerUser(String... params) throws ResponseException {
        if (params.length >= 3) {
            var username = params[0];
            var password = params[1];
            var email = params[2];
            var user = new UserData(username, password, email);
            try {
                var authData = server.registerUser(user);
                state = State.LOGGEDIN;
                authToken = authData.authToken();
                return String.format("Successfully registered and signed in");
            } catch (ResponseException ex) {
                switch (ex.getStatusCode()) {
                    case 400 -> throw new ResponseException(400, "Expected: <username> <password> <email>");
                    case 403 -> throw new ResponseException(403, "Username already taken");
                    case 500 -> throw new ResponseException(500, "Error: try again");
                }
            }
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    public String help() {
        if (state == State.LOGGEDOUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - playing chess
                    help - with possible commands
                    """;
        }
        return """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - a game
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.LOGGEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }

    public String getState() {
        return state.toString();
    }
}
