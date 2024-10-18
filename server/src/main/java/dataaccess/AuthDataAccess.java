package dataaccess;

import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;

public interface AuthDataAccess {
    void clearUsers() throws DataAccessException;
    void clearAuth() throws DataAccessException;
    void clearGames() throws DataAccessException;
    boolean userExists(UserData user);
    void createUser(UserData user);
    AuthData login(UserData user) throws DataAccessException;
    boolean verifyUser(UserData user);
    void logout(AuthData auth) throws DataAccessException;
    Map<Integer, GameData> getGames(AuthData auth) throws DataAccessException;
    GameData createGame(GameData game) throws DataAccessException;

    // There are so many problems happening right now and I can't handle this anymore tonight, so we'll come back to
    // this in the morning
}
