package ui;

import exception.ResponseException;
import model.GameData;
import model.UserData;
import server.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;

    private State state = State.LOGGEDOUT;
    private String authToken;
    private Map<String, GameData> games = new HashMap<>();

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
                case "list" -> listGames();
                case "create" -> createGame(params);
                case "login" -> loginUser(params);
                case "logout" -> logoutUser();
                case "register" -> registerUser(params);
                case "help" -> help();
                case "quit" -> "quit"; //TODO:: might need to change state or smthg here
                default -> "Error: type \"help\" for available commands";
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String listGames() throws ResponseException {
        if (authToken != null) {
            assertSignedIn();
            try {
                var retrievedGames = server.listGames(authToken);
                int i = 1;
                for (GameData game : retrievedGames) {
                    var name = game.gameName();
                    if (games.containsKey(name)) {
                        name = String.format(name + "_[" + i + "]");
                        i = i + 1;
                        var newNameGame = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), name, game.game());
                        games.put(name, newNameGame);
                    } else {
                        games.put(name, game);
                    }
                }
                i = 1;
                StringBuilder response = new StringBuilder();
                for (GameData game : games.values()) {
                    response.append("\n").append(i).append(". ");
                    response.append(gameDataToString(game));
                    i = i + 1;
                }
                return response.toString();
            } catch (ResponseException ex) {
                switch (ex.getStatusCode()) {
                    case 401 -> throw new ResponseException(401, "Error: not logged in");
                    case 500 -> throw new ResponseException(500, "Error: try again");
                }
            }
        }
        throw new ResponseException(401, "Error: not logged in");
    }

    public String createGame(String... param) throws ResponseException {
        if (param.length == 1) {
            assertSignedIn();
            var gameName = param[0];
            var newGame = new GameData(0, null, null, gameName, null);
            try {
                var createdGame = server.createGame(authToken, newGame);
                return String.format("Successfully created game " + createdGame.gameName());
            } catch (ResponseException ex) {
                switch (ex.getStatusCode()) {
                    case 400 -> throw new ResponseException(400, "Error: expected <game name>");
                    case 401 -> throw new ResponseException(401, "Error: not logged in");
                    case 500 -> throw new ResponseException(500, "Error: try again");
                }
            }
        } else if (param.length > 1) {
            throw new ResponseException(400, "Error: game name must be one word");
        }
        throw new ResponseException(401, "Error: not logged in");
    }

    public String loginUser(String... params) throws ResponseException {
        if (params.length >= 2) {
            if (state == State.LOGGEDIN) {
                throw new ResponseException(500, "Error: already logged in");
            }
            var username = params[0];
            var password = params[1];
            var loginUser = new UserData(username, password, null);
            try {
                var authData = server.loginUser(loginUser);
                state = State.LOGGEDIN;
                authToken = authData.authToken();
                return "Successfully signed in";
            } catch (ResponseException ex) {
                throw new ResponseException(401, "Error: incorrect username or password");
            }
        }
        throw new ResponseException(500, "Error: expected <username> <password>");
    }

    public String logoutUser() throws ResponseException {
        if (authToken != null) {
            assertSignedIn();
            try {
                server.logoutUser(authToken);
                state = State.LOGGEDOUT;
                authToken = null;
                return ("Successfully logged out");
            } catch (ResponseException ex) {
                throw new ResponseException(401, "Error: not logged in");
            }
        }
        throw new ResponseException(401, "Error: not logged in");
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
                return "Successfully registered and signed in";
            } catch (ResponseException ex) {
                switch (ex.getStatusCode()) {
                    case 400 -> throw new ResponseException(400, "Error: expected <username> <password> <email>");
                    case 403 -> throw new ResponseException(403, "Error: username already taken");
                    case 500 -> throw new ResponseException(500, "Error: try again");
                }
            }
        }
        throw new ResponseException(400, "Error: expected <username> <password> <email>");
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

    private String gameDataToString(GameData gameData) {
        var gameName = gameData.gameName();
        var whiteUsername = gameData.whiteUsername();
        var blackUsername = gameData.blackUsername();

        if (whiteUsername == null) {
            whiteUsername = "None";
        }
        if (blackUsername == null) {
            blackUsername = "None";
        }
        return String.format("game name: " + gameName + "\n\tplayer white: " + whiteUsername + "\n\tplayer black: " + blackUsername);
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.LOGGEDOUT) {
            throw new ResponseException(400, "Error: not logged in");
        }
    }

    public String getState() {
        return state.toString();
    }
}
