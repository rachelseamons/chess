package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.JoinRequest;
import service.ChessException;

import java.util.*;

public class MemoryDataAccess implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<String, AuthData> auths = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();
    private Integer currGameID = 15;

    public UserData getUserByUsername(String username) {
        if (users.containsKey(username)) {
            return users.get(username);
        }
        return null;
    }

    public UserData createUser(UserData user) {
        users.put(user.username(), user);
        return user;
    }

    public AuthData createAuth(String username) {
        var authToken = generateToken();
        AuthData newData = new AuthData(authToken, username);
        auths.put(authToken, newData);
        return newData;
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void clear() {
        users.clear();
        auths.clear();
        games.clear();
    }

    public boolean verifyUser(UserData user) {
        return users.containsKey(user.username()) && users.get(user.username()).password().equals(user.password());
    }

    public AuthData getUserByAuthtoken(String authToken) {
        if (auths.containsKey(authToken)) {
            return auths.get(authToken);
        }
        return null;
    }

    public void logoutUser(String authToken) {
        auths.remove(authToken);
    }

    public GameData createGame(GameData game) {
        var gameID = currGameID;
        currGameID = currGameID + 1;
        var newGame = new GameData(gameID, null, null, game.gameName(), new ChessGame());
        games.put(gameID, newGame);

        return newGame;
    }

    public Set<GameData> listGames() {
        return new HashSet<>(games.values());
    }

    public void joinGame(JoinRequest request, String username) throws ChessException {
        var gameID = request.gameID();
        var playerColor = request.playerColor();

        if (!games.containsKey(request.gameID())) {
            throw new ChessException("bad request", 400);
        }
        var gameName = games.get(gameID).gameName();

        if (playerColor.equals("WHITE")) {
            if (games.get(gameID).whiteUsername() != null) {
                throw new ChessException("already taken", 403);
            }

            //if the game exists and white is available, add the user as whiteUsername
            var blackPlayer = games.get(gameID).blackUsername();
            var newGame = new GameData(gameID, username, blackPlayer, gameName, new ChessGame());
            games.put(gameID, newGame);
        } else if (playerColor.equals("BLACK")) {
            if (games.get(gameID).blackUsername() != null) {
                throw new ChessException("already taken", 403);
            }

            //if the game exists and black is available, add the user as blackUsername
            var whitePlayer = games.get(gameID).whiteUsername();
            var newGame = new GameData(gameID, whitePlayer, username, gameName, new ChessGame());
            games.put(gameID, newGame);
        } else {
            //bad request if playerColor isn't BLACK or WHITE
            throw new ChessException("bad request", 400);
        }
    }
}
