package dataAccess;

import javax.xml.crypto.Data;
import java.util.Collection;

public interface DataAccess {
    void clear() throws DataAccessException;

    String registerUser(User user) throws DataAccessException;

    String login(String username, String password) throws DataAccessException;

    boolean logout(String authToken) throws DataAccessException;

    Collection<String> listGames(String authToken) throws DataAccessException;

    String createGame(String authToken, String gameName) throws DataAccessException;

    String joinGame(String authToken, String playerColor, String gameID) throws DataAccessException;
}
