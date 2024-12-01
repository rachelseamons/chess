package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import server.JoinRequest;
import service.ChessException;

import java.util.Set;

public interface DataAccess {
    UserData getUserByUsername(String username) throws ChessException;

    UserData createUser(UserData user) throws ChessException;

    AuthData createAuth(String username) throws ChessException;

    void clear() throws ChessException;

    boolean verifyUser(UserData user) throws ChessException;

    AuthData getUserByAuthtoken(String authToken) throws ChessException;

    void logoutUser(String authToken);

    GameData createGame(GameData game);

    Set<GameData> listGames();

    void joinGame(JoinRequest request, String username) throws ChessException;
}
