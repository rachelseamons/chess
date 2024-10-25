package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.ChessException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
}
