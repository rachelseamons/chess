package dataAccess;

import model.Game;
import model.User;

import java.util.HashMap;
import java.util.Map;

public class DataAccessMemory implements DataAccess {
    private HashMap<Integer, Game> games = new HashMap<>();
    private HashMap<String, User> users = new HashMap<>();
    private HashMap<Integer, String> auth = new HashMap<>();
    private int currAuthToken = 0;
    private int currGameID = 0;

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
        } else return users.get(username).equals(user);
    }

    @Override
    public void createUser(User user) {
        String username = user.getUsername();
        users.put(username, user);
    }

    @Override
    public Integer login(String username) throws DataAccessException {
        //finding next available authToken
        while (auth.get(currAuthToken) != null) {
            currAuthToken++;
        }

        if (users.get(username) != null && users.get(username).getAuthToken() != null) {
            var authToken = users.get(username).getAuthToken();
            //this should indicate a user who's already logged in, so make sure they're in auth
            //and return their old authToken
            if (auth.get(authToken) == null) {
                throw new DataAccessException("Error: description");
            }
            return users.get(username).getAuthToken();
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

    @Override
    public void logout(Integer authToken) throws DataAccessException {
        var user = auth.get(authToken);
        if (user == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        auth.remove(authToken);

        currAuthToken = authToken;
        users.get(user).setAuthToken(null);
    }

    @Override
    public Map<Integer, Game> getGames(Integer authToken) throws DataAccessException {
        var user = auth.get(authToken);
        if (user == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        return games;
    }

    @Override
    public Integer createGame(Integer authToken, String gameName) throws DataAccessException {
        var user = auth.get(authToken);
        if (user == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        //finding next available gameID
        while (games.get(currGameID) != null) {
            currGameID++;
        }

        var newGame = new Game(gameName);
        games.put(currGameID, newGame);
        return currGameID;
    }
}
