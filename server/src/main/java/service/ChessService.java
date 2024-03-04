package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.User;

import javax.xml.crypto.Data;
import java.util.Collection;

public class ChessService {
    DataAccess dataAccess;

    public ChessService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() throws DataAccessException {

    }


    public String registerUser(User user) throws DataAccessException {
        return null;
    }


    public String login(String username, String password) throws DataAccessException {
        return null;
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
