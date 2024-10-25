package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import server.JoinRequest;
import service.ChessException;

import java.util.Set;

public interface DataAccess {
    UserData getUserByUsername(String username);

    UserData createUser(UserData user);

    AuthData createAuth(String username);

    void clear();

    boolean verifyUser(UserData user);

    AuthData getUserByAuthtoken(String authToken);

    void logoutUser(String authToken);

    GameData createGame(GameData game);

    Set<GameData> listGames();

    void joinGame(JoinRequest request, String username) throws ChessException;
}
