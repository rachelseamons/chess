package service;

import dataaccess.DataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.JoinRequest;

import java.util.Set;

public class Service {
    private final DataAccess dataAccess;

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData registerUser(UserData user) throws ChessException {
        if (dataAccess.getUserByUsername(user.username()) != null) {
            throw new ChessException("already taken", 403);
        }
        var registeredUser = dataAccess.createUser(user);
        return dataAccess.createAuth(registeredUser.username());
    }

    public void clear() throws ChessException {
        dataAccess.clear();
    }

    public AuthData loginUser(UserData user) throws ChessException {
        if (!dataAccess.verifyUser(user)) {
            throw new ChessException("unauthorized", 401);
        }

        return dataAccess.createAuth(user.username());
    }

    public void logoutUser(String authToken) throws ChessException {
        if (dataAccess.getUserByAuthtoken(authToken) == null) {
            throw new ChessException("unauthorized", 401);
        }

        dataAccess.logoutUser(authToken);
    }

    public GameData createGame(String authToken, GameData game) throws ChessException {
        if (dataAccess.getUserByAuthtoken(authToken) == null) {
            throw new ChessException("unauthorized", 401);
        }

        return dataAccess.createGame(game);
    }

    public Set<GameData> listGames(String authToken) throws ChessException {
        if (dataAccess.getUserByAuthtoken(authToken) == null) {
            throw new ChessException("unauthorized", 401);
        }

        return dataAccess.listGames();
    }

    public void joinGame(String authToken, JoinRequest request) throws ChessException {
        if (dataAccess.getUserByAuthtoken(authToken) == null) {
            throw new ChessException("unauthorized", 401);
        }
        var username = dataAccess.getUserByAuthtoken(authToken).username();

        dataAccess.joinGame(request, username);
    }
}
