package dataAccess;

import model.Game;
import model.User;

import java.util.Map;

public interface DataAccess {
    void clear() throws DataAccessException;

    boolean userExists(User user);

    void createUser(User user);

    Integer login(String username) throws DataAccessException;

    boolean verifyUser(String username, String password);

    void logout(Integer authToken) throws DataAccessException;

    Map<Integer, Game> getGames(Integer authToken) throws DataAccessException;

    Integer createGame(Integer authToken, String gameName) throws DataAccessException;
}
