package dataAccess;

import java.util.Collection;

public class DataAccessMemory implements DataAccess {
    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public String registerUser(User user) throws DataAccessException {
        return null;
    }

    @Override
    public String login(String username, String password) throws DataAccessException {
        return null;
    }

    @Override
    public boolean logout(String authToken) throws DataAccessException {
        return false;
    }

    @Override
    public Collection<String> listGames(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public String createGame(String authToken, String gameName) throws DataAccessException {
        return null;
    }

    @Override
    public String joinGame(String authToken, String playerColor, String gameID) throws DataAccessException {
        return null;
    }
}
