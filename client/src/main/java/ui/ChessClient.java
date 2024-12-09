package ui;

import exception.ResponseException;
import model.GameData;
import model.UserData;
import model.JoinRequest;
import serverfacade.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ChessClient {
    private final ServerFacade server;

    private State state = State.LOGGEDOUT;
    private String authToken;
    private Map<Integer, GameData> games = new HashMap<>();

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            tokens[0] = tokens[0].toLowerCase();
            var cmd = tokens[0];
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "observe" -> observeGame(params);
                case "join" -> joinGame(params);
                case "list" -> listGames();
                case "create" -> createGame(params);
                case "login" -> loginUser(params);
                case "logout" -> logoutUser();
                case "register" -> registerUser(params);
                case "help" -> help();
                case "quit" -> "quit";
                default -> "Error: type \"help\" for available commands";
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String observeGame(String... param) throws ResponseException {
        if (param.length == 1) {
            assertSignedIn();
            updateGames();

            var gameNumber = param[0];
            for (int i = 0; i < gameNumber.length(); i++) {
                if (!Character.isDigit(gameNumber.charAt(i))) {
                    throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED + "Error: game ID does not exist");
                }
            }

            var gameNumberInt = Integer.parseInt(gameNumber);
            if (!games.containsKey(gameNumberInt)) {
                throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED + "Error: game ID does not exist");
            }
            var printer = new BoardPrinter(true);
            printer.print();
            return "";
            //if I were to be continuing this project, I would add a map at this level
            //that tracks all the observers for each game (Map<Integer, Set<String>> observers).
        }
        throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED + "Error: expected <gameID>");
    }

    public String joinGame(String... params) throws ResponseException {
        if (params.length == 2) {
            assertSignedIn();
            updateGames();

            var gameNumber = params[0];
            for (int i = 0; i < gameNumber.length(); i++) {
                if (!Character.isDigit(gameNumber.charAt(i))) {
                    throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED + "Error: game ID does not exist");
                }
            }

            var gameNumberInt = Integer.parseInt(gameNumber);
            if (!games.containsKey(gameNumberInt)) {
                throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED + "Error: game ID does not exist");
            }
            var playerColor = params[1];
            playerColor = playerColor.toUpperCase();

            var gameID = games.get(gameNumberInt).gameID();
            var request = new JoinRequest(playerColor, gameID);
            try {
                server.joinGame(authToken, request);
                updateGames();

                if (playerColor.equals("WHITE")) {
                    var printer = new BoardPrinter(true);
                    printer.print();
                } else {
                    var printer = new BoardPrinter(false);
                    printer.print();
                }
                return "";
            } catch (ResponseException ex) {
                switch (ex.getStatusCode()) {
                    case 400 ->
                            throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED + "Error: expected <gameID> [WHITE|BLACK]");
                    case 401 ->
                            throw new ResponseException(401, EscapeSequences.SET_TEXT_COLOR_RED + "Error: not logged in");
                    case 403 ->
                            throw new ResponseException(403, EscapeSequences.SET_TEXT_COLOR_RED + "Error: player color already taken");
                }
            }
        }
        throw new ResponseException(400, EscapeSequences.SET_TEXT_COLOR_RED + "Error: expected <gameID> [WHITE|BLACK]");
    }

    public String listGames() throws ResponseException {
        if (authToken != null) {
            assertSignedIn();
            try {
                updateGames();
                if (games.isEmpty()) {
                    return "No games created";
                }

                StringBuilder response = new StringBuilder();
                for (Integer gameNumber : games.keySet()) {
                    response.append("\n").append(gameNumber).append(". ");
                    response.append(gameDataToString(games.get(gameNumber)));
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
                    case 500 -> throw new ResponseException(500, "Error: username, password, or email is too long");
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

    private void updateGames() throws ResponseException {
        try {
            Map<Integer, GameData> updatedGames = new HashMap<>();
            var retrievedGames = server.listGames(authToken);

            for (GameData updatedGame : retrievedGames) {
                updatedGames.put(updatedGame.gameID(), updatedGame);
            }

            for (Integer oldGame : games.keySet()) {
                if (updatedGames.containsKey(games.get(oldGame).gameID())) {
                    games.put(oldGame, updatedGames.get(games.get(oldGame).gameID()));
                    updatedGames.remove(games.get(oldGame).gameID());
                }
            }

            var i = games.size() + 1;
            for (GameData game : updatedGames.values()) {
                games.put(i, game);
            }

            for (Integer gameNumber : updatedGames.keySet()) {
                games.put(gameNumber, updatedGames.get(gameNumber));
            }
        } catch (ResponseException ex) {
            throw new ResponseException(500, "Error: could not get games");
        }
    }
}
