package dataAccess;

import chess.ChessGame;
import model.User;

import java.util.Collection;
import java.util.HashMap;

public class DataAccessMemory implements DataAccess {
    private HashMap<Integer, ChessGame> games = new HashMap<>();
    private HashMap<String, User> users = new HashMap<>();
    private HashMap<Integer, String> auth = new HashMap<>();
    private int currAuthToken = 0;

    @Override
    public void clear() throws DataAccessException {
        games.clear();
        users.clear();
        auth.clear();

        if (!games.isEmpty() || !users.isEmpty() || !auth.isEmpty()) {
            throw new DataAccessException("Error: description");
        }
    }

    @Override
    public boolean userExists(User user) {
        String username = user.getUsername();
        if (users.get(username) == null) {
            return false;
        } else if (users.get(username).equals(user)) {
            return true;
        } else return false;
    }

    @Override
    public void createUser(User user) {
        String username = user.getUsername();
        users.put(username, user);
    }

    @Override
    public Integer login(String username) {
        //finding next available authToken
        //TODO::when removing from auth, set currAuthToken to the removed authToken
        while (auth.get(currAuthToken) != null) {
            currAuthToken++;
        }

        if (users.get(username) != null && users.get(username).getAuthToken() != null) {
            users.get(username).setAuthToken(currAuthToken);
            return currAuthToken;
        }

        auth.put(currAuthToken, username);
        return currAuthToken;
    }

    @Override
    public boolean verifyUser(String username, String password) {
        if (users.get(username) == null) {
            return false;
        }
        var user = users.get(username);

        return user.verifyPassword(password);
    }
}
