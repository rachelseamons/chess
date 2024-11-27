package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.mindrot.jbcrypt.BCrypt;
import server.JoinRequest;
import service.ChessException;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLDataAccess implements DataAccess {

    public SQLDataAccess() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public UserData getUserByUsername(String username) throws ChessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var retrievedUser = rs.getString("username");
                        return new UserData(retrievedUser, null, null);
                    }
                }
            }
        } catch (Exception ex) {
            throw new ChessException(ex.getMessage(), 45);
        }
        return null;
    }

    @Override
    public UserData createUser(UserData user) throws ChessException {
        try {
            var username = user.username();
            var email = user.email();
            var password = BCrypt.hashpw(user.password(), BCrypt.gensalt());
            var statement = "INSERT INTO users (username, email, password) VALUES(?, ?, ?)";
            try (var conn = DatabaseManager.getConnection()) {
                try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                    ps.setString(1, username);
                    ps.setString(2, email);
                    ps.setString(3, password);
                    ps.executeUpdate();
                }
            }
        } catch (Exception ex) {
            if (ex.getMessage().startsWith("Duplicate entry")) {
                throw new ChessException("already taken", 403);
            } else {
                throw new ChessException("unknown dataAccess error", 45);
            }
        }
        return new UserData(user.username(), null, user.email());
    }

    @Override
    public AuthData createAuth(String username) throws ChessException {
        var authToken = UUID.randomUUID().toString();
        var statement = "INSERT INTO auth (authToken, username) VALUES(?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                ps.setString(2, username);
                ps.executeUpdate();
            }
        } catch (Exception ex) {
            throw new ChessException("unknown dataAccess error", 45);
        }
        return new AuthData(authToken, username);
    }

    @Override
    public void clear() throws ChessException {
        List<String> statements = new ArrayList<>();
        statements.add("TRUNCATE users");
        statements.add("TRUNCATE auth");
        //TODO:: clear the other db tables once they're created
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : statements) {
                try (var ps = conn.prepareStatement(statement)) {
                    ps.executeUpdate();
                }
            }
        } catch (Exception ex) {
            throw new ChessException("Error: " + ex.getMessage(), 500);
        }
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
              username varchar(256) NOT NULL,
              password varchar(256) NOT NULL,
              email varchar(256) NOT NULL,
              PRIMARY KEY (username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,

            """
            CREATE TABLE IF NOT EXISTS auth (
              authToken varchar(256) NOT NULL,
              username varchar(256) NOT NULL,
              PRIMARY KEY (authToken),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
//
//            """
//            CREATE TABLE IF NOT EXISTS games (
//              'id' int NOT NULL AUTO_INCREMENT,
//              'gameName' varchar(256) NOT NULL,
//              'whiteUsername' varchar(256),
//              'blackUsername' varchar(256),
//              'game' ChessGame NOT NULL, //does this need to be json?
//              PRIMARY KEY ('id'),
//              INDEX(gameName)
//            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
//            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to create configure database");
        }
    }
}
