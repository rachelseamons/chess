package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import server.JoinRequest;
import service.ChessException;

import java.sql.SQLException;
import java.util.Set;

public class SQLDataAccess implements DataAccess {

    public SQLDataAccess() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public UserData getUserByUsername(String username) {
        return null;
    }

    @Override
    public UserData createUser(UserData user) {
        return null;
    }

    @Override
    public AuthData createAuth(String username) {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean verifyUser(UserData user) {
        return false;
    }

    @Override
    public AuthData getUserByAuthtoken(String authToken) {
        return null;
    }

    @Override
    public void logoutUser(String authToken) {

    }

    @Override
    public GameData createGame(GameData game) {
        return null;
    }

    @Override
    public Set<GameData> listGames() {
        return null;
    }

    @Override
    public void joinGame(JoinRequest request, String username) throws ChessException {

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
              'id' in NOT NULL AUTO_INCREMENT,
              'username' varchar(256) NOT NULL,
              'password' varchar(256) NOT NULL,
              'email' varchar(256) NOT NULL,
              PRIMARY KEY ('id'),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,

            """
            CREATE TABLE IF NOT EXISTS auth (
              'authToken' in NOT NULL AUTO_INCREMENT,
              'username' varchar(256) NOT NULL,
              PRIMARY KEY ('authToken')
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,

            """
            CREATE TABLE IF NOT EXISTS games (
              'id' int NOT NULL AUTO_INCREMENT,
              'gameName' varchar(256) NOT NULL,
              'whiteUsername' varchar(256),
              'blackUsername' varchar(256),
              'game' ChessGame NOT NULL, //does this need to be json?
              PRIMARY KEY ('id'),
              INDEX(gameName)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatment = conn.prepareStatement(statement)) {
                    preparedStatment.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to create configure database");
        }
    }
}
