package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.User;

import java.util.Collection;

public class ChessService {
    DataAccess dataAccess;

    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() throws DataAccessException {
        dataAccess.clear();
    }


    public Integer registerUser(User user) throws DataAccessException {
        //test if user already exists
        if (dataAccess.userExists(user)) {
            throw new DataAccessException("Error: already exists");
        }

        dataAccess.createUser(user);

        var authToken = dataAccess.login(user.getUsername());

        return authToken;
    }


    public Integer login(String username, String user) throws DataAccessException {
        if (!dataAccess.verifyUser(username, user)) {
            throw new DataAccessException("Error: unauthorized");
        }

        return dataAccess.login(username);
    }


    public boolean logout(String authToken) throws DataAccessException {
        return false;
    }


    public Collection<String> listGames(String authToken) throws DataAccessException {
        return null;
    }


    public String createGame(String authToken, String gameName) throws DataAccessException {
        return null;
    }


    public String joinGame(String authToken, String playerColor, String gameID) throws DataAccessException {
        return null;
    }
}
