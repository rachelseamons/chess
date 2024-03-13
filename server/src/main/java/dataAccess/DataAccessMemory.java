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

    }

    @Override
    public boolean userExists(String username) {
        if (users.get(username) == null) {
            return false;
        } else return true;
    }

    @Override
    public void createUser(String username, String password) {
        var user = new User(username, password);
        users.put(username, user);
    }

    @Override
    public Integer login(String username) {
        //finding next available authToken
        //TODO::when removing from auth, set currAuthToken to the removed authToken
        while (auth.get(currAuthToken) != null) {
            currAuthToken++;
        }

        auth.put(currAuthToken, username);
        return currAuthToken;
    }
}
